package com.rsaha.dynamic.classGenerator;

import static java.util.Collections.singletonList;
import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.rsaha.dynamic.classGenerator.MultipleCustomClassLoader.CompiledClassLoader;
import com.rsaha.parser.ClassParser;

import sun.misc.Unsafe;

import static com.rsaha.util.UtilityClass.checkForKeyword;
//import static com.rsaha.util.UtilityClass.checkForKeywordSplitWithNewLine;

/**
 * by Rajib Saha
 */
public class GenericCodeReaderFromFile extends Decorator{
	private static final String JAVA_FILE_EXTENSION = ".java";
	private static final String ENTRY_CLASS = "entry-class";
	private static final String SRC = "src";
	private static final String CLASS_KEYWORD = "class";
	private ClassDecorator classDecorator = new ClassDecorator();
	private ClassParser parser = new ClassParser();
	String defaultPath = "";
	String defaultClassName = "DefaultClass";
	String fullClassName = defaultPath.replace('.', '/') + "/" + defaultClassName;
	String className = null;
	String path = null;
	boolean packageDefined = false;
	boolean classDefined = false;
	boolean methodDefined = false;
	private static String filename = "config.properties";
	private static String srcPath;
	final StringBuilder source = new StringBuilder();
	/*private String entryClassOfApp;*/
	private static String fullPathOfEntryclass;
	private static Set<String> fullClassNames;
	
	public GenericCodeReaderFromFile(){
	}
	
	public GenericCodeReaderFromFile(File file){
		try {
			BufferedReader br = new BufferedReader(new  FileReader(file));
			readSourceFile(br);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public Class dynamicClassCreation(String fileName) throws Exception {
    	/*String sCurrentLine;
    	loadConfigFile();
    	File [] listOfFiles = listFilesRecursively(srcPath);
    	final StringBuilder source = new StringBuilder();
    	BufferedReader br = new BufferedReader(new  FileReader(fullPathOfEntryclass));
    	readSourceFile(source, br);
    	br.close();
    	*/
    	int posOfLastSlash = fileName.indexOf("/");
    	if(posOfLastSlash>0){
    		defaultPath = fileName.substring(0,posOfLastSlash);
    	}
    	decorateWithDefaultPackage(classDefined,packageDefined, defaultPath, source);
    	if(!classDefined){
    		fileName = fileName.trim();
    		if(fileName.contains(JAVA_FILE_EXTENSION)){
    			fileName = fileName.substring(0,fileName.length()-JAVA_FILE_EXTENSION.length());
    		}
    		fullClassName = fileName;
    		defaultClassName = fileName;
    		if(fileName.contains("/")){
    			defaultClassName = fileName.substring(fileName.indexOf('/')+1);
    		}
    		int lastPositionOfSemicolon= classDecorator.decorate(defaultPath, defaultClassName, source);
    		parser.setLastPositionOfSemicolon(lastPositionOfSemicolon);
    		methodDefined = parser.parse(source);
    	}else{
    		setFullClassName(packageDefined, defaultPath, className, path);
    		decorateWithDefaultImport(source);
    		methodDefined = parser.parse(source);
    	}
    	/*if(!methodDefined){
    		source.insert(27," public void defaultMethod() {\n");
    	}*/
    	
    	String modifiedSource = parser.getModifiedSource();
    	System.out.println(modifiedSource);
    	// A byte array output stream containing the bytes that would be written to the .class file
        //final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    	//final ByteArrayOutputStream byteArrayOutputStream = compileJavaCode(modifiedSource);
    	Class aClass = compileJavaCode(modifiedSource);
    	return aClass;
    	/*
        final byte[] bytes = byteArrayOutputStream.toByteArray();
        // use the unsafe class to load in the class bytes
        final Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        final Unsafe unsafe = (Unsafe) f.get(null);
        System.out.println("Now the class will be created :"+fullClassName);
        System.out.println("Now the class bytes will be created :"+bytes);
        Class aClass = null;
        try{
        	  aClass = unsafe.defineClass(fullClassName, bytes, 0, bytes.length,this.getClass().getClassLoader(),this.getClass().getProtectionDomain());
        }catch(Throwable t){
        	System.out.println(t.getMessage());
        	if(t instanceof LinkageError){
        		 	loadClass();
        	}
        	
        }
        System.out.println("class created :"+fileName);
        return aClass;
        */
        //executeClass(aClass);
    }

	private ByteArrayOutputStream compileJavaFile(String modifiedSource) {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + JAVA_FILE_EXTENSION), SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return modifiedSource;
            }

            @Override
            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };

        final JavaFileManager javaFileManager = new ForwardingJavaFileManager(
                ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null)) {

            @Override
            public JavaFileObject getJavaFileForOutput(Location location,
                                                       String className,
                                                       JavaFileObject.Kind kind,
                                                       FileObject sibling) throws IOException {
                return simpleJavaFileObject;
            }
        };
        
       ToolProvider.getSystemJavaCompiler().getTask(
                null, javaFileManager, null, null, null, singletonList(simpleJavaFileObject)).call();
        
        
       
        return byteArrayOutputStream;
	}
	
	
	
	private Class<?> compileJavaCode(String modifiedSource) {
		/*final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + JAVA_FILE_EXTENSION), SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return modifiedSource;
            }

            @Override
            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };

        final JavaFileManager javaFileManager = new ForwardingJavaFileManager(
                ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null)) {

            @Override
            public JavaFileObject getJavaFileForOutput(Location location,
                                                       String className,
                                                       JavaFileObject.Kind kind,
                                                       FileObject sibling) throws IOException {
                return simpleJavaFileObject;
            }
        };

        ToolProvider.getSystemJavaCompiler().getTask(
                null, javaFileManager, null, null, null, singletonList(simpleJavaFileObject)).call();
        return byteArrayOutputStream;
        */
		Class<?> clazz = null;
		CustomClassCompiler customClassCompiler = CustomClassCompiler.getInstance();
		try {
			clazz = customClassCompiler.compile(fullClassName, modifiedSource);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clazz;
	}
	
	

	private void loadClass() throws ClassNotFoundException, IOException {
		Class<?> myClass=this.getClass();
		System.out.printf("my class is Class@%x%n", myClass.hashCode());
		System.out.println("reloading");
		URL[] urls={ myClass.getProtectionDomain().getCodeSource().getLocation() };
		ClassLoader delegateParent = myClass.getClassLoader().getParent();
		try(URLClassLoader cl=new URLClassLoader(urls, delegateParent)) {
		  Class<?> reloaded=cl.loadClass(myClass.getName());
		  System.out.printf("reloaded my class: Class@%x%n", reloaded.hashCode());
		  System.out.println("Different classes: "+(myClass!=reloaded));
		}
	}



	public void executeClass(final Class aClass)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class noparams[] = {};
		final Object o = aClass.newInstance();
        Method method = null;
        String [] methodNames = parser.getMethodNames();
        if(!methodDefined){
        	 method = aClass.getDeclaredMethod("defaultMethod", noparams);
        }else if(methodDefined){
        	method = aClass.getDeclaredMethod(methodNames[1], noparams);
        }
        Object returned = method.invoke(o, null);
	}





	private void readSourceFile(BufferedReader br) throws IOException {
		String sCurrentLine;
		while((sCurrentLine = br.readLine())!=null){
			sCurrentLine = sCurrentLine.trim();
    		if(packageDefined == false || classDefined == false){
	    		if(sCurrentLine.trim().startsWith(PACKAGE_KEYWORD)){
	    			packageDefined = true;
	    			path = sCurrentLine.substring("package".length()+1,sCurrentLine.length()-1);
	    		}else if(checkForKeyword(sCurrentLine,CLASS_KEYWORD)){
	    			classDefined = true;
	    			int classIndex = sCurrentLine.indexOf(CLASS_KEYWORD);
	    			className = sCurrentLine.substring(classIndex+6,sCurrentLine.length()-1);
	    			sCurrentLine = sCurrentLine+classDecorator.createPseudoCode();
	    		}
    		}
    		
    		source.append(sCurrentLine.trim()+"\n");
    	}
	}

	

	private void setFullClassName(boolean packageDefined, String defaultPath, String className, String path) {
		if(packageDefined)
			fullClassName = path.replace('.', '/') + "/" + className;
		else
			fullClassName = defaultPath.replace('.', '/') + "/" + className;
		//return fullClassName;
	}

	private static void handleException(){
		
	}
	
	
	
	
	
	
	
		private static String getClassNames(){
		return null;
	}

}