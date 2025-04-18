package ru.korshunov.kanban.manager.history;

import ru.korshunov.kanban.Node;
import ru.korshunov.kanban.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;
    private final Map<Integer, Node<Task>> nodes;

    public InMemoryHistoryManager() {
        nodes = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())) {
            removeNode(nodes.get(task.getId()));
        }
        linkLast(task);
    }

    private void removeNode(Node<Task> node) {
        int id = node.getData().getId();
        nodes.remove(id);
        Node<Task> prev = node.getPrev();
        Node<Task> next = node.getNext();

        if (prev == null) {
            head = next;

            if (head != null) {
                head.setPrev(null);
            }
        }

        if (next == null) {
            tail = prev;

            if (tail != null) {
                tail.setNext(null);
            }
        }

        if (prev != null && next != null) {
            prev.setNext(next);
            next.setPrev(prev);
        }
        size--;
    }

    private void linkLast(Task element) {
        final Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;

        if (oldTail == null)
            head = newNode;
        else {
            oldTail.setNext(newNode);
        }

        nodes.put(element.getId(), newNode);
        size++;
    }

    @Override
    public void remove(int id) {
        if (nodes.containsKey(id)) {
            removeNode(nodes.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> node = head;

        for (int i = 0; i < size; i++) {
            if (node != null) {
                Task task = node.getData();
                if (task != null) {
                    tasks.add(task);

                    node = node.getNext();
                }
            }
        }

        return tasks;
    }
}
