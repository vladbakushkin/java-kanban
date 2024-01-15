package manager;

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
    void test1_getTasks() {
        Task newTask = new Task("newTask", "newTask", TaskStatus.NEW);
        final int taskId = taskManager.createTask(newTask);

        final Task savedTask = taskManager.getTask(taskId);

        assertEquals(1, savedTask.getId(), "Неверно генерируется ID.");

        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void test2_getEpics() {
        Epic newEpic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(newEpic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertEquals(1, savedEpic.getId(), "Неверно генерируется ID.");

        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(newEpic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void test3_getSubtasks() {
        Epic newEpic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("subtask", "subtask", TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.createSubtask(newSubtask);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertEquals(2, savedSubtask.getId(), "Неверно генерируется ID.");

        List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(newSubtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void test4_deleteTasks() {
        Task newTask = new Task("newTask", "newTask", TaskStatus.NEW);
        taskManager.createTask(newTask);

        assertEquals(taskManager.getTasks().size(), 1);

        taskManager.deleteTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "Список задач не пуст.");
    }

    @Test
    void test5_deleteEpics() {
        Epic newEpic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("subtask", "subtask", TaskStatus.NEW, epicId);
        taskManager.createSubtask(newSubtask);

        taskManager.deleteEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "Список эпиков не пуст.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список подзадач не пуст.");
    }

    @Test
    void test6_deleteSubtasks() {
        Epic newEpic = new Epic("epic", "epic", TaskStatus.IN_PROGRESS);
        final int epicId = taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("subtask", "subtask", TaskStatus.IN_PROGRESS, epicId);
        taskManager.createSubtask(newSubtask);

        taskManager.deleteSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty(), "Список подзадач не пуст.");
        assertTrue(taskManager.getEpic(epicId).getSubtasksId().isEmpty(), "Список ID подзадач не пуст.");
        assertEquals(taskManager.getEpic(epicId).getStatus(), TaskStatus.NEW, "Статус эпик не NEW");
    }

    @Test
    void test7_getTask() {
        Task newTask = new Task("newTask", "newTask", TaskStatus.NEW);
        final int taskId = taskManager.createTask(newTask);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(newTask, savedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask, tasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void test8_getEpic() {
        Epic newEpic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(newEpic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(newEpic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(newEpic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void test9_getSubtask() {
        Epic newEpic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(newEpic);

        Subtask newSubtask = new Subtask("subtask", "subtask", TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.createSubtask(newSubtask);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(newSubtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(newSubtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void test10_createTask() {
        Task newTask = new Task("newTask", "newTask", TaskStatus.NEW);
        final int taskId = taskManager.createTask(newTask);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(newTask, savedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void test11_createEpic() {
        Epic newEpic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(newEpic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(newEpic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(newEpic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void test12_createSubtaskWithEpic() {
        Epic newEpic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(newEpic);

        Subtask newSubtask = new Subtask("subtask", "subtask", TaskStatus.IN_PROGRESS, epicId);
        final int subtaskId = taskManager.createSubtask(newSubtask);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(newSubtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(newSubtask, subtasks.get(0), "Подзадачи не совпадают.");

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Неправильный статус эпика.");
        assertTrue(taskManager.getEpic(epicId).getSubtasksId().contains(subtaskId),
                "Эпике не содержит подзадачу");
    }

    @Test
    void test13_createSubtaskWithoutEpic() {
        Subtask newSubtask = new Subtask("subtask", "subtask", TaskStatus.NEW, 1);
        final int subtaskId = taskManager.createSubtask(newSubtask);

        assertEquals(-1, subtaskId, "Эпик найден.");

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNull(savedSubtask, "Подзадача найдена.");
    }

    @Test
    void test14_updateTask() {
        Task task = new Task("task", "task", TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);

        Task newTask = new Task("newTask", "newTaskDescription", TaskStatus.IN_PROGRESS);
        newTask.setId(taskId);
        taskManager.updateTask(newTask);

        assertEquals("newTask", taskManager.getTask(taskId).getName(),
                "Неверное новое имя задачи");
        assertEquals("newTaskDescription", taskManager.getTask(taskId).getDescription(),
                "Неверное новое описание задачи");
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTask(taskId).getStatus(),
                "Неверное новый статус задачи");
    }

    @Test
    void test15_updateEpic() {
        Epic epic = new Epic("epic", "epic", TaskStatus.DONE);
        final int epicId = taskManager.createEpic(epic);

        Epic newEpic = new Epic("newEpic", "newEpicDescription", TaskStatus.IN_PROGRESS);
        newEpic.setId(epicId);
        taskManager.updateEpic(newEpic);

        assertEquals("newEpic", taskManager.getEpic(epicId).getName(),
                "Неверное новое имя эпика");
        assertEquals("newEpicDescription", taskManager.getEpic(epicId).getDescription(),
                "Неверное новое описание эпика");
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Неверный новый статус эпика");
    }

    @Test
    void test16_updateSubtask() {
        Epic epic = new Epic("epic", "epic", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("subtask", "subtask", TaskStatus.NEW, 1);
        final int subtaskId = taskManager.createSubtask(subtask);

        Subtask newSubtask = new Subtask("newSubtask", "newSubtaskDescription",
                TaskStatus.IN_PROGRESS, 1);
        newSubtask.setId(subtaskId);
        taskManager.updateSubtask(newSubtask);

        assertEquals("newSubtask", taskManager.getSubtask(subtaskId).getName(),
                "Неверное новое имя подзадачи");
        assertEquals("newSubtaskDescription", taskManager.getSubtask(subtaskId).getDescription(),
                "Неверное новое описание подзадачи");
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubtask(subtaskId).getStatus(),
                "Неверное новый статус подзадачи");
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Неверный новый статус эпика");
    }

    @Test
    void test17_deleteTask() {
        Task task = new Task("task", "task", TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);

        taskManager.deleteTask(taskId);

        assertTrue(taskManager.getTasks().isEmpty(), "Список задач не пуст");
        assertNull(taskManager.getTask(taskId), "Возвращается удаленная задача");
    }

    @Test
    void test18_deleteEpic() {
        Epic epic = new Epic("epic", "epic", TaskStatus.DONE);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("subtask", "subtask", TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask);

        taskManager.deleteEpic(epicId);

        assertTrue(taskManager.getEpics().isEmpty(), "Список эпиков не пуст");
        assertNull(taskManager.getEpic(epicId), "Возвращается удаленный эпик");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список подзадач не пуст");
    }

    @Test
    void test19_deleteSubtask() {
        Epic epic = new Epic("epic", "epic", TaskStatus.IN_PROGRESS);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("subtask", "subtask", TaskStatus.IN_PROGRESS, epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.deleteSubtask(subtaskId);

        assertFalse(taskManager.getEpic(epicId).getSubtasksId().contains(subtaskId),
                "Эпик содержит подзадачу");
        assertTrue(taskManager.getSubtasks().isEmpty(),
                "Список подзадач не пуст");
        assertEquals(TaskStatus.NEW, taskManager.getEpic(epicId).getStatus(),
                "Неверный новый статус эпика");
        assertNull(taskManager.getSubtask(subtaskId),
                "Возвращается удаленная подзадача");
    }

    @Test
    void test20_getSubtasksFromEpic() {
        Epic epic = new Epic("epic", "epic", TaskStatus.IN_PROGRESS);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("subtask", "subtask", TaskStatus.IN_PROGRESS, epicId);
        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtasksFromEpic(epicId);

        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtasks.get(0), subtask, "Подзадачи не совпадают.");
    }

    @Test
    void test21_getHistory() {
        Task task = new Task("task", "task", TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);

        taskManager.getTask(taskId);

        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.size(), "Неверный размер истории");
        assertEquals(task, history.get(0), "Задачи в истории не совпадают");
    }
}
