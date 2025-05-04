import com.google.gson.Gson;
import httpTaskServer.HttpTaskServer;
import managers.Managers;
import managers.TaskManager;
import model.Epic;
import model.Progress;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import httpTaskServer.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = (new Managers()).getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = BaseHttpHandler.gson;

    public HttpTaskManagerTasksTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void tesGetTasksList() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Progress.NEW);
        manager.addTask(task);
        task = new Task("Test 3", "Testing task 3",
                Progress.IN_PROGRESS);
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void tesGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Progress.NEW);
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Progress.NEW);
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void tesUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Progress.NEW);
        manager.addTask(task);
        task = new Task("Test 2", "Testing task 2",
                Progress.IN_PROGRESS);
        task.setId(1);
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
    }

    @Test
    public void tesNotFoundTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Progress.NEW);
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testAddTaskWithIntersection() throws IOException, InterruptedException {
        // создаём задачу
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime localDateTime = LocalDateTime.now();
        Task task = new Task("Test 1", "Testing task 1",
                Progress.NEW, duration, localDateTime);
        manager.addTask(task);

        localDateTime = localDateTime.plusMinutes(10);
        task = new Task("Test 2", "Testing task 2",
                Progress.NEW, duration, localDateTime);

        // конвертируем task "Test 2" в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Progress.NEW);
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2 sub", "Testing task 2 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);
        epic = new Epic("Test 3", "Testing task 3");
        // конвертируем её в JSON
        manager.addEpic(epic);
        subtask = new Subtask("Test 3 sub", "Testing task 3 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2 sub", "Testing task 2 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);
        epic = new Epic("Test 3", "Testing task 3");
        // конвертируем её в JSON
        manager.addEpic(epic);
        subtask = new Subtask("Test 3 sub", "Testing task 3 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2 sub", "Testing task 2 sub", epic.getId(), Progress.NEW);
        String taskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> subtaskList = manager.getSubtasks();

        assertNotNull(subtaskList, "Задачи не возвращаются");
        assertEquals(1, subtaskList.size(), "Некорректное количество задач");
        assertEquals("Test 2 sub", subtaskList.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2 sub", "Testint task 2 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(30));
        String taskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
    }

    @Test
    public void testNotFoundSubtaskById() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2 sub", "Testing task 2 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);
        epic = new Epic("Test 3", "Testing task 3");
        // конвертируем её в JSON
        manager.addEpic(epic);
        subtask = new Subtask("Test 3 sub", "Testing task 3 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testAddSubtaskWithIntersection() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2 sub", "Testing task 2 sub", epic.getId(), Progress.NEW);
        LocalDateTime localDateTime = LocalDateTime.now();
        subtask.setStartTime(localDateTime);
        subtask.setDuration(Duration.ofMinutes(30));
        manager.addSubtask(subtask);

        subtask = new Subtask("Test 3 sub", "Testing task 3 sub", epic.getId(), Progress.IN_PROGRESS);
        subtask.setStartTime(localDateTime.plusMinutes(10));
        subtask.setDuration(Duration.ofMinutes(30));
        String taskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2 sub", "Testint task 2 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        epic = new Epic("Test 3", "Testing task 3");
        // конвертируем её в JSON
        manager.addEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testNotFoundEpicById() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testEpicSubtasksByIdEpic() throws IOException, InterruptedException{
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2 sub", "Testing task 2 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testNotFoundEpicSubtasksByIdEpic() throws IOException, InterruptedException{
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Test 2 sub", "Testing task 2 sub", epic.getId(), Progress.NEW);
        manager.addSubtask(subtask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> epicsList = manager.getEpics();

        assertNotNull(epicsList, "Задачи не возвращаются");
        assertEquals(1, epicsList.size(), "Некорректное количество задач");
        assertEquals("Test 2", epicsList.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        manager.addEpic(epic);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Progress.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        // конвертируем её в JSON
        manager.addTask(task);
        manager.printById(1);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 1", "Testing task 1",
                Progress.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        // конвертируем её в JSON
        manager.addTask(task);
        task = new Task("Test 2", "Testing task 2",
                Progress.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(40));
        // конвертируем её в JSON
        manager.addTask(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

}