public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        Manager manager = new Manager();

        // создать две задачи
        Task task1 = new Task("Переезд", "Собрать коробки", "NEW");
        Task task2 = new Task("Переезд", "Упаковать кошку", "IN_PROGRESS");
        manager.createTask(task1);                                                                            // id = 1
        manager.createTask(task2);                                                                            // id = 2

        // создать один эпик с двумя подзадачами
        Epic epic1 = new Epic("Важный эпик 1", "Описание_эпика_1", "NEW");
        Subtask subtask1 = new Subtask("Подзадача1_эпика_1", "Описание_подзадачи1_эпика1",
                "NEW", 3);
        Subtask subtask2 = new Subtask("Подзадача2_эпика_1", "Описание_подзадачи2_эпика_1",
                "NEW", 3);
        manager.createEpic(epic1);                                                                            // id = 3
        manager.createSubtask(subtask1);                                                                      // id = 4
        manager.createSubtask(subtask2);                                                                      // id = 5

        // создать один эпик с одной подзадачей
        Epic epic2 = new Epic("Важный эпик 2", "Описание_эпика_2", "NEW");
        Subtask subtask3 = new Subtask("Подзадача1_эпика_2", "Описание_подзадачи1_эпика_2",
                "IN_PROGRESS", 6);
        manager.createEpic(epic2);                                                                            // id = 6
        manager.createSubtask(subtask3);                                                                      // id = 7

        // Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)
        System.out.println(manager);
        System.out.println(manager.getTask(1));

        // изменить статусы созданных объектов
        subtask3.setStatus("DONE");

        // распечатать измененные списки задач
        System.out.println(manager.getEpic(6));

        // удалить задачу
        manager.deleteTask(1);
        System.out.println("Задача с id = 1: "+ manager.getTask(1));

        // удалить эпик
        manager.deleteEpic(6);
        System.out.println("Эпик с id = 6: " + manager.getEpic(6));

        System.out.println(manager);
    }
}
