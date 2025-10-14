package Views;

import Controllers.ProjectController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Optional;

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

}
