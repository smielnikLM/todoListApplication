package com.example.demo.project;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.task.Task;

@RestController
@RequestMapping("/projects")
public class ProjectController {
	private final ProjectRepository projectRepository;
	
	public ProjectController(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}
	
	private String printAllProjects() {
		String[] output = {""};
		projectRepository.findAll().forEach(project -> {
			output[0] += "Project ID: " + project.getId()
					+ ", Project name: " + project.getName()
					+ ", Project description: " + project.getDescription() + "\n";
		});
		return output[0];
	}
	
	private String printOneProject(Project project) {
		String output = "Project ID: " + project.getId()
					+ ", Project name: " + project.getName()
					+ ", Project description: " + project.getDescription() + "\n";
		return output;
	}
	
	@GetMapping("/allProjects")
	public ResponseEntity<String> getAllProjects() {
		return ResponseEntity.ok(printAllProjects());
	}
	
	@GetMapping("/oneProject")
	public ResponseEntity<String> getOneProject(@RequestParam("id") Integer id) {
		Project project = projectRepository.findById(id).orElse(null);
		if (project == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No project found for id: " + id);
		}
		return ResponseEntity.ok(printOneProject(project));
	}
	
	@PutMapping("/add")
	public ResponseEntity<String> addProject(@RequestParam("name") String name, @RequestParam("desc") String desc) {
		Project project = new Project(name, desc);
		projectRepository.save(project);
		return ResponseEntity.ok(printOneProject(project));
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteProject(@RequestParam("id") Integer id) {
		projectRepository.deleteById(id);
		return ResponseEntity.ok(printAllProjects());
	}
	
	@PutMapping("/edit/name")
	public ResponseEntity<String> editProjectName(@RequestParam("id") Integer id, @RequestParam("name") String newProjectName) {
		Project oldProject = projectRepository.findById(id).orElse(null);
		if (oldProject == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No project found for id: " + id);	
		} else if (newProjectName == null || newProjectName.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty task name provided");
		}
		Project updatedProject = new Project(oldProject.getId(), newProjectName, oldProject.getDescription());
		projectRepository.save(updatedProject);
		return ResponseEntity.ok(printOneProject(updatedProject));
	}
	
	@PutMapping("/edit/desc")
	public ResponseEntity<String> editProjectDesc(@RequestParam("id") Integer id, @RequestParam("desc") String newProjectDesc) {
		Project oldProject = projectRepository.findById(id).orElse(null);
		if (oldProject == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No project found for id: " + id);	
		} else if (newProjectDesc == null || newProjectDesc.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty task name provided");
		}
		Project updatedProject = new Project(oldProject.getId(), oldProject.getName(), newProjectDesc);
		projectRepository.save(updatedProject);
		return ResponseEntity.ok(printOneProject(updatedProject));
	}
}
