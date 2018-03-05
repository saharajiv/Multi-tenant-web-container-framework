package com.rsaha.dynamic.classGenerator;

import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;





public class CustomClassCompiler  {
   private static final CustomClassCompiler customClassCompiler = new CustomClassCompiler();
   private List<String> classNameList = new ArrayList<>();
   private final Map<ClassJavaFileObject,Integer> outputFilesCountMap = new HashMap<>();
   
   public static CustomClassCompiler getInstance(){
	   return customClassCompiler;
   }
	
	private CustomClassCompiler(){
		
	}
    
    
    public Class<?> compile(String fullName, String sourceCode) throws Exception {
	  classNameList.add(fullName);	
  	  JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

  	    JavaFileObject compilationUnit =
  	        new StringJavaFileObject(fullName, sourceCode);

  	    SimpleJavaFileManager fileManager =
  	        new SimpleJavaFileManager(compiler.getStandardFileManager(null, null, null));

  	    JavaCompiler.CompilationTask compilationTask = compiler.getTask(
  	        null, fileManager, null, null, null, Arrays.asList(compilationUnit));

  	    compilationTask.call();

  	    Class generatedClass = loadClass(fullName, fileManager);
  	    return generatedClass;
  	    
  	  }

	private Class loadClass(String fullName, SimpleJavaFileManager fileManager) throws ClassNotFoundException {
		CompiledClassLoader classLoader =
  	        new CompiledClassLoader(fileManager.getGeneratedOutputFiles());

  	    Class<?> derivedClass = classLoader.loadClass(fullName);
  	    return derivedClass;
	}
    
    
    class CompiledClassLoader extends ClassLoader {
        private final Map<ClassJavaFileObject,Integer> filesCountMap;
        
        private CompiledClassLoader(Map<ClassJavaFileObject,Integer> filesMap) {
          this.filesCountMap = filesMap;
        }
        
        
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
          Iterator<ClassJavaFileObject> itr = filesCountMap.keySet().iterator();
          while (itr.hasNext()) {
            ClassJavaFileObject file = itr.next();
            if (file.getClassName().equals(name) && filesCountMap.get(file)>1) {
            	itr.remove();
            	byte[] bytes = file.getBytes();
            	return super.defineClass(name, bytes, 0, bytes.length);
            }
          }
          return super.findClass(name);
        }
      }
    
    
     class StringJavaFileObject extends SimpleJavaFileObject {
        private final String code;

        public StringJavaFileObject(String name, String code) {
          super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
              Kind.SOURCE);
          this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
          return code;
        }
      }
     
     
     class SimpleJavaFileManager extends ForwardingJavaFileManager {
	   

	    protected SimpleJavaFileManager(JavaFileManager fileManager) {
	      super(fileManager);
	      //outputFiles = new ArrayList<ClassJavaFileObject>();
	      
	    }

	    @Override
	    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
	      ClassJavaFileObject file = new ClassJavaFileObject(className, kind);
	      if(outputFilesCountMap.containsKey(file)){
	    	  int count = outputFilesCountMap.get(file);
	    	  outputFilesCountMap.put(file,count++);
	      }
	      outputFilesCountMap.put(file,1);
	      return file;
	    }

	    public Map<ClassJavaFileObject,Integer> getGeneratedOutputFiles() {
	      return outputFilesCountMap;
	    }
	  }

   
}



class ClassJavaFileObject extends SimpleJavaFileObject {
    private final ByteArrayOutputStream outputStream;
    private final String className;
    
    //new SimpleJavaFileObject(URI.create(fullClassName + JAVA_FILE_EXTENSION), SOURCE) 
    
    protected ClassJavaFileObject(String className, Kind kind) {
      super(URI.create(className + kind.extension), kind);
      this.className = className;
      outputStream = new ByteArrayOutputStream();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
      return outputStream;
    }

    public byte[] getBytes() {
      return outputStream.toByteArray();
    }

    public String getClassName() {
      return className;
    }
  }

