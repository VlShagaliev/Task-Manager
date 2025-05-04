package httpTaskServer;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface HandleGetPostDelete{

    void handleGet(HttpExchange exchange) throws IOException;

    void handlePost(HttpExchange exchange) throws IOException;

    void handleDelete(HttpExchange exchange) throws IOException;
}
