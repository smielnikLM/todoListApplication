package com.example.demo.employee;

import java.util.List;

import com.example.demo.project.Project;
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
	private Integer id;
	
	private String name;
	
	@ManyToMany
	@JoinTable(name = "employee_task",
			joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"))
	private List<Task> assignedTasks;
	
	@ManyToMany
	@JoinTable(name = "employee_project",
			joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"))
	private List<Project> assignedProjects;
	
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
	
	public void removeTaskAssignment(Task task) {
		assignedTasks.remove(task);
	}
	
	public boolean checkTaskAssignment(Task task) {
		return assignedTasks.contains(task);
	}
	
	public void assignProject(Project project) {
		assignedProjects.add(project);
	}
	
	public void removeProjectAssignement(Project project) {
		assignedProjects.remove(project);
	}
	
	public boolean checkProjectAssignement(Project project) {
		return assignedProjects.contains(project);
	}
}
