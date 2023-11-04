import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.Task;
import task.Epic;
import task.Subtask;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new InMemoryTaskManager();

        // создать две задачи
        Task task1 = new Task("Переезд", "Собрать коробки", "NEW");
        Task task2 = new Task("Переезд", "Упаковать кошку", "IN_PROGRESS");
        taskManager.createTask(task1);                                                                            // id = 1
        taskManager.createTask(task2);                                                                            // id = 2

        // создать один эпик с двумя подзадачами
        Epic epic1 = new Epic("Важный эпик 1", "Описание_эпика_1", "NEW");
        Subtask subtask1 = new Subtask("Подзадача1_эпика_1", "Описание_подзадачи1_эпика1",
                "NEW", 3);
        Subtask subtask2 = new Subtask("Подзадача2_эпика_1", "Описание_подзадачи2_эпика_1",
                "NEW", 3);
        taskManager.createEpic(epic1);                                                                            // id = 3
        taskManager.createSubtask(subtask1);                                                                      // id = 4
        taskManager.createSubtask(subtask2);                                                                      // id = 5

        // создать один эпик с одной подзадачей
        Epic epic2 = new Epic("Важный эпик 2", "Описание_эпика_2", "NEW");
        Subtask subtask3 = new Subtask("Подзадача1_эпика_2", "Описание_подзадачи1_эпика_2",
                "IN_PROGRESS", 6);
        taskManager.createEpic(epic2);                                                                            // id = 6
        taskManager.createSubtask(subtask3);                                                                      // id = 7

        // Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)
        System.out.println(taskManager);
        System.out.println(taskManager.getTask(1));

        // изменить статусы созданных объектов
        subtask3.setStatus("DONE");

        // распечатать измененные списки задач
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getEpic(6));

        // удалить задачу
        taskManager.deleteTask(1);
        System.out.println("Задача с id = 1: " + taskManager.getTask(1));

        // удалить эпик
        taskManager.deleteEpic(6);
        System.out.println("Эпик с id = 6: " + taskManager.getEpic(6));

        System.out.println(taskManager);
    }
}
