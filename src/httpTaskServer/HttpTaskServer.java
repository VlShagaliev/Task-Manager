package httpTaskServer;

import com.sun.net.httpserver.HttpServer;
import fileBackedTaskManager.FileBackedTaskManager;
import managers.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static TaskManager taskManager;
    private static File file = new File("Backup.csv");
    private static HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        HttpTaskServer.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        HttpTaskServer server = new HttpTaskServer(taskManagerFromFile);
        server.start();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
        server.start();
    }

    public void stop(){
        server.stop(0);
    }
}


