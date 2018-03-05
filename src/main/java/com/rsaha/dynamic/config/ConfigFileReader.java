package com.rsaha.dynamic.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.rsaha.dynamic.classGenerator.GenericCodeReaderFromFile;

public class ConfigFileReader {
	private static String FILENAME = "config.properties";
	private static String srcPath;
	public static String fullPathOfEntryclass;
	final private static String[] config_file_Keys= {"src","entry-class","container-enabled","execution-strategy","execution-file"};
	public final static Map<String,String> CONFIG_PROP_MAP = new HashMap<String,String>();
	
	
	
	public static void loadConfigFile() {
		Properties prop = new Properties();
    	InputStream input = null;
    	try {
    		input = ConfigFileReader.class.getClassLoader().getResourceAsStream(FILENAME);
    		if(input==null){
    	            System.out.println("Sorry, unable to load the config file " + FILENAME);
    		    return;
    		}
    		prop.load(input);
    		loadPropertiesFromPropertyMap(prop);
    		//fullPathOfEntryclass = srcPath+"/"+entryClassOfApp;
    		fullPathOfEntryclass = CONFIG_PROP_MAP.get("src")+"/"+CONFIG_PROP_MAP.get("entry-class");
    	    //System.out.println(srcPath);
	        //System.out.println(fullPathOfEntryclass);
    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
        			input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
	}




	private static void loadPropertiesFromPropertyMap(Properties prop) {
		srcPath = prop.getProperty(config_file_Keys[0]);
		//prop.getProperty(config_file_Keys[1]);
		for(String key:config_file_Keys){
			CONFIG_PROP_MAP.put(key, prop.getProperty(key));
		}
	}
}
