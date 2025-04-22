import fileBackedTaskManager.FileBackedTaskManager;
import model.Progress;
import model.Task;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest {
    @Test
    void testCreateFile() throws IOException, FileBackedTaskManager.ManagerSaveException {
        File file = File.createTempFile("Backup","csv");
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task = new Task("1", "1", Progress.NEW);
        task.setId(1);
        taskManager.addTask(task);
        task = new Task("2", "2", Progress.NEW);
        task.setId(2);
        taskManager.addTask(task);
        Assertions.assertTrue(file.isAbsolute());
    }

    @Test
    void testFileIsNotEmpty() throws IOException, FileBackedTaskManager.ManagerSaveException {
        File file = File.createTempFile("Backup","csv");
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task = new Task("1", "1", Progress.NEW);
        task.setId(1);
        taskManager.addTask(task);
        task = new Task("2", "2", Progress.NEW);
        task.setId(2);
        taskManager.addTask(task);
        Assertions.assertTrue(file.length() > 0);
    }

    @Test
    void testFileIsEmpty() throws IOException {
        File file = File.createTempFile("Backup","csv");
        Assertions.assertTrue(file.length() == 0);
    }

    @Test
    void testLoadFromFile() throws IOException, FileBackedTaskManager.ManagerSaveException {
        File file = File.createTempFile("Backup","csv");
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task = new Task("1", "1", Progress.NEW);
        task.setId(1);
        taskManager.addTask(task);
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        Task taskFromFile = null;
        try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(fileReader)){
            reader.readLine();
            taskFromFile = fileBackedTaskManager.fromString(reader.readLine());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        }
        Assertions.assertEquals(taskFromFile,task);
    }

    @Test
    void testCheckDateTimeFromFile() throws IOException {
        File file = File.createTempFile("Backup","csv");
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task = new Task("1", "1", Progress.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setId(1);
        taskManager.addTask(task);
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        Task taskFromFile = null;
        try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(fileReader)){
            reader.readLine();
            taskFromFile = fileBackedTaskManager.fromString(reader.readLine());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        }
        Assertions.assertTrue(taskFromFile.getStartTime() != null);
    }
}
