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

import java.io.IOException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

public class WPBMemCacheClient {

	private MemcachedClient client;
	
	public WPBMemCacheClient()
	{
	
	}
	public void initialize(String addresses) throws IOException
	{
		client = new MemcachedClient(AddrUtil.getAddresses(addresses));
	}
	
	public String getFingerPrint(String key)
	{
		Object result = client.get(key);
		if (result != null)
		{
			return result.toString();
		}
		return null;
	}
	public void putFingerPrint(String key, String value)
	{
		client.set(key, 30*5, value);
	}
}
