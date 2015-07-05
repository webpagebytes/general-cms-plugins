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

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.WPBArticlesCache;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBMemCacheArticlesCache implements WPBArticlesCache {
	
	public static final String CACHE_KEY = "wpbarticlescache";
	private WPBMemCacheClient memcacheClient;
	private String fingerPrint;
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBArticle> localCache;
	private static final Object lock = new Object();
	public WPBMemCacheArticlesCache(WPBMemCacheClient memcacheClient)
	{
		this.memcacheClient = memcacheClient;
		dataStorage = WPBAdminDataStorageFactory.getInstance();
		try
		{
			if (dataStorage != null)
			{
				Refresh();
			}
		} catch (WPBIOException e)
		{
			
		}
	}
	public WPBArticle getByExternalKey(String externalKey) throws WPBIOException
	{
		if (localCache == null)
		{
			Refresh();
		}
		if (localCache != null)
		{
			return localCache.get(externalKey);
		}
		return null;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<String, WPBArticle> tempMap = new HashMap<String, WPBArticle>();
			List<WPBArticle> recList = dataStorage.getAllRecords(WPBArticle.class, "externalKey", AdminSortOperator.ASCENDING);
			
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e)
			{
				throw new WPBIOException("cannot calculate fingerprint", e);
			}
			
			for(WPBArticle item: recList)
			{
				md.update(item.getVersion().getBytes());
				tempMap.put(item.getExternalKey(), item);
			}
			localCache = tempMap;
			fingerPrint = CmsBase64Utility.toBase64(md.digest());			
			memcacheClient.putFingerPrint(CACHE_KEY, fingerPrint);
		}
		
	}
	
	public String getFingerPrint() {
		return fingerPrint;
	}

}
