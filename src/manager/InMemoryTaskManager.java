package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int uid = 0;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Map.Entry<Integer, Epic> epic : epics.entrySet()) {
            epic.getValue().clearSubtasksId();
            epic.getValue().setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int createTask(Task task) {
        int id = ++uid;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = ++uid;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("epic == null");
            return -1;
        }

        int id = ++uid;
        subtask.setId(id);
        subtasks.put(id, subtask);

        epic.addSubtaskId(id);
        updateEpicStatus(epic);

        return id;
    }

    @Override
    public int updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
        return subtask.getId();
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        historyManager.remove(epicId);

        for (Integer subtaskId : epic.getSubtasksId()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        epic.removeSubtaskId(subtaskId);
        subtasks.remove(subtaskId);
        historyManager.remove(subtaskId);
        updateEpicStatus(epic);
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(int epicId) {
        List<Subtask> subtasksFromEpic = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> subtask : subtasks.entrySet()) {
            if (subtask.getValue().getEpicId() == epicId) {
                subtasksFromEpic.add(subtask.getValue());
            }
        }
        return subtasksFromEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateEpicStatus(Epic epic) {
        List<TaskStatus> statuses = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                statuses.add(subtask.getStatus());
            }
        }

        if (statuses.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        }

        int countNew = 0;
        int countDone = 0;
        for (TaskStatus status : statuses) {
            if (status.equals(TaskStatus.NEW)) {
                countNew++;
            }
            if (status.equals(TaskStatus.DONE)) {
                countDone++;
            }
        }

        if (countNew == statuses.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (countDone == statuses.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public String toString() {
        return "ru.yandex.practicum.manager.Manager{" +
                "tasks=" + tasks +
                ", epicTasks=" + epics +
                ", subTasks=" + subtasks +
                '}';
    }
}
