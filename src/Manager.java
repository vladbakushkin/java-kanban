import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Manager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private int uid;

    public Manager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        uid = 0;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
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

        epic.getSubtasksId().add(id);
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
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());    // Получение эпика, соотв. сабтаске
        epic.getSubtasksId().remove((Integer) subtaskId);              // Удаление сабтаски из списка сабтасков в эпике
        subtasks.remove(subtaskId);
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

        for (String status : statuses) {
            if (status.equals("NEW")) {
                epic.setStatus("NEW");
            } else if (status.equals("DONE")) {
                epic.setStatus("DONE");
            } else {
                epic.setStatus("IN_PROGRESS");
            }
        }
    }

    @Override
    public String toString() {
        return "Manager{" +
                "tasks=" + tasks +
                ", epicTasks=" + epics +
                ", subTasks=" + subtasks +
                '}';
    }
}
