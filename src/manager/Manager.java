package manager;

import task.Subtask;
import task.Task;
import task.Epic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int uid = 0;

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Map.Entry<Integer, Epic> epic : epics.entrySet()) {
            epic.getValue().clearSubtasksId();
            epic.getValue().setStatus("NEW");
        }
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public int createTask(Task task) {
        int id = ++uid;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int createEpic(Epic epic) {
        int id = ++uid;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

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

    public int updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public int updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
        return subtask.getId();
    }

    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpic(int epicId) {
        Iterator<Map.Entry<Integer, Subtask>> iterator = subtasks.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().getEpicId() == epicId) {
                iterator.remove();
            }
        }
        epics.remove(epicId);
    }

    public void deleteSubtask(int subtaskId) {
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        epic.removeSubtaskId(subtaskId);
        subtasks.remove(subtaskId);
        updateEpicStatus(epic);
    }

    public ArrayList<Subtask> getSubtasksFromEpic(int epicId) {
        ArrayList<Subtask> subtasksFromEpic = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> subtask : subtasks.entrySet()) {
            if (subtask.getValue().getEpicId() == epicId) {
                subtasksFromEpic.add(subtask.getValue());
            }
        }
        return subtasksFromEpic;
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<String> statuses = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                statuses.add(subtask.getStatus());
            }
        }

        if (statuses.isEmpty()) {
            epic.setStatus("NEW");
        }

        int countNew = 0;
        int countDone = 0;
        for (String status : statuses) {
            if (status.equals("NEW")) {
                countNew++;
            }
            if (status.equals("DONE")) {
                countDone++;
            }
        }

        if (countNew == statuses.size()) {
            epic.setStatus("NEW");
        } else if (countDone == statuses.size()) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
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
