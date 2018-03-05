package com.rsaha.dynamic.container;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ObjectContainer{
	
	public ObjectContainer() {
		// TODO Auto-generated constructor stub
	}

	public void createAppContainer() {
		AppContainer appContainer = new AppContainer(); 
		appContainer.runContainer();
	}
	
	

	
}
