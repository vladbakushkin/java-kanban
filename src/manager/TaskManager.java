package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Task> getPrioritizedTasks();

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    int updateTask(Task task);

    int updateEpic(Epic epic);

    int updateSubtask(Subtask subtask);

    void deleteTask(int taskId);

    void deleteEpic(int epicId);

    void deleteSubtask(int subtaskId);

    List<Subtask> getSubtasksFromEpic(int epicId);

    List<Task> getHistory();
}
