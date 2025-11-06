package Controllers;

import DAOs.ResourceDAO;
import Models.Resource;

import java.sql.SQLException;

public class ResourceController {

    private final Resource resource;
    private ResourceDAO dao;

    public ResourceController(Resource resource, ResourceDAO dao) {
        this.resource = resource;
        this.dao = dao;
    }

    public Resource getResource() {
        return resource;
    }

    /** Save resource to database */
    public void saveResource() {
        try {
            dao.save(resource);
            System.out.println("Resource saved successfully.");
        } catch (Exception e) {
            System.err.println("Failed to save resource: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Load resource by ID from database into current model */
    public void loadResource(int id) throws SQLException {
        Resource loaded = dao.load(id);
        if (loaded != null) {
            resource.setId(loaded.getId());
            resource.setName(loaded.getName());
            resource.setAllocation(loaded.getAllocation());
        }
    }

    /** Update the existing resource record in database */
    public void updateResource() throws SQLException {
        dao.update(resource);
    }

    /** Delete a resource by its ID */
    public void deleteResource(int id) throws SQLException {
        dao.delete(id);
    }

    /** Get the next available resource ID */
    public int getNextResourceId() throws SQLException {
        return dao.getNextId();
    }

    /** Swap DAO implementation at runtime (optional) */
    public void setDao(ResourceDAO dao) {
        this.dao = dao;
    }
}
