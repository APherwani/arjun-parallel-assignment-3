import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineList<T> {
    private class Node {
        public Node (int key) {
            this.key = key;
            this.item = null;
            this.next = null;
        }
        public Node (T item) {
            this.item = item;
            this.key = item.hashCode();
            this.next = null;
        }
        public T item;
        public int key;
        public Node next;
        private Lock lock = new ReentrantLock();

        public void lock() {
            lock.lock();
        }

        public void unlock() {
            lock.unlock();
        }
    }

    private Node head = null;
    public FineList() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    public boolean add(T item) {
        int key = item.hashCode();
        head.lock();
        Node pred = head;
        try {
            Node cur = pred.next;
            cur.lock();
            try {
                while (cur.key < key) {
                    pred.unlock();
                    pred = cur;
                    cur = cur.next;
                    cur.lock();
                }
                if (cur.key == key) {
                    pred.unlock();
                    cur.unlock(); // check this
                    return false;
                }
                Node node = new Node(item);
                node.next = cur;
                pred.next = node;
                return true;
            } finally {
                cur.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    public boolean remove(T item) {
        Node pred = null, cur = null;
        int key = item.hashCode();
        head.lock();
        try {
            pred = head;
            cur = pred.next;
            cur.lock();
            try {
                while (cur.key < key) {
                    pred.unlock();
                    pred = cur;
                    cur = cur.next;
                    cur.lock();
                }
                if (cur.key == key) {
                    pred.next = cur.next;
                    return true;
                }
                return false;
            } finally {
                cur.unlock();
            } 
        } finally {
            pred.unlock();
        }
    }

    public T get(int key) throws NoSuchElementException {
        Node pred = null, cur = null;
        head.lock();
        try {
            pred = head;
            cur = pred.next;
            cur.lock();
            try {
                while (cur.key < key) {
                    pred.unlock();
                    pred = cur;
                    cur = cur.next;
                    cur.lock();
                }
                if (cur.key == key) {
                    return cur.item;
                } else {
                    throw new NoSuchElementException();
                }
            } finally {
                cur.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    public T pop() throws NoSuchElementException {
        Node pred = null, cur = null;
        pred = head; // pred here is the first sentinel node
        cur = pred.next; // cur could be the last sentinel node or an element in between.
        pred.lock();
        cur.lock();
        if (cur.next == null && cur.key == Integer.MAX_VALUE) {
            head.unlock();
            cur.unlock();
            throw new NoSuchElementException();
        }
        else {
            T item = pred.next.item;
            pred.next = cur.next;
            head.unlock();
            cur.unlock();
            return item;
        }
    }

    public boolean isEmpty() {
        Node pred = null, cur = null;
        pred = head; // pred here is the first sentinel node
        cur = pred.next; // cur could be the last sentinel node or an element in between.
        pred.lock();
        cur.lock();
        if (cur.next == null) {
            pred.unlock();
            cur.unlock();
            return true; // here the list is empty
        }
        pred.unlock();
        cur.unlock();
        return false;
    }
}
