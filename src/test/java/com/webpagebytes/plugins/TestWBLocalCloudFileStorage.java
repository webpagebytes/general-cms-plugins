package com.webpagebytes.plugins;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.cms.WPBFileInfo;
import com.webpagebytes.cms.WPBFilePath;
import com.webpagebytes.plugins.WPBLocalFileStorage;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WPBLocalFileStorage.class})
public class TestWBLocalCloudFileStorage {

WPBLocalFileStorage storage;
String dataDirectory;
Map<String, String> params;
@Before
public void setup() throws IOException
{
	dataDirectory = "some path";
	params = new HashMap<String, String>();
	params.put("dataDirectory", dataDirectory);
	params.put("basePublicUrlPath", "");
}


@Test
public void test_storeFile()
{
	try
	{
		Properties expectedProps = new Properties();
		
		PowerMock.suppress(PowerMock.method(WPBLocalFileStorage.class, "initialize", Map.class));
		storage = PowerMockito.spy(new WPBLocalFileStorage());
		storage.initialize(params);

		WPBFilePath file = new WPBFilePath("public", "test.txt");
		String content = "this is string A";
		//this should produce the following metadata
		//{size=16, creationTime=1415284853146, crc32=164053373, md5=0mYb3qigZJiST7S2aM0evg==, path=test.txt, contentType=application/octet-stream}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PowerMockito.doReturn(bos).when(storage, "createStorageOutputStream", Matchers.any(String.class));
		
		ArgumentCaptor<Properties> capture = ArgumentCaptor.forClass(Properties.class);
		PowerMockito.doNothing().when(storage, "storeFileProperties", capture.capture(), Matchers.any(String.class));
		storage.storeFile(bais, file);
		assertTrue(Arrays.equals(bos.toByteArray(), content.getBytes()));
		assertTrue (capture.getValue().get("size").equals("16"));
		assertTrue (capture.getValue().get("crc32").equals("164053373"));
		assertTrue (capture.getValue().get("md5").equals("0mYb3qigZJiST7S2aM0evg=="));
		assertTrue (capture.getValue().get("contentType").equals("application/octet-stream"));
		assertTrue (capture.getValue().get("creationTime").toString().length()>6);
		assertTrue (capture.getValue().get("path").equals("test.txt"));	
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	
}

@Test
public void test_getFileInfo()
{
	try
	{
		
		PowerMock.suppress(PowerMock.method(WPBLocalFileStorage.class, "initialize", Map.class));
		storage = PowerMockito.spy(new WPBLocalFileStorage());
		storage.initialize(params);
		
		String crc32 = "123456";
		String creationTime = "567890";
		String filePath = "test.txt";
		String contentType = "text/plain";
		String size = "23";
		String md5 = "abc123==";
		String customX = "customX";
		WPBFilePath file = new WPBFilePath("public", filePath);
		Properties properties = new Properties();
		properties.put("crc32", crc32);
		properties.put("creationTime", creationTime);
		properties.put("filePath", filePath);
		properties.put("size", size);
		properties.put("md5", md5);
		properties.put("contentType", contentType);
		properties.put("customX", customX);
		
		PowerMockito.doReturn(true).when(storage, "checkIfFileExists", Matchers.any(String.class));		
		PowerMockito.doReturn(properties).when(storage, "getFileProperties", Matchers.any(String.class));
		
		WPBFileInfo fileInfo = storage.getFileInfo(file);
		
		assertTrue(fileInfo.getContentType().equals(contentType));
		assertTrue(fileInfo.getCrc32() == Long.valueOf(crc32));
		assertTrue(fileInfo.getMd5().equals(md5));
		assertTrue(fileInfo.getCreationDate() == Long.valueOf(creationTime));
		assertTrue(fileInfo.getSize() == Long.valueOf(size));
		
		Map expectedCustomProps = new HashMap();
		expectedCustomProps.put("customX", customX);
		assertTrue(fileInfo.getCustomProperties().equals(expectedCustomProps));
	
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_UpdateFileCustomProperties()
{
	try
	{
		PowerMock.suppress(PowerMock.method(WPBLocalFileStorage.class, "initialize", Map.class));
		storage = PowerMockito.spy(new WPBLocalFileStorage());
		storage.initialize(params);
		
		WPBFilePath file = new WPBFilePath("public", "test.txt");		
		PowerMockito.doReturn(true).when(storage, "checkIfFileExists", Matchers.any(String.class));		
		
		Properties props = new Properties();
		String customX = "customX";
		String customY = "customY"; 
		// test with two custom props, make sure the key is not equal with value
		props.put(customX, customY);
		props.put(customY, customX);
		
		PowerMockito.doReturn(props).when(storage, "getFileProperties", Matchers.any(String.class));
		
		ArgumentCaptor<Properties> capture = ArgumentCaptor.forClass(Properties.class);
		PowerMockito.doNothing().when(storage, "storeFileProperties", capture.capture(), Matchers.any(String.class));
	
		Map<String, String> customProps = new HashMap<String, String>();
		customProps.put(customX, customY);
		customProps.put(customY, customX);
		storage.updateFileCustomProperties(file, customProps);
		
		assertTrue (capture.getValue().getProperty(customY).equals(customX));
		assertTrue (capture.getValue().getProperty(customX).equals(customY));
		
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_updateContentType()
{
	try
	{
		PowerMock.suppress(PowerMock.method(WPBLocalFileStorage.class, "initialize", Map.class));
		storage = PowerMockito.spy(new WPBLocalFileStorage());
		storage.initialize(params);
		
		WPBFilePath file = new WPBFilePath("public", "test.txt");		
		PowerMockito.doReturn(true).when(storage, "checkIfFileExists", Matchers.any(String.class));		
		
		Properties props = new Properties();
		
		PowerMockito.doReturn(props).when(storage, "getFileProperties", Matchers.any(String.class));
		
		ArgumentCaptor<Properties> capture = ArgumentCaptor.forClass(Properties.class);
		PowerMockito.doNothing().when(storage, "storeFileProperties", capture.capture(), Matchers.any(String.class));
	
		String contentType = "text/html";
		storage.updateContentType(file, contentType);
		
		assertTrue (capture.getValue().get("contentType").equals(contentType));
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getFileContent()
{
	try
	{
		PowerMock.suppress(PowerMock.method(WPBLocalFileStorage.class, "initialize", Map.class));
		storage = PowerMockito.spy(new WPBLocalFileStorage());
		storage.initialize(params);
		
		WPBFilePath file = new WPBFilePath("public", "test.txt");		
		PowerMockito.doReturn(true).when(storage, "checkIfFileExists", Matchers.any(String.class));		
		
		InputStream is = new ByteArrayInputStream("".getBytes());
		PowerMockito.doReturn(is).when(storage, "createStorageInputStream", Matchers.any(String.class));
		
		InputStream result = storage.getFileContent(file);
		
		assertTrue(result == is);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getFileContent_file_not_exists()
{
	try
	{
		PowerMock.suppress(PowerMock.method(WPBLocalFileStorage.class, "initialize", Map.class));
		storage = PowerMockito.spy(new WPBLocalFileStorage());
		storage.initialize(params);
		
		WPBFilePath file = new WPBFilePath("public", "test.txt");		
		PowerMockito.doReturn(false).when(storage, "checkIfFileExists", Matchers.any(String.class));		
		
		storage.getFileContent(file);
		
		assertTrue(false);
		
	} 
	catch (IOException e)
	{
		// all is good
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
	
}


}
