package Controllers;

import DAOs.TaskDAO;
import Models.Task;

import java.sql.SQLException;

public class TaskController {
    private final Task task;
    private TaskDAO dao;

    public TaskController(Task task, TaskDAO dao) {
        this.task = task;
        this.dao = dao;
    }

    public Task getTask() {
        return task;
    }

    /** Save task to database */
    public void saveTask() {
        try {
            dao.save(task);
            System.out.println("Task saved successfully.");
        } catch (Exception e) {
            System.err.println("Failed to save task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Load task by ID from database into current model */
    public void loadTask(int id) throws SQLException {
        Task loaded = dao.load(id);
        if (loaded != null) {
            task.setId(loaded.getId());
            task.setProjectId(loaded.getProjectId());
            task.setName(loaded.getName());
            task.setStart(loaded.getStart());
            task.setEnd(loaded.getEnd());
            task.setDependencies(loaded.getDependencies());
            task.setResources(loaded.getResources());
        }
    }

    /** Update the existing task record in database */
    public void updateTask() throws SQLException {
        dao.update(task);
    }

    /** Delete a task by its ID */
    public void deleteTask(int id) throws SQLException {
        dao.delete(id);
    }
}
