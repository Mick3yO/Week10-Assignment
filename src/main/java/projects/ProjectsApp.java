package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;
import projects.entity.*;

//Main class for managing projects
public class ProjectsApp {

		// Scanner object for reading user input
		private Scanner scanner = new Scanner(System.in);
		
		// Service object for managing project data
		private ProjectService projectService = new ProjectService();
		
		// Represents the currently selected project
		private Project curProject;
		
		// List of user operations available in the application
		private List<String> operations = List.of(
				"1) Add a project",
				"2) List projects",
				"3) Select a project"
				);

	// Main method
	public static void main(String[] args) {
		// Starts the process of user interactions
		new ProjectsApp().processUserSelections();
	}
	
	// This method processes user selections in a loop until the user decides to exit
	private void processUserSelections() {
	boolean done = false;
		
		while(!done) {
			try {
				int selection = getUserSelection();
					// Switch based on user selection
					switch(selection) {
						case -1:
							// Exit the application
							done = exitMenu();
							break;
						case 1:
							// Create a new project
							createProject();
							break;	
						case 2:
							// List all projects
							listProjects();
							break;
						case 3:
							// Select a project
							selectProject();
							break;
						default:
							// Invalid selection
							System.out.println("\n" + selection + " is not valid. Try again.");
				}
			} catch (Exception e) {
				// Handle exception and print error
				System.out.println("\nError" + e.toString() + " Try again.");
				e.printStackTrace();
			}
		}
	}
	
	// This method lists all the projects
	private List<Project> listProjects() {
		// Get all projects from the service
		List <Project> projects = projectService.fetchAllProjects();
		
		// Print all projects
		projects.forEach(project -> System.out
				.println("   " + project.getProjectId()  
				+ ": " + project.getProjectName()));
		
		return projects;
	}

	// This method allows the user to select a project
	private void selectProject() {
		// List all projects
		List <Project> projects = listProjects();
		
		// Get user input for project ID
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		// Set current project based on user selection
		curProject = projectService.fetchProjectbyId(projectId);	
	}

	// This method allows the user to create a new project
	private void createProject() {
		// Get user inputs for project details
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		// Create a new project object
		Project project = new Project();
		
		// Set project details
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		// Add the project to the database
		Project dbProject = projectService.addProject(project);
		
		// Print the project that has been added
		System.out.println("You added this project:\n" + dbProject);
	}

	// This method gets user's selection input
	private int getUserSelection() {
		// Print available operations
		printOperations();
		
		// Get user's selection
		Integer input = getIntInput("\nEnter a menu selection");
		
		// If input is null, return -1 to indicate exit, else return the input
		return Objects.isNull(input) ? -1 : input;
	}
		
	// This method prints the available operations
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press Enter to quit:");
		
		// Print all operations
		operations.forEach(line -> System.out.println("   " + line));
		
		// If no project is selected, notify the user
		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			// If a project is selected, notify the user which project it is
			System.out.println("\nYou are working with project: " + curProject);
		}
	}
	
	// This method gets an integer input from the user
	private Integer getIntInput(String prompt) {
		// Get the string input from the user
		String input = getStringInput(prompt);
		
		// If input is null, return null
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			// Try to convert the input to an integer
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			// If conversion fails, throw an exception
			throw new DbException(input + " is not a valid number.");
		}
	}

	// This method gets a string input from the user
	private String getStringInput(String prompt) {
		// Print the prompt
		System.out.print(prompt + ": ");
		
		// Get the user input
		String line = scanner.nextLine();
		
		// If line is blank, return null, else return the input
		return line.isBlank() ? null : line.trim();
	}
	
	// This method is for exiting the menu
	private boolean exitMenu() {
		// Print exit message
		System.out.println("Exiting the menu...");
		
		// Return true to indicate exit
		return true;
	}
	
	// This method gets a BigDecimal input from the user
	private BigDecimal getDecimalInput(String prompt) {
		// Get the string input from the user
		String input = getStringInput(prompt);
		
		// If input is null, return null
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			// Try to convert the input to BigDecimal
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			// If conversion fails, throw an exception
			throw new DbException(input + " is not a valid number.");
		}
	}
}


