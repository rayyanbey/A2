package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234"; // Change as needed

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTablesIfNotExist() {
        try (Connection conn = getConnection()) {
            // create base tables if they don't exist
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS projects (" +
                "id SERIAL PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL)"
            );
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS tasks (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "start_date DATE, " +
                "end_date DATE, " +
                "dependencies TEXT, " +
                "resources TEXT)"
            );
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS resources (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "allocation DOUBLE PRECISION)"
            );

            // Ensure project_id columns exist for older schemas and add if missing
            // ALTER ... ADD COLUMN IF NOT EXISTS is supported in PostgreSQL
            try {
                conn.createStatement().execute("ALTER TABLE tasks ADD COLUMN IF NOT EXISTS project_id INT");
            } catch (SQLException ignored) {
                // safe to ignore; column may already exist or operation unsupported on very old PG
            }
            try {
                conn.createStatement().execute("ALTER TABLE resources ADD COLUMN IF NOT EXISTS project_id INT");
            } catch (SQLException ignored) {
            }

            // Try to add foreign key constraints if they don't exist; ignore failures
            try {
                conn.createStatement().execute("ALTER TABLE tasks ADD CONSTRAINT tasks_project_fk FOREIGN KEY (project_id) REFERENCES projects(id)");
            } catch (SQLException ignored) {
            }
            try {
                conn.createStatement().execute("ALTER TABLE resources ADD CONSTRAINT resources_project_fk FOREIGN KEY (project_id) REFERENCES projects(id)");
            } catch (SQLException ignored) {
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
