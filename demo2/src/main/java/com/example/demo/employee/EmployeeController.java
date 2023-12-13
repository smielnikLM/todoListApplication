package com.example.demo.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
	private final EmployeeRepository employeeRepository;
	
	public EmployeeController(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}
	
	private String printAllEmployees() {
		String[] output = {""};
		employeeRepository.findAll().forEach(employee -> {
			output[0] += "Employee ID: " + employee.getId()
					+ ", Employee Name: " + employee.getName() + "\n";
		});
		return output[0];
	}
	
	private String printOneEmployee(Employee employee) {
		String output = "Employee ID: " + employee.getId()
						+ "Employee Name: " + employee.getName() + "\n";
		return output;
	}
	
	@GetMapping("/allEmployees")
	public ResponseEntity<String> getAllEmployees() {
		return ResponseEntity.ok(printAllEmployees());
	}
	
	@GetMapping("/oneEmployee")
	public ResponseEntity<String> getOneEmployee(@RequestParam("id") Integer id) {
		Employee employee = employeeRepository.findById(id).orElse(null);
		if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No employee found for id: " + id);
		}
		return ResponseEntity.ok(printOneEmployee(employee));
	}
	
	@PostMapping("")
	public ResponseEntity addEmployee(@RequestBody Employee employee) {
		if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not valid employee provided, please provide valid json object. Provided Employee: " + employee);
		}
		return ResponseEntity.ok().body(employeeRepository.save(employee));
	}
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteEmployee(@RequestParam("id") Integer id) {
		employeeRepository.deleteById(id);
		return ResponseEntity.ok(printAllEmployees());
	}
	
	@PutMapping("/edit")
	public ResponseEntity<String> editEmployeeName(@RequestParam("id") Integer id, @RequestParam("name") String newName) {
		Employee oldEmployee = employeeRepository.findById(id).orElse(null);
		if (oldEmployee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user found for id: " + id);
		} else if (newName == null || newName.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty user name provided");
		}
		Employee updatedEmployee = new Employee(oldEmployee.getId(), newName);
		employeeRepository.save(updatedEmployee);
		return ResponseEntity.ok(printOneEmployee(updatedEmployee));
	}
}
