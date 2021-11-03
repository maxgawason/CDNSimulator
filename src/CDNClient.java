import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class CDNClient {
    File dataFile;
    Scanner scanner;
    public CDNClient(String dataFileName) {
        dataFile = new File(dataFileName);
        try {
            scanner = new Scanner(dataFile);
        } catch (FileNotFoundException ex) {

        }
    }

    public ArrayList<String> getCurrentRequests(Long currentTime) {
        ArrayList<String> currentRequests = new ArrayList<String>();
        while (scanner.hasNext(currentTime.toString())) {
            String fullRequestLine = scanner.nextLine();
            String[] splitRequestLine = fullRequestLine.split("\\s+");
            currentRequests.add(splitRequestLine[1]); //TODO: look this up
        }
        return currentRequests;
    }

    public boolean hasMoreData() {
        return scanner.hasNext();
    }
}
