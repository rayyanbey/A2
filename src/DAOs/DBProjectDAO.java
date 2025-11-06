package DAOs;

import Models.Project;
import Models.Resource;
import Models.Task;
import Utils.DBUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBProjectDAO implements ProjectDAO {

    @Override
    public void save(Project project) {
        // ensure tables exist before any DB operations
        DBUtils.createTablesIfNotExist();
        try (Connection conn = DBUtils.getConnection()) {
            if (project.getId() > 0) {
                // Update existing project
                PreparedStatement ps = conn.prepareStatement("UPDATE projects SET title = ? WHERE id = ?");
                ps.setString(1, project.getTitle());
                ps.setInt(2, project.getId());
                ps.executeUpdate();

                // Delete existing tasks and resources
                PreparedStatement delTasks = conn.prepareStatement("DELETE FROM tasks WHERE project_id = ?");
                delTasks.setInt(1, project.getId());
                delTasks.executeUpdate();

                PreparedStatement delRes = conn.prepareStatement("DELETE FROM resources WHERE project_id = ?");
                delRes.setInt(1, project.getId());
                delRes.executeUpdate();

                // Insert updated tasks
                PreparedStatement taskPs = conn.prepareStatement(
                    "INSERT INTO tasks (project_id, name, start_date, end_date, dependencies, resources) VALUES (?, ?, ?, ?, ?, ?)");
                for (Task t : project.getTasks()) {
                    taskPs.setInt(1, project.getId());
                    taskPs.setString(2, t.getName());
                    taskPs.setDate(3, Date.valueOf(t.getStart()));
                    taskPs.setDate(4, Date.valueOf(t.getEnd()));
                    taskPs.setString(5, t.getDependencies());
                    taskPs.setString(6, t.getResources());
                    taskPs.executeUpdate();
                }

                // Insert updated resources
                PreparedStatement resPs = conn.prepareStatement(
                    "INSERT INTO resources (project_id, name, allocation) VALUES (?, ?, ?)");
                for (Resource r : project.getResources()) {
                    resPs.setInt(1, project.getId());
                    resPs.setString(2, r.getName());
                    resPs.setDouble(3, r.getAllocation());
                    resPs.executeUpdate();
                }
            } else {
                // Insert new project
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO projects (title) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, project.getTitle());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                int projectId = rs.next() ? rs.getInt(1) : 1;

                // Insert tasks
                PreparedStatement taskPs = conn.prepareStatement(
                    "INSERT INTO tasks (project_id, name, start_date, end_date, dependencies, resources) VALUES (?, ?, ?, ?, ?, ?)");
                for (Task t : project.getTasks()) {
                    taskPs.setInt(1, projectId);
                    taskPs.setString(2, t.getName());
                    taskPs.setDate(3, Date.valueOf(t.getStart()));
                    taskPs.setDate(4, Date.valueOf(t.getEnd()));
                    taskPs.setString(5, t.getDependencies());
                    taskPs.setString(6, t.getResources());
                    taskPs.executeUpdate();
                }

                // Insert resources
                PreparedStatement resPs = conn.prepareStatement(
                    "INSERT INTO resources (project_id, name, allocation) VALUES (?, ?, ?)");
                for (Resource r : project.getResources()) {
                    resPs.setInt(1, projectId);
                    resPs.setString(2, r.getName());
                    resPs.setDouble(3, r.getAllocation());
                    resPs.executeUpdate();
                }

                project.setId(projectId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Project load(int id) {
        // ensure tables exist before any DB operations
        DBUtils.createTablesIfNotExist();
        Project project = null;
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT title FROM projects WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                project = new Project(id, rs.getString("title"));

                // Load tasks
                PreparedStatement taskPs = conn.prepareStatement("SELECT * FROM tasks WHERE project_id = ?");
                taskPs.setInt(1, id);
                ResultSet taskRs = taskPs.executeQuery();
                while (taskRs.next()) {
                    Task t = new Task(
                        taskRs.getInt("id"),
                        id,
                        taskRs.getString("name"),
                        taskRs.getDate("start_date").toLocalDate(),
                        taskRs.getDate("end_date").toLocalDate(),
                        taskRs.getString("dependencies"),
                        taskRs.getString("resources")
                    );
                    project.addTask(t);
                }

                // Load resources
                PreparedStatement resPs = conn.prepareStatement("SELECT * FROM resources WHERE project_id = ?");
                resPs.setInt(1, id);
                ResultSet resRs = resPs.executeQuery();
                while (resRs.next()) {
                    Resource r = new Resource(resRs.getInt("id"), id, resRs.getString("name"), resRs.getDouble("allocation"));
                    project.addResource(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }

    @Override
    public void update(Project project) {
        save(project); // For simplicity, update by saving
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM projects WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNextId() {
        try (Connection conn = DBUtils.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id) FROM projects");
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
