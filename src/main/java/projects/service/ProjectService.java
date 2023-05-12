package projects.service;

import java.util.List;
import java.util.NoSuchElementException;


import projects.dao.ProjectDao;
import projects.entity.Project;

//The ProjectService class acts as the middleman between the controller (in this case, ProjectsApp) and the data access object (ProjectDao). 
//It provides methods that can be used by the controller to interact with the data source.
public class ProjectService {

	// Create an instance of ProjectDao
	private ProjectDao projectDao = new ProjectDao();

	// This method is used to add a new project. It calls the insertProject method of the ProjectDao
	// and passes the project to be inserted. It returns the inserted project.
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	// This method is used to retrieve all the projects. It calls the fetchAllProjects method of the ProjectDao.
	// It returns a list of all the projects.
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	// This method is used to retrieve a specific project by its ID. It calls the fetchProjectById method of the ProjectDao
	// and passes the ID of the project to be fetched. If the project is not found, it throws a NoSuchElementException.
	public Project fetchProjectbyId(Integer projectId) {
		return projectDao.fetchProjectById(projectId)
				.orElseThrow(() -> new NoSuchElementException(
				"Project with ID ="+ projectId + " does not exist."));
	}
}
