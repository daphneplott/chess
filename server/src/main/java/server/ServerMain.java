package server;

import server.Server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
//        THIS IMPLEMENTATION WILL HARDCODE IN SQL DAOS
        // Better fix pending.
        server.run(8080);
        System.out.println("♕ 240 Chess Server");
    }
}