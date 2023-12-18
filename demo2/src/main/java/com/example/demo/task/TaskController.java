package com.example.demo.task;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.example.demo.employee.Employee;
import com.example.demo.employee.EmployeeRepository;

@RestController
@RequestMapping("/tasks")
public class TaskController {
	private final TaskRepository taskRepository;
	
	@Value("${tasks.allowedTreeLevel}")
	private int allowedTreeLevel;
	
	public TaskController(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}
	
	private String printAllTasks() {
		String[] output = {""};
		taskRepository.findAll().forEach(task -> {
			output[0] += "Task ID: " + task.getId()
					+ ", Task Name: " + task.getName()
					+ ", Task Description: " + task.getDescription()
					+ ", Task Status: " + task.getStatus();
			if (!task.isRoot()) {
				output[0] += ", Parent Task ID: " + task.getParentId();				
			} else {
				output[0] += ", Task is root";
			}
			output[0] += "\n";
		});
		return output[0];
	}
	
	private String printOneTask(Task task) {
		String output = "Task ID: " + task.getId()
					+ ", Task Name: " + task.getName()
					+ ", Task Description: " + task.getDescription()
					+ ", Task Status: " + task.getStatus();
		if (!task.isRoot()) {
			output += ", Parent Task ID: " + task.getParentId();				
		} else {
			output += ", Task is root";
		}
		output += "\n";
		return output;
	}
	
	private int checkTaskTreeDepth(Task task) {
		int c = 1;
		while (!task.isRoot() && task != null) {
			task = taskRepository.findById(task.getParentId()).orElse(null);
			c++;
		}
		return c;
	}
	
	@GetMapping("/allTasks")
	public ResponseEntity<String> getAllTasks() {
		System.out.println(allowedTreeLevel);
		return ResponseEntity.ok(printAllTasks());
	}
	
	@GetMapping("/oneTask")
	public ResponseEntity<String> getOneTask(@RequestParam("id") Integer id) {
		Task task = taskRepository.findById(id).orElse(null);
		if (task == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No task found for id: " + id);
		}
		return ResponseEntity.ok(printOneTask(task));
		
	}
	
	@PostMapping("")
	public ResponseEntity addTask(@RequestBody Task task) {
		if (task == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not valid task provided, please provide valid json object. Provided Task: " + task);
		}
		task.setStatus(StatusValues.NEW);
		return ResponseEntity.ok().body(taskRepository.save(task));
	}
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteTask(@RequestParam("id") Integer id) {
		taskRepository.deleteById(id);
		return ResponseEntity.ok(printAllTasks());
	}
	
	@PutMapping("/editName")
	public ResponseEntity<String> editTaskName(@RequestParam("id") Integer id, @RequestParam("name") String newTaskName) {
		Task oldTask = taskRepository.findById(id).orElse(null);
		if (oldTask == null || newTaskName == null || newTaskName.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No task found for id: " + id);	
		} else if (newTaskName == null || newTaskName.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty task name provided");
		}
		Task updatedTask = new Task(oldTask.getId(), newTaskName, oldTask.getDescription(), oldTask.getStatus(), oldTask.getAssignedEmployees());
		taskRepository.save(updatedTask);
		return ResponseEntity.ok(printOneTask(updatedTask));
	}
	
	@PutMapping("/editDesc")
	public ResponseEntity<String> editTaskDesc(@RequestParam("id") Integer id, @RequestParam("description") String newTaskDesc) {
		Task oldTask = taskRepository.findById(id).orElse(null);
		if (oldTask == null || newTaskDesc == null || newTaskDesc.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No task found for id: " + id);	
		} else if (newTaskDesc == null || newTaskDesc.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Empty task description provided");
		}
		Task updatedTask = new Task(oldTask.getId(), oldTask.getName(), newTaskDesc, oldTask.getStatus(), oldTask.getAssignedEmployees());
		taskRepository.save(updatedTask);
		return ResponseEntity.ok(printOneTask(updatedTask));
	}
	
	@PutMapping("/editStatus")
	public ResponseEntity<String> changeStatus(@RequestParam("id") Integer id, @RequestParam("status") String newStatusValue) {
		Task oldTask = taskRepository.findById(id).orElse(null);
		if (oldTask == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No task found for id:" + id);
		}
		StatusValues newStatus;
		switch (newStatusValue) {
		case "NEW":
			newStatus = StatusValues.NEW;
			break;
		case "IN_PROGRESS":
			newStatus = StatusValues.IN_PROGRESS;
			break;
		case "POSTPONED":
			newStatus = StatusValues.POSTPONED;
			break;
		case "DONE":
			newStatus = StatusValues.DONE;
			break;
		default:
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Given Value: " + newStatusValue + " does not match any of the acceptable values: (NEW, IN_PROGRESS, POSTPONED, DONE)");
		}
		Task updatedTask = new Task(oldTask.getId(), oldTask.getName(), oldTask.getDescription(), newStatus, oldTask.getAssignedEmployees());
		taskRepository.save(updatedTask);
		return ResponseEntity.ok(printOneTask(updatedTask));
	}
	
	@PutMapping("/subTasks")
	public ResponseEntity<String> addSubTask(@RequestParam("parentId") Integer parentId, @RequestParam("taskName") String subTaskName, @RequestParam("taskDesc") String subTaskDesc) {
		Task task = taskRepository.findById(parentId).orElse(null);
		if (task == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No task found for id: " + parentId);
		}
		int treeLevelCounter = checkTaskTreeDepth(task);
		if (treeLevelCounter == allowedTreeLevel) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tree depth exceeded allowed number. Trying to add at level: " + treeLevelCounter + ". Allowed level: " + allowedTreeLevel);
		}
		Task subTask = new Task(subTaskName, subTaskDesc);
		task.addSubTask(subTask);
		taskRepository.save(task);
		taskRepository.save(subTask);
		return ResponseEntity.ok(printOneTask(subTask));
	}
}
