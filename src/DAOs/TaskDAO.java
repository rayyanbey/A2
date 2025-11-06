package DAOs;

import Models.Task;

public interface TaskDAO {
    void save(Task task) throws java.sql.SQLException;
    Task load(int id) throws java.sql.SQLException;
    void update(Task task) throws java.sql.SQLException;
    void delete(int id) throws java.sql.SQLException;
    int getNextId() throws java.sql.SQLException;
}
