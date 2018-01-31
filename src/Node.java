import java.util.ArrayList;

public class Node {

    private String name;
    private ArrayList<Notification> history;
    private Notification lastStatusUpdate;

    public Node(String nodeName) {
        name = nodeName;
        history = new ArrayList<>();
        lastStatusUpdate = null;
    }

    public void addHistory(Notification notification) {
        history.add(notification);
    }

    public void statusUpdate(Notification notification) {
        lastStatusUpdate = notification;
    }

    public ArrayList<Notification> getHistory() {
        return history;
    }


    public String getName() {
        return name;
    }

    public Notification getLastStatusUpdate() {
        return lastStatusUpdate;
    }
}
