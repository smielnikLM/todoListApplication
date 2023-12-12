package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
	@Autowired
	private final TaskRepository taskRepository;
	
	public TaskController(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}
	
	@RequestMapping("/tasks")
	public Iterable<Task> findAllTasks() {
		return taskRepository.findAll();
	}
	
	@PostMapping("/tasks")
	public Task addTask(@RequestBody Task task) {
		return this.taskRepository.save(task);
	}
	
	@DeleteMapping("/tasks")
	public Iterable<Task> removeTask(@RequestParam("id") String identifier) {
		Integer parsedId = Integer.parseInt(identifier);
		taskRepository.deleteById(parsedId);
		return taskRepository.findAll();
	}
	
	@PutMapping("/tasks")
	public Iterable<Task> switchDoneState(@RequestParam("id") String id) {
		Integer parsedId = Integer.parseInt(id);
		Task task = taskRepository.findById(parsedId).orElse(null);
		task.setIsDone(!task.getIsDone());
		taskRepository.save(task);
		return taskRepository.findAll();
	}
	
	@PutMapping("/editName")
	public Task editTaskName(@RequestParam("id") String id, @RequestParam("taskName") String newTaskName) {
		Integer parsedId = Integer.parseInt(id);
		Task oldTask = taskRepository.findById(parsedId).orElse(null);
		if (oldTask == null || newTaskName == null || newTaskName.equals("")) {
			throw new IllegalArgumentException("No task found for id: " + id + ", or empty task name provided");	
		}
		Task updatedTask = new Task(oldTask.getId(), newTaskName, oldTask.getTaskDesc(), oldTask.getIsDone());
		taskRepository.save(updatedTask);
		return updatedTask;
	}
	
	@PutMapping("/editDesc")
	public Task editTaskDesc(@RequestParam("id") String id, @RequestParam("taskDesc") String newTaskDesc) {
		Integer parsedId = Integer.parseInt(id);
		Task oldTask = taskRepository.findById(parsedId).orElse(null);
		if (oldTask == null || newTaskDesc == null || newTaskDesc.equals("")) {
			throw new IllegalArgumentException("No task found for id: " + id + ", or empty task description provided");	
		}
		Task updatedTask = new Task(oldTask.getId(), oldTask.getTaskName(), newTaskDesc, oldTask.getIsDone());
		taskRepository.save(updatedTask);
		return updatedTask;
	}
}
