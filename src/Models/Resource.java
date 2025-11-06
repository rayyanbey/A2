package Models;

public class Resource {
    private int id;
    private int projectId;
    private String name;
    private double allocation; // 1.0 = full, 0.5 = half etc.

    public Resource(int id, int projectId, String name, double allocation){
        this.id = id;
        this.projectId = projectId;
        this.name = name; this.allocation = allocation;
    }

    public Resource(String name, double allocation){
        this(0, 0, name, allocation);
    }

    public int getId(){return id;}
    public int getProjectId(){return projectId;}
    public String getName(){return name;}
    public double getAllocation(){return allocation;}

    public void setId(int id){this.id = id;}
    public void setProjectId(int projectId){this.projectId = projectId;}
    public void setName(String name){this.name = name;}
    public void setAllocation(double allocation){this.allocation = allocation;}
}