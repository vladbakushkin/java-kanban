package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void test22_updateEpicStatusWithoutSubtasks() {
        Epic epic = new Epic("epic", "epic", TaskStatus.IN_PROGRESS);
        final int epicId = taskManager.createEpic(epic);

        taskManager.updateEpicStatus(epic);

        assertEquals(taskManager.getEpic(epicId).getStatus(), TaskStatus.NEW, "Неправильный статус эпика.");
    }

    @Test
    void test23_updateEpicStatusWhenAllSubtasksNew() {
        Epic epic = new Epic("epic", "epic", TaskStatus.DONE);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask2);

        taskManager.updateEpicStatus(epic);

        assertEquals(taskManager.getEpic(epicId).getStatus(), TaskStatus.NEW, "Неправильный статус эпика.");
    }

    @Test
    void test24_updateEpicStatusWhenAllSubtasksDone() {
        Epic epic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.DONE, epicId);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.DONE, epicId);
        taskManager.createSubtask(subtask2);

        taskManager.updateEpicStatus(epic);

        assertEquals(taskManager.getEpic(epicId).getStatus(), TaskStatus.DONE, "Неправильный статус эпика.");
    }

    @Test
    void test25_updateEpicStatusWhenAllSubtasksNewAndDone() {
        Epic epic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.DONE, epicId);
        taskManager.createSubtask(subtask2);

        taskManager.updateEpicStatus(epic);

        assertEquals(taskManager.getEpic(epicId).getStatus(), TaskStatus.IN_PROGRESS, "Неправильный статус эпика.");
    }

    @Test
    void test26_updateEpicStatusWhenAllSubtasksInProgress() {
        Epic epic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.IN_PROGRESS, epicId);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.IN_PROGRESS, epicId);
        taskManager.createSubtask(subtask2);

        taskManager.updateEpicStatus(epic);

        assertEquals(taskManager.getEpic(epicId).getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void test27_getHistoryManager() {
        HistoryManager historyManager = taskManager.getHistoryManager();

        assertEquals(InMemoryHistoryManager.class, historyManager.getClass(), "Неверный класс менеджера истории");
    }

    @Test
    void test28_getPrioritizedTasks() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);

        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.IN_PROGRESS, epicId1, 60, LocalDateTime.now().plusMinutes(60));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.IN_PROGRESS, epicId1, 60, LocalDateTime.now().plusMinutes(120));
        taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("subtask3", "subtask3", TaskStatus.IN_PROGRESS, epicId1, 60, LocalDateTime.now().minusMinutes(60));
        taskManager.createSubtask(subtask3);

        Task task2 = new Task("task2", "task2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(6, prioritizedTasks.size(), "Неверное количество задач.");
        assertEquals(epic1, prioritizedTasks.get(0), "Неверный порядок задач в списке.");
        assertEquals(subtask3, prioritizedTasks.get(1), "Неверный порядок задач в списке.");
        assertEquals(task1, prioritizedTasks.get(2), "Неверный порядок задач в списке.");
        assertEquals(subtask1, prioritizedTasks.get(3), "Неверный порядок задач в списке.");
        assertEquals(subtask2, prioritizedTasks.get(4), "Неверный порядок задач в списке.");
        assertEquals(task2, prioritizedTasks.get(5), "Неверный порядок задач в списке.");
    }

    @Test
    void test29_checkCrossingTasks() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, 60, now);
        taskManager.createTask(task1);

        Task task2 = new Task("task2", "task2", TaskStatus.NEW, 30, now);
        taskManager.createTask(task2);

        Task task3 = new Task("task3", "task3", TaskStatus.NEW, 30, now.plusMinutes(30));
        taskManager.createTask(task3);

        Task task4 = new Task("task4", "task4", TaskStatus.NEW, 60, now.plusMinutes(60));
        taskManager.createTask(task4);

        Epic epic1 = new Epic("epic1", "epic1", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.IN_PROGRESS, epicId, 60, now.plusMinutes(120));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.IN_PROGRESS, epicId, 60, now.plusMinutes(60));
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("epic2", "epic2", TaskStatus.NEW);
        final int epicId2 = taskManager.createEpic(epic2);

        Subtask subtask5 = new Subtask("subtask5", "subtask5", TaskStatus.IN_PROGRESS, epicId2, 60, now.plusMinutes(120));
        taskManager.createSubtask(subtask5);

        Task task5 = new Task("task5", "task5", TaskStatus.NEW, 60, now.minusMinutes(60));
        taskManager.createTask(task5);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        System.out.println();
        for (Task prioritizedTask : prioritizedTasks) {
            System.out.println(prioritizedTask);
        }

        assertEquals(6, prioritizedTasks.size(), "Неверное количество задач.");
        assertEquals(task5, prioritizedTasks.get(0), "Неверный порядок задач в списке.");
        assertEquals(task1, prioritizedTasks.get(1), "Неверный порядок задач в списке.");
        assertEquals(task4, prioritizedTasks.get(2), "Неверный порядок задач в списке.");
        assertEquals(epic1, prioritizedTasks.get(3), "Неверный порядок задач в списке.");
        assertEquals(subtask1, prioritizedTasks.get(4), "Неверный порядок задач в списке.");
        assertEquals(epic2, prioritizedTasks.get(5), "Неверный порядок задач в списке.");
    }
}