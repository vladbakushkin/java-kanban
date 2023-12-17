package manager;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return super.getSubtasks();
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
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return super.getSubtask(id);
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
    public List<Subtask> getSubtasksFromEpic(int epicId) {
        return super.getSubtasksFromEpic(epicId);
    }

    @Override
    public List<Task> getHistory() {
        save();
        return super.getHistory();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            List<Task> tasks = super.getTasks();
            List<Epic> epics = super.getEpics();
            List<Subtask> subtasks = super.getSubtasks();

            writer.write("id,type,name,status,description,epic\n");
            for (Task task : tasks) {
                writer.write(toString(task));
            }
            for (Epic epic : epics) {
                writer.write(toString(epic));
            }
            for (Subtask subtask : subtasks) {
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
    private String toString(Task task) {
        String result = String.format("%s,%s,%s,%s,%s", task.getId(), task.getClass().getSimpleName().toUpperCase(),
                task.getName(), task.getStatus(), task.getDescription());
        if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            result += "," + subtask.getEpicId();
        }
        result += "\n";
        return result;
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
                Subtask subtask = new Subtask(values[2], values[4], TaskStatus.valueOf(values[3]), Integer.parseInt(values[5]));
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
        String[] historyId = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String id : historyId) {
            history.add(Integer.valueOf(id));
        }
        return history;
    }

    /**
     * Восстанавливает данные менеджера из файла при запуске программы
     * @param file относительный путь до файла
     * @return объект менеджера
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        Map<Integer, Task> tasks = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (!line.isEmpty()) {
                if (line.startsWith("id")) {
                    line = reader.readLine();
                    continue;
                }
                String[] values = line.split(",");
                int id = Integer.parseInt(values[0]);
                TaskType taskType = TaskType.valueOf(values[1]);
                switch (taskType) {
                    case TASK:
                        Task task = taskManager.fromString(line);
                        taskManager.createTask(task);
                        task.setId(id);
                        tasks.put(id, task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) taskManager.fromString(line);
                        taskManager.createEpic(epic);
                        epic.setId(id);
                        tasks.put(id, epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) taskManager.fromString(line);
                        taskManager.createSubtask(subtask);
                        subtask.setId(id);
                        tasks.put(id, subtask);
                        break;
                }
                line = reader.readLine();
            }

            line = reader.readLine();
            List<Integer> history = historyFromString(line);
            for (Integer id : history) {
                for (Map.Entry<Integer, Task> pair : tasks.entrySet()) {
                    if (Objects.equals(pair.getKey(), id)) {
                        taskManager.getHistoryManager().add(pair.getValue());
                    }
                }
            }
        } catch (IOException exception) {
            throw new ManagerLoadException("Ошибка при чтении файла");
        }
        return taskManager;
    }

    public static void main(String[] args) {

        FileBackedTaskManager taskManager = new FileBackedTaskManager(new File("resources/tasks.csv"));

        // создать две задачи
        Task task1 = new Task("t1", "id_1", TaskStatus.NEW);
        Task task2 = new Task("t2", "id_2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // создать один эпик с двумя подзадачами
        Epic epic1 = new Epic("e1", "id_3", TaskStatus.NEW);
        Subtask subtask1 = new Subtask("e1_s1", "id_4",
                TaskStatus.NEW, 3);
        Subtask subtask2 = new Subtask("e1_s2", "id_5",
                TaskStatus.NEW, 3);
        Subtask subtask3 = new Subtask("e1_s3", "id_6",
                TaskStatus.NEW, 3);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        // создать один эпик с одной подзадачей
        Epic epic2 = new Epic("e2", "id_7", TaskStatus.NEW);
        taskManager.createEpic(epic2);

        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);

        taskManager.getHistory();

        FileBackedTaskManager fileBackedTaskManager = loadFromFile(new File("resources/tasks.csv"));
        System.out.println("все задачи:" + fileBackedTaskManager);
        System.out.println("история:" + fileBackedTaskManager.getHistory());
    }
}
