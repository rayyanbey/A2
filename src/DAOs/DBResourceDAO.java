package DAOs;

import Models.Resource;
import Utils.DBUtils;

import java.sql.*;

public class DBResourceDAO implements ResourceDAO {

    @Override
    public void save(Resource resource) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            if (resource.getId() > 0) {
                // Update existing resource
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE resources SET project_id = ?, name = ?, allocation = ? WHERE id = ?");
                ps.setInt(1, resource.getProjectId());
                ps.setString(2, resource.getName());
                ps.setDouble(3, resource.getAllocation());
                ps.setInt(4, resource.getId());
                ps.executeUpdate();
            } else {
                // Insert new resource
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO resources (project_id, name, allocation) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, resource.getProjectId());
                ps.setString(2, resource.getName());
                ps.setDouble(3, resource.getAllocation());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    resource.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Resource load(int id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM resources WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Resource(rs.getInt("id"), rs.getInt("project_id"), rs.getString("name"), rs.getDouble("allocation"));
            }
        }
        return null;
    }

    @Override
    public void update(Resource resource) throws SQLException {
        save(resource); // For simplicity, update by saving
    }

    @Override
    public void delete(int id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM resources WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public int getNextId() throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT MAX(id) FROM resources");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        }
        return 1;
    }
}
