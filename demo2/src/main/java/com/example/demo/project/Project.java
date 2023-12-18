package com.example.demo.project;

import java.util.List;

import com.example.demo.employee.Employee;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	
	private String description;
	
	@ManyToMany(mappedBy = "assignedProjects")
	private List<Employee> assignedEmployees;
	
	public Project(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public Project(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public void assignEmployee(Employee employee) {
		assignedEmployees.add(employee);
	}
	
	public void removeAssignement(Employee employee) {
		assignedEmployees.remove(employee);
	}
	
	public boolean checkAssignement(Employee employee) {
		return assignedEmployees.contains(employee);
	}
}
