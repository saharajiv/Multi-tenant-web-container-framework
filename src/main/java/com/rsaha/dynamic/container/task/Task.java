package com.rsaha.dynamic.container.task;

import java.util.concurrent.Callable;

public interface Task<TaskResult> extends Callable<TaskResult>{

	public TaskResult executeTask();
		
	
	
}
