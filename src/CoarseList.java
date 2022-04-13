import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseList {
    private class Node {
        public Node (int key) {
            this.key = key;
            this.next = null;
        }
        int key;
        Node next;
    }
    private Lock lock = new ReentrantLock();
    private Node head = null;
    
    public CoarseList() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    public boolean add(int key) {
        Node pred, cur;
        lock.lock();

        try {
            pred = head;
            cur = head.next;
            while (cur.key < key) {
                pred = cur;
                cur = cur.next;
            }
            if (cur.key == key) {
                return false;
            } else {
                Node node = new Node(key);
                node.next = cur;
                pred.next = node;
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(int key) {
        Node pred, cur;
        lock.lock();
        try {
            pred = head;
            cur = pred.next;
            while (cur.key < key) {
                pred = cur;
                cur = cur.next;
            }
            if (key == cur.key) {
                pred.next = cur.next;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean get(int key) {
        Node pred, cur;
        
        lock.lock();
        try {
            pred = head;
            cur = pred;
            while (cur.key < key) {
                pred = cur;
                cur = cur.next;
            }
            if (key == cur.key) {
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        Node pred, cur;
        lock.lock();
        pred = head;
        cur = pred.next;
        if (cur.next == null) {
            return true;
        }
        return false;
    }
}