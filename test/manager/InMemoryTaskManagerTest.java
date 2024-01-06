package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
//        Task task = new Task("task", "task", TaskStatus.NEW);
//        taskManager.createTask(task);
//        Epic epic = new Epic("epic", "epic", TaskStatus.IN_PROGRESS);
//        taskManager.createEpic(epic);
//        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, 2);
//        taskManager.createSubtask(subtask1);
//        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, 2);
//        taskManager.createSubtask(subtask2);
    }

    /*
    @Test
    void updateEpicStatusWithoutSubtasks() {
        inMemoryTaskManager.updateEpicStatus(epic);
        assertEquals(inMemoryTaskManager.getEpic(1).getStatus(), TaskStatus.NEW);
    }

    @Test
    void updateEpicStatusWhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, 1);
        inMemoryTaskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, 1);
        inMemoryTaskManager.createSubtask(subtask2);
        assertEquals(inMemoryTaskManager.getEpic(1).getStatus(), TaskStatus.NEW);
    }

    @Test
    void updateEpicStatusWhenAllSubtasksDone() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.DONE, 1);
        inMemoryTaskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.DONE, 1);
        inMemoryTaskManager.createSubtask(subtask2);
        assertEquals(inMemoryTaskManager.getEpic(1).getStatus(), TaskStatus.DONE);
    }

    @Test
    void updateEpicStatusWhenAllSubtasksNewAndDone() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, 1);
        inMemoryTaskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.DONE, 1);
        inMemoryTaskManager.createSubtask(subtask2);
        assertEquals(inMemoryTaskManager.getEpic(1).getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateEpicStatusWhenAllSubtasksInProgress() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.IN_PROGRESS, 1);
        inMemoryTaskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.IN_PROGRESS, 1);
        inMemoryTaskManager.createSubtask(subtask2);
        assertEquals(inMemoryTaskManager.getEpic(1).getStatus(), TaskStatus.IN_PROGRESS);
    }
    */
}