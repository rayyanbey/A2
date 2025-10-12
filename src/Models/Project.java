package Models;

import java.util.*;
public class Project {
    private String title;
    private List<Task> tasks = new ArrayList<>();
    private List<Resource> resources = new ArrayList<>();

    public Project(String title){ this.title = title; }
    public String getTitle(){return title;}
    public List<Task> getTasks(){return tasks;}
    public List<Resource> getResources(){return resources;}

    public void addTask(Task t){ tasks.add(t); }
    public void addResource(Resource r){ resources.add(r); }
    public void clear(){ tasks.clear(); resources.clear(); title = ""; }
}