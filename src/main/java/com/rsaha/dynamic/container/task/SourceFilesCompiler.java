package com.rsaha.dynamic.container.task;

import static com.rsaha.dynamic.config.ConfigFileReader.CONFIG_PROP_MAP;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import com.rsaha.dynamic.classGenerator.AppLauncher;
import com.rsaha.dynamic.classGenerator.GenericCodeReaderFromFile;

public class SourceFilesCompiler implements Callable<Boolean>{
	
	private static final String EXECUTION_FILE_NAME = "execution-file";
	private List<File> filesToBeCompiled;
	private static final String EXECUTION_STRATEGY = "execution-strategy";
	private static final String EXECUTION_STRATEGY_SINGLE = "single";
	private static final String EXECUTION_STRATEGY_ALL = "all";
	private static final String EXECUTION_SEQUENCE = "execution-sequence";
	private static final String EXECUTION_SEQUENCE_SERIAL = "serial";
	private static final String EXECUTION_SEQUENCE_PARALLEL = "parallel";
	private Map<String, Class> fileNameAndClassMap = new HashMap<>();
	private String srcPath = null;
	private boolean compilationCompleted;
	
	public SourceFilesCompiler(){
		
	}
	
	public SourceFilesCompiler(List<File>filesTobeCompiled){
		this.filesToBeCompiled = filesTobeCompiled;
		srcPath = CONFIG_PROP_MAP.get("src");
	}
	
	//@Override
	public void run(){
		//String srcPath = CONFIG_PROP_MAP.get("src");
		Map<String,GenericCodeReaderFromFile> javaCodeReaderFiles = AppLauncher.createMapOfFileAndReaderObjects(filesToBeCompiled, srcPath.length());
		//Map<String, Class> fileNameAndClassMap = new HashMap<>();//Class[javaCodeReaderFiles.size()];
		System.out.println("size of javaCodeReaderFiles and keys");
		System.out.println("size "+ javaCodeReaderFiles.size());
		
		compile(javaCodeReaderFiles);
		/*try {
			for(Map.Entry<String,GenericCodeReaderFromFile> genericCodeReaderFromFile:javaCodeReaderFiles.entrySet()){
				Class javaClass = genericCodeReaderFromFile.getValue().dynamicClassCreation(genericCodeReaderFromFile.getKey());
				fileNameAndClassMap.put(genericCodeReaderFromFile.getKey(), javaClass) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	*/
		execute(srcPath, javaCodeReaderFiles, fileNameAndClassMap);
	}
	
	
	@Override
	public Boolean call() throws Exception {
		//String srcPath = CONFIG_PROP_MAP.get("src");
		Map<String,GenericCodeReaderFromFile> javaCodeReaderFiles = AppLauncher.createMapOfFileAndReaderObjects(filesToBeCompiled, srcPath.length());
		//Map<String, Class> fileNameAndClassMap = new HashMap<>();//Class[javaCodeReaderFiles.size()];
		System.out.println("size of javaCodeReaderFiles and keys");
		System.out.println("size "+ javaCodeReaderFiles.size());
		
		compile(javaCodeReaderFiles);
		/*try {
			for(Map.Entry<String,GenericCodeReaderFromFile> genericCodeReaderFromFile:javaCodeReaderFiles.entrySet()){
				Class javaClass = genericCodeReaderFromFile.getValue().dynamicClassCreation(genericCodeReaderFromFile.getKey());
				fileNameAndClassMap.put(genericCodeReaderFromFile.getKey(), javaClass) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	*/
		return true;
		//execute(srcPath, javaCodeReaderFiles, fileNameAndClassMap);
	}
	
	private void compile(Map<String,GenericCodeReaderFromFile> javaCodeReaderFiles){
		System.out.println("Inside the compile method");
		try {
			for(Map.Entry<String,GenericCodeReaderFromFile> genericCodeReaderFromFile:javaCodeReaderFiles.entrySet()){
				System.out.println("will Recompile class :"+genericCodeReaderFromFile.getKey());
				Class javaClass = genericCodeReaderFromFile.getValue().dynamicClassCreation(genericCodeReaderFromFile.getKey());
				System.out.println("Recompiled class created :"+javaClass.getName());
				fileNameAndClassMap.put(genericCodeReaderFromFile.getKey(), javaClass) ;
				System.out.println("ReCompiled the class : "+genericCodeReaderFromFile.getKey());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	private void execute(String srcPath, Map<String, GenericCodeReaderFromFile> javaCodeReaderFiles,
			Map<String, Class> fileNameAndClassMap) {
		if(javaCodeReaderFiles.size()>0){
			Iterator it = javaCodeReaderFiles.keySet().iterator();
				System.out.println("Going to execute the file "+ it.next());
			if(executionStrategySingle()){
				executeSingleFile(javaCodeReaderFiles, fileNameAndClassMap);
			}else if(executionStrategyAll()){
				if(CONFIG_PROP_MAP.containsKey(EXECUTION_SEQUENCE)){
					String executionSequence = CONFIG_PROP_MAP.get(EXECUTION_SEQUENCE);
					if(executionSequence.equalsIgnoreCase(EXECUTION_SEQUENCE_SERIAL)){
						executeAllSequentially(javaCodeReaderFiles, fileNameAndClassMap);
					}else if(executionSequence.equalsIgnoreCase(EXECUTION_SEQUENCE_PARALLEL)){
						executeAllParallely(javaCodeReaderFiles, fileNameAndClassMap);
					}
				}
			}
		}
	}

	private void executeAllSequentially(Map<String, GenericCodeReaderFromFile> javaCodeReaderFiles,
			Map<String, Class> fileNameAndClassMap) {
		for(Map.Entry<String, GenericCodeReaderFromFile> javaCodeReaderFile: javaCodeReaderFiles.entrySet()){
			try {
				javaCodeReaderFile.getValue().executeClass(fileNameAndClassMap.get(javaCodeReaderFile.getKey()));
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void executeAllParallely(Map<String, GenericCodeReaderFromFile> javaCodeReaderFiles,
			Map<String, Class> fileNameAndClassMap) {
		for(Map.Entry<String, GenericCodeReaderFromFile> javaCodeReaderFile: javaCodeReaderFiles.entrySet()){
				new Thread(new Runnable(){
					public void run(){
						try {
							javaCodeReaderFile.getValue().executeClass(fileNameAndClassMap.get(javaCodeReaderFile.getKey()));
						} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
								| InvocationTargetException e) {
							e.printStackTrace();
						}
				}});
		}
	}

	private void executeSingleFile(Map<String, GenericCodeReaderFromFile> javaCodeReaderFiles,
			Map<String, Class> fileNameAndClassMap) {
		String executionFile = "";
		if(CONFIG_PROP_MAP.containsKey(EXECUTION_FILE_NAME))
			 executionFile = CONFIG_PROP_MAP.get(EXECUTION_FILE_NAME);
			System.out.println("File to be executed :"+ executionFile);
			System.out.println("Files in the existing map");
			for(Map.Entry<String, GenericCodeReaderFromFile> entry : javaCodeReaderFiles.entrySet()){
				System.out.println(entry.getKey());
			}
		try {
			javaCodeReaderFiles.get(executionFile).executeClass(fileNameAndClassMap.get(executionFile));
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private boolean executionStrategySingle(){
		if(CONFIG_PROP_MAP.containsKey(EXECUTION_STRATEGY) && CONFIG_PROP_MAP.get(EXECUTION_STRATEGY).equalsIgnoreCase(EXECUTION_STRATEGY_SINGLE))
			return true;
		return false;	
	}
	
	
	private boolean executionStrategyAll(){
		if(CONFIG_PROP_MAP.containsKey(EXECUTION_STRATEGY) && CONFIG_PROP_MAP.get(EXECUTION_STRATEGY).equalsIgnoreCase(EXECUTION_STRATEGY_ALL))
			return true;
		return false;	
	}

}
