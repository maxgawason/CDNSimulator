import java.util.*;

class DelayListNode {
    DelayListNode previousNode;
    DelayListNode nextNode;
    Long object;
    DelayData delayData;
    boolean exists;

    public DelayListNode(Long object, DelayListNode previousNode, DelayListNode nextNode) {
        this.object = object;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
        this.delayData = new DelayData();
        this.exists = false;

    }

    public Long getObject() {
        return object;
    }

    public void setPreviousNode(DelayListNode previousNode) {
        this.previousNode = previousNode;
    }

    public void setNextNode(DelayListNode nextNode) {
        this.nextNode = nextNode;
    }

    public void exists() {
        this.exists = true;
    }
}
class DelayData {
    int numWindows;
    Long cumulativeDelay;
    Long windowStartTime;
    final Long Z = 30L;//fix this

    public DelayData() {
        numWindows = 0;
        cumulativeDelay = 0L;
        windowStartTime = -10000L;
    }

    public double getRank(int ttna) {
        double aggDelay = (double) cumulativeDelay / (double) numWindows;
        return aggDelay/ttna;
    }

    public void access(Long time) {
        Long TSSW = time - windowStartTime;
        if (TSSW >= Z) {
            numWindows++;
            cumulativeDelay += Z;
            windowStartTime = time;
        } else {
            cumulativeDelay += Z - TSSW;
        }
    }
}


public class MAD extends Cache {
    HashMap<Long, DelayListNode> cacheContents = new HashMap<>();
    DelayListNode head;
    DelayListNode tail;
    public MAD(Long cacheSize) {
        super(cacheSize);
    }

    public void addObject(Long object, Long time) {
        if (cacheContents.containsKey(object)) {
            cacheContents.get(object).exists();
        } else {
            containsObject(object, time);
            cacheContents.get(object).exists();
        }
    }

    public void evictObject() {
        DelayListNode lowestRank = findLowRank();
        //stitch it out
        DelayListNode next = lowestRank.nextNode;
        DelayListNode previous = lowestRank.previousNode;
        if (next != null && previous != null) {
            next.previousNode = previous;
            previous.nextNode = next;
        } else if (next != null) {
            tail = next;
            tail.previousNode = null;
        }
        cacheContents.remove(lowestRank.object);
        lowestRank = null;
    }

    public DelayListNode findLowRank() {
        DelayListNode lowNode = head;
        int ttna = 1;
        double lowestRank = head.delayData.getRank(ttna);
        DelayListNode currentNode = head;
        while (currentNode != null) {
            if (currentNode.exists) {
                ttna++;
                double currentRank = currentNode.delayData.getRank(ttna);
                if (currentRank < lowestRank) {
                    lowNode = currentNode;
                    lowestRank = currentRank;
                }
            }
            currentNode = currentNode.previousNode;
        }
        return lowNode;
    }

    public boolean containsObject(Long object, Long time) {
        boolean result = false;
        if (cacheContents.containsKey(object)) {
            cacheContents.get(object).delayData.access(time);
            if (cacheContents.get(object).exists) {
                result = true;
            }
        } else {
            DelayListNode newDelayObject;
            if (head != null) {
                newDelayObject = new DelayListNode(object, head, null);
                head.nextNode = newDelayObject;
                newDelayObject.previousNode = head;
                head = newDelayObject;
            } else {
                newDelayObject = new DelayListNode(object, null, null);
                head = newDelayObject;
                tail = head;
            }
            cacheContents.put(object, newDelayObject);
        }

        return result;
    }
}
