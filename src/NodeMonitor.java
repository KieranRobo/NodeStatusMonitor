
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NodeMonitor {
    ArrayList<String[]> log;
    HashMap<Node, String> nodeStatus;

    public NodeMonitor(String fileName) {
        nodeStatus = new HashMap<>();


        log = loadLog(fileName);

        if (log != null) {
            parseLog();
        }
    }

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

    private void updateStatus() {
        for (Node node : nodeStatus.keySet()) {
            ArrayList<String[]> nodeHistory = node.getHistory();

            for (int i=0; i < nodeHistory.size(); i++) {
                String[] logLine = nodeHistory.get(i);
                long broadcastTime = Long.parseLong(logLine[1]);

                // Node HELLO means node is ALIVE
                if (logLine[3].equals("HELLO") && broadcastTime > node.getLastStatusUpdate()) {
                    node.statusUpdate(i, logLine[0] + " " + logLine[2] + " " + logLine[3]);
                    nodeStatus.put(node, "ALIVE");
                }

                if (logLine[3].equals("FOUND")) {
                    Node node2 = getNode(logLine[4]);

                    // If node1 FOUND node2, both node1 and node2 are ALIVE
                    if (broadcastTime > node.getLastStatusUpdate()) {
                        node.statusUpdate(broadcastTime, logLine[0] + " " + logLine[2] + " " + logLine[3] + " " + logLine[4]);
                        nodeStatus.put(node, "ALIVE");
                    }
                    if (broadcastTime > node2.getLastStatusUpdate()) {
                        node2.statusUpdate(broadcastTime, logLine[0] + " " + logLine[2] + " " + logLine[3] + " " + logLine[4]);
                        nodeStatus.put(node2, "ALIVE");
                    }
                }

                // node1 LOST node2 means node2 is DEAD
                if (logLine[3].equals("LOST")) {
                    Node node2 = getNode(logLine[4]);
                    if (broadcastTime > node2.getLastStatusUpdate()) {
                        node2.statusUpdate(broadcastTime, logLine[0] + " " + logLine[2] + " " + logLine[3] + " " + logLine[4]);
                        nodeStatus.put(node2, "DEAD");
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

    private Node getNode(String nodeName) {
        for (Node node : nodeStatus.keySet()) {
            if (node.getName().equals(nodeName))
                return node;
        }
        return null;
    }

    private void displayData() {
        for(int i=0; i < log.size(); i++) {
            String[] logLine = log.get(i);
            System.out.println("Received: " + logLine[0]);
            System.out.println("Generated: " + logLine[1]);
            System.out.println("Node: " + logLine[2]);
            System.out.println("Status: " + logLine[3]);
            if (logLine.length == 5)
                System.out.println("Status To Node: " + logLine[4]);

            System.out.println();
        }
    }
}
