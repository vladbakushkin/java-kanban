package manager;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int updateTask(Task task) {
        int id = super.updateTask(task);
        save();
        return id;
    }

    @Override
    public int updateEpic(Epic epic) {
        int id = super.updateEpic(epic);
        save();
        return id;
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        int id = super.updateSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }

    @Override
    public List<Task> getHistory() {
        save();
        return super.getHistory();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("id,type,name,status,description,epic\n");
            for (Task task : super.getTasks()) {
                writer.write(toString(task));
            }
            for (Epic epic : super.getEpics()) {
                writer.write(toString(epic));
            }
            for (Subtask subtask : super.getSubtasks()) {
                writer.write(toString(subtask));
            }

            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении файла");
        }
    }

    /**
     * Преобразует задачу в строковое описание
     *
     * @param task объект задачи
     * @return строковое представление задачи
     */
    private static String toString(Task task) {
        StringBuilder result = new StringBuilder(String.format("%s,%s,%s,%s,%s", task.getId(),
                task.getClass().getSimpleName().toUpperCase(), task.getName(), task.getStatus(), task.getDescription()));
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            result.append(",").append(subtask.getEpicId());
        }
        result.append("\n");
        return result.toString();
    }

    /**
     * Создает задачу из строки
     *
     * @param value строка в формате "id,type,name,status,description,epic"
     * @return объект задачи из строки
     */
    private Task fromString(String value) {
        String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        TaskType taskType = TaskType.valueOf(values[1]);
        switch (taskType) {
            case TASK:
                Task task = new Task(values[2], values[4], TaskStatus.valueOf(values[3]));
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(values[2], values[4], TaskStatus.valueOf(values[3]));
                epic.setId(id);
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(values[2], values[4], TaskStatus.valueOf(values[3]),
                        Integer.parseInt(values[5]));
                subtask.setId(id);
                return subtask;
            default:
                throw new RuntimeException("Ошибка создания задачи из строки.");
        }
    }

    /**
     * Сохраняет историю в строку
     *
     * @param manager объект типа HistoryManager
     * @return строку, состоящую из id задач в истории
     */
    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            if (i == history.size() - 1) {
                result.append(history.get(i).getId());
                break;
            }
            result.append(history.get(i).getId()).append(",");
        }
        return result.toString();
    }

    /**
     * Создает историю из строки
     *
     * @param value строковое представление задачи
     * @return список id задач из истории
     */
    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value == null) {
            return history;
        }
        String[] historyId = value.split(",");
        for (String id : historyId) {
            history.add(Integer.valueOf(id));
        }
        return history;
    }

    private void addTask(Task task) {
        int id = task.getId();
        task.setId(id);
        tasks.put(id, task);
    }

    private void addEpic(Epic epic) {
        int id = epic.getId();
        epic.setId(id);
        epics.put(id, epic);
    }

    private void addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("epic == null");
            return;
        }

        int id = subtask.getId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        epic.addSubtaskId(id);
        updateEpicStatus(epic);
    }

    /**
     * Восстанавливает данные менеджера из файла при запуске программы
     *
     * @param file относительный путь до файла
     * @return объект менеджера
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line == null) {
                return taskManager;
            }
            while (!line.isBlank()) {
                if (line.startsWith("id")) {
                    line = reader.readLine();
                    continue;
                }
                String[] values = line.split(",");
                TaskType taskType = TaskType.valueOf(values[1]);
                switch (taskType) {
                    case TASK:
                        Task task = taskManager.fromString(line);
                        taskManager.addTask(task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) taskManager.fromString(line);
                        taskManager.addEpic(epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) taskManager.fromString(line);
                        taskManager.addSubtask(subtask);
                        break;
                }
                line = reader.readLine();
            }

            line = reader.readLine();
            List<Integer> history = historyFromString(line);

            for (Integer id : history) {
                for (Task task : taskManager.getTasks()) {
                    if (Objects.equals(task.getId(), id)) {
                        taskManager.getHistoryManager().add(task);
                    }
                }
                for (Epic epic : taskManager.getEpics()) {
                    if (Objects.equals(epic.getId(), id)) {
                        taskManager.getHistoryManager().add(epic);
                    }
                }
                for (Subtask subtask : taskManager.getSubtasks()) {
                    if (Objects.equals(subtask.getId(), id)) {
                        taskManager.getHistoryManager().add(subtask);
                    }
                }
            }
        } catch (IOException exception) {
            throw new ManagerLoadException("Ошибка при чтении файла");
        }
        return taskManager;
    }

}
