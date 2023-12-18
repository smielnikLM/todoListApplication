package com.example.demo.task;

import java.util.List;
import java.util.Set;

import com.example.demo.employee.Employee;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Task {
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Setter(AccessLevel.NONE)
	private String name;

	@Setter(AccessLevel.NONE)
	private String description;
	
	private StatusValues status;
	
	@Setter(AccessLevel.NONE)
	@ManyToMany(mappedBy = "assignedTasks")
	private List<Employee> assignedEmployees;
	
	private Integer parentId;
	
	public Task(String name, String description) {
		this.name = name;
		this.description = description;
		this.status = StatusValues.NEW;
	}
	
	public Task(Integer id, String name, String description, StatusValues status, List<Employee> assignedEmployees) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.status = status;
		this.assignedEmployees = assignedEmployees;
	}
	
	public void assignEmployee(Employee employee) {
		assignedEmployees.add(employee);
	}
	
	public void removeAssignment(Employee employee) {
		assignedEmployees.remove(employee);
	}
	
	public boolean checkAssignment(Employee employee) {
		return assignedEmployees.contains(employee);
	}
	
	public void addSubTask(Task task) {
		task.setParentId(this.getId());
	}
	
	public boolean isRoot() {
		return (parentId == null);
	}
}
