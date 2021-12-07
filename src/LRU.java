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

    public LRU(Long cacheSize) {
        super(cacheSize);
    }

    public void addObject(Long object, Long time) {
        if (head == null) {
            head = new ListNode(object, null, null);
            tail = head;
        } else {
            ListNode newObject = new ListNode(object, head, null);
            head.setNextNode(newObject);
            head = newObject;
        }
        cacheContents.put(object, head);
    }

    public void evictObject() {
        cacheContents.remove(tail.getObject());
        ListNode delete = tail;
        tail = tail.nextNode;
        delete = null;
    }

    public boolean containsObject(Long object, Long time) {
        return cacheContents.containsKey(object);
    }
}
