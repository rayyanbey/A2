import Controllers.TaskController;
import DAOs.DBTaskDAO;
import Models.Task;
import org.junit.Test;
import java.time.LocalDate;
import static org.junit.Assert.*;

public class TaskControllerTest {

    @Test
    public void testSaveAndLoadTask() throws Exception {
        Task task = new Task(0, "Task A", LocalDate.of(2023,1,1), LocalDate.of(2023,1,5), "", "");
        TaskController controller = new TaskController(task, new DBTaskDAO());

        controller.saveTask();
        assertTrue(task.getId() > 0);

        Task loaded = new Task(0, "", LocalDate.now(), LocalDate.now(), "", "");
        TaskController loader = new TaskController(loaded, new DBTaskDAO());
        loader.loadTask(task.getId());

        assertEquals(task.getName(), loaded.getName());
        assertEquals(task.getStart(), loaded.getStart());
    }

    @Test
    public void testUpdateAndDeleteTask() throws Exception {
        Task task = new Task(0, "Task B", LocalDate.of(2023,2,1), LocalDate.of(2023,2,3), "", "");
        TaskController controller = new TaskController(task, new DBTaskDAO());
        controller.saveTask();

        task.setName("Task B Updated");
        controller.updateTask();

        Task loaded = new Task(0, "", LocalDate.now(), LocalDate.now(), "", "");
        TaskController loader = new TaskController(loaded, new DBTaskDAO());
        loader.loadTask(task.getId());
        assertEquals("Task B Updated", loaded.getName());

        controller.deleteTask(task.getId());
        // attempt to load deleted task
        Task afterDelete = new Task(0, "", LocalDate.now(), LocalDate.now(), "", "");
        TaskController afterLoader = new TaskController(afterDelete, new DBTaskDAO());
        afterLoader.loadTask(task.getId());
        // if not found, fields remain as initialized
        assertEquals("", afterDelete.getName());
    }
}
