package com.chunruo.core.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.chunruo.core.util.Configuration;
import com.chunruo.core.util.StringUtil;

/**
 * 读取配制文件
 *
 */
@Order(1)
@Component
public class Configuration {
	protected final transient Log log = LogFactory.getLog(getClass());
	private static volatile Configuration instance;	
	private Properties props;
	
	private Configuration(Environment env){
        try {
        	String loadFilePath = "/config.properties";
        	String[] profiles = env.getActiveProfiles();
        	if(profiles != null && profiles.length > 0){
        		loadFilePath = String.format("/config-%s.properties", StringUtil.null2Str(profiles[0]));
        	}
        	
        	log.debug("Configuration load file " + loadFilePath);
        	InputStream is = getClass().getResourceAsStream(loadFilePath);
        	props = new Properties();
			props.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Configuration getInstance(Environment env) {
		if(instance == null) {
			synchronized (Configuration.class) {
				if (instance == null) {
					 instance = new Configuration(env);
				}
			}
		}
		return instance;
	}
	
	public String getProperty(String key) {
		try{
		    return (String) props.get(key);
		}catch(Exception e){
			e.printStackTrace();
		}
		return key;
	}
}
