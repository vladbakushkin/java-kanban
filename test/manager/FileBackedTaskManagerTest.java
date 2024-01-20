package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @BeforeEach
    public void setUp() throws IOException {
        file = new File("resources/testTasks.csv");
        assertTrue(file.createNewFile(), "Не удалось создать файл.");
        taskManager = new FileBackedTaskManager(file);
    }

    @AfterEach
    public void cleanUpFile() {
        assertTrue(file.delete(), "Не удалось удалить файл.");
    }

    @Test
    void test29_loadFromFile() {
        // создать две задачи
        Task task1 = new Task("t1", "id_1", TaskStatus.NEW);
        final int taskId1 = taskManager.createTask(task1);
        Task task2 = new Task("t2", "id_2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task2);

        // создать один эпик с двумя подзадачами
        Epic epic1 = new Epic("e1", "id_3", TaskStatus.NEW);
        final int epicId1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("e1_s1", "id_4",
                TaskStatus.NEW, epicId1);
        final int subtaskId1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("e1_s2", "id_5",
                TaskStatus.NEW, epicId1);
        taskManager.createSubtask(subtask2);

        // создать один эпик с одной подзадачей
        Epic epic2 = new Epic("e2", "id_6", TaskStatus.NEW);
        final int epicId2 = taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("e1_s3", "id_7",
                TaskStatus.NEW, epicId2);
        taskManager.createSubtask(subtask3);

        taskManager.getTask(taskId1);
        taskManager.getEpic(epicId1);
        taskManager.getSubtask(subtaskId1);

        taskManager.getHistory();

        FileBackedTaskManager loadTaskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = loadTaskManager.getTasks();
        List<Epic> epics = loadTaskManager.getEpics();
        List<Subtask> subtasks = loadTaskManager.getSubtasks();

        List<Task> history = loadTaskManager.getHistory();

        assertEquals(2, tasks.size(), "Неверное число задач");
        assertEquals(2, epics.size(), "Неверное число эпиков");
        assertEquals(3, subtasks.size(), "Неверное число подзадач");

        assertEquals(3, history.size(), "Неверное число задач в истории");

        assertEquals(task1, tasks.get(0), "Задачи не равны.");
        assertEquals(epic1, epics.get(0), "Эпики не равны.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не равны.");
    }

    @Test
    void test30_loadWithEmptyTasks() {
        FileBackedTaskManager loadTaskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = loadTaskManager.getTasks();
        List<Epic> epics = loadTaskManager.getEpics();
        List<Subtask> subtasks = loadTaskManager.getSubtasks();

        assertTrue(tasks.isEmpty(), "Возвращает не пустой список задач");
        assertTrue(epics.isEmpty(), "Возвращает не пустой список эпиков");
        assertTrue(subtasks.isEmpty(), "Возвращает не пустой список подзадач");
    }

    @Test
    void test31_loadWithEpicWithoutSubtasks() {
        Epic epic = new Epic("epic", "epicDescription", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);

        FileBackedTaskManager loadTaskManager = FileBackedTaskManager.loadFromFile(file);

        List<Epic> epics = loadTaskManager.getEpics();
        List<Subtask> subtasks = loadTaskManager.getSubtasks();

        assertEquals(1, epics.size(), "Неверное число эпиков");
        assertTrue(subtasks.isEmpty(), "Возвращает не пустой список подзадач");

        assertEquals(epic, loadTaskManager.getEpic(epicId), "Эпики не равны.");
    }

    @Test
    void test32_loadWithEmptyHistory() {
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

        FileBackedTaskManager loadTaskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = loadTaskManager.getTasks();
        List<Epic> epics = loadTaskManager.getEpics();
        List<Subtask> subtasks = loadTaskManager.getSubtasks();

        List<Task> history = loadTaskManager.getHistory();

        assertEquals(1, tasks.size(), "Неверное число задач");
        assertEquals(1, epics.size(), "Неверное число эпиков");
        assertEquals(2, subtasks.size(), "Неверное число подзадач");

        assertTrue(history.isEmpty(), "Возвращает не пустой список подзадач");

        assertEquals(task, tasks.get(0), "Задачи не равны.");
        assertEquals(epic, epics.get(0), "Эпики не равны.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не равны.");
    }

    @Test
    void test33_CheckIdCreatedTaskAfterLoadFromFile() {
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

        FileBackedTaskManager loadTaskManager = FileBackedTaskManager.loadFromFile(file);

        Task task2 = new Task("task2", "task2", TaskStatus.NEW);
        loadTaskManager.createTask(task2);
        final int task2Id = task2.getId();

        assertEquals(5, task2Id, "Неверный ID задачи");
    }

    @Test
    void test34_loadFromFileWithTimes() {
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

        FileBackedTaskManager loadTaskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = loadTaskManager.getTasks();
        List<Epic> epics = loadTaskManager.getEpics();
        List<Subtask> subtasks = loadTaskManager.getSubtasks();

        List<Task> history = loadTaskManager.getHistory();

        List<Task> prioritizedTasks = loadTaskManager.getPrioritizedTasks();

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