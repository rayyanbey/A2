package DAOs;

import Models.Project;

import java.io.*;

public class FileProjectDAO implements ProjectDAO {
    private static final String FILE_PATH = "project.ser";

    @Override
    public void save(Project project) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(project);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Project load(int id) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Project) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void update(Project project) {
        save(project);
    }

    @Override
    public void delete(int id) {
        new File(FILE_PATH).delete();
    }

    @Override
    public int getNextId() {
        return 1; // For file, always 1
    }
}
