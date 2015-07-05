/*
 *   Copyright 2015 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.plugins;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.cms.WPBAuthentication;
import com.webpagebytes.cms.WPBAuthenticationResult;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBLocalAuthentication implements WPBAuthentication {

	public static final String TOKEN_COOKIE = "wpbToken";
	private Map<String, String> authenticationParams = new HashMap<String, String>();
	
	public void initialize(Map<String, String> params) throws WPBException
	{
		if (params != null)
		{
			authenticationParams.putAll(params);
		}		
	}
	
	public static String getTokenCookie(HttpServletRequest request)
	{
		Cookie[] cookies = request.getCookies(); 
		if (cookies == null) return null;
		for(Cookie cookie: cookies)
		{
			if (cookie.getName().equals(TOKEN_COOKIE))
			{
				return cookie.getValue();
			}
		}
		return null;
	}
	
	public WPBAuthenticationResult checkAuthentication(HttpServletRequest request) throws WPBIOException
	{
		// this authentication takes the token as base64, decodes it and verifies if it's a file, if yes then the filename 
		//is the userIdentifier 
		WPBDefaultAuthenticationResult result = new WPBDefaultAuthenticationResult();
		result.setLoginLink(authenticationParams.get(WPBAuthentication.CONFIG_LOGIN_PAGE_URL));
		result.setLogoutLink(authenticationParams.get(WPBAuthentication.CONFIG_LOGOUT_URL));
		result.setUserProfileLink(authenticationParams.get(WPBAuthentication.CONFIG_PROFILE_URL));
		
		String token = getTokenCookie(request);
		if (token == null) return result;
		String path = new String(CmsBase64Utility.fromSafePathBase64(token));
		File file = new File(path);
		if (file.exists())
		{
			result.setUserIdentifier(file.getName());
		}		
		return result;
	}
}
