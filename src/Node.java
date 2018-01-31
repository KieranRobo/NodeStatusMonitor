import java.util.ArrayList;

public class Node {

    private String name;
    private ArrayList<String[]> history;
    private long lastStatusUpdate;

    // lastUpdateDetails is hard coded string - should be improved. (works for now)
    private String lastUpdateDetails;

    public Node(String nodeName) {
        name = nodeName;
        history = new ArrayList<>();
        lastStatusUpdate = 0;
    }

    public void addHistory(String[] log) {
        history.add(log);
    }

    public void statusUpdate(long time, String details) {
        lastStatusUpdate = time;
        lastUpdateDetails = details;
    }

    public ArrayList<String[]> getHistory() {
        return history;
    }

    public String getName() {
        return name;
    }

    public long getLastStatusUpdate() {
        return lastStatusUpdate;
    }

    public String getLastUpdateDetails() {
        return lastUpdateDetails;
    }
}
