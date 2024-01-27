package manager.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.file.FileBackedTaskManager;
import server.KVTaskClient;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskType;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager {

    private final Gson gson;
    private final KVTaskClient client;

    public HttpTaskManager(String url) throws URISyntaxException {
        super(null);
        gson = Managers.getGson();
        client = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);

        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put("epics", jsonEpics);

        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put("subtasks", jsonSubtasks);

        String jsonHistory = gson.toJson(historyManager.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }

    public static HttpTaskManager loadFromServer(String url) throws URISyntaxException {
        HttpTaskManager taskManager = new HttpTaskManager(url);

        String jsonTasks = taskManager.client.load("tasks");
        List<Task> tasks = taskManager.gson.fromJson(jsonTasks, new TypeToken<ArrayList<Task>>() {
        }.getType());
        taskManager.addTasks(tasks);

        String jsonEpics = taskManager.client.load("epics");
        List<Epic> epics = taskManager.gson.fromJson(jsonEpics, new TypeToken<ArrayList<Epic>>() {
        }.getType());
        taskManager.addTasks(epics);

        String jsonSubtasks = taskManager.client.load("subtasks");
        List<Subtask> subtasks = taskManager.gson.fromJson(jsonSubtasks, new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        taskManager.addTasks(subtasks);

        String jsonHistory = taskManager.client.load("history");
        List<Integer> history = taskManager.gson.fromJson(jsonHistory, new TypeToken<ArrayList<Integer>>() {
        }.getType());
        for (Integer id : history) {
            for (Task task : taskManager.prioritizedTasks.keySet()) {
                if (Objects.equals(task.getId(), id)) {
                    taskManager.getHistoryManager().add(task);
                }
            }
        }

        return taskManager;
    }

    private void addTasks(List<? extends Task> tasks) {
        for (Task task : tasks) {
            if (uid < task.getId()) {
                uid = task.getId();
            }
            TaskType taskType = TaskType.valueOf(task.getClass().getSimpleName().toUpperCase());
            switch (taskType) {
                case TASK:
                    this.tasks.put(task.getId(), task);
                    prioritizedTasks.put(task, task.getStartTime());
                    break;
                case EPIC:
                    this.epics.put(task.getId(), (Epic) task);
                    prioritizedTasks.put(task, task.getStartTime());
                    break;
                case SUBTASK:
                    this.subtasks.put(task.getId(), (Subtask) task);
                    prioritizedTasks.put(task, task.getStartTime());
                    break;
            }
        }
    }
}
