package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks.keySet());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();

        for (Task task : prioritizedTasks.keySet()) {
            if (task.getClass() == Task.class) {
                prioritizedTasks.remove(task);
            }
        }
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
        for (Task task : prioritizedTasks.keySet()) {
            if (task.getClass() == Subtask.class) {
                prioritizedTasks.remove(task);
            }
        }
        for (Task task : prioritizedTasks.keySet()) {
            if (task.getClass() == Epic.class) {
                prioritizedTasks.remove(task);
            }
        }
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Map.Entry<Integer, Epic> epic : epics.entrySet()) {
            epic.getValue().clearSubtasksId();
            epic.getValue().setStatus(TaskStatus.NEW);
        }

        for (Task task : prioritizedTasks.keySet()) {
            if (task.getClass() == Subtask.class) {
                prioritizedTasks.remove(task);
            }
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
    public int createTask(Task newTask) {
        if (newTask.getStartTime() != null) {
            for (Task task : prioritizedTasks.keySet()) {
                if (task.getClass() == Epic.class) {
                    continue;
                }
                if ((newTask.getStartTime().isAfter(task.getStartTime()) && newTask.getStartTime().isBefore(task.getEndTime())) ||
                        newTask.getStartTime().isEqual(task.getStartTime())) {
                    System.out.println("Задачи пересекаются по времени. Задача не создана.");
                    return -1;
                }
            }
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
        if (newSubtask.getStartTime() != null) {
            for (Task task : prioritizedTasks.keySet()) {
                if (task.getClass() == Epic.class) {
                    continue;
                }
                if ((newSubtask.getStartTime().isAfter(task.getStartTime()) && newSubtask.getStartTime().isBefore(task.getEndTime())) ||
                        newSubtask.getStartTime().isEqual(task.getStartTime())) {
                    System.out.println("Подзадачи пересекаются по времени. Подзадача не создана.");
                    return -1;
                }
            }
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

    protected void updateEpicTime(Epic epic) {

        prioritizedTasks.remove(epic);

        LocalDateTime startTime = null;
        long duration = 0;
        LocalDateTime endTime = null;

        Subtask firstSubtask;

        if (!(epic.getSubtasksId().isEmpty())) {
            firstSubtask = getSubtask(epic.getSubtasksId().get(0));

            startTime = firstSubtask.getStartTime();
            endTime = firstSubtask.getEndTime();

            for (Subtask subtask : subtasks.values()) {
                if (subtask.getStartTime() != null) {
                    if (subtask.getStartTime().isBefore(startTime)) {
                        startTime = subtask.getStartTime();
                    }
                    if (subtask.getEndTime().isAfter(endTime)) {
                        endTime = subtask.getEndTime();
                    }
                    duration += subtask.getDuration();
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

    @Override
    public String toString() {
        return "ru.yandex.practicum.manager.Manager{" +
                "tasks=" + tasks +
                ", epicTasks=" + epics +
                ", subTasks=" + subtasks +
                '}';
    }
}
