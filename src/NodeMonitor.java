
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class NodeMonitor implements Monitor {
    private ArrayList<String[]> log;
    private Map<Node, String> nodeStatus;

    private final String FOUND = "FOUND", LOST = "LOST", HELLO = "HELLO";
    private final String ALIVE = "ALIVE", DEAD = "DEAD", UNKNOWN = "UNKNOWN";

    public NodeMonitor(String fileName) {
        nodeStatus = new HashMap<>();

        log = loadLog(fileName);

        if (log != null) {
            parseLog();
        }
    }

    /**
     * Creates easier to work with ArrayList from specified log file.
     * @param logName location on users system of log file.
     * @return ArrayList of ana array of strings, holding each line of the log.
     */
    private ArrayList<String[]> loadLog(String logName) {
        ArrayList<String[]> logData = new ArrayList<String[]>();
        try {
            FileReader reader = new FileReader(logName);
            BufferedReader buffReader = new BufferedReader(reader);

            String line;
            while ((line = buffReader.readLine()) != null){
                logData.add(line.split("\\s+"));
            }
            buffReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Log file could not be found.");
            return null;
        } catch (IOException e) {
            System.out.println("Error reading from file.");
            return null;
        }
        return logData;
    }

    /**
     * Takes log data and adds relevant info to each nodes' history, and creates nodes if they havent
     * been seen before.
     */
    private void parseLog() {
        Node node1, node2;

        for(int i=0; i < log.size(); i++) {
            String[] logLine = log.get(i);

            if (getNode(logLine[2]) == null) { // check if we have seen node1 before
                nodeStatus.put(new Node(logLine[2]), null);
            }
            node1 = getNode(logLine[2]);

            if (logLine.length == 5) {
                if (getNode(logLine[4]) == null) { // check if we have seen node2 before
                    node2 = new Node(logLine[4]);
                    nodeStatus.put(node2, null);
                }
                else {
                    node2 = getNode(logLine[4]);
                }
                Notification notification =
                        new Notification(Long.parseLong(logLine[1]), Long.parseLong(logLine[0]), node1, logLine[3], node2);

                node1.addHistory(notification);
                node2.addHistory(notification);
            }
            else
            {
                Notification notification =
                        new Notification(Long.parseLong(logLine[1]), Long.parseLong(logLine[0]), node1, logLine[3]);
                node1.addHistory(notification);
            }
        }
        updateStatus();
    }

    /**
     * Updates the status of each node in system based on log data in the nodes' history.
     * Lots of duplicate code?
     */
    private void updateStatus() {
        for (Node node : nodeStatus.keySet()) {
            List<Notification> nodeHistory = node.getHistory();

            for (int i=0; i < nodeHistory.size(); i++) {
                Notification notification = nodeHistory.get(i);
                long broadcastTime = notification.getSentTime();

                // Node HELLO means Node is alive
                if (notification.getMessage().equals(HELLO)) {
                    if (node.getLastStatusUpdate() == null) {
                        node.statusUpdate(notification);
                        nodeStatus.put(node, ALIVE);
                    }
                    else if (broadcastTime > node.getLastStatusUpdate().getSentTime()) {
                        node.statusUpdate(notification);
                        nodeStatus.put(node, ALIVE);
                    }
                }

                // Node1 FOUND Node2 means both Node1 and Node2 are ALIVE
                if (notification.getMessage().equals(FOUND)) {
                    Node node2 = notification.getNode2();

                    if (node.getLastStatusUpdate() == null) {
                        node.statusUpdate(notification);
                        nodeStatus.put(node, ALIVE);
                    }
                    else if(broadcastTime > node.getLastStatusUpdate().getSentTime()) {
                        node.statusUpdate(notification);
                        nodeStatus.put(node, ALIVE);
                    }

                    if (node2.getLastStatusUpdate() == null) {
                        node2.statusUpdate(notification);
                        nodeStatus.put(node2, ALIVE);
                    }
                    else if(broadcastTime > node2.getLastStatusUpdate().getSentTime()) {
                        node.statusUpdate(notification);
                        nodeStatus.put(node2, ALIVE);
                    }
                }

                // node1 LOST node2 means node1 is ALIVE and node2 is DEAD
                if (notification.getMessage().equals(LOST)) {
                    Node node2 = notification.getNode2();

                    if (node.getLastStatusUpdate() == null) {
                        node.statusUpdate(notification);
                        nodeStatus.put(node, ALIVE);
                    }
                    else if (broadcastTime > node.getLastStatusUpdate().getSentTime()) {
                        node.statusUpdate(notification);
                        nodeStatus.put(node, ALIVE);
                    }


                    if (node2.getLastStatusUpdate() == null) {
                        node2.statusUpdate(notification);
                        nodeStatus.put(node2, DEAD);
                    }
                    else if(broadcastTime > node2.getLastStatusUpdate().getSentTime()) {
                        node2.statusUpdate(notification);
                        nodeStatus.put(node2, DEAD);
                    }
                }
            }

        }
    }

    public void displayNodeStatus() {
        for (Node node : nodeStatus.keySet()) {
            Notification lastUpdate = node.getLastStatusUpdate();
            if (lastUpdate.getNode2() == null) {
                System.out.println(node.getName() + " " + nodeStatus.get(node) + " " + lastUpdate.getReceivedTime() +
                        " " + lastUpdate.getNode1().getName() + " " + lastUpdate.getMessage());
            }
            else
            {
                System.out.println(node.getName() + " " + nodeStatus.get(node) + " " + lastUpdate.getReceivedTime() +
                        " " + lastUpdate.getNode1().getName() + " " + lastUpdate.getMessage()
                        + " " + lastUpdate.getNode2().getName());
            }
        }
    }

    /**
     * Finds node, if it exists in system, of user provided node name
     * @param nodeName name of the node to be searched for.
     * @return null if the node wasn't found, or the Node if it was.
     */
    private Node getNode(String nodeName) {
        for (Node node : nodeStatus.keySet()) {
            if (node.getName().equals(nodeName))
                return node;
        }
        return null;
    }
}
