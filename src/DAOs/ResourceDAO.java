package DAOs;

import Models.Resource;

public interface ResourceDAO {
    void save(Resource resource) throws java.sql.SQLException;
    Resource load(int id) throws java.sql.SQLException;
    void update(Resource resource) throws java.sql.SQLException;
    void delete(int id) throws java.sql.SQLException;
    int getNextId() throws java.sql.SQLException;
}
