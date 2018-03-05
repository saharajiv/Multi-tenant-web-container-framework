package com.rsaha.dynamic.container;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Callable;

public class ClassFileTimeStampReader<String> implements Callable<String>{

	
	
	private void readClassFileTimeStamps(){
		 Path path = Paths.get("C:\\Users\\jorgesys\\workspaceJava\\myfile.txt");
		    BasicFileAttributes attr;
		    try {
		    attr = Files.readAttributes(path, BasicFileAttributes.class);
		    System.out.println("Creation date: " + attr.creationTime());
		    //System.out.println("Last access date: " + attr.lastAccessTime());
		    //System.out.println("Last modified date: " + attr.lastModifiedTime());
		    } catch (IOException e) {
		    	System.out.println("oops error! " + e.getMessage());
		}
	}

	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
