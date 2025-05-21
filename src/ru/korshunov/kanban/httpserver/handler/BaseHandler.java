package ru.korshunov.kanban.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class BaseHandler {

    protected String[] getPathParts(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        return path.split("/");
    }

    protected void responseOk(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void responseCode201(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    protected void responseCode201(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(201, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void responseNotFound(HttpExchange exchange) throws IOException {
        byte[] response = "Ресурс не найден".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void responseHasAnIntersection(HttpExchange exchange, String nameTask) throws IOException {
        byte[] response = ("Задача '" + nameTask + "' пересекается с существующими задачами").getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

}
