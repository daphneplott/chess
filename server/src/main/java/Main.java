import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
//        if (args.length >= 2 && args[1].equals("sql")) {
//            server = new Server(false);
//        } else {
//            server = new Server(true);
//        }
        server.run(8080);
        System.out.println("♕ 240 Chess Server");
    }
}
