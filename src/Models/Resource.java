package Models;

public class Resource {
    private String name;
    private double allocation; // 1.0 = full, 0.5 = half etc.

    public Resource(String name, double allocation){
        this.name = name; this.allocation = allocation;
    }
    public String getName(){return name;}
    public double getAllocation(){return allocation;}
}