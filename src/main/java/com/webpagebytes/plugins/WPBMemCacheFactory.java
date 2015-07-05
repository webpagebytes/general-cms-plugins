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
import java.util.Map;

import com.webpagebytes.cms.WPBArticlesCache;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.WPBPageModulesCache;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.WPBParametersCache;
import com.webpagebytes.cms.WPBProjectCache;
import com.webpagebytes.cms.WPBUrisCache;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBMemCacheFactory implements WPBCacheFactory {

	private Object lock = new Object();
	private WPBMemCacheClient memcacheClient;
	private static WPBUrisCache uriCacheInstance;
	private static WPBPagesCache pageCacheInstance;
	private static WPBParametersCache parametersCacheInstance;
	private static WPBPageModulesCache pageModulesCacheInstance;
	private static WPBFilesCache filesCacheInstance;
	private static WPBArticlesCache articlesCacheInstance;
	private static WPBMessagesCache messagesCacheInstance;
	private static WPBProjectCache projectCacheInstance;	
	private static final String CONFIG_MEMCACHE_SERVERS = "memcache_servers";
	private static final String CONFIG_MEMCACHE_CHECK_INTERVAL = "cache_check_interval";
	
	private int sleepTime = 1000; //default sleep time is 10 seconds
	
	public void initialize(Map<String, String> params) throws WPBIOException
	{
		try
		{
			memcacheClient = new WPBMemCacheClient();
			String address = "";
			if (params!= null && params.get(CONFIG_MEMCACHE_SERVERS) != null)
			{
				address = params.get(CONFIG_MEMCACHE_SERVERS);
			}
			memcacheClient.initialize(address);
			
			if (params.get(CONFIG_MEMCACHE_CHECK_INTERVAL) != null)
			{
				try
				{
					sleepTime = Integer.valueOf(params.get(CONFIG_MEMCACHE_CHECK_INTERVAL));
				} catch (NumberFormatException e)
				{
					// do nothing, rely on default value
				}
			}
			WPBMemCacheSyncRunnable syncRunnable = new WPBMemCacheSyncRunnable(this, memcacheClient, sleepTime);
			(new Thread(syncRunnable)).start();
			
		} catch (IOException e)
		{
			throw new WPBIOException("cannot create memcache client", e);
		}
	}
	
	public WPBUrisCache getUrisCacheInstance()
	{
		synchronized (lock) {			
			if (null == uriCacheInstance)
			{
				uriCacheInstance = new WPBMemCacheUrisCache(memcacheClient);
			}
		}
		return uriCacheInstance;
	}
	public WPBPagesCache getPagesCacheInstance()
	{
		synchronized (lock) {		
			if (null == pageCacheInstance)
			{
				pageCacheInstance = new WPBMemCachePagesCache(memcacheClient);
			}
		}
		return pageCacheInstance;
	}
	public WPBParametersCache getParametersCacheInstance()
	{
		synchronized (lock) {
			if (parametersCacheInstance == null)
			{
				parametersCacheInstance = new WPBMemCacheParametersCache(memcacheClient);
			}
		}
		return parametersCacheInstance;
	}
	
	public WPBPageModulesCache getPageModulesCacheInstance()
	{
		synchronized (lock) {
			if (pageModulesCacheInstance == null)
			{
				pageModulesCacheInstance = new WPBMemCachePageModulesCache(memcacheClient);
			}
		}
		return pageModulesCacheInstance;
	}
	public WPBFilesCache getFilesCacheInstance()
	{
		synchronized (lock) {
			if (filesCacheInstance == null)
			{
				filesCacheInstance = new WPBMemCacheFilesCache(memcacheClient);
			}
		}
		return filesCacheInstance;
	}
	public WPBArticlesCache getArticlesCacheInstance()
	{
		synchronized (lock) {
			if (articlesCacheInstance == null)
			{
				articlesCacheInstance = new WPBMemCacheArticlesCache(memcacheClient);
			}
		}
		return articlesCacheInstance;
	}
	public WPBMessagesCache getMessagesCacheInstance()
	{
		synchronized (lock) {
			if (messagesCacheInstance == null)
			{
				messagesCacheInstance = new WPBMemCacheMessagesCache(memcacheClient); 
			}
		}
		return messagesCacheInstance;
	}
	public WPBProjectCache getProjectCacheInstance()
	{
		synchronized (lock) {
			if (projectCacheInstance == null)
			{
				projectCacheInstance = new WPBMemCacheProjectCache(memcacheClient);
			}
		}
		return projectCacheInstance;
	}
}
