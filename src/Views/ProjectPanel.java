package Views;

import Controllers.ProjectController;
import Models.Project;
import Models.Resource;
import Models.Task;
import Utils.FileUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
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

        addTaskBtn.addActionListener(e ->{
            int id = tasksmodel.getRowCount();
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
                Task t = new Task(id, name, sd, ed, deps==null?"":deps, res==null?"":res);
                projectController.addTask(t);
                tasksmodel.addRow(new Object[]{id, name, sd, ed, deps, res});
            }catch(Exception ex){ JOptionPane.showMessageDialog(this, "Invalid date format"); }
        });

        addResBtn.addActionListener(e ->{
            String name = JOptionPane.showInputDialog(this, "Resource name:");
            if(name==null || name.trim().isEmpty()) return;
            String alloc = JOptionPane.showInputDialog(this, "Allocation (1.0 = full):", "1.0");
            double a = 1.0;
            try{ a = Double.parseDouble(alloc); }catch(Exception ignored){}
            Resource r = new Resource(name, a);
            projectController.addResource(r);
            resourcesmodel.addRow(new Object[]{name, a});
        });

        btnUploadTasks.addActionListener(e ->{
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                try{
                    List<Task> ts = FileUtils.loadTasksFromCSV(f);
                    for(Task t: ts){ projectController.addTask(t);
                        tasksmodel.addRow(new Object[]{t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getDependencies(), t.getResources()});
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
                    for(Resource r: rs){ projectController.addResource(r);
                        resourcesmodel.addRow(new Object[]{r.getName(), r.getAllocation()});
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


    }
}
