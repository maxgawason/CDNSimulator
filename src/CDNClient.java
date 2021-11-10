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

    public ArrayList<Long> getCurrentRequests(Long currentTime) {
        ArrayList<Long> currentRequests = new ArrayList<Long>();
        while (scanner.hasNext(currentTime.toString())) {
            String fullRequestLine = scanner.nextLine();
            String[] splitRequestLine = fullRequestLine.split("\\s+");
            currentRequests.add(Long.parseLong(splitRequestLine[1]));
        }
        return currentRequests;
    }

    public boolean hasMoreData() {
        return scanner.hasNext();
    }
}
