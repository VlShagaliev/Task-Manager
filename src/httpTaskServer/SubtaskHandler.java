package httpTaskServer;

import com.sun.net.httpserver.HttpExchange;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.List;

class SubtaskHandler extends BaseHttpHandler implements HandleGetPostDelete {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        String[] path = getPath(exchange);
        String jsonResponse;
        int statusCode;
        if (path.length == 2) {
            List<Task> tasks = taskManager.getSubtasks();
            jsonResponse = gson.toJson(tasks);
            statusCode = 200;
        } else {
            int id = Integer.parseInt(path[2]);
            if (taskManager.checkIdSubtask(id)) {
                Task task = taskManager.getSubtask(id);
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
        String requestBody = readRequestBody(exchange);
        Subtask task = gson.fromJson(requestBody, Subtask.class);
        int statusCode = 201;
        String response;
        try {
            if (task.getId() == 0) {
                taskManager.addSubtask(task);
                response = String.format("Задача успешно добавлена! Id подзадачи: %d", task.getId());
            } else {
                taskManager.updateSubtask(task);
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
        taskManager.deleteSubtaskById(Integer.parseInt(path[2]));
        sendText(exchange, "Задача удалена!", 200);
    }

}
