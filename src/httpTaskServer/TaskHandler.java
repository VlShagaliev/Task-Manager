package httpTaskServer;

import com.sun.net.httpserver.HttpExchange;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

class TaskHandler extends BaseHttpHandler implements HandleGetPostDelete {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        String[] path = getPath(exchange);
        String jsonResponse;
        int statusCode;
        if (path.length == 2) {
            List<Task> tasks = taskManager.getTasks();
            jsonResponse = gson.toJson(tasks);
            statusCode = 200;
        } else {
            int id = Integer.parseInt(path[2]);
            if (taskManager.checkIdInTask(id)) {
                Task task = taskManager.getTask(id);
                jsonResponse = gson.toJson(task);
                statusCode = 200;
            } else {
                sendNotFound(exchange);
                return;
            }
        }
        sendText(exchange, jsonResponse, statusCode);
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {

        String json = readRequestBody(exchange);
        Task task = gson.fromJson(json, Task.class);
        int statusCode = 201;
        String response;
        try {
            if (task.getId() == 0) {
                taskManager.addTask(task);
                if (taskManager.checkIdInTask(task.getId())) {
                    response = String.format("Задача успешно добавлена! Id задачи: %d", task.getId());
                } else {
                    sendHasInteractions(exchange);
                    return;
                }
            } else {
                taskManager.updateTask(task);
                response = "Задача успешно обновлена!";
            }
        } catch (InMemoryTaskManager.TaskValidException exception) {
            response = exception.getMessage();
            statusCode = 406;
        }
        sendText(exchange, response, statusCode);
    }

    @Override
    public void handleDelete(HttpExchange exchange) throws IOException {
        String[] path = getPath(exchange);
        taskManager.deleteTaskById(Integer.parseInt(path[2]));
        sendText(exchange, "Задача удалена!", 200);
    }

}
