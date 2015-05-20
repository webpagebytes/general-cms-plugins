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


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.plugins.WPBSQLDataStoreDao.WBSQLQueryOperator;
import com.webpagebytes.plugins.WPBSQLDataStoreDao.WBSQLSortDirection;

public class WPBSQLAdminDataStorage implements WPBAdminDataStorage {
	private static final Logger log = Logger.getLogger(WPBSQLAdminDataStorage.class.getName());
	private static final String KEY_FILED_NAME = "externalKey";
	
	private WPBSQLDataStoreDao sqlDataStorageDao;
	
	public WPBSQLAdminDataStorage()
	{
		
	}

	public void initialize(Map<String, String> params) throws WPBIOException
	{
		sqlDataStorageDao = new WPBSQLDataStoreDao(params);
	}
	private WPBSQLDataStoreDao.WBSQLQueryOperator adminOperatorToSQLOperator(AdminQueryOperator adminOperator)
	{
		switch (adminOperator)
		{
		case LESS_THAN:
			return WBSQLQueryOperator.LESS_THAN;
		case GREATER_THAN:
			return WBSQLQueryOperator.GREATER_THAN;
		case EQUAL:
			return WBSQLQueryOperator.EQUAL;
		case GREATER_THAN_OR_EQUAL:
			return WBSQLQueryOperator.GREATER_THAN_OR_EQUAL;
		case LESS_THAN_OR_EQUAL:
			return WBSQLQueryOperator.LESS_THAN_OR_EQUAL;
		case NOT_EQUAL:
			return WBSQLQueryOperator.NOT_EQUAL;
		default:
			return null;
		}
	}
	
	private WPBSQLDataStoreDao.WBSQLSortDirection adminDirectionToSQLDirection(AdminSortOperator sortOperator)
	{
		switch (sortOperator)
		{
		case ASCENDING:
			return WBSQLSortDirection.ASCENDING;
		case DESCENDING:
			return WBSQLSortDirection.DESCENDING;
		case NO_SORT:
			return WBSQLSortDirection.NO_SORT;
		default:
			return null;
		}
	}
	
	public<T> void delete(String recordid, Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "delete records {0}", recordid);
			sqlDataStorageDao.deleteRecord(dataClass, KEY_FILED_NAME, recordid);			
			T obj = dataClass.newInstance();
			sqlDataStorageDao.setObjectProperty(obj, KEY_FILED_NAME, recordid);
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot delete record " + recordid, e);
		}
	}
	
	public<T> void delete(Long recordid, Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "delete records {0}", recordid);
			sqlDataStorageDao.deleteRecord(dataClass, KEY_FILED_NAME, recordid);
			T obj = dataClass.newInstance();
			sqlDataStorageDao.setObjectProperty(obj, KEY_FILED_NAME, recordid);
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot delete record " + recordid, e);
		}		
	}
	
	public<T> void delete(Class<T> dataClass, String property, AdminQueryOperator operator, Object parameter) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "delete records with property condition {0}", property);		
			Set<String> properties = new HashSet<String>();
			properties.add(property);
			Map<String, WBSQLQueryOperator> operators = new HashMap<String, WBSQLQueryOperator>();
			operators.put(property, adminOperatorToSQLOperator(operator));
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(property, parameter);
			sqlDataStorageDao.deleteRecords(dataClass, properties, operators, values);
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot delete records ", e);
		}
	}
	
	public<T> List<T> getAllRecords(Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "get all records {0}", dataClass.getSimpleName());			
			List<T> result = (List<T>) sqlDataStorageDao.getAllRecords(dataClass);
			return result;
		} catch (Exception e)
		{
			throw new WPBIOException("cannot get all records", e);
		} 
	}
	
	public<T> List<T> getAllRecords(Class<T> dataClass, String property, AdminSortOperator sortOperator) throws WPBIOException
	{
		try
		{
			Object [] logObjects = { dataClass.getSimpleName(), property};
			log.log(Level.INFO, "get all records {0} with condition on property {1}", logObjects);			
			
			Set<String> properties = new HashSet<String>();
			Map<String, WBSQLQueryOperator> operators = new HashMap<String, WBSQLQueryOperator>();
			Map<String, Object> values = new HashMap<String, Object>();
			List<T> result = (List<T>)sqlDataStorageDao.queryWithSort(dataClass, properties, operators, values, property, adminDirectionToSQLDirection(sortOperator));
			return result;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot get all records with sorting", e);
		}

	}

	public<T> T add(T t) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "add record for class {0}", t.getClass().getSimpleName());			
			T res = sqlDataStorageDao.addRecord(t, KEY_FILED_NAME);			
			return res;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}

	public<T> T addWithKey(T t) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "add record with key for class {0}", t.getClass().getSimpleName());			
			T res = sqlDataStorageDao.addRecordWithKey(t, KEY_FILED_NAME);
			return res;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}

	public<T> T get(Long dataid, Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "get record for key {0}", dataid);			
			return (T) sqlDataStorageDao.getRecord(dataClass, KEY_FILED_NAME, dataid);
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}
	
	public<T> T get(String dataid, Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "get record for key {0}", dataid);
			return (T) sqlDataStorageDao.getRecord(dataClass, KEY_FILED_NAME, dataid);
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}
	
	public<T> T update(T t) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "update record for class {0}", t.getClass().getSimpleName());
			sqlDataStorageDao.updateRecord(t, KEY_FILED_NAME);
			return t;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}
	
	public<T> List<T> query(Class<T> dataClass, String property, AdminQueryOperator operator, Object parameter) throws WPBIOException
	{
		try
		{
			Set<String> properties = new HashSet<String>();
			properties.add(property);
			Map<String, WBSQLQueryOperator> operators = new HashMap<String, WBSQLQueryOperator>();
			operators.put(property, adminOperatorToSQLOperator(operator));
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(property, parameter);
			List<T> result = (List<T>)sqlDataStorageDao.query(dataClass, properties, operators, values);
			return result;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot get all records with sorting", e);
		}

	}
	
	public<T> List<T> queryEx(Class<T> dataClass, Set<String> propertyNames, Map<String, AdminQueryOperator> operators, Map<String, Object> values) throws WPBIOException
	{
		try
		{
			Map<String, WBSQLQueryOperator> localOperators = new HashMap<String, WBSQLQueryOperator>();
			for(String property: propertyNames)
			{
				localOperators.put(property, adminOperatorToSQLOperator(operators.get(property)));
			}
			List<T> result = (List<T>)sqlDataStorageDao.query(dataClass, propertyNames, localOperators, values);
			return result;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot get all records with sorting", e);
		}
	}

	public<T> List<T> queryWithSort(Class<T> dataClass, String property, AdminQueryOperator operator, Object parameter, String sortProperty, AdminSortOperator sortOperator) throws WPBIOException
	{
		try
		{
			Set<String> properties = new HashSet<String>();
			properties.add(property);
			Map<String, WBSQLQueryOperator> operators = new HashMap<String, WBSQLQueryOperator>();
			operators.put(property, adminOperatorToSQLOperator(operator));
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(property, parameter);
			List<T> result = (List<T>)sqlDataStorageDao.queryWithSort(dataClass, properties, operators, values, sortProperty, adminDirectionToSQLDirection(sortOperator));
			return result;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot get all records with sorting", e);
		}

	}
		

	
	public String getUploadUrl(String returnUrl)
	{
		return "";
	}
	
	public<T> void deleteAllRecords(Class<T> dataClass) throws WPBIOException
	{
		try
		{
			sqlDataStorageDao.deleteRecords(dataClass);
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot delete all records for class records " + dataClass.getSimpleName(), e);
		}
	}
	
	public String getUniqueId()
	{
		return java.util.UUID.randomUUID().toString();
	}


}
