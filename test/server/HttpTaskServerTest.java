package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private KVServer kvServer;
    private final Gson gson = Managers.getGson();

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        TaskManager taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void test1_getAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        taskServer.getTaskManager().createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Задачи не возвращаются.");
        assertEquals(1, actual.size(), "Неверное количество задач.");
        assertEquals(task1, actual.get(0), "Задачи не совпадают.");
    }

    @Test
    void test2_getTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        taskServer.getTaskManager().createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Задачи не возвращаются.");
        assertEquals(task1, actual, "Задачи не совпадают.");
    }

    @Test
    void test3_addTask() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        String json = gson.toJson(task1);
        task1.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Задачи не возвращаются.");
        assertEquals(task1, actual, "Задачи не совпадают.");
    }

    @Test
    void test4_updateTask() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        taskServer.getTaskManager().createTask(task1);

        task1.setName("newName");
        String json = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Задачи не возвращаются.");
        assertEquals("newName", actual.getName(), "Имена задач не совпадают.");
    }

    @Test
    void test5_deleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        taskServer.getTaskManager().createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        assertTrue(taskServer.getTaskManager().getTasks().isEmpty(), "Список задач не пустой");
    }

    @Test
    void test6_deleteTasks() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        taskServer.getTaskManager().createTask(task1);
        Task task2 = new Task("task2", "task2", TaskStatus.NEW);
        taskServer.getTaskManager().createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        assertTrue(taskServer.getTaskManager().getTasks().isEmpty(), "Список задач не пустой");
    }

    @Test
    void test7_getAllSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Подзадачи не возвращаются.");
        assertEquals(2, actual.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, actual.get(0), "Подзадачи не совпадают.");
        assertEquals(subtask2, actual.get(1), "Подзадачи не совпадают.");
    }

    @Test
    void test8_getSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<Subtask>() {
        }.getType();
        Subtask actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Подзадачи не возвращаются.");
        assertEquals(subtask1, actual, "Подзадачи не совпадают.");
    }

    @Test
    void test9_addSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        String json = gson.toJson(subtask1);
        subtask1.setId(2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<Subtask>() {
        }.getType();
        Subtask actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Подзадачи не возвращаются.");
        assertEquals(subtask1, actual, "Подзадачи не совпадают.");
    }

    @Test
    void test10_updateSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask1);

        subtask1.setName("newNameSubtask");
        String json = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<Subtask>() {
        }.getType();
        Subtask actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Подзадачи не возвращаются.");
        assertEquals("newNameSubtask", actual.getName(), "Имена подзадач не совпадают.");
    }

    @Test
    void test11_deleteSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        assertFalse(taskServer.getTaskManager().getSubtasks().contains(subtask1),
                "Список подзадач содержит subtask1");
    }

    @Test
    void test12_deleteSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        assertTrue(taskServer.getTaskManager().getSubtasks().isEmpty(), "Список подзадач не пустой");
    }

    @Test
    void test13_getAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        taskServer.getTaskManager().createEpic(epic1);
        Epic epic2 = new Epic("epic2", "epic2", TaskStatus.NEW);
        taskServer.getTaskManager().createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Эпики не возвращаются.");
        assertEquals(2, actual.size(), "Неверное количество эпиков.");
        assertEquals(epic1, actual.get(0), "Эпики не совпадают.");
        assertEquals(epic2, actual.get(1), "Эпики не совпадают.");
    }

    @Test
    void test14_getEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        taskServer.getTaskManager().createEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<Epic>() {
        }.getType();
        Epic actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Эпики не возвращаются.");
        assertEquals(epic1, actual, "Эпики не совпадают.");
    }

    @Test
    void test15_addEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);

        String json = gson.toJson(epic1);
        epic1.setId(1);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<Epic>() {
        }.getType();
        Epic actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Эпики не возвращаются.");
        assertEquals(epic1, actual, "Эпики не совпадают.");
    }

    @Test
    void test16_updateEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        taskServer.getTaskManager().createEpic(epic1);

        epic1.setName("newNameEpic");
        String json = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<Epic>() {
        }.getType();
        Epic actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Эпики не возвращаются.");
        assertEquals("newNameEpic", actual.getName(), "Имена эпиков не совпадают.");
    }

    @Test
    void test17_deleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask2);
        Epic epic2 = new Epic("epic2", "epic2", TaskStatus.NEW);
        taskServer.getTaskManager().createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        assertFalse(taskServer.getTaskManager().getEpics().contains(epic1),
                "Список эпиков содержит epic1");
        assertTrue(taskServer.getTaskManager().getSubtasks().isEmpty(), "Список подзадач эпика не пустой");
    }

    @Test
    void test18_deleteEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask2);
        Epic epic2 = new Epic("epic2", "epic2", TaskStatus.NEW);
        taskServer.getTaskManager().createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        assertTrue(taskServer.getTaskManager().getEpics().isEmpty(), "Список эпиков не пустой");
    }

    @Test
    void test19_getEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        ArrayList<Subtask> actual = gson.fromJson(response.body(), taskType);

        assertEquals(subtask1, actual.get(0), "Подзадачи эпика не равны");
        assertEquals(subtask2, actual.get(1), "Подзадачи эпика не равны");
    }

    @Test
    void test20_getHistory() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        final int subtask1Id = taskServer.getTaskManager().createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epic1Id);
        taskServer.getTaskManager().createSubtask(subtask2);

        taskServer.getTaskManager().getEpic(epic1Id);
        taskServer.getTaskManager().getSubtask(subtask1Id);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> actual = gson.fromJson(response.body(), taskType);

        assertEquals(2, actual.size(), "Неверное количество задач в истории");
        assertEquals(epic1Id, actual.get(0).getId(), "Неправильный порядок задач в истории");
        assertEquals(subtask1Id, actual.get(1).getId(), "Неправильный порядок задач в истории");
    }

    @Test
    void test21_getTasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskServer.getTaskManager().createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id,
                15, LocalDateTime.now().plusMinutes(30));
        final int subtask1Id = taskServer.getTaskManager().createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epic1Id,
                15, LocalDateTime.now());
        final int subtask2Id = taskServer.getTaskManager().createSubtask(subtask2);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код статуса.");

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> actual = gson.fromJson(response.body(), taskType);

        assertEquals(3, actual.size(), "Неверное количество задач в списке");
        assertEquals(epic1Id, actual.get(0).getId(), "Неправильный порядок задач в списке");
        assertEquals(subtask2Id, actual.get(1).getId(), "Неправильный порядок задач в списке");
        assertEquals(subtask1Id, actual.get(2).getId(), "Неправильный порядок задач в списке");
    }
}