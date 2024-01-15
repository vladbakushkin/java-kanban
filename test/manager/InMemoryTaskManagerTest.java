package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

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
}