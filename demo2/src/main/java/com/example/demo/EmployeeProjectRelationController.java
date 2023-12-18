package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.employee.Employee;
import com.example.demo.employee.EmployeeRepository;
import com.example.demo.project.Project;
import com.example.demo.project.ProjectRepository;

@RestController
@RequestMapping("/projectRelation")
public class EmployeeProjectRelationController {
	private final EmployeeRepository employeeRepository;
	
	private final ProjectRepository projectRepository;
	
	public EmployeeProjectRelationController(EmployeeRepository employeeRepository, ProjectRepository projectRepository) {
		this.employeeRepository = employeeRepository;
		this.projectRepository = projectRepository;
	}
	
	private String printAllRelations() {
		String[] output = {""};
		
		projectRepository.findAll().forEach(project -> {
			output[0] += "Project ID: " + project.getId()
					+ ", Project name: " + project.getName()
					+ ", Assigned Employees: [";
			project.getAssignedEmployees().forEach(employee -> {
				output[0] += "(Employee ID: " + employee.getId()
						+ ", Employee Name: " + employee.getName() + ")";
			});
			output[0] += "]\n";
		});
		
		return output[0];
	}
	
	private String printOneRelation(Project project) {
		String[] output = {""};
		
		output[0] += "Project ID: " + project.getId()
				+ ", Project name: " + project.getName()
				+ ", Assigned employees: [";
		project.getAssignedEmployees().forEach(employee -> {
			output[0] += "(Employee ID: " + employee.getId()
					+ ", Employee Name: " + employee.getName() + ")";
		});
		output[0] += "]\n";
		
		return output[0];
	}
	
	private boolean checkForRelation(Project project, Employee employee) {
		return (project.checkAssignement(employee) && employee.checkProjectAssignement(project));
	}
	
	@GetMapping("/allRelations")
	public ResponseEntity<String> getAllRelations() {
		return ResponseEntity.ok(printAllRelations());
	}
	
	@GetMapping("/oneRelation")
	public ResponseEntity<String> getOneRelation(@RequestParam("id") Integer id) {
		Project project = projectRepository.findById(id).orElse(null);
		if (project == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No project found for id: " + id);
		}
		return ResponseEntity.ok(printOneRelation(project));
	}
	
	@PutMapping("")
	public ResponseEntity<String> addRelation(@RequestParam("projectId") Integer projectId, @RequestParam("employeeId") Integer employeeId) {
		Project project = projectRepository.findById(projectId).orElse(null);
		Employee employee = employeeRepository.findById(employeeId).orElse(null);
		
		if (project == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No project found for id: " + projectId);
		} else if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No employee found for id: " + employeeId);
		}
		
		if (checkForRelation(project, employee)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Relation for projectId: " + projectId + " and employeeId: " + employeeId + " already exists");
		}
		
		project.assignEmployee(employee);
		employee.assignProject(project);
		
		projectRepository.save(project);
		employeeRepository.save(employee);
		
		return ResponseEntity.ok(printAllRelations());
	}
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteRelation(@RequestParam("projectId") Integer projectId, @RequestParam("employeeId") Integer employeeId) {
		Project project = projectRepository.findById(projectId).orElse(null);
		Employee employee = employeeRepository.findById(employeeId).orElse(null);
		
		if (project == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No project found for id: " + projectId);
		} else if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No employee found for id: " + employeeId);
		}
		
		if (!checkForRelation(project, employee)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Relation for projectId: " + projectId + " and employeeId: " + employeeId + " does not exist");
		}
		
		project.removeAssignement(employee);
		employee.removeProjectAssignement(project);
		
		projectRepository.save(project);
		employeeRepository.save(employee);
		
		return ResponseEntity.ok(printAllRelations());
	}
}
