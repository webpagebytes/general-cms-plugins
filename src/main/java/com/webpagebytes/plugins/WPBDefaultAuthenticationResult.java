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

import com.webpagebytes.cms.WPBAuthenticationResult;

public class WPBDefaultAuthenticationResult implements WPBAuthenticationResult {

	private String userIdentifier;
	private String logoutLink;
	private String loginLink;
	private String userProfileLink;
	
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public String getLogoutLink() {
		return logoutLink;
	}
	public String getLoginLink() {
		return loginLink;
	}
	public String getUserProfileLink() {
		return userProfileLink;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public void setLogoutLink(String logoutLink) {
		this.logoutLink = logoutLink;
	}
	public void setLoginLink(String loginLink) {
		this.loginLink = loginLink;
	}
	public void setUserProfileLink(String userProfileLink) {
		this.userProfileLink = userProfileLink;
	}

	
}
