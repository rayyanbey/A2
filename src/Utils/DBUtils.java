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
                "resources TEXT, " +
                "project_id INT)"
            );
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS resources (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "allocation DOUBLE PRECISION, " +
                "project_id INT)"
            );

            // SAFER MIGRATION STEPS FOR EXISTING DBS
            // Ensure id column exists as integer (nullable), create sequence, backfill NULL ids, set default, set sequence value
            try {
                // projects
                conn.createStatement().execute("ALTER TABLE projects ADD COLUMN IF NOT EXISTS id integer");
                conn.createStatement().execute("CREATE SEQUENCE IF NOT EXISTS projects_id_seq");
                conn.createStatement().execute("UPDATE projects SET id = nextval('projects_id_seq') WHERE id IS NULL");
                conn.createStatement().execute("ALTER TABLE projects ALTER COLUMN id SET DEFAULT nextval('projects_id_seq')");
                try { conn.createStatement().executeQuery("SELECT setval('projects_id_seq', COALESCE((SELECT MAX(id) FROM projects), 1))"); } catch (SQLException ignored) {}
                try { conn.createStatement().execute("ALTER TABLE projects ADD CONSTRAINT IF NOT EXISTS projects_pk PRIMARY KEY (id)"); } catch (SQLException ignored) {}
            } catch (SQLException ignored) {}

            try {
                // tasks
                conn.createStatement().execute("ALTER TABLE tasks ADD COLUMN IF NOT EXISTS id integer");
                conn.createStatement().execute("CREATE SEQUENCE IF NOT EXISTS tasks_id_seq");
                conn.createStatement().execute("UPDATE tasks SET id = nextval('tasks_id_seq') WHERE id IS NULL");
                conn.createStatement().execute("ALTER TABLE tasks ALTER COLUMN id SET DEFAULT nextval('tasks_id_seq')");
                try { conn.createStatement().executeQuery("SELECT setval('tasks_id_seq', COALESCE((SELECT MAX(id) FROM tasks), 1))"); } catch (SQLException ignored) {}
                try { conn.createStatement().execute("ALTER TABLE tasks ADD CONSTRAINT IF NOT EXISTS tasks_pk PRIMARY KEY (id)"); } catch (SQLException ignored) {}
                try { conn.createStatement().execute("ALTER TABLE tasks ADD CONSTRAINT IF NOT EXISTS tasks_project_fk FOREIGN KEY (project_id) REFERENCES projects(id)"); } catch (SQLException ignored) {}
            } catch (SQLException ignored) {}

            try {
                // resources
                conn.createStatement().execute("ALTER TABLE resources ADD COLUMN IF NOT EXISTS id integer");
                conn.createStatement().execute("CREATE SEQUENCE IF NOT EXISTS resources_id_seq");
                conn.createStatement().execute("UPDATE resources SET id = nextval('resources_id_seq') WHERE id IS NULL");
                conn.createStatement().execute("ALTER TABLE resources ALTER COLUMN id SET DEFAULT nextval('resources_id_seq')");
                try { conn.createStatement().executeQuery("SELECT setval('resources_id_seq', COALESCE((SELECT MAX(id) FROM resources), 1))"); } catch (SQLException ignored) {}
                try { conn.createStatement().execute("ALTER TABLE resources ADD CONSTRAINT IF NOT EXISTS resources_pk PRIMARY KEY (id)"); } catch (SQLException ignored) {}
                try { conn.createStatement().execute("ALTER TABLE resources ADD CONSTRAINT IF NOT EXISTS resources_project_fk FOREIGN KEY (project_id) REFERENCES projects(id)"); } catch (SQLException ignored) {}
            } catch (SQLException ignored) {}

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
