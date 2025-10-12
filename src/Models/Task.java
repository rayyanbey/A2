package Models;

import java.time.LocalDate;

public class Task {
    private int id;
    private String name;
    private LocalDate start;
    private LocalDate end;
    private String dependencies;
    private String resources;

    public Task(int id, String name, LocalDate start, LocalDate end, String dependencies, String resources) {
        this.id = id; this.name = name; this.start = start; this.end = end;
        this.dependencies = dependencies; this.resources = resources;
    }

    // getters + setters
    public int getId(){return id;}
    public String getName(){return name;}
    public LocalDate getStart(){return start;}
    public LocalDate getEnd(){return end;}
    public String getDependencies(){return dependencies;}
    public String getResources(){return resources;}
}