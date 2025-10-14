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

    // Helper method to extract date from datetime string
    private static String extractDateFromDateTime(String dateTimeStr) {
        // Remove any time components (+ or - followed by time)
        String dateOnly = dateTimeStr.replaceAll("[+-]\\d{4}", "");
        return dateOnly.trim();
    }

    public static List<Task> loadTasksFromCSV(File f) throws IOException {
        List<Task> tasks = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();

                // Extract date portion and parse
                String startDateStr = extractDateFromDateTime(parts[2].trim());
                String endDateStr = extractDateFromDateTime(parts[3].trim());

                LocalDate s = LocalDate.parse(startDateStr, DTF);
                LocalDate e = LocalDate.parse(endDateStr, DTF);

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

                // For resources with task-specific allocations, use average allocation or default to 1.0
                double totalAllocation = 0.0;
                int allocationCount = 0;

                // Parse task-specific allocations (format: taskId:percentage)
                for(int i = 1; i < p.length; i++){
                    String allocation = p[i].trim();
                    if(!allocation.isEmpty() && allocation.contains(":")){
                        try{
                            String[] allocParts = allocation.split(":");
                            if(allocParts.length == 2){
                                double allocValue = Double.parseDouble(allocParts[1].trim()) / 100.0; // Convert percentage to decimal
                                totalAllocation += allocValue;
                                allocationCount++;
                            }
                        }catch(NumberFormatException ignored){}
                    }
                }

                // Use average allocation or default to 1.0 if no valid allocations found
                double avgAllocation = allocationCount > 0 ? totalAllocation / allocationCount : 1.0;
                rs.add(new Resource(name, avgAllocation));
            }
        }
        return rs;
    }
}
