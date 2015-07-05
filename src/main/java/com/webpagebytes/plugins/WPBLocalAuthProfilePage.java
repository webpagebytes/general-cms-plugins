package com.webpagebytes.plugins;

import java.io.File;




import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.utility.CmsBase64Utility;

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

public class WPBLocalAuthProfilePage extends HttpServlet {

private static final long serialVersionUID = 1L;

private static final String login_page = " " +
"<!DOCTYPE html>" +
"<html><head><style>" +
".box {" + 
"border: 1px solid #A0A0A0;" +
"background-color: #E0E0E0;" +
"width: 400px;" +
"margin: 30px auto;} \n" +
".line {" +
"padding: 20px 0px 10px;" +
"margin: 0px auto;" +
"width: 90%;" +
"text-align:center;} \n" +
".error {" +
"margin: 0px auto;" +
"width: 90%;" +
"color: #FF0000" +
"text-align:center;} \n" +
"</style></head>" +
"<body>" +
"<div class='box'>" +
"<div class='line'> Demo profile </div>" +
"<div class='line'> STR_USER_NAME </div>" +
"<div class='error'> STR_ERROR </div>" +
"</div></form>" +
"</body></html>";
		
private static final String ERROR_NO_LOGIN = "You are not logged in";

public void doGet(HttpServletRequest req, HttpServletResponse resp)
	     throws ServletException, java.io.IOException
{
	String strUserName = "";
	String strError = "";
	String token = WPBLocalAuthentication.getTokenCookie(req);
	if (token != null) 
	{
		String path = new String(CmsBase64Utility.fromSafePathBase64(token));
		File file = new File(path);
		if (file.exists())
		{
			strUserName = "User name: " + file.getName();
		} else
		{
			strError =  ERROR_NO_LOGIN;
		}
	} else
	{
		strError = ERROR_NO_LOGIN;
	}

	String html = login_page.replaceAll("STR_USER_NAME", strUserName).replaceAll("STR_ERROR", strError);
	resp.getOutputStream().write(html.getBytes());
	resp.flushBuffer();
}



}
