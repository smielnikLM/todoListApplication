package com.example.demo.employee;

import java.util.List;

import com.example.demo.task.Task;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	String name;
	
	@ManyToMany
	@JoinTable(name = "employee_task",
			joinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"))
	List<Task> assignedTasks;
	
	public Employee(String name) {
		this.name = name;
	}
	
	public Employee(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public void assignTask(Task task) {
		assignedTasks.add(task);
	}
	
	public void removeAssignment(Task task) {
		assignedTasks.remove(task);
	}
	
	public boolean checkAssignment(Task task) {
		return assignedTasks.contains(task);
	}
}
