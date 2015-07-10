package com.webpagebytes.plugins;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigReader {
private static Map<String, String> configs = new HashMap<String, String>();

private ConfigReader() { }

public static void readConfig(String configPath) throws IOException
{
	configs.clear();
	Properties properties = new Properties();
	properties.load(new FileInputStream(configPath));
	for (final String name: properties.stringPropertyNames())
		configs.put(name, properties.getProperty(name));
}

public static Map<String, String> getConfigs()
{
	Map<String, String> result = new HashMap<String, String>();
	result.putAll(configs);
	return result;
}

}
