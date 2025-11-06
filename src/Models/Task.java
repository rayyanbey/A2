package Models;

import java.time.LocalDate;

public class Task {
    private int id;
    private int projectId;
    private String name;
    private LocalDate start;
    private LocalDate end;
    private String dependencies;
    private String resources;

    public Task() {}
    public Task(int id, int projectId, String name, LocalDate start, LocalDate end, String dependencies, String resources) {
        this.id = id; this.projectId = projectId; this.name = name; this.start = start; this.end = end;
        this.dependencies = dependencies; this.resources = resources;
    }

    // Overloaded constructor kept for backward compatibility with tests and callers that don't set projectId
    public Task(int id, String name, LocalDate start, LocalDate end, String dependencies, String resources) {
        this(id, 0, name, start, end, dependencies, resources);
    }

    // getters + setters
    public int getId(){return id;}
    public int getProjectId(){return projectId;}
    public String getName(){return name;}
    public LocalDate getStart(){return start;}
    public LocalDate getEnd(){return end;}
    public String getDependencies(){return dependencies;}
    public String getResources(){return resources;}
    public void setId(int id){this.id = id;}
    public void setProjectId(int projectId){this.projectId = projectId;}
    public void setName(String name){this.name = name;}
    public void setStart(LocalDate start){this.start = start;}
    public void setEnd(LocalDate end){this.end = end;}
    public void setDependencies(String dependencies){this.dependencies = dependencies;}
    public void setResources(String resources){this.resources = resources;}
}