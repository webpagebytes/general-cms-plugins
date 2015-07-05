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

import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBProjectCache;
import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.Pair;

public class WPBMemCacheProjectCache implements WPBProjectCache {
	public static final String CACHE_KEY = "wpbprojectcache";
	private WPBProject project;
	private Pair<String, String> defaultLocale;
	private Set<String> supportedLanguages;
	private WPBAdminDataStorage dataStorage;
	private static final Object lock = new Object();
	private String fingerPrint = "";
	private WPBMemCacheClient memcacheClient;
	
	public WPBMemCacheProjectCache(WPBMemCacheClient memcacheClient)
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
	public String getDefaultLanguage() throws WPBIOException
	{
		return project.getDefaultLanguage();
	}
	
	public Pair<String, String> getDefaultLocale() throws WPBIOException
	{
		return defaultLocale;
	}
	public Set<String> getSupportedLocales() throws WPBIOException
	{
		return supportedLanguages;
	}
	public WPBProject getProject() throws WPBIOException
	{
		return project;
	}
	
	private WPBProject createDefaultProject() throws WPBIOException
	{
		WPBProject project = new WPBProject();
		project.setExternalKey(WPBProject.PROJECT_KEY);
		project.setDefaultLanguage("en");
		project.setSupportedLanguages("en");
		project.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());	
		project.setVersion(UUID.randomUUID().toString());
		dataStorage.addWithKey(project);		
		return project;
	}
	
	public void Refresh() throws WPBIOException {
		synchronized (lock) {
			project = dataStorage.get(WPBProject.PROJECT_KEY, WPBProject.class);
			if (null == project)
			{
				project = createDefaultProject();
			}
			String defaultLanguage = project.getDefaultLanguage();
			String[] langs_ = defaultLanguage.split("_");
			if (langs_.length == 1)
			{
				defaultLocale = new Pair<String, String>(langs_[0], "");
			} else
			if (langs_.length == 2)
			{
				defaultLocale = new Pair<String, String>(langs_[0], langs_[1]);
			} else
				throw new WPBIOException("Invalid default language");
			
			supportedLanguages = project.getSupportedLanguagesSet();
			fingerPrint = project.getVersion();
			memcacheClient.putFingerPrint(CACHE_KEY, fingerPrint);
		}
	}
	
	public String getFingerPrint() {
		return fingerPrint;
	}


}
