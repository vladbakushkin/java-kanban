package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @Test
    void getTasksStandard() {
        Task newTask = new Task("newTask", "newTask", TaskStatus.NEW);
        final int id = taskManager.createTask(newTask);

        final Task savedTask = taskManager.getTask(id);
        assertEquals(1, savedTask.getId(), "Неверно генерируется ID.");

        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getTasksWithoutTasks() {
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertTrue(tasks.isEmpty(), "Список задач не пуст.");
    }

    @Test
    void getTasksWithWrongId() {
        Task newTask = new Task("newTask", "newTask", TaskStatus.NEW);
        final int id = taskManager.createTask(newTask);

        final Task savedTask = taskManager.getTask(id);
        assertEquals(1, savedTask.getId(), "Неверно генерируется ID.");

        List<Task> tasks = taskManager.getTasks();
    }
/*
    @Test
    void getEpics() {
        List<Epic> epics = taskManager.getEpics();
        assertEquals(epics.size(), 1);
    }

    @Test
    void getSubtasks() {
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(subtasks.size(), 2);
    }

    @Test
    void deleteTasks() {
        taskManager.deleteTasks();
        assertEquals(taskManager.getTasks().size(), 0);
    }

    @Test
    void deleteEpics() {
        taskManager.deleteEpics();
        assertEquals(taskManager.getEpics().size(), 0);
    }

    @Test
    void deleteSubtasks() {
        taskManager.deleteSubtasks();
        assertEquals(taskManager.getSubtasks().size(), 0);
    }

    @Test
    void getTask() {
        Task task = taskManager.getTask(1);
        assertNotNull(task);
    }

    @Test
    void getEpic() {
        Epic epic = taskManager.getEpic(2);
        assertNotNull(epic);
    }

    @Test
    void getSubtask() {
        Subtask subtask = taskManager.getSubtask(3);
        assertNotNull(subtask);
    }

    @Test
    void createTask() {
        Task newTask = new Task("newTask", "newTask", TaskStatus.NEW);
        final int id = taskManager.createTask(newTask);

        final Task savedTask = taskManager.getTask(id);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(newTask, savedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
        int id = taskManager.createEpic(new Epic("epic", "epic", TaskStatus.NEW));
        assertEquals(id, 5);
    }

    @Test
    void createSubtask() {
        int id = taskManager.createSubtask(new Subtask("subtask", "subtask", TaskStatus.NEW, 2));
        assertEquals(id, 5);
    }

    @Test
    void updateTask() {
        Task task = new Task("newTask", "newTaskDescription", TaskStatus.IN_PROGRESS);
        task.setId(1);
        taskManager.updateTask(task);
        assertEquals(taskManager.getTask(1).getName(), "newTask");
        assertEquals(taskManager.getTask(1).getDescription(), "newTaskDescription");
        assertEquals(taskManager.getTask(1).getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("newEpic", "newEpicDescription", TaskStatus.DONE);
        epic.setId(2);
        taskManager.updateEpic(epic);
        assertEquals(taskManager.getEpic(2).getName(), "newEpic");
        assertEquals(taskManager.getEpic(2).getDescription(), "newEpicDescription");
        assertEquals(taskManager.getEpic(2).getStatus(), TaskStatus.DONE);
    }

    @Test
    void updateSubtask() {
        Subtask subtask = new Subtask("newSubtask", "newSubtaskDescription", TaskStatus.DONE, 2);
        subtask.setId(3);
        taskManager.updateSubtask(subtask);
        assertEquals(taskManager.getSubtask(3).getName(), "newSubtask");
        assertEquals(taskManager.getSubtask(3).getDescription(), "newSubtaskDescription");
        assertEquals(taskManager.getSubtask(3).getStatus(), TaskStatus.DONE);
    }

    @Test
    void deleteTask() {
        taskManager.deleteTask(1);
        assertNull(taskManager.getTask(1));
    }

    @Test
    void deleteEpic() {
        taskManager.deleteEpic(2);
        assertNull(taskManager.getEpic(2));
    }

    @Test
    void deleteSubtask() {
        taskManager.deleteSubtask(3);
        assertNull(taskManager.getSubtask(3));
    }

    @Test
    void getSubtasksFromEpic() {
        List<Subtask> subtasks = taskManager.getSubtasksFromEpic(2);
        Subtask subtask3 = new Subtask("subtask1", "subtask1", TaskStatus.NEW, 2);
        subtask3.setId(3);
        Subtask subtask4 = new Subtask("subtask2", "subtask2", TaskStatus.NEW, 2);
        subtask4.setId(4);
        assertEquals(subtasks.get(0), subtask3);
        assertEquals(subtasks.get(1), subtask4);
    }

    @Test
    void getHistory() {
        taskManager.getTask(1);
        Task task = new Task("task", "task", TaskStatus.NEW);
        task.setId(1);
        assertEquals(taskManager.getHistory().get(0), task);
    }

 */
}
