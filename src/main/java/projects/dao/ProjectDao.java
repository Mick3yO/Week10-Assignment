package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import provided.util.DaoBase;
import projects.exception.DbException;

//This class is responsible for Database Access for the Project Entity
public class ProjectDao extends DaoBase {
		// Define the table names used in SQL queries
		private static final String CATEGORY_TABLE = "category";
		private static final String MATERIAL_TABLE = "material";
		private static final String PROJECT_TABLE = "project";
		private static final String PROJECT_CATEGORY_TABLE = "project_category";
		private static final String STEP_TABLE = "step";

		// This method inserts a new project into the database
		public Project insertProject(Project project) {
			// Define the SQL query to insert a project
			String sql = ""
					+ "INSERT INTO " + PROJECT_TABLE + " "
					+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)";
			
			// Use try-with-resources to automatically close the connection
			try (Connection conn = DbConnection.getConnection()) {
				// Start a database transaction
				startTransaction(conn); 
				
				// Use PreparedStatement to avoid SQL injection
				try(PreparedStatement stmt = conn.prepareStatement(sql)) {
					// Set the parameters of the SQL query
					setParameter(stmt, 1, project.getProjectName(), String.class);
					setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
					setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
					setParameter(stmt, 4, project.getDifficulty(), Integer.class);
					setParameter(stmt, 5, project.getNotes(), String.class);
					
					// Execute the SQL query
					stmt.executeUpdate();
					
					// Get the ID of the last inserted project
					Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
					
					// Commit the transaction
					commitTransaction(conn);
					
					// Set the ID of the project and return it
					project.setProjectId(projectId);
					return project;
				} catch(Exception e) {
					// If an error occurs, rollback the transaction
					rollbackTransaction(conn);
					throw new DbException(e);
				}
			} catch (SQLException e) {
				throw new DbException(e);
			}
		}

		// This method retrieves all projects from the database
		public List<Project> fetchAllProjects() {
			// Define the SQL query to get all projects
			String sql = ""
					+ "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
			
			// Use try-with-resources to automatically close the connection
			try (Connection conn = DbConnection.getConnection()) {
				// Start a database transaction
				startTransaction(conn); 
				
				// Use PreparedStatement to avoid SQL injection
				try(PreparedStatement stmt = conn.prepareStatement(sql)) {
					// Execute the SQL query and get the ResultSet
					try(ResultSet rs = stmt.executeQuery()) {
						// Create a list to hold the projects
						List<Project> projects = new LinkedList<>();
						
						// Iterate over the ResultSet and add each project to the list
						while(rs.next()) {
							projects.add(extract(rs, Project.class));
						}
						
						// Return the list of projects
						return projects;
					}
				} catch(Exception e) {
					// If an error occurs, rollback the transaction
					rollbackTransaction(conn);
					throw new DbException(e);
				}
			} catch (SQLException e) {
				throw new DbException(e);
			}
		}

		// This method retrieves a project by its ID from the database
		public Optional <Project> fetchProjectById(Integer projectId) {
			// Define the SQL query to get a project by its ID
			String sql = ""
					+ "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
			
			// Use try-with-resources to automatically close the connection
			try (Connection conn = DbConnection.getConnection()) {
				// Start a database transaction
				startTransaction(conn); 
				try {
					Project project = null;
					
					// Use PreparedStatement to avoid SQL injection
					try(PreparedStatement stmt = conn.prepareStatement(sql)) {
						// Set the parameter of the SQL query
						setParameter(stmt, 1, projectId, Integer.class);
						
						// Execute the SQL query and get the ResultSet
						try(ResultSet rs = stmt.executeQuery()) {
							// If the ResultSet has a next row, extract the project
							if(rs.next()) {
								project = extract(rs, Project.class);
							}
						}
					}
					
					// If a project was found, fetch its materials, steps and categories
					if(Objects.nonNull(project)) {
						project.getMaterials()
							.addAll(fetchMaterialsForProject(conn, projectId));
						project.getSteps()
							.addAll(fetchStepsForProject(conn, projectId));
						project.getCategories()
							.addAll(fetchCategoriesForProject(conn, projectId));
					}
					
					// Commit the transaction
					commitTransaction(conn);
					
					// Return the project wrapped in an Optional
					return Optional.ofNullable(project);
				} catch(Exception e) {
					// If an error occurs, rollback the transaction
					rollbackTransaction(conn);
					throw new DbException(e);
				}
				
			} catch (SQLException e) {
				throw new DbException(e);
			}
		}

		// This method fetches the materials of a project
		private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
			// Define the SQL query to get the materials of a project
			String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
			
			// Use PreparedStatement to avoid SQL injection
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				// Set the parameter of the SQL query
				setParameter(stmt, 1, projectId, Integer.class);
				
				// Execute the SQL query and get the ResultSet
				try(ResultSet rs = stmt.executeQuery()) {
					// Create a list to hold the materials
					List<Material> materials = new LinkedList<Material>();
					
					// Iterate over the ResultSet and add each material to the list
					while(rs.next()) {
						materials.add(extract(rs, Material.class));
					}
					
					// Return the list of materials
					return materials;
				}
				
			}
		}

		// This method fetches the steps of a project
		private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
			// Define the SQL query to get the steps of a project
			String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
			
			// Use PreparedStatement to avoid SQL injection
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				// Set the parameter of the SQL query
				setParameter(stmt, 1, projectId, Integer.class);
				
				// Execute the SQL query and get the ResultSet
				try(ResultSet rs = stmt.executeQuery()) {
					// Create a list to hold the steps
					List<Step> steps = new LinkedList<Step>();
					
					// Iterate over the ResultSet and add each step to the list
					while(rs.next()) {
						steps.add(extract(rs, Step.class));
					}
					
					// Return the list of steps
					return steps;
				}
			}
		}
			
					
				

				// This method fetches the categories of a project
				private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
					// Define the SQL query to get the categories of a project
					String sql = ""
							+ "SELECT c.* "
							+ "FROM " + CATEGORY_TABLE + " c "
							+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
							+ "WHERE project_id = ? ";
					
					// Use PreparedStatement to avoid SQL injection
					try(PreparedStatement stmt = conn.prepareStatement(sql)) {
						// Set the parameter of the SQL query
						setParameter(stmt, 1, projectId, Integer.class);
						
						// Execute the SQL query and get the ResultSet
						try(ResultSet rs = stmt.executeQuery()) {
							// Create a list to hold the categories
							List<Category> categories = new LinkedList<Category>();
							
							// Iterate over the ResultSet and add each category to the list
							while(rs.next()) {
								categories.add(extract(rs, Category.class));
							}
							
							// Return the list of categories
							return categories;
						}
					}
				}
				}



