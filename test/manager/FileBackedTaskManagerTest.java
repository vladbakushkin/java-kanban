package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void setUp() {
        taskManager = new FileBackedTaskManager(new File("resources/testTasks.csv"));
        Task task = new Task("task", "task", TaskStatus.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("epic", "epic", TaskStatus.IN_PROGRESS);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, 2);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, 2);
        taskManager.createSubtask(subtask2);
    }

    @Test
    void loadFromFile() {
    }
}