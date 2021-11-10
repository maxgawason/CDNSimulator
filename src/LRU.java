import java.util.HashMap;

class ListNode {
    ListNode previousNode;
    ListNode nextNode;
    Long object;
    public ListNode(Long object, ListNode previousNode, ListNode nextNode) {
        this.object = object;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
    }

    public Long getObject() {
        return object;
    }

    public void setPreviousNode(ListNode previousNode) {
        this.previousNode = previousNode;
    }

    public void setNextNode(ListNode nextNode) {
        this.nextNode = nextNode;
    }
}

public class LRU extends Cache {
    HashMap<Long, ListNode> cacheContents = new HashMap<Long, ListNode>();
    ListNode head;
    ListNode tail;
    Long addListTime = 0L;
    Long addHashTime = 0L;
    Long removeListTime = 0L;
    Long removeHashTime = 0L;


    public LRU(Long cacheSize) {
        super(cacheSize);
    }

    public void addObject(Long object) {
        Long start = System.nanoTime();
        if (head == null) {
            head = new ListNode(object, null, null);
            tail = head;
        } else {
            ListNode newObject = new ListNode(object, head, null);
            head.setNextNode(newObject);
            head = newObject;
        }
        addListTime += System.nanoTime() - start;
        start = System.nanoTime();
        cacheContents.put(object, head);
        addHashTime += System.nanoTime() - start;
    }

    public void evictObject() {
        Long time = System.nanoTime();
        cacheContents.remove(tail.getObject());
        removeHashTime += System.nanoTime() - time;
        time = System.nanoTime();
        ListNode delete = tail;
        tail = tail.nextNode;
        delete = null;
        removeListTime += System.nanoTime() - time;
    }

    public boolean containsObject(Long object) {
        return cacheContents.containsKey(object);
    }

    public void printStatistics() {
        System.out.println("addListTime: " + String.valueOf(addListTime).length() + " addHashTime: " + String.valueOf(addListTime).length() + " removeListTime: " + String.valueOf(removeListTime).length() + " removeHashTime: " + String.valueOf(removeListTime).length());
    }

    public void resetStatistics() {
        addHashTime = 0L;
        addListTime = 0L;
        removeHashTime = 0L;
        removeListTime = 0L;
    }
}
