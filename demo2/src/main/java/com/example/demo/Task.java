package com.example.demo;

import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String taskName;
	
	private String taskDesc;
	
	private boolean isDone;
	
	private Task() {}
	
	public Task(String taskName, String taskDesc) {
		this.taskName = taskName;
		this.taskDesc = taskDesc;
		this.isDone = false;
	}
	
	public Task(Integer id, String taskName, String taskDesc, boolean isDone) {
		this.id = id;
		this.taskName = taskName;
		this.taskDesc = taskDesc;
		this.isDone = isDone;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getTaskName() {
		return taskName;
	}
	
	public String getTaskDesc() {
		return taskDesc;
	}
	
	public boolean getIsDone() {
		return isDone;
	}
	
	public void setIsDone(boolean isDone) {
		this.isDone = isDone;
	}
}
