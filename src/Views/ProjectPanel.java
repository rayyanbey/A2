package Views;

import Controllers.ProjectController;
import Models.Project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProjectPanel extends JPanel {
    private final ProjectController projectController;
    private final JTable tasksTable;
    private final JTable resourcesTable;
    private final DefaultTableModel tasksmodel;
    private final DefaultTableModel resourcesmodel;
    private final JTextField projectTitle;

    public ProjectPanel(){
        setLayout(new BorderLayout());
        Project project = new Project("New Project");
        projectController = new ProjectController(project);

        //TOP toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnNew = new JButton("New");
        JButton btnSave = new JButton("Save");
        JButton btnClose = new JButton("Close");
        JButton btnUploadTasks = new JButton("Upload Tasks");
        JButton btnUploadResources = new JButton("Upload Resources");
        JButton analyze = new  JButton("Analyze");
        JButton visualize = new  JButton("Visualize");

        projectTitle = new JTextField(project.getTitle());
        projectTitle.setColumns(30);

        //adding buttons on toolbar
        toolBar.add(btnNew);
        toolBar.add(btnSave);
        toolBar.add(btnClose);

        toolBar.addSeparator();

        toolBar.add(btnUploadTasks);
        toolBar.add(btnUploadResources);

        toolBar.addSeparator();
        toolBar.add(analyze);
        toolBar.add(visualize);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(new JLabel("Project Title:"));
        toolBar.add(projectTitle);

        add(toolBar, BorderLayout.NORTH);

        //Tasks and Resource Table
        tasksmodel = new DefaultTableModel(new Object[]{"Id","Task","Start","End","Deps","Resources"},0);
        tasksTable = new JTable(tasksmodel);
        resourcesmodel = new DefaultTableModel(new Object[]{"Name","Allocation"},0);
        resourcesTable = new JTable(resourcesmodel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tasksTable),
                new JScrollPane(resourcesTable));
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        //bottom panel for add task and resource
        JPanel bottom = new JPanel();
        JButton addTaskBtn = new JButton("Add Task");
        JButton addResBtn = new JButton("Add Resource");
        bottom.add(addTaskBtn); bottom.add(addResBtn);
        add(bottom, BorderLayout.SOUTH);
    }
}
