package manager.web;

import manager.Managers;
import manager.TaskManagerTest;
import manager.exception.ManagerLoadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new KVServer();
        server.start();
        taskManager = (HttpTaskManager) Managers.getDefault();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testHttpTM1_loadFromServer() throws URISyntaxException {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        final int task1Id = taskManager.createTask(task1);
        Task task2 = new Task("task2", "task2", TaskStatus.IN_PROGRESS);
        final int task2Id = taskManager.createTask(task2);

        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epic1Id = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epic1Id);
        final int subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epic1Id);
        final int subtask2Id = taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("epic2", "epic2", TaskStatus.NEW);
        final int epic2Id = taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("subtask3", "subtask3", TaskStatus.NEW, epic2Id);
        taskManager.createSubtask(subtask3);

        taskManager.getTask(task1Id);
        taskManager.getEpic(epic1Id);
        taskManager.getSubtask(subtask1Id);

        taskManager.getHistory();

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:8078");

        List<Task> tasks = httpTaskManager.getTasks();
        List<Epic> epics = httpTaskManager.getEpics();
        List<Subtask> subtasks = httpTaskManager.getSubtasks();

        List<Task> history = httpTaskManager.getHistory();

        assertEquals(2, tasks.size(), "Неверное число задач");
        assertEquals(2, epics.size(), "Неверное число эпиков");
        assertEquals(3, subtasks.size(), "Неверное число подзадач");

        assertEquals(3, history.size(), "Неверное число задач в истории");

        assertEquals(task1, tasks.get(0), "Задачи не равны.");
        assertEquals(epic1, epics.get(0), "Эпики не равны.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не равны.");
    }

    @Test
    void testHttpTM2_loadWithEmptyTasks() {
        Assertions.assertThrows(ManagerLoadException.class, () -> HttpTaskManager.loadFromServer("http://localhost:8078"));
    }

    @Test
    void testHttpTM3_loadWithEpicWithoutSubtasks() throws URISyntaxException {
        Epic epic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:8078");

        List<Epic> epics = httpTaskManager.getEpics();
        List<Subtask> subtasks = httpTaskManager.getSubtasks();

        assertEquals(1, epics.size(), "Неверное число эпиков");
        assertTrue(subtasks.isEmpty(), "Возвращает не пустой список подзадач");

        assertEquals(epic, httpTaskManager.getEpic(epicId), "Эпики не равны.");
    }

    @Test
    void testHttpTM4_loadWithEmptyHistory() throws URISyntaxException {
        Task task = new Task("task", "task", TaskStatus.NEW);
        taskManager.createTask(task);

        Epic epic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("e1_s1", "id_4",
                TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("e1_s2", "id_5",
                TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask2);

        taskManager.getHistory();

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:8078");

        List<Task> tasks = httpTaskManager.getTasks();
        List<Epic> epics = httpTaskManager.getEpics();
        List<Subtask> subtasks = httpTaskManager.getSubtasks();

        List<Task> history = httpTaskManager.getHistory();

        assertEquals(1, tasks.size(), "Неверное число задач");
        assertEquals(1, epics.size(), "Неверное число эпиков");
        assertEquals(2, subtasks.size(), "Неверное число подзадач");

        assertTrue(history.isEmpty(), "Возвращает не пустой список подзадач");

        assertEquals(task, tasks.get(0), "Задачи не равны.");
        assertEquals(epic, epics.get(0), "Эпики не равны.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не равны.");
    }

    @Test
    void testHttpTM5_CheckIdCreatedTaskAfterLoadFromFile() throws URISyntaxException {
        Task task = new Task("task", "task", TaskStatus.NEW);
        taskManager.createTask(task);

        Epic epic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "subtask1",
                TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2",
                TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask2);

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:8078");

        Task task2 = new Task("task2", "task2", TaskStatus.NEW);
        httpTaskManager.createTask(task2);
        final int task2Id = task2.getId();

        assertEquals(5, task2Id, "Неверный ID задачи");
    }

    @Test
    void testHttpTM6_loadFromFileWithTimes() throws URISyntaxException {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, 60, LocalDateTime.now());
        final int taskId1 = taskManager.createTask(task1);
        Task task2 = new Task("task2", "task2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epicId1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.IN_PROGRESS, epicId1,
                60, LocalDateTime.now().plusMinutes(60));
        final int subtaskId1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.IN_PROGRESS, epicId1,
                60, LocalDateTime.now().plusMinutes(120));
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("epic2", "epic2", TaskStatus.NEW);
        final int epicId2 = taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("subtask3", "subtask3", TaskStatus.IN_PROGRESS, epicId2,
                60, LocalDateTime.now().minusMinutes(60));
        taskManager.createSubtask(subtask3);

        taskManager.getTask(taskId1);
        taskManager.getEpic(epicId1);
        taskManager.getSubtask(subtaskId1);

        taskManager.getHistory();

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:8078");

        List<Task> tasks = httpTaskManager.getTasks();
        List<Epic> epics = httpTaskManager.getEpics();
        List<Subtask> subtasks = httpTaskManager.getSubtasks();

        List<Task> history = httpTaskManager.getHistory();

        List<Task> prioritizedTasks = httpTaskManager.getPrioritizedTasks();

        assertEquals(2, tasks.size(), "Неверное число задач");
        assertEquals(2, epics.size(), "Неверное число эпиков");
        assertEquals(3, subtasks.size(), "Неверное число подзадач");

        assertEquals(3, history.size(), "Неверное число задач в истории");
        assertEquals(7, prioritizedTasks.size(), "Неверное число задач в приоритетном списке задач.");

        assertEquals(epic2, prioritizedTasks.get(0), "Неверный порядок в prioritizedTasks.");
        assertEquals(subtask3, prioritizedTasks.get(1), "Неверный порядок в prioritizedTasks.");
        assertEquals(task1, prioritizedTasks.get(2), "Неверный порядок в prioritizedTasks.");
        assertEquals(epic1, prioritizedTasks.get(3), "Неверный порядок в prioritizedTasks.");
        assertEquals(subtask1, prioritizedTasks.get(4), "Неверный порядок в prioritizedTasks.");
        assertEquals(subtask2, prioritizedTasks.get(5), "Неверный порядок в prioritizedTasks.");
        assertEquals(task2, prioritizedTasks.get(6), "Неверный порядок в prioritizedTasks.");

        assertEquals(task1, tasks.get(0), "Задачи не равны.");
        assertEquals(epic1, epics.get(0), "Эпики не равны.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не равны.");
    }
}