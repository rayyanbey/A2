import Controllers.ProjectController;
import Models.Project;
import Models.Resource;
import Models.Task;
import org.junit.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.Assert.*;

public class ProjectControllerTest {

    @Test
    public void testOverlappingTasksReport() {
        Project project = new Project(0, "Test Project");
        ProjectController controller = new ProjectController(project, null); // DAO not needed for this test

        // Add overlapping tasks
        Task task1 = new Task(1, "Task 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 5), "", "");
        Task task2 = new Task(2, "Task 2", LocalDate.of(2023, 1, 3), LocalDate.of(2023, 1, 7), "", "");
        Task task3 = new Task(3, "Task 3", LocalDate.of(2023, 1, 10), LocalDate.of(2023, 1, 15), "", "");

        controller.addTask(task1);
        controller.addTask(task2);
        controller.addTask(task3);

        List<String> overlaps = controller.overlappingTasksReport();

        assertEquals(1, overlaps.size());
        assertTrue(overlaps.get(0).contains("Task 1") && overlaps.get(0).contains("Task 2"));
    }

    @Test
    public void testProjectCompletionTime() {
        Project project = new Project(0, "Test Project");
        ProjectController controller = new ProjectController(project, null);

        // Add tasks with different end dates
        Task task1 = new Task(1, "Task 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 5), "", "");
        Task task2 = new Task(2, "Task 2", LocalDate.of(2023, 1, 3), LocalDate.of(2023, 1, 10), "", "");
        Task task3 = new Task(3, "Task 3", LocalDate.of(2023, 1, 6), LocalDate.of(2023, 1, 8), "", "");

        controller.addTask(task1);
        controller.addTask(task2);
        controller.addTask(task3);

        long duration = controller.projectDurationDays();

        // Start is 2023-01-01, end is 2023-01-10, duration = 10 days
        assertEquals(10, duration);
    }

    @Test
    public void testNoOverlappingTasks() {
        Project project = new Project(0, "Test Project");
        ProjectController controller = new ProjectController(project, null);

        Task task1 = new Task(1, "Task 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 5), "", "");
        Task task2 = new Task(2, "Task 2", LocalDate.of(2023, 1, 6), LocalDate.of(2023, 1, 10), "", "");

        controller.addTask(task1);
        controller.addTask(task2);

        List<String> overlaps = controller.overlappingTasksReport();

        assertTrue(overlaps.isEmpty());
    }

    @Test
    public void testProjectDurationWithNoTasks() {
        Project project = new Project(0, "Test Project");
        ProjectController controller = new ProjectController(project, null);

        long duration = controller.projectDurationDays();

        assertEquals(0, duration);
    }
}
