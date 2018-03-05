package com.rsaha.dynamic.container.task;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public abstract class AbstractTask implements Task<TaskResult>{

	private Set<Task> dependentTasks = new LinkedHashSet<>();
	
	protected void setDependentTasks(Set<Task> tasks){
		dependentTasks = tasks;
	}


	@Override
	public TaskResult call() throws Exception {
		TaskResult obj = executeTask();
		return obj;
	}

	
	

}
