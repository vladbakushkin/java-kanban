import manager.*;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        // создать две задачи
        Task task1 = new Task("t1", "id_1", TaskStatus.NEW);
        Task task2 = new Task("t2", "id_2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task1);                                                                            // id = 1
        taskManager.createTask(task2);                                                                            // id = 2

        // создать один эпик с двумя подзадачами
        Epic epic1 = new Epic("e1", "id_3", TaskStatus.NEW);
        Subtask subtask1 = new Subtask("e1_s1", "id_4",
                TaskStatus.NEW, 3);
        Subtask subtask2 = new Subtask("e1_s2", "id_5",
                TaskStatus.NEW, 3);
        Subtask subtask3 = new Subtask("e1_s3", "id_6",
                TaskStatus.NEW, 3);
        taskManager.createEpic(epic1);                                                                            // id = 3
        taskManager.createSubtask(subtask1);                                                                      // id = 4
        taskManager.createSubtask(subtask2);                                                                      // id = 5
        taskManager.createSubtask(subtask3);                                                                      // id = 5

        // создать один эпик с одной подзадачей
        Epic epic2 = new Epic("e2", "id_7", TaskStatus.NEW);
        taskManager.createEpic(epic2);                                                                            // id = 6

        // Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)
        System.out.println("1: " + taskManager);

        // изменить статусы созданных объектов
        subtask3.setStatus(TaskStatus.DONE);

        // распечатать измененные списки задач
        System.out.println("2: " + taskManager.getEpic(3));
        System.out.println("3: " + taskManager.getEpic(7));

        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.getEpic(7);
        taskManager.getEpic(3);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(5);
        taskManager.getSubtask(4);
        taskManager.getSubtask(6);
        taskManager.getSubtask(4);
        taskManager.getSubtask(6);

        // удалить задачу
        taskManager.deleteTask(1);

        // удалить эпик
        taskManager.deleteEpic(3);

        System.out.println("Число записей в истории: " + taskManager.getHistory().size());
        System.out.println("История просмотров: " + taskManager.getHistory());

    }
}
