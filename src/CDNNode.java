import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CDNNode {
    HashSet<String> currentObjects;
    List<String> objectsToBeSentOut;
    public CDNNode() {
        currentObjects = new HashSet<String>();
        objectsToBeSentOut = new ArrayList<String>();
    }

    public void receiveRequestData(List<String> requestedData) {
        //TODO: check if something actually has the object
        for (String object : requestedData) {
            objectsToBeSentOut.add(object);
        }
    }

    public List<String> outgoingRequests() {
        return objectsToBeSentOut;
    }

}
