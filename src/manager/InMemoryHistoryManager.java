package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeMap = new HashMap<>();

    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    /**
     * Добавляет задачу в конец CustomLinkedList
     *
     * @param task объект задачи
     */
    private void linkLast(Task task) {
        Node l = last;
        Node newNode = new Node(last, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        nodeMap.put(task.getId(), newNode);
    }

    /**
     * Собирает все задачи из CustomLinkedList в обычный ArrayList
     *
     * @return список задач в виде ArrayList
     */
    ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = first;

        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }

        return tasks;
    }

    private void removeNode(Node node) {
        Node next = node.next;
        Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.task = null;
    }

    private class Node {

        Task task;

        Node prev;

        Node next;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}
