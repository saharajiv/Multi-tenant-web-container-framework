package com.rsaha.dynamic.container.task.processor;

import java.util.List;
import java.util.ArrayList;

import com.rsaha.dynamic.container.task.Task;

public class TaskProcessor {
	
	private List<Task> tasks = new ArrayList<>();
	
	public void subscribe(Task task){
		tasks.add(task);
	}
	
	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}


}
