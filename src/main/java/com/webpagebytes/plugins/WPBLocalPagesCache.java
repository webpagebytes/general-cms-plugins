/*
 *   Copyright 2014 Webpagebytes
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalPagesCache implements WPBPagesCache {
	private String fingerPrint = ""; 
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBPage> localCacheByExternalId;
	private Map<String, WPBPage> localCacheByName;	
	private static final Object lock = new Object();
	public WPBLocalPagesCache()
	{
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
	public WPBPage getByExternalKey(String externalKey) throws WPBIOException
	{
		if (localCacheByExternalId != null)
		{
			return localCacheByExternalId.get(externalKey);
		}
		return null;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<String, WPBPage> tempMapByID = new HashMap<String, WPBPage>();
			Map<String, WPBPage> tempMapByName = new HashMap<String, WPBPage>();
			List<WPBPage> recList = dataStorage.getAllRecords(WPBPage.class);
			for(WPBPage item: recList)
			{
				tempMapByID.put(item.getExternalKey(), item);
				tempMapByName.put(item.getName(), item);
			}
			localCacheByExternalId = tempMapByID;
			localCacheByName = tempMapByName;
			fingerPrint = UUID.randomUUID().toString();
		}
		
	}

	public WPBPage get(String pageName) throws WPBIOException
	{
		if (localCacheByName != null)
		{
			return localCacheByName.get(pageName);
		}
		return null;
	}
	
	public String getFingerPrint() {
		return fingerPrint;
	}

}
