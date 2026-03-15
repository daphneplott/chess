package client;

import chess.*;
import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        // ServerUrl string default
        // If given the args, change serverUrl
        // Create client object
        // Do something like client.run()

        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new ChessClient(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
