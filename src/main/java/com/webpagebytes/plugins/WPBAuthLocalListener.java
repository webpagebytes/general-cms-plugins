package com.webpagebytes.plugins;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class WPBAuthLocalListener implements ServletContextListener {

public static final String AUTH_LOCAL_CONFIG_FILE = "wpbAuthLocalConfigFile";

@Override
public void contextInitialized(ServletContextEvent servletContext) {
	String configPath = servletContext.getServletContext().getInitParameter(AUTH_LOCAL_CONFIG_FILE);
	if (null == configPath)
	{
		throw new RuntimeException("There is no wpbAuthLocalConfigFile parameter defined on WPBAuthLocalListener context initialized "); 
	}
	try
	{
		ConfigReader.readConfig(configPath);
	} catch (IOException e)
	{
		throw new RuntimeException(" Exception reading wpbAuthLocalConfigFile", e);
	}

}

@Override
public void contextDestroyed(ServletContextEvent sce) {
}


}
