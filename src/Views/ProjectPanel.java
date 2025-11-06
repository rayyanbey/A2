package Views;

import Controllers.ProjectController;
import Controllers.ResourceController;
import Controllers.TaskController;
import Models.Project;
import Models.Resource;
import Models.Task;
import Utils.FileUtils;
import DAOs.DBProjectDAO;
import DAOs.DBResourceDAO;
import DAOs.DBTaskDAO;
import DAOs.FileProjectDAO;
import DAOs.ProjectDAO;
import DAOs.ResourceDAO;
import DAOs.TaskDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ProjectPanel extends JPanel {
    private final ProjectController projectController;
    private final JTable tasksTable;
    private final JTable resourcesTable;
    private final DefaultTableModel tasksmodel;
    private final DefaultTableModel resourcesmodel;
    private final JTextField projectTitle;

    public ProjectPanel(){
        setLayout(new BorderLayout());
        Project project = new Project(0, "New Project");
        ProjectDAO dao = new DBProjectDAO(); // Default to DB
        projectController = new ProjectController(project, dao);

        // create DAOs to use with task/resource controllers when needed
        final TaskDAO taskDao = new DBTaskDAO();
        final ResourceDAO resourceDao = new DBResourceDAO();

        //TOP toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnNew = new JButton("New");
        JButton btnSave = new JButton("Save");
        JButton btnClose = new JButton("Close");
        JButton btnUploadTasks = new JButton("Upload Tasks");
        JButton btnUploadResources = new JButton("Upload Resources");
        JButton analyze = new  JButton("Analyze");
        JButton visualize = new  JButton("Visualize");
        JButton btnLoad = new JButton("Load");
        JButton btnDelete = new JButton("Delete");

        projectTitle = new JTextField(project.getTitle());
        projectTitle.setColumns(30);

        //adding buttons on toolbar
        toolBar.add(btnNew);
        toolBar.add(btnLoad);
        toolBar.add(btnSave);
        toolBar.add(btnDelete);
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
        resourcesmodel = new DefaultTableModel(new Object[]{"Id","Name","Allocation"},0);
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

        addTaskBtn.addActionListener(e ->{
            String name = JOptionPane.showInputDialog(this,"Task Name:");
            if(name==null || name.trim().isEmpty()){
                return;
            }
            String s = JOptionPane.showInputDialog(this,"Start Time:");
            String en = JOptionPane.showInputDialog(this,"End Time:");

            try{
                LocalDate sd = LocalDate.parse(s.trim(), java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalDate ed = LocalDate.parse(en.trim(), java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                String deps = JOptionPane.showInputDialog(this, "Dependencies (comma separated ids):");
                String res = JOptionPane.showInputDialog(this, "Resources (comma separated):");
                // create task with id 0 so DAO treats it as a new insert
                Task t = new Task(0, name, sd, ed, deps==null?"":deps, res==null?"":res);
                if(projectController.getProject().getId() > 0) {
                    // project already persisted: set projectId and save to DB immediately
                    t.setProjectId(projectController.getProject().getId());
                    t.setId(0);
                    TaskController tc = new TaskController(t, taskDao);
                    tc.saveTask();
                    // add to project model after saving so it has DB id
                    projectController.addTask(t);
                    tasksmodel.addRow(new Object[]{t.getId(), name, sd, ed, deps, res});
                } else {
                    // project not saved yet: keep task in-memory and show placeholder id
                    projectController.addTask(t);
                    tasksmodel.addRow(new Object[]{"", name, sd, ed, deps, res});
                }
            }catch(Exception ex){ JOptionPane.showMessageDialog(this, "Invalid date format"); }
        });

        addResBtn.addActionListener(e ->{
            String name = JOptionPane.showInputDialog(this, "Resource name:");
            if(name==null || name.trim().isEmpty()) return;
            String alloc = JOptionPane.showInputDialog(this, "Allocation (1.0 = full):", "1.0");
            double a = 1.0;
            try{ a = Double.parseDouble(alloc); }catch(Exception ignored){}
            Resource r = new Resource(name, a);
            if(projectController.getProject().getId() > 0){
                r.setProjectId(projectController.getProject().getId());
                r.setId(0);
                ResourceController rc = new ResourceController(r, resourceDao);
                rc.saveResource();
                projectController.addResource(r);
                resourcesmodel.addRow(new Object[]{r.getId(), r.getName(), r.getAllocation()});
            } else {
                projectController.addResource(r);
                resourcesmodel.addRow(new Object[]{"", name, a});
            }
        });

        btnUploadTasks.addActionListener(e ->{
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                try{
                    List<Task> ts = FileUtils.loadTasksFromCSV(f);
                    for(Task t: ts){
                        if(projectController.getProject().getId() > 0) {
                            t.setProjectId(projectController.getProject().getId());
                            t.setId(0); // force insert
                            TaskController tc = new TaskController(t, taskDao);
                            tc.saveTask();
                            projectController.addTask(t);
                            tasksmodel.addRow(new Object[]{t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getDependencies(), t.getResources()});
                        } else {
                            projectController.addTask(t);
                            tasksmodel.addRow(new Object[]{"", t.getName(), t.getStart(), t.getEnd(), t.getDependencies(), t.getResources()});
                        }
                    }
                } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Load failed: "+ex.getMessage()); }
            }
        });

        btnUploadResources.addActionListener(e ->{
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                try{
                    List<Resource> rs = FileUtils.loadResourcesFromCSV(f);
                    for(Resource r: rs){
                        if(projectController.getProject().getId() > 0){
                            r.setProjectId(projectController.getProject().getId());
                            r.setId(0); // force insert
                            ResourceController rc = new ResourceController(r, resourceDao);
                            rc.saveResource();
                            projectController.addResource(r);
                            resourcesmodel.addRow(new Object[]{r.getId(), r.getName(), r.getAllocation()});
                        } else {
                            projectController.addResource(r);
                            resourcesmodel.addRow(new Object[]{"", r.getName(), r.getAllocation()});
                        }
                    }
                } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Load failed: "+ex.getMessage()); }
            }
        });

        analyze.addActionListener(e->{
            AnalysisPanel ap = new AnalysisPanel(projectController);
            ap.setVisible(true);
        });

        visualize.addActionListener(e->{
            JFrame g = new JFrame("Gantt View");
            g.setSize(1000,400);
            g.setLocationRelativeTo(this);
            g.add(new GanttPanel(projectController));
            g.setVisible(true);
        });

        btnNew.addActionListener(e->{
            projectController.getProject().clear();
            tasksmodel.setRowCount(0);
            resourcesmodel.setRowCount(0);
            projectTitle.setText("");
        });

        btnSave.addActionListener(e -> {
            projectController.getProject().setTitle(projectTitle.getText());
            projectController.saveProject();
            // After saving project we now have an id - if any tasks/resources were added earlier without projectId, update them now
            int pid = projectController.getProject().getId();
            if(pid > 0){
                // persist tasks that may not have been saved yet
                for(Task t : projectController.getProject().getTasks()){
                    if(t.getProjectId() == 0){
                        t.setProjectId(pid);
                        t.setId(0); // force insert
                        TaskController tc = new TaskController(t, taskDao);
                        tc.saveTask();
                    }
                }
                for(Resource r : projectController.getProject().getResources()){
                    if(r.getProjectId() == 0){
                        r.setProjectId(pid);
                        r.setId(0); // force insert
                        ResourceController rc = new ResourceController(r, resourceDao);
                        rc.saveResource();
                    }
                }
                // refresh UI tables to show assigned IDs
                tasksmodel.setRowCount(0);
                for(Task t : projectController.getProject().getTasks()){
                    tasksmodel.addRow(new Object[]{t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getDependencies(), t.getResources()});
                }
                resourcesmodel.setRowCount(0);
                for(Resource r : projectController.getProject().getResources()){
                    resourcesmodel.addRow(new Object[]{r.getId(), r.getName(), r.getAllocation()});
                }
            }
            JOptionPane.showMessageDialog(this, "Project saved.");
        });

        btnLoad.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(this, "Enter Project ID to load:");
            if(idStr != null && !idStr.trim().isEmpty()){
                try{
                    int id = Integer.parseInt(idStr.trim());
                    projectController.loadProject(id);
                    // Update UI
                    tasksmodel.setRowCount(0);
                    for(Task t : projectController.getProject().getTasks()){
                        tasksmodel.addRow(new Object[]{t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getDependencies(), t.getResources()});
                    }
                    resourcesmodel.setRowCount(0);
                    for(Resource r : projectController.getProject().getResources()){
                        resourcesmodel.addRow(new Object[]{r.getId(), r.getName(), r.getAllocation()});
                    }
                    projectTitle.setText(projectController.getProject().getTitle());
                    JOptionPane.showMessageDialog(this, "Project loaded.");
                }catch(NumberFormatException ex){
                    JOptionPane.showMessageDialog(this, "Invalid ID format.");
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this project?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                int id = projectController.getProject().getId();
                if(id > 0){
                    try {
                        projectController.deleteProject(id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    JOptionPane.showMessageDialog(this, "Project deleted.");
                    projectController.getProject().clear();
                    tasksmodel.setRowCount(0);
                    resourcesmodel.setRowCount(0);
                    projectTitle.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "No project loaded to delete.");
                }
            }
        });
    }
}
