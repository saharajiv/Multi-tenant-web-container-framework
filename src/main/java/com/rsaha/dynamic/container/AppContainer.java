package com.rsaha.dynamic.container;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.rsaha.dynamic.container.task.ExistingSourceFileWatcher;
import com.rsaha.dynamic.container.task.SourceFilesCompiler;

public class AppContainer implements Runnable{
	
	public void runContainer(){
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		while(true){
			try {
				Future<List<File>> future = executorService.submit(new ExistingSourceFileWatcher());
				if(future!=null && future.get()!=null && !future.get().isEmpty()){
					Iterator it = future.get().iterator();
					while(it.hasNext()){
						System.out.println("Future object : "+it.next());
					}
					executorService.submit(new SourceFilesCompiler(future.get()));
				}
				//Thread.sleep(1000*60);
				
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			} catch (ExecutionException e) {
				System.out.println(e.getMessage());
			} catch(Throwable t){
				System.out.println(t.getMessage());
			}
		}	
		// do not want to stop the application
		//executorService.shutdown();
	}

	
	public void runContainer2(){
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		//executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		while(true){
			try {
				Future<List<File>> future = executorService.submit(new ExistingSourceFileWatcher());
				if(future!=null && future.get()!=null && !future.get().isEmpty()){
					Iterator it = future.get().iterator();
					while(it.hasNext()){
						System.out.println("Future object : "+it.next());
					}
					executorService.submit(new SourceFilesCompiler(future.get()));
				}
				//Thread.sleep(1000*60);
				
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			} catch (ExecutionException e) {
				System.out.println(e.getMessage());
			}
		}	
		// do not want to stop the application
		//executorService.shutdown();
	}
	
	
	
	public void runContainer3(){
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		while(true){
			try {
				Future<List<File>> future = executorService.submit(new ExistingSourceFileWatcher());
				if(future!=null && future.get()!=null && !future.get().isEmpty()){
					Iterator it = future.get().iterator();
					while(it.hasNext()){
						System.out.println("Future object : "+it.next());
					}
					executorService.submit(new SourceFilesCompiler(future.get()));
				}
				//Thread.sleep(1000*60);
				
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			} catch (ExecutionException e) {
				System.out.println(e.getMessage());
			}
		}	
		// do not want to stop the application
		//executorService.shutdown();
	}

	

	@Override
	public void run() {
		while(true){
			
		}
		
	}
	

}
