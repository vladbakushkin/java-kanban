package manager.memory;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    protected final Map<Task, LocalDateTime> prioritizedTasks = new TreeMap<>(taskComparator);

    protected int uid = 0;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks.keySet());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
        prioritizedTasks.keySet().removeIf(task -> task.getClass() == Task.class);
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
        prioritizedTasks.keySet().removeIf(task -> task.getClass() == Subtask.class);
        prioritizedTasks.keySet().removeIf(task -> task.getClass() == Epic.class);
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Map.Entry<Integer, Epic> epic : epics.entrySet()) {
            epic.getValue().clearSubtasksId();
            epic.getValue().setStatus(TaskStatus.NEW);
        }
        prioritizedTasks.keySet().removeIf(task -> task.getClass() == Subtask.class);
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
    public int createTask(Task newTask) {
        if (isCrossOverInTime(newTask)) {
            return -1;
        }

        int taskId = ++uid;
        newTask.setId(taskId);
        tasks.put(taskId, newTask);

        prioritizedTasks.put(newTask, newTask.getStartTime());
        return taskId;
    }

    @Override
    public int createEpic(Epic newEpic) {
        int epicId = ++uid;
        newEpic.setId(epicId);
        epics.put(epicId, newEpic);
        updateEpicStatus(newEpic);
        prioritizedTasks.put(newEpic, newEpic.getStartTime());
        return epicId;
    }

    @Override
    public int createSubtask(Subtask newSubtask) {
        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic == null) {
            System.out.println("epic == null");
            return -1;
        }
        if (isCrossOverInTime(newSubtask)) {
            return -1;
        }

        int subtaskId = ++uid;
        newSubtask.setId(subtaskId);
        subtasks.put(subtaskId, newSubtask);

        prioritizedTasks.put(newSubtask, newSubtask.getStartTime());

        epic.addSubtaskId(subtaskId);
        updateEpicStatus(epic);
        updateEpicTime(epic);

        return subtaskId;
    }

    @Override
    public int updateTask(Task task) {
        tasks.put(task.getId(), task);
        prioritizedTasks.put(task, task.getStartTime());
        return task.getId();
    }

    @Override
    public int updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        prioritizedTasks.put(epic, epic.getStartTime());
        updateEpicTime(epic);
        return epic.getId();
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.put(subtask, subtask.getStartTime());
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return subtask.getId();
    }

    @Override
    public void deleteTask(int taskId) {
        Task task = tasks.remove(taskId);
        prioritizedTasks.remove(task);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        historyManager.remove(epicId);
        prioritizedTasks.remove(epic);

        for (Integer subtaskId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        epic.removeSubtaskId(subtaskId);
        Subtask subtask = subtasks.remove(subtaskId);
        historyManager.remove(subtaskId);
        prioritizedTasks.remove(subtask);
        updateEpicStatus(epic);
        updateEpicTime(epic);
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
        List<Integer> epicSubtasksId = epic.getSubtasksId();
        List<Subtask> epicSubtasks = new ArrayList<>();

        for (Integer subtaskId : epicSubtasksId) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }

        epic.setStatus(InMemoryTaskManager.calculateStatus(epicSubtasks));
    }

    protected void updateEpicTime(Epic epic) {

        prioritizedTasks.remove(epic);

        LocalDateTime startTime = null;
        Duration duration = null;
        LocalDateTime endTime = null;

        if (!(epic.getSubtasksId().isEmpty())) {
            Subtask firstSubtask = subtasks.get(epic.getSubtasksId().get(0));
            startTime = firstSubtask.getStartTime();
            endTime = firstSubtask.getEndTime();

            List<Integer> epicSubtasksId = epic.getSubtasksId();
            List<Subtask> epicSubtasks = new ArrayList<>();

            for (Integer subtaskId : epicSubtasksId) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }

            for (Subtask subtask : epicSubtasks) {
                if (subtask.getStartTime() != null) {
                    duration = subtask.getDuration();
                    if (subtask.getStartTime().isBefore(startTime)) {
                        startTime = subtask.getStartTime();
                    }
                    if (subtask.getEndTime().isAfter(endTime)) {
                        endTime = subtask.getEndTime();
                    }
                    duration = duration.plus(subtask.getDuration());
                }
            }
        }

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);

        prioritizedTasks.put(epic, epic.getStartTime());
    }

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }

    public static TaskStatus calculateStatus(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            return TaskStatus.NEW;
        }

        int countNew = 0;
        int countDone = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus().equals(TaskStatus.NEW)) {
                countNew++;
            }
            if (subtask.getStatus().equals(TaskStatus.DONE)) {
                countDone++;
            }
        }

        if (countNew == subtasks.size()) {
            return TaskStatus.NEW;
        } else if (countDone == subtasks.size()) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

    private boolean isCrossOverInTime(Task newTask) {
        if (newTask.getStartTime() != null) {
            for (Task task : prioritizedTasks.keySet()) {
                if (task.getClass() == Epic.class || task.getStartTime() == null) {
                    continue;
                }
                if ((newTask.getStartTime().isAfter(task.getStartTime()) && newTask.getStartTime().isBefore(task.getEndTime())) ||
                        newTask.getStartTime().isEqual(task.getStartTime())) {
                    System.out.println("Задачи пересекаются по времени. Задача не создана.");
                    return true;
                }
            }
        }
        return false;
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
