package httpTaskServer;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

class EpicHandler extends BaseHttpHandler implements HandleGetPostDelete {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        String[] path = getPath(exchange);
        String jsonResponse;
        int statusCode;
        if (path.length == 2) {
            List<Task> tasks = taskManager.getEpics();
            jsonResponse = gson.toJson(tasks);
            statusCode = 200;
        } else if (path.length == 3) {
            int id = Integer.parseInt(path[2]);
            if (taskManager.checkIdInEpic(id)) {
                Task task = taskManager.getEpic(id);
                jsonResponse = gson.toJson(task);
                statusCode = 200;
            } else {
                sendNotFound(exchange);
                return;
            }
        } else {
            int id = Integer.parseInt(path[2]);
            if (taskManager.checkIdInEpic(id)) {
                HashMap<Integer, Subtask> subtaskHashMap = taskManager.getEpic(id).getSubtaskHashMap();
                jsonResponse = gson.toJson(subtaskHashMap);
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
        Epic task = gson.fromJson(requestBody, Epic.class);
        int statusCode = 201;
        String response;
        if (task.getId() == 0) {
            taskManager.addEpic(task);
            response = String.format("Задача успешно добавлена! Id Эпика: %d", task.getId());
        } else {
            taskManager.updateEpic(task);
            response = "Задача успешно обновлена!";
        }
        sendText(exchange, response, statusCode);
    }

    @Override
    public void handleDelete(HttpExchange exchange) throws IOException {
        String[] path = getPath(exchange);
        taskManager.deleteEpicById(Integer.parseInt(path[2]));
        sendText(exchange, "Задача удалена!", 200);
    }
}
