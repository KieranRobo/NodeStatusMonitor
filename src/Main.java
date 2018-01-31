
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Log file location must be specified.");
        }
        else if(args.length > 1) {
            System.out.println("Too many arguments! Only the node log location should be specified.");
        }
        else {
            System.out.println("Running " + args[0]);
            NodeMonitor monitor = new NodeMonitor(args[0]);

            monitor.displayNodeStatus();
        }
    }

}
