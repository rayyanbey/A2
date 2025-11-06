import Controllers.ResourceController;
import DAOs.DBResourceDAO;
import Models.Resource;
import org.junit.Test;
import java.sql.SQLException;
import static org.junit.Assert.*;

public class ResourceControllerTest {

    @Test
    public void testSaveResource() throws SQLException {
        Resource resource = new Resource("Test Resource", 1.0);
        ResourceController controller = new ResourceController(resource, new DBResourceDAO());

        controller.saveResource();

        assertTrue(resource.getId() > 0);
    }

    @Test
    public void testLoadResource() throws SQLException {
        Resource resource = new Resource("Test Resource", 1.0);
        ResourceController controller = new ResourceController(resource, new DBResourceDAO());

        controller.saveResource();
        int id = resource.getId();

        Resource newResource = new Resource("", 0.0);
        ResourceController newController = new ResourceController(newResource, new DBResourceDAO());
        newController.loadResource(id);

        assertEquals("Test Resource", newResource.getName());
        assertEquals(1.0, newResource.getAllocation(), 0.01);
    }

    @Test
    public void testUpdateResource() throws SQLException {
        Resource resource = new Resource("Test Resource", 1.0);
        ResourceController controller = new ResourceController(resource, new DBResourceDAO());

        controller.saveResource();
        resource.setName("Updated Resource");
        resource.setAllocation(0.5);
        controller.updateResource();

        Resource loaded = new Resource("", 0.0);
        ResourceController loadController = new ResourceController(loaded, new DBResourceDAO());
        loadController.loadResource(resource.getId());

        assertEquals("Updated Resource", loaded.getName());
        assertEquals(0.5, loaded.getAllocation(), 0.01);
    }

    @Test
    public void testDeleteResource() throws SQLException {
        Resource resource = new Resource("Test Resource", 1.0);
        ResourceController controller = new ResourceController(resource, new DBResourceDAO());

        controller.saveResource();
        int id = resource.getId();

        controller.deleteResource(id);

        Resource loaded = new Resource("", 0.0);
        ResourceController loadController = new ResourceController(loaded, new DBResourceDAO());
        loadController.loadResource(id);

        // loadResource leaves fields unchanged if not found, so name should still be empty string
        assertEquals("", loaded.getName());
    }
}
