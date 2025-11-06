import Views.ProjectPanel;

import Utils.DBUtils;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        DBUtils.createTablesIfNotExist();

        JFrame frame = new JFrame("Project Planning");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100,700);
        frame.setLocationRelativeTo(null);

        ProjectPanel panel = new ProjectPanel();
        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}