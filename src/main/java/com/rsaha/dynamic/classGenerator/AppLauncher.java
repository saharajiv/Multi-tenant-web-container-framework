package com.rsaha.dynamic.classGenerator;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.rsaha.dynamic.config.ConfigFileReader;
import com.rsaha.dynamic.container.ObjectContainer;
import com.rsaha.util.UtilityClass;
import static com.rsaha.util.UtilityClass.listFilesRecursively;

import static com.rsaha.dynamic.config.ConfigFileReader.CONFIG_PROP_MAP;


/**
 * by Rajib Saha
 */
public class AppLauncher{
	private static final String ENTRY_CLASS = "entry-class";
	private static final String SRC = "src";
	private static String filename = "config.properties";
	private static String srcPath;
	private static String fullPathOfEntryclass;
	private final static String CONTAINER_ENABLED = "container-enabled";

	public static final void main(String... args) throws Exception {
		ConfigFileReader.loadConfigFile();
       try{
			Map<String,GenericCodeReaderFromFile> javaCodeReaderFiles = createjavaCodeReaderFiles();
			Map<String, Class> fileNameAndClassMap = new HashMap<>();
			for(Map.Entry<String,GenericCodeReaderFromFile> genericCodeReaderFromFile:javaCodeReaderFiles.entrySet()){
				fileNameAndClassMap.put(genericCodeReaderFromFile.getKey(), genericCodeReaderFromFile.getValue().dynamicClassCreation(genericCodeReaderFromFile.getKey()));
			}
			if(javaCodeReaderFiles.size()>0)
				executeJavaFile(javaCodeReaderFiles, fileNameAndClassMap);
			else{
				System.out.println("No java files present in the "+srcPath+" location to execute");
			}
			startContainerIfEnabled();
       }catch(IllegalAccessException iae){
    	   throw new RuntimeException("The class in the file is not accessible. Please define the Entry level class and method as public");
       }
	}



	private static void executeJavaFile(Map<String, GenericCodeReaderFromFile> javaCodeReaderFiles, Map<String, Class> fileNameAndClassMap)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		if(CONFIG_PROP_MAP.containsKey(ENTRY_CLASS)){
			javaCodeReaderFiles.get(CONFIG_PROP_MAP.get(ENTRY_CLASS)).executeClass(fileNameAndClassMap.get(CONFIG_PROP_MAP.get(ENTRY_CLASS)));
		}else{
			//executes the first class if the entry class is not mentioned, instantiates the first class
			javaCodeReaderFiles.entrySet().iterator().next().getValue().executeClass(fileNameAndClassMap.entrySet().iterator().next().getValue());
		}
		
	}


	private static Map<String,GenericCodeReaderFromFile> createjavaCodeReaderFiles() throws IOException{
		//ConfigFileReader.loadConfigFile();
		srcPath = CONFIG_PROP_MAP.get("src");
    	List<File> listOfFiles = listFilesRecursively(srcPath);
    	int lengthOfSrcPath=srcPath.length(); 
    	Map<String, GenericCodeReaderFromFile> codeReaderFiles = createMapOfFileAndReaderObjects(listOfFiles,
				lengthOfSrcPath);
    	return codeReaderFiles;
	}



	public static Map<String, GenericCodeReaderFromFile> createMapOfFileAndReaderObjects(List<File> listOfFiles,
			int lengthOfSrcPath) {
		String fullyQualifiedClassName = null;
    	Map<String,GenericCodeReaderFromFile> codeReaderFiles = new HashMap<String,GenericCodeReaderFromFile>();
    	for(File file : listOfFiles){
    		GenericCodeReaderFromFile genericReaderFile = new GenericCodeReaderFromFile(file);
	    	fullyQualifiedClassName = file.getPath().substring(lengthOfSrcPath+1);
	    	codeReaderFiles.put(fullyQualifiedClassName,genericReaderFile) ;
    	}
		return codeReaderFiles;
	}
	
	/*
	private static List<File> listFilesRecursively(String path) throws IOException{
		List<File> allFiles = new ArrayList<File>();
        File folder = new File(path);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isFile()){
                //System.out.println(path+"/"+file.getName());
                allFiles.add(file);
            }
            else if (file.isDirectory()){
            	allFiles.addAll(listFilesRecursively(file.getPath()));
            }
        }
        return allFiles;
    }
	*/
	private static void startContainerIfEnabled() {
		if(CONFIG_PROP_MAP.containsKey(CONTAINER_ENABLED) && CONFIG_PROP_MAP.get(CONTAINER_ENABLED).equalsIgnoreCase("true")){
			enableContainerService();
		}
	}

	private static void enableContainerService() {
		ObjectContainer objectContainer = new ObjectContainer();
		objectContainer.createAppContainer();
	}
	

	
	

}