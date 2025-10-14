package Views;

import Controllers.ProjectController;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AnalysisPanel extends JDialog {

    public AnalysisPanel(ProjectController controller){
        setTitle("Analysis");
        setModal(true);
        setSize(500,400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTextArea out = new JTextArea();
        out.setEditable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("Project start: ").append(controller.projectStart().orElse(null)).append("\n");
        sb.append("Project end: ").append(controller.projectEnd().orElse(null)).append("\n");
        sb.append("Duration (days): ").append(controller.projectDurationDays()).append("\n\n");
        sb.append("Overlapping tasks:\n");
        controller.overlappingTasksReport().forEach(s -> sb.append(" - ").append(s).append("\n"));
        sb.append("\nResource effort (days):\n");
        for(Map.Entry<String, Long> e : controller.resourceEffortDays().entrySet()){
            sb.append(String.format("%s : %d\n", e.getKey(), e.getValue()));
        }
        out.setText(sb.toString());
        add(new JScrollPane(out), BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e->dispose());
        add(close, BorderLayout.SOUTH);
    }
}
