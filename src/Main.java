import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Log file location must be specified.");
        }
        else {
            System.out.println("Running " + args[0]);
            NodeMonitor monitor = new NodeMonitor(args[0]);

            monitor.displayNodeStatus();
        }
    }

}
