package Models;

import java.io.Serializable;
import java.util.*;
public class Project implements Serializable {
    private int id;
    private String title;
    private List<Task> tasks = new ArrayList<>();
    private List<Resource> resources = new ArrayList<>();

    public Project(int id, String title){
        this.id = id;
        this.title = title;
    }
    public int getId() { return id; }
    public String getTitle(){return title;}
    public List<Task> getTasks(){return tasks;}
    public List<Resource> getResources(){return resources;}

    public void addTask(Task t){ tasks.add(t); }
    public void addResource(Resource r){ resources.add(r); }
    public void clear(){ tasks.clear(); resources.clear(); title = ""; id = 0; }

    public void addAllTasks(List<Task> tasks) {
        this.tasks.addAll(tasks);
    }

    public void addAllResources(List<Resource> resources) {
        this.resources.addAll(resources);
    }

    public void setTitle(String title){ this.title = title; }
    public void setId(int id) { this.id = id; }
}