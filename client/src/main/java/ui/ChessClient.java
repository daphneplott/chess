package ui;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Scanner;

public class ChessClient {

    private ServerFacade server;
    private Scanner scanner;
    private boolean authenticated = false;
    private AuthData auth;
    private int joinedID;
    private String joinedColor;
    private GameData gameData;
    private ArrayList<GameData> games;


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to Chess");
        System.out.println(help());

        // REPL loop of REPL loops
        // Starting prompt - enter preLogin
        scanner = new Scanner(System.in);
        String next = preLogin();
        while (true) {
            if (next.equals("quit")) {
                System.out.println("Thanks for playing! Goodbye!");
                scanner.close();
                return;
            } else if (next.equals("logged in")) {
                next = postLogin();
            } else if (next.equals("logged out")) {
                next = preLogin();
            } else if (next.equals("gameplay")) {
                next = gamePlay();
            } else if (next.equals("quite game")) {
                next = postLogin();
            }
        }
    }

    private String preLogin() {
        // Supports help, quit, login, register
        // Return 'quit' or 'logged in'
        String input;
        String[] tokens;
        String cmd;
        while (true) {
            System.out.print("[logged out] >>> ");
            input = scanner.nextLine();
            tokens = input.toLowerCase().split(" ");
            cmd = ((tokens.length > 0) ? tokens[0] : "help");
            switch (cmd) {
                case "login" -> {
                    if (tokens.length == 3) {
                        ServerFacade.loginRegisterResponse response = server.login(tokens);
                        if (response.responseCode() == 200) {
                            auth = response.auth();
                            authenticated = true;
                            return "logged in";
                        } else if (response.responseCode() == 401) {
                            System.out.println("Password incorrect");
                        } else if (response.responseCode() == 500) {
                            System.out.println("Something went wrong on our side. Please try again.");
                        }
                    } else {
                        System.out.println("Expected: login <Username> <Password>");
                    }
                }
                case "register" -> {
                    if (tokens.length == 3) {
                        ServerFacade.loginRegisterResponse response = server.register(tokens);
                        if (response.responseCode() == 200) {
                            auth = response.auth();
                            authenticated = true;
                            return "logged in";
                        } else if (response.responseCode() == 403) {
                            System.out.println("Username already taken. Please try with a new username.");
                        } else if (response.responseCode() == 500) {
                            System.out.println("Something went wrong on our side. Please try again.");
                        }
                    } else {
                        System.out.println("Expected: register <Username> <Password> <Email>");
                    }
                }
                case "quit" -> {
                    return "quit";
                }
                default -> System.out.println(help());
            }
        }
    }

    private String postLogin() {
        // Supports help, logout, quit
        // create game, list games, join game, observe game
        // Returns 'quit' or 'logged out' or 'gameplay'
        String input;
        String[] tokens;
        String cmd;
        while (true) {
            System.out.print("[logged in] >>> ");
            input = scanner.nextLine();
            tokens = input.toLowerCase().split(" ");
            cmd = ((tokens.length > 0) ? tokens[0] : "help");
            switch (cmd) {
                case "logout" -> {
                    ServerFacade.logoutJoinObserveResponse response = server.logout(auth);
                    if (response.responseCode() == 200) {
                        auth = null;
                        authenticated = false;
                        return "logged out";
                    } else if (response.responseCode() == 401) {
                        System.out.println("Unauthorized");
                    } else if (response.responseCode() == 500) {
                        System.out.println("Something went wrong on our side. Please try again.");
                    }
                }
                case "create" -> {
                    if (tokens.length == 2) {
                        ServerFacade.createGameResponse response = server.create(tokens, auth);
                        if (response.responseCode() == 401) {
                            System.out.println("Unauthorized");
                        } else if (response.responseCode() == 500) {
                            System.out.println("Something went wrong on our side. Please try again.");
                        }
                    } else {
                        System.out.println("Expected: create <Game Name>");
                    }
                }
                case "list" -> {
                    ServerFacade.listGamesResponse response = server.list(auth);
                    if (response.responseCode() == 200) {
                        games = response.list();
                        System.out.println(outputGames());
                    } else if (response.responseCode() == 401) {
                        System.out.println("Unauthorized");
                    } else if (response.responseCode() == 500) {
                        System.out.println("Something went wrong on our side. Please try again.");
                    }
                }
                case "play" -> {
                    if (tokens.length == 3) {
                        ServerFacade.logoutJoinObserveResponse response = server.join(tokens, auth);
                        if (response.responseCode() == 200) {
                            joinedID = Integer.parseInt(tokens[1]);
                            joinedColor = tokens[2];
                            return "gameplay";
                        } else if (response.responseCode() == 401) {
                            System.out.println("Unauthorized");
                        } else if (response.responseCode() == 403) {
                            System.out.println("That color is already taken. Please choose a different color or game.");
                        } else if (response.responseCode() == 500) {
                            System.out.println("Something went wrong on our side. Please try again.");
                        }
                    } else {
                        System.out.println("Expected: join <ID> [White|Black]");
                    }
                }
                case "observe" -> {
                    if (tokens.length == 2) {
                        ServerFacade.logoutJoinObserveResponse response = server.observe(tokens, auth);
                        if (response.responseCode() == 200) {
                            joinedID = Integer.parseInt(tokens[1]);
                            joinedColor = "white";
                            return "gameplay";
                        } else if (response.responseCode() == 401) {
                            System.out.println("Unauthorized");
                        } else if (response.responseCode() == 500) {
                            System.out.println("Something went wrong on our side. Please try again.");
                        }
                    } else {
                        System.out.println("Expected: observe <ID>");
                    }
                }
                case "quit" -> {
                    return "quit";
                }
                default -> System.out.println(help());
            }
        }
    }

    private String gamePlay() {
        // draw
        // quit
        // Returns "quit game"
        String input;
        String[] tokens;
        String cmd;
        System.out.println(drawBoard(joinedColor));
        gameData = games.get(joinedID - 1);
        while (true) {
            System.out.print("[game play] >>> ");
            input = scanner.nextLine();
            tokens = input.toLowerCase().split(" ");
            cmd = ((tokens.length > 0) ? tokens[0] : "help");
            switch (cmd) {
                case "quit" -> {
                    return "quit game";
                }
                default -> System.out.println(help());
            }
        }
    }

    private String help() {

    }

    private String outputGames() {

    }

    private String drawBoard(String color) {

    }
}
