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
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;

@RestController
@RequestMapping("/relations")
public class EmployeeTaskRelationController {
	private final EmployeeRepository employeeRepository;
	
	private final TaskRepository taskRepository;
	
	public EmployeeTaskRelationController(EmployeeRepository employeeRepository, TaskRepository taskRepository) {
		this.employeeRepository = employeeRepository;
		this.taskRepository = taskRepository;
	}
	
	private String printAllRelations() {
		String[] output = {""};
		
		taskRepository.findAll().forEach(task -> {
			output[0] += "Task ID: " + task.getId()
					+ ", Task Name: " + task.getName()
					+ ", Assigned Employees: [";
			task.getAssignedEmployees().forEach(employee -> {
				output[0] += "(Employee ID: " + employee.getId()
						+ ", Employee Name: " + employee.getName() + ")";
			});
			output[0] += "]\n";
		});
		
		return output[0];
	}
	
	private String printOneRelation(Task task) {
		String[] output = {""};
		
		output[0] += "Task ID: " + task.getId()
		+ ", Task Name: " + task.getName()
		+ ", Assigned Employees: [";
		task.getAssignedEmployees().forEach(employee -> {
			output[0] += "(Employee ID: " + employee.getId()
			+ ", Employee Name: " + employee.getName() + ")";
		});
		output[0] += "]\n";
		
		return output[0];
	}
	
	private boolean checkForRelation(Task task, Employee employee) {
		return (task.checkAssignment(employee) && employee.checkAssignment(task));
	}
	
	@GetMapping("/allRelations")
	public ResponseEntity<String> getAllRelations() {
		return ResponseEntity.ok(printAllRelations());
	}
	
	@GetMapping("/oneRelation")
	public ResponseEntity<String> getOneRelation(@RequestParam("taskId") Integer taskId) {
		Task task = taskRepository.findById(taskId).orElse(null);
		if (task == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No task found for id: " + taskId);
		}
		return ResponseEntity.ok(printOneRelation(task));
	}
	
	@PutMapping("")
	public ResponseEntity<String> addRelation(@RequestParam("taskId") Integer taskId, @RequestParam("employeeId") Integer employeeId) {
		Task task = taskRepository.findById(taskId).orElse(null);
		Employee employee = employeeRepository.findById(employeeId).orElse(null);
		
		if (task == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task not found for id: " + taskId);
		} else if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee not found for id: " + employeeId);
		}
		
		if (checkForRelation(task, employee)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Relation for taskId: " + taskId + " and employeeId: " + employeeId + " already exists");
		}
		
		task.assignEmployee(employee);
		employee.assignTask(task);
		
		taskRepository.save(task);
		employeeRepository.save(employee);
		
		return ResponseEntity.ok(printAllRelations());
	}
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteRelation(@RequestParam("taskId") Integer taskId, @RequestParam("employeeId") Integer employeeId) {
		Task task = taskRepository.findById(taskId).orElse(null);
		Employee employee = employeeRepository.findById(employeeId).orElse(null);
		
		if (task == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task not found for id: " + taskId);
		} else if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee not found for id: " + employeeId);
		}
		
		if (!checkForRelation(task, employee)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Relation for taskId: " + taskId + " and employeeId: " + employeeId + " does not exist");
		}

		task.removeAssignment(employee);
		employee.removeAssignment(task);
		
		taskRepository.save(task);
		employeeRepository.save(employee);
		
		return ResponseEntity.ok(printAllRelations());
	}
}
