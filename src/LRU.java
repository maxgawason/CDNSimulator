import java.util.HashMap;

class ListNode {
    ListNode previousNode;
    ListNode nextNode;
    String object;
    public ListNode(String object, ListNode previousNode, ListNode nextNode) {
        this.object = object;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
    }

    public String getObject() {
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
    HashMap<String, ListNode> cacheContents = new HashMap<String, ListNode>();
    ListNode head;
    ListNode tail;


    public LRU(Long cacheSize, String cacheType) {
        super(cacheSize, cacheType);
    }

    public void addObject(String object) {
        if (head == null) {
            head = new ListNode(object, null, null);
            tail = head;
        } else {
            ListNode newObject = new ListNode(object, head, null);
            head.nextNode = newObject;
            head = newObject;
        }
        cacheContents.put(object, head);
    }

    public void evictObject() {
        cacheContents.remove(tail.getObject());
        tail = tail.nextNode;
    }

    public boolean containsObject(String object) {
        return cacheContents.containsKey(object);
    }
}
