package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void test1_add() {
        Task task = new Task("task", "task", TaskStatus.NEW);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "Неверное число задач в истории.");
    }

    @Test
    void test2_addDuplicateTask() {
        Task task = new Task("task", "task", TaskStatus.NEW);

        historyManager.add(task);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "Неверное число задач в истории.");
        assertEquals(task, history.get(0), "Задачи в истории не совпадают.");
    }

    @Test
    void test3_removeFromBegin() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("task2", "task2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("task3", "task3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        assertEquals(2, historyManager.getHistory().size(), "Неверное число задач в истории.");
        assertEquals(task2, historyManager.getHistory().get(0), "Задачи в истории не совпадают.");
        assertEquals(task3, historyManager.getHistory().get(1), "Задачи в истории не совпадают.");
    }

    @Test
    void test4_removeFromMiddle() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("task2", "task2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("task3", "task3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        assertEquals(2, historyManager.getHistory().size(), "Неверное число задач в истории.");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи в истории не совпадают.");
        assertEquals(task3, historyManager.getHistory().get(1), "Задачи в истории не совпадают.");
    }

    @Test
    void test5_removeFromEnd() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("task2", "task2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("task3", "task3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        assertEquals(2, historyManager.getHistory().size(), "Неверное число задач в истории.");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи в истории не совпадают.");
        assertEquals(task2, historyManager.getHistory().get(1), "Задачи в истории не совпадают.");
    }

    @Test
    void test6_getHistory() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("task2", "task2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("task3", "task3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(3, history.size(), "Неверное число задач в истории.");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи в истории не совпадают.");
        assertEquals(task2, historyManager.getHistory().get(1), "Задачи в истории не совпадают.");
        assertEquals(task3, historyManager.getHistory().get(2), "Задачи в истории не совпадают.");
    }

    @Test
    void test7_getEmptyHistory() {
        final List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty(), "История не пустая.");
    }
}