package httpTaskServer;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.io.IOException;

class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                // Логика получения задач
                handleGet(exchange);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange,response,200);
    }

    @Override
    public void handlePost(HttpExchange exchange) {

    }

    @Override
    public void handleDelete(HttpExchange exchange) {

    }
}
