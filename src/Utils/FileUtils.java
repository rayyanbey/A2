package Utils;

import Models.Resource;
import Models.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd");


    public static List<Task> loadTasksFromCSV(File f) throws IOException {
        List<Task> tasks = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                LocalDate s = LocalDate.parse(parts[2].trim(), DTF);
                LocalDate e = LocalDate.parse(parts[3].trim(), DTF);
                String deps = parts.length>4?parts[4].trim():"";
                String res = parts.length>5?parts[5].trim():"";
                tasks.add(new Task(id, name, s, e, deps, res));
            }
        }
        return tasks;
    }

    public static List<Resource> loadResourcesFromCSV(File f) throws IOException {
        List<Resource> rs = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                String name = p[0].trim();
                double alloc = p.length>1 && !p[1].isEmpty() ? Double.parseDouble(p[1].trim()) : 1.0;
                rs.add(new Resource(name, alloc));
            }
        }
        return rs;
    }
}

