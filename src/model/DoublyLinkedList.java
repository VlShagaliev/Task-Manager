package model;

import java.util.ArrayList;

public class DoublyLinkedList<T> {
    public Node<T> head;
    public Node<T> tail;

    public void linkLast(Node<T> newNode){
        if (head == null){
            head = newNode;
            tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
            if (tail.prev == head){
                head.next = tail;
                tail.prev = head;
            }
        }
    }

    public ArrayList<T> getTasks(){
        Node<T> currentNode = head;
        ArrayList<T> taskList = new ArrayList<>();
        while (currentNode != null){
            taskList.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return taskList;
    }
}