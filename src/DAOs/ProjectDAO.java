package DAOs;

import Models.Project;

public interface ProjectDAO {
    void save(Project project) throws java.sql.SQLException;
    Project load(int id) throws java.sql.SQLException;
    void update(Project project) throws java.sql.SQLException;
    void delete(int id) throws java.sql.SQLException;
    int getNextId() throws java.sql.SQLException;
}
