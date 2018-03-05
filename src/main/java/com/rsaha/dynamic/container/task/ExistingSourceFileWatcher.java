package com.rsaha.dynamic.container.task;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.ArrayList;


import com.rsaha.util.UtilityClass;

import static com.rsaha.dynamic.config.ConfigFileReader.CONFIG_PROP_MAP;


public class ExistingSourceFileWatcher implements Callable<List<File>> {
	
	TimerTask timerTask;
	private final String SRC = "src";
	Map <String,Long> fileLastModifiedMap = new HashMap<String,Long>();
	List <File> existingfilesToBeReloaded = null;
	
	@Override
	public List<File> call() throws Exception {
		trackExistingFiles();
		return existingfilesToBeReloaded;
	}
	
	private void trackExistingFiles(){
		List<File> existingFiles = UtilityClass.allFiles;
		existingfilesToBeReloaded = new ArrayList<File>();
		for(File file:existingFiles){
			if(fileLastModifiedMap.containsKey(file.getPath())){
				long oldTimeStamp = fileLastModifiedMap.get(file.getPath());
				File newFile = new File(file.getAbsolutePath());
				//System.out.println("New file created : "+newFile.getAbsolutePath());
				long currentTimeStamp = newFile.lastModified();
				//System.out.println("File ::::"+file.getName()+" : Old timestamp ->" +oldTimeStamp+ "\tnew timestamp "+currentTimeStamp);
				if(currentTimeStamp>oldTimeStamp){
					System.out.println("timestamp of "+file.getName()+"  Changed! ::::::::::Old timestamp ->" +oldTimeStamp+ "\tnew timestamp "+currentTimeStamp);
					fileLastModifiedMap.put(file.getPath(), file.lastModified());
					existingfilesToBeReloaded.add(file);
					System.out.println("from Thread: file updated- "+file);
				}
			}else{
				fileLastModifiedMap.put(file.getPath(), file.lastModified());
				//System.out.println("from Thread: "+file);
			}
			
		}
	}
	
	
	public void getUpdatedFilesFromSource(){
		String path = CONFIG_PROP_MAP.get("src");
		try {
			List <File> updatedListFiles = UtilityClass.listFilesRecursively(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
