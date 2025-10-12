package Controllers;

import Models.Project;
import Models.Resource;
import Models.Task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ProjectController {
    private final Project project;

    public ProjectController(Project project){
        this.project = project;
    }

    public Project getProject(){return project;}

    public void addTask(Task task){
        project.addTask(task);
    }

    public  void addResource(Resource resource){
        project.addResource(resource);
    }

    public Optional<LocalDate> projectStart(){
        return project.getTasks().stream().map(Task::getStart).min(LocalDate::compareTo);
    }

    public Optional<LocalDate> projectEnd(){
        return project.getTasks().stream().map(Task::getEnd).max(LocalDate::compareTo);
    }

    public long projectDurationDays(){
        Optional<LocalDate> s = projectStart();
        Optional<LocalDate> e = projectEnd();
        if(s.isPresent() && e.isPresent()){
            return ChronoUnit.DAYS.between(s.get(), e.get()) + 1;
        } else return 0;
    }

    public List<String> overlappingTasksReport(){
        List<Task> tasks = project.getTasks();
        List<String> overlaps = new ArrayList<>();
        for(int i=0;i<tasks.size();i++){
            for(int j=i+1;j<tasks.size();j++){
                Task a = tasks.get(i), b = tasks.get(j);
                if(!(a.getEnd().isBefore(b.getStart()) || b.getEnd().isBefore(a.getStart()))){
                    overlaps.add(String.format("Task %d (%s) overlaps Task %d (%s)",
                            a.getId(), a.getName(), b.getId(), b.getName()));
                }
            }
        }
        return overlaps;
    }

    public Map<String, Long> resourceEffortDays(){
        Map<String, Long> map = new HashMap<>();
        for(Task t : project.getTasks()){
            long dur = java.time.temporal.ChronoUnit.DAYS.between(t.getStart(), t.getEnd()) + 1;
            String[] res = t.getResources().split("\\s*,\\s*");
            for(String r : res){
                if(r.trim().isEmpty()) continue;
                String name = r.replace("*","").trim();
                map.put(name, map.getOrDefault(name, 0L) + dur);
            }
        }
        return map;
    }


}
