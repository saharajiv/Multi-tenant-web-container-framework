package com.rsaha.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UtilityClass {
	
	public static List<File> allFiles = new ArrayList<File>();
	
	public static boolean checkForKeywordSplitWithNewLine(String sCurrentLine, String keyword,boolean scrapCommentTags) {
		//StringBuilder sb = new StringBuilder(sCurrentLine);
		for(String line : sCurrentLine.split("\n")){
			String modifiedLine = line;
			if(scrapCommentTags)
				 modifiedLine = scrapCommentTags(line);
			if(checkForKeyword(modifiedLine,keyword))				
				return true;
		}
		return false;
	}
	
	
	private static String scrapCommentTags(String line) {
		if(line.length()>2){
			if(line.substring(0, 2).equals("//") || line.substring(0, 2).equals("/*") || line.substring(0, 2).equals("*/"))
				return line.substring(2);
		}
		return line;	
	}


	public static boolean checkForKeyword(String sCurrentLine,String keyword) {
		//String [] keywords = sCurrentLine.split(" ");
		StringBuilder sb = new StringBuilder(sCurrentLine);
		for(String word : sCurrentLine.split(" ")){
			if(word.equals(keyword))
				return true;
		}
		return false;
	}
	
	
	public static List<File> listFilesRecursively(String path) throws IOException{
		
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

}
