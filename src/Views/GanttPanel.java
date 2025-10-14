package Views;

import Controllers.ProjectController;
import Models.Task;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class GanttPanel extends JPanel {
    private final ProjectController controller;
    private LocalDate start;
    private long totalDays;

    public GanttPanel(ProjectController controller) {
        this.controller = controller;
        this.setPreferredSize(new Dimension(1000,300));
        computeRange();
    }

    private void computeRange(){
        Optional<LocalDate> s = controller.projectStart();
        Optional<LocalDate> e = controller.projectEnd();
        if(s.isPresent() && e.isPresent()){
            start = s.get();
            totalDays = java.time.temporal.ChronoUnit.DAYS.between(start, e.get()) + 1;
        } else {
            start = LocalDate.now();
            totalDays = 1;
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        computeRange();
        Graphics2D g2 = (Graphics2D) g;
        List<Task> tasks = controller.getProject().getTasks();
        int top = 20;
        int rowHeight = 30;
        int left = 150;
        int width = Math.max(600,(int) totalDays * 4);

        g2.drawString("Task",10,15);

        for(int i = 0; i < totalDays; i++){
            int x = left + i*4;
            if(i % 5 == 0) g2.drawString(String.valueOf(start.plusDays(i).getDayOfMonth()), x, 15);
            g2.drawLine(x, 18, x, 18 + tasks.size()*rowHeight + 10);
        }

        for(int i=0;i<tasks.size();i++){
            Task t = tasks.get(i);
            long sOff = java.time.temporal.ChronoUnit.DAYS.between(start, t.getStart());
            long dur = java.time.temporal.ChronoUnit.DAYS.between(t.getStart(), t.getEnd()) + 1;
            int x = left + (int)sOff * 4;
            int y = top + i * rowHeight;
            int w = (int)dur * 4;
            g2.setColor(new Color(80, 140, 220));
            g2.fillRect(x, y, w, rowHeight-8);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, w, rowHeight-8);
            g2.drawString(t.getId() + " - " + t.getName(), 10, y + rowHeight/2);
        }
    }

}
