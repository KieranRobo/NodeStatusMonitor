public class Notification {

    private long sent, received;
    private Node node1, node2;
    private String message;

    public Notification(long sentTime, long receivedTime, Node node, String message) {
        this.sent = sentTime;
        this.received = receivedTime;
        this.node1 = node;
        this.node2 = null;
        this.message = message;
    }

    public Notification(long sentTime, long receivedTime, Node node1, String message, Node node2) {
        this.sent = sentTime;
        this.received = receivedTime;
        this.node1 = node1;
        this.node2 = node2;
        this.message = message;
    }

    public long getSentTime() {
        return sent;
    }

    public long getReceivedTime() {
        return received;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public String getMessage() {
        return message;
    }

}
