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



import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBLocalAuthLogout extends HttpServlet {

private static final long serialVersionUID = 1L;

public void init() throws ServletException
{

}

public void doGet(HttpServletRequest req, HttpServletResponse resp)
	     throws ServletException, java.io.IOException
{
	String token = WPBLocalAuthentication.getTokenCookie(req);
	if (token != null && token.length()>0)
	{
		String path = new String(CmsBase64Utility.fromSafePathBase64(token));
		File file = new File(path);
		if (file.exists())
		{
			file.delete();
		}
		// delete the cookie
		Cookie cookie = new Cookie(WPBLocalAuthentication.TOKEN_COOKIE, "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		resp.addCookie(cookie);
	}
	
	resp.sendRedirect(WPBLocalAuthentication.loginPageUrl);
}

}
