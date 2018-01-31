
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class NodeMonitor {
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
                node2.addHistory(logLine);
            }
            node1.addHistory(logLine);
        }
        updateStatus();
    }

    /**
     * Updates the status of each node in system based on log data in the nodes' history.
     */
    private void updateStatus() {
        for (Node node : nodeStatus.keySet()) {
            List<String[]> nodeHistory = node.getHistory();

            for (int i=0; i < nodeHistory.size(); i++) {
                String[] logLine = nodeHistory.get(i);
                long broadcastTime = Long.parseLong(logLine[1]);

                // Node HELLO means node is ALIVE
                if (logLine[3].equals(HELLO) && broadcastTime > node.getLastStatusUpdate()) {
                    node.statusUpdate(i, logLine[0] + " " + logLine[2] + " " + logLine[3]);
                    nodeStatus.put(node, ALIVE);
                }

                if (logLine[3].equals(FOUND)) {
                    Node node2 = getNode(logLine[4]);

                    // If node1 FOUND node2, both node1 and node2 are ALIVE
                    if (broadcastTime > node.getLastStatusUpdate()) {
                        node.statusUpdate(broadcastTime, logLine[0] + " " + logLine[2] + " " + logLine[3] + " " + logLine[4]);
                        nodeStatus.put(node, ALIVE);
                    }
                    if (broadcastTime > node2.getLastStatusUpdate()) {
                        node2.statusUpdate(broadcastTime, logLine[0] + " " + logLine[2] + " " + logLine[3] + " " + logLine[4]);
                        nodeStatus.put(node2, ALIVE);
                    }
                }

                // node1 LOST node2 means node1 is ALIVE and node2 is DEAD
                if (logLine[3].equals(LOST)) {
                    Node node2 = getNode(logLine[4]);
                    if (broadcastTime > node.getLastStatusUpdate()) {
                        node.statusUpdate(broadcastTime, logLine[0] + " " + logLine[2] + " " + logLine[3] + " " + logLine[4]);
                        nodeStatus.put(node, ALIVE);
                    }
                    if (broadcastTime > node2.getLastStatusUpdate()) {
                        node2.statusUpdate(broadcastTime, logLine[0] + " " + logLine[2] + " " + logLine[3] + " " + logLine[4]);
                        nodeStatus.put(node2, DEAD);
                    }
                }
            }

        }
    }

    public void displayNodeStatus() {
        for (Node node : nodeStatus.keySet()) {
            System.out.println(node.getName() + " " + nodeStatus.get(node) + " " + node.getLastUpdateDetails());
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
