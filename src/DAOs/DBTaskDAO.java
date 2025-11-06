package DAOs;

import Models.Task;
import Utils.DBUtils;

import java.sql.*;

public class DBTaskDAO implements TaskDAO {

    @Override
    public void save(Task task) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            if (task.getId() > 0) {
                // Update existing task
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tasks SET project_id = ?, name = ?, start_date = ?, end_date = ?, dependencies = ?, resources = ? WHERE id = ?");
                ps.setInt(1, task.getProjectId());
                ps.setString(2, task.getName());
                ps.setDate(3, Date.valueOf(task.getStart()));
                ps.setDate(4, Date.valueOf(task.getEnd()));
                ps.setString(5, task.getDependencies());
                ps.setString(6, task.getResources());
                ps.setInt(7, task.getId());
                ps.executeUpdate();
            } else {
                // Insert new task; force DEFAULT for id so DB assigns it
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO tasks (id, project_id, name, start_date, end_date, dependencies, resources) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, task.getProjectId());
                ps.setString(2, task.getName());
                ps.setDate(3, Date.valueOf(task.getStart()));
                ps.setDate(4, Date.valueOf(task.getEnd()));
                ps.setString(5, task.getDependencies());
                ps.setString(6, task.getResources());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    task.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Task load(int id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM tasks WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Task(rs.getInt("id"), rs.getInt("project_id"), rs.getString("name"),
                    rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
                    rs.getString("dependencies"), rs.getString("resources"));
            }
        }
        return null;
    }

    @Override
    public void update(Task task) throws SQLException {
        save(task); // For simplicity, update by saving
    }

    @Override
    public void delete(int id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM tasks WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public int getNextId() throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT MAX(id) FROM tasks");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        }
        return 1;
    }
}
