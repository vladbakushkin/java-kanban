package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;

    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
    }

    private void handler(HttpExchange httpExchange) {
        try {
            System.out.println("\n/tasks");
            String path = httpExchange.getRequestURI().getPath().substring(7);

            switch (path) {
                case "task":
                    handleTask(httpExchange);
                    break;
                case "subtask":
                    handleSubtask(httpExchange);
                    break;
                case "epic":
                    handleEpic(httpExchange);
                    break;
                case "subtask/epic":
                    handleEpicSubtasks(httpExchange);
                    break;
                case "history":
                    handleHistory(httpExchange);
                    break;
                case "":
                    handleAllTasks(httpExchange);
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpExchange.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    private void handleTask(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        final String requestMethod = httpExchange.getRequestMethod();

        String response;
        int taskId;

        switch (requestMethod) {
            case "GET":
                if (query == null) {
                    response = gson.toJson(taskManager.getTasks());
                    System.out.println("Получили все задачи");
                    sendText(httpExchange, response);
                    return;
                }
                taskId = Integer.parseInt(query.substring(3));
                response = gson.toJson(taskManager.getTask(taskId));
                System.out.println("Вернули задачу, taskId = " + taskId);
                sendText(httpExchange, response);
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.deleteTasks();
                    System.out.println("Удалили все задачи");
                    httpExchange.sendResponseHeaders(200, 0);
                    return;
                }
                taskId = Integer.parseInt(query.substring(3));
                taskManager.deleteTask(taskId);
                System.out.println("Удалили задачу, taskId = " + taskId);
                httpExchange.sendResponseHeaders(200, 0);
                break;
            case "POST":
                String jsonTask = readText(httpExchange);
                Task task = gson.fromJson(jsonTask, Task.class);
                if (task.getId() > 0) {
                    final int id = taskManager.updateTask(task);
                    if (id > 0) {
                        System.out.println("Обновили задачу, taskId = " + id);
                        response = gson.toJson(task);
                        sendText(httpExchange, response);
                    }
                } else {
                    final int id = taskManager.createTask(task);
                    if (id > 0) {
                        System.out.println("Создали задачу, taskId = " + id);
                        response = gson.toJson(task);
                        sendText(httpExchange, response);
                    }
                }
                break;
        }
    }

    private void handleSubtask(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        final String requestMethod = httpExchange.getRequestMethod();

        String response;
        int subtaskId;

        switch (requestMethod) {
            case "GET":
                if (query == null) {
                    response = gson.toJson(taskManager.getSubtasks());
                    System.out.println("Получили все подзадачи");
                    sendText(httpExchange, response);
                    return;
                }
                subtaskId = Integer.parseInt(query.substring(3));
                response = gson.toJson(taskManager.getSubtask(subtaskId));
                System.out.println("Вернули подзадачу, subtaskId = " + subtaskId);
                sendText(httpExchange, response);
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.deleteSubtasks();
                    System.out.println("Удалили все подзадачи");
                    httpExchange.sendResponseHeaders(200, 0);
                    return;
                }
                subtaskId = Integer.parseInt(query.substring(3));
                taskManager.deleteSubtask(subtaskId);
                System.out.println("Удалили подзадачу, subtaskId = " + subtaskId);
                httpExchange.sendResponseHeaders(200, 0);
                break;
            case "POST":
                String jsonSubtask = readText(httpExchange);
                Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
                if (subtask.getId() > 0) {
                    final int id = taskManager.updateSubtask(subtask);
                    if (id > 0) {
                        taskManager.updateSubtask(subtask);
                        System.out.println("Обновили подзадачу, subtaskId = " + id);
                        response = gson.toJson(subtask);
                        sendText(httpExchange, response);
                    }
                } else {
                    final int id = taskManager.createSubtask(subtask);
                    if (id > 0) {
                        System.out.println("Создали подзадачу, subtaskId = " + id);
                        response = gson.toJson(subtask);
                        sendText(httpExchange, response);
                    }
                }
                break;
        }
    }

    private void handleEpic(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        final String requestMethod = httpExchange.getRequestMethod();

        String response;
        int epicId;

        switch (requestMethod) {
            case "GET":
                if (query == null) {
                    response = gson.toJson(taskManager.getEpics());
                    System.out.println("Получили все эпики");
                    sendText(httpExchange, response);
                    return;
                }
                epicId = Integer.parseInt(query.substring(3));
                response = gson.toJson(taskManager.getEpic(epicId));
                System.out.println("Вернули эпик, epicId = " + epicId);
                sendText(httpExchange, response);
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.deleteEpics();
                    System.out.println("Удалили все эпики");
                    httpExchange.sendResponseHeaders(200, 0);
                    return;
                }
                epicId = Integer.parseInt(query.substring(3));
                taskManager.deleteEpic(epicId);
                System.out.println("Удалили эпик, epicId = " + epicId);
                httpExchange.sendResponseHeaders(200, 0);
                break;
            case "POST":
                String jsonEpic = readText(httpExchange);
                Epic epic = gson.fromJson(jsonEpic, Epic.class);
                if (epic.getId() > 0) {
                    final int id = taskManager.updateEpic(epic);
                    if (id > 0) {
                        System.out.println("Обновили эпик, epicId = " + epic.getId());
                        response = gson.toJson(epic);
                        sendText(httpExchange, response);
                    }
                } else {
                    final int id = taskManager.createEpic(epic);
                    if (id > 0) {
                        System.out.println("Создали эпик, epicId = " + epic.getId());
                        response = gson.toJson(epic);
                        sendText(httpExchange, response);
                    }
                }
                break;
        }
    }

    private void handleEpicSubtasks(HttpExchange httpExchange) throws IOException {
        if (!httpExchange.getRequestMethod().equals("GET")) {
            System.out.println("/ ждет GET-запрос, а получил: " + httpExchange.getRequestMethod());
            httpExchange.sendResponseHeaders(405, 0);
        }
        String query = httpExchange.getRequestURI().getQuery();
        final int epicId = Integer.parseInt(query.substring(3));
        String response = gson.toJson(taskManager.getSubtasksFromEpic(epicId));
        System.out.println("Получили все subtasks эпика, epicId = " + epicId);
        sendText(httpExchange, response);
    }

    private void handleHistory(HttpExchange httpExchange) throws IOException {
        if (!httpExchange.getRequestMethod().equals("GET")) {
            System.out.println("/ ждет GET-запрос, а получил: " + httpExchange.getRequestMethod());
            httpExchange.sendResponseHeaders(405, 0);
        }
        String response = gson.toJson(taskManager.getHistory());
        System.out.println("Получили историю задач.");
        sendText(httpExchange, response);
    }

    private void handleAllTasks(HttpExchange httpExchange) throws IOException {
        if (!httpExchange.getRequestMethod().equals("GET")) {
            System.out.println("/ ждет GET-запрос, а получил: " + httpExchange.getRequestMethod());
            httpExchange.sendResponseHeaders(405, 0);
        }
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        System.out.println("Получили все задачи.");
        sendText(httpExchange, response);
    }
}
