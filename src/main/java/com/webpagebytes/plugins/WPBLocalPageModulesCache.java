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
import com.webpagebytes.cms.WPBPageModulesCache;
import com.webpagebytes.cms.cmsdata.WPBPageModule;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalPageModulesCache implements WPBPageModulesCache {
	private String fingerPrint = "";
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBPageModule> localCacheByID;
	private Map<String, WPBPageModule> localCacheByName;
	private static final Object lock = new Object();
	public WPBLocalPageModulesCache()
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
	public WPBPageModule getByExternalKey(String externalKey) throws WPBIOException
	{
		if (localCacheByID != null)
		{
			return localCacheByID.get(externalKey);
		}
		return null;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<String, WPBPageModule> tempMapByID = new HashMap<String, WPBPageModule>();
			Map<String, WPBPageModule> tempMapByName = new HashMap<String, WPBPageModule>();
			
			List<WPBPageModule> recList = dataStorage.getAllRecords(WPBPageModule.class);
			for(WPBPageModule item: recList)
			{
				tempMapByID.put(item.getExternalKey(), item);
				tempMapByName.put(item.getName(), item);
			}
			localCacheByID = tempMapByID;
			localCacheByName = tempMapByName;
			fingerPrint = UUID.randomUUID().toString();
		}
		
	}

		
	public WPBPageModule get(String moduleName) throws WPBIOException
	{
		if (localCacheByName != null)
		{
			return localCacheByName.get(moduleName);
		}
		return null;
	}
	
	public String getFingerPrint() {
		return fingerPrint;
	}
	
}
