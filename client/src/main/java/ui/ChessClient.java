package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChessClient implements NotificationHandler {

    private final ServerFacade server;
    private final WebSocketFacade ws;
    private Scanner scanner;
    private AuthData auth;
    private int joinedID;
    private String orientationColor;
    private GameData gameData;
    private ArrayList<GameData> games;
    private final HashMap<Integer, Integer> ids = new HashMap<>();
    private boolean observing = false;
    private boolean gameOngoing = true;
    private final ArrayList<Integer> endedGames = new ArrayList<>();


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        System.out.println("Welcome to Chess");
        System.out.println(help("login"));
        scanner = new Scanner(System.in);
        String next = preLogin();
        while (true) {
            switch (next) {
                case "quit" -> {
                    System.out.println("Thanks for playing! Goodbye!");
                    scanner.close();
                    return;
                }
                case "logged in" -> {
                    System.out.println("Success! You've logged in.");
                    next = postLogin();
                }
                case "logged out" -> {
                    System.out.println("Success! You've logged out.");
                    next = preLogin();
                }
                case "gameplay" -> next = gamePlay();
                case "quit game" -> next = postLogin();
            }
        }
    }

    private String preLogin() {
        String input;
        String[] tokens;
        String cmd;
        while (true) {
            System.out.print("[logged out] >>> ");
            input = scanner.nextLine();
            tokens = input.toLowerCase().split(" ");
            cmd = ((tokens.length > 0) ? tokens[0] : "help");
            String next = evalPreLogin(cmd,tokens);
            if (!next.equals("none")) {
                return next;
            }
        }
    }

    private String evalPreLogin(String cmd, String[] tokens) {
        switch (cmd) {
            case "login" -> {
                if (tokens.length == 3) {
                    ServerFacade.LoginRegisterResponse response = server.login(tokens);
                    if (response.responseCode() == 200) {
                        auth = response.auth();
                        return "logged in";
                    } else if (response.responseCode() == 401) {
                        System.out.println(clean(response.message().message()));
                    } else if (response.responseCode() == 500) {
                        System.out.println("Something went wrong on our side. Please try again.");
                    }
                } else {
                    System.out.println("Expected: login <Username> <Password>");
                }
            }
            case "register" -> {
                if (tokens.length == 4) {
                    ServerFacade.LoginRegisterResponse response = server.register(tokens);
                    if (response.responseCode() == 200) {
                        auth = response.auth();
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
            default -> System.out.println(help("login"));
        }
        return "none";
    }

    private String postLogin() {
        String input;
        String[] tokens;
        String cmd;

        ServerFacade.ListGamesResponse listGamesResponse = server.list(auth);
        if (listGamesResponse.responseCode() == 200) {
            games = listGamesResponse.list();
        } else if (listGamesResponse.responseCode() == 401) {
            System.out.println("Could not access list of games, error: unauthorized");
        } else if (listGamesResponse.responseCode() == 500) {
            System.out.println("Something went wrong on our side. Please try again.");
        }
        while (true) {
            System.out.print("[logged in] >>> ");
            input = scanner.nextLine();
            tokens = input.toLowerCase().split(" ");
            cmd = ((tokens.length > 0) ? tokens[0] : "help");
            String next = evalPostLogin(cmd, tokens);
            if (!next.equals("none")) {
                return next;
            }
        }
    }

    private String evalPostLogin(String cmd, String[] tokens) {
        switch (cmd) {
            case "logout" -> {
                ServerFacade.LogoutJoinResponse response = server.logout(auth);
                if (response.responseCode() == 200) {
                    auth = null;
                    return "logged out";
                } else if (response.responseCode() == 401) {
                    System.out.println("Unauthorized");
                } else if (response.responseCode() == 500) {
                    System.out.println("Something went wrong on our side. Please try again.");
                }
            }
            case "create" -> {
                if (tokens.length == 2) {
                    ServerFacade.CreateGameResponse response = server.create(tokens, auth);
                    if (response.responseCode() == 401) {
                        System.out.println("Unauthorized");
                    } else if (response.responseCode() == 500) {
                        System.out.println("Something went wrong on our side. Please try again.");
                    }
                } else {
                    System.out.println("Expected: create <Game Name>");
                }
                ServerFacade.ListGamesResponse listGamesResponse = server.list(auth);
                if (listGamesResponse.responseCode() == 200) {
                    games = listGamesResponse.list();
                } else if (listGamesResponse.responseCode() == 401) {
                    System.out.println("Could not access list of games, error: unauthorized");
                } else if (listGamesResponse.responseCode() == 500) {
                    System.out.println("Could not fetch updated list ...");
                }
            }
            case "list" -> {
                ServerFacade.ListGamesResponse listGamesResponse = server.list(auth);
                if (listGamesResponse.responseCode() == 200) {
                    games = listGamesResponse.list();
                } else if (listGamesResponse.responseCode() == 401) {
                    System.out.println("Could not access list of games, error: unauthorized");
                } else if (listGamesResponse.responseCode() == 500) {
                    System.out.println("Something went wrong on our side. Please try again.");
                }
                System.out.println(outputGames());
            }
            case "play" -> {
                String next = evalPostLoginPlay(tokens);
                if (!next.equals("none")) {
                    return next;
                }
            }
            case "observe" -> {
                if (tokens.length == 2) {
                    int gameID;
                    try {
                        gameID = Integer.parseInt(tokens[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Expected: observe <ID>");
                        return "none";
                    }
                    if (gameID <= games.toArray().length && gameID > 0) {
                        joinedID = gameID;
                        orientationColor = "white";
                        observing = true;
                        return "gameplay";
                    } else {
                        System.out.println("ID not found");
                    }
                } else {
                    System.out.println("Expected: observe <ID>");
                }
            }
            case "quit" -> {
                return "quit";
            }
            default -> System.out.println(help("authenticated"));
        }
        return "none";
    }

    private String evalPostLoginPlay(String[] tokens) {
        if (tokens.length == 3 && (tokens[2].equals("white") || tokens[2].equals("black"))) {
            int gameId;
            try {
                gameId = Integer.parseInt(tokens[1]);
                if (! (gameId <= games.toArray().length && gameId > 0) ) {
                    System.out.println("ID not found");
                    return "none";
                }
                if ( (Objects.equals(games.get(gameId-1).blackUsername(), auth.username()) && tokens[2].equals("black")) ||
                        (Objects.equals(games.get(gameId-1).whiteUsername(), auth.username()) && tokens[2].equals("white"))) {
                    joinedID = gameId;
                    orientationColor = tokens[2];
                    return "gameplay";
                }
                else {
                    String[] newTokens = tokens.clone();
                    newTokens[1] = String.valueOf(ids.get(gameId));
                    ServerFacade.LogoutJoinResponse response = server.join(newTokens, auth);
                    if (response.responseCode() == 200) {
                        joinedID = gameId;
                        orientationColor = tokens[2];
                        return "gameplay";
                    } else if (response.responseCode() == 401) {
                        System.out.println("Unauthorized");
                    } else if (response.responseCode() == 403) {
                        System.out.println("That color is already taken. Please choose a different color or game.");
                    } else if (response.responseCode() == 500) {
                        System.out.println("Something went wrong on our side. Please try again.");
                    }
                    ServerFacade.ListGamesResponse listGamesResponse = server.list(auth);
                    if (listGamesResponse.responseCode() == 200) {
                        games = listGamesResponse.list();
                    } else if (listGamesResponse.responseCode() == 401) {
                        System.out.println("Could not access list of games, error: unauthorized");
                    } else if (listGamesResponse.responseCode() == 500) {
                        System.out.println("Could not fetch updated list ...");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Expected: join <ID> [White|Black]");
            }
        } else {
            System.out.println("Expected: join <ID> [White|Black]");
        }
        return "none";
    }

    private String gamePlay() {
        String input;
        String[] tokens;
        String cmd;
        gameData = games.get(joinedID - 1);
        String joinedColor;
        if (observing) {
            joinedColor = "observer";
        } else {
            joinedColor = orientationColor;
        }
        try {
            ws.enterGame(auth.username(),gameData.gameID(), joinedColor);
        } catch (IOException e) {
            System.out.println("Error joining game, please try again");
            return "quit game";
        }
        if (observing) {
            while (true) {
                System.out.print("[observing] >>> ");
                input = scanner.nextLine();
                tokens = input.toLowerCase().split(" ");
                cmd = ((tokens.length > 0) ? tokens[0] : "help");
                String returnedValue = evalObserving(cmd, tokens);
                if (!returnedValue.isEmpty()) {
                    return returnedValue;
                }
            }
        }
        gameOngoing = !endedGames.contains(gameData.gameID());

        while (gameOngoing) {
            System.out.print("[game play] >>> ");
            input = scanner.nextLine();
            if (!gameOngoing) {
                observing = true;
                break;
            }
            tokens = input.toLowerCase().split(" ");
            cmd = ((tokens.length > 0) ? tokens[0] : "help");
            String evaluated = evalGamePlay(cmd, tokens);
            if (evaluated.equals("quit game")) {
                return evaluated;
            }
        }
        while (true) {
            System.out.print("[observing] >>> ");
            input = scanner.nextLine();
            tokens = input.toLowerCase().split(" ");
            cmd = ((tokens.length > 0) ? tokens[0] : "help");
            if (cmd.equals("quit")) {
                observing = false;
                try {
                    ws.leaveGame(auth.username(), gameData.gameID());
                    return "quit game";
                } catch (IOException e) {
                    System.out.println("Error leaving game. Please try again.");
                }
            } else {
                System.out.println(help("post resign"));
            }
        }
    }

    private String evalObserving(String cmd, String[] tokens) {
        switch (cmd) {
            case "quit" -> {
                observing = false;
                try {
                    ws.leaveGame(auth.username(),gameData.gameID());
                    return "quit game";
                } catch (IOException e) {
                    System.out.println("Error leaving game. Please try again.");
                }
            }
            case "redraw" -> System.out.println(drawBoard(orientationColor));
            case "highlight" -> highlightHelper(tokens);
            case "help" -> System.out.println(help("observing"));
        }
        return "";
    }

    private String evalGamePlay(String cmd, String[] tokens) {
        switch (cmd) {
            case "quit" -> {
                observing = false;
                try {
                    ws.leaveGame(auth.username(),gameData.gameID());
                    return "quit game";
                } catch (IOException e) {
                    System.out.println("Error leaving game. Please try again.");
                }
            }
            case "redraw" -> System.out.println(drawBoard(orientationColor));
            case "resign" -> {
                System.out.print("You are about to resign, confirm? [y/n]: ");
                String confirm = scanner.nextLine();
                if (confirm.toLowerCase().startsWith("y")) {
                    try {
                        ws.resign(auth.username(),gameData.gameID());
                        endedGames.add(gameData.gameID());
                        gameOngoing = false;
                    } catch (IOException e) {
                        System.out.println("Error resigning. Please try again.");
                    }
                }
            } case "move" -> {
                ChessGame.TeamColor teamColor = (orientationColor.equals("white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                if (!gameData.game().getTeamTurn().equals(teamColor)) {
                    System.out.println("Error: not your turn");
                    return "";
                }
                if (tokens.length == 4) {
                    ChessPosition start;
                    ChessPosition end;
                    ChessPiece.PieceType promotion;
                    try {
                        start = ChessMoveParser.parsePosition(tokens[1]);
                        end = ChessMoveParser.parsePosition(tokens[2]);
                        promotion = ChessMoveParser.parsePromotion(tokens[3]);
                        ws.makeMove(auth.username(),gameData.gameID(),auth.authToken(),new ChessMove(start, end, promotion));
                    } catch (ParseException e) {
                        System.out.println("Expected: move <Start> <End> <Promotion> with positions of form a6, Promotion as a piece type or 'null'");
                    } catch (IOException e) {
                        System.out.println("Error making move. Please try again.");
                    }
                } else {
                    System.out.println("Expected: move <Start> <End> <Promotion> with positions of form a6, Promotion as a piece type or 'null'");
                }
            } case "highlight" -> highlightHelper(tokens);
            default -> System.out.println(help("in game"));
        }
        return "";
    }

    private void highlightHelper(String[] tokens) {
        if (tokens.length == 2) {
            try {
                ChessPosition pos = ChessMoveParser.parsePosition(tokens[1]);
                System.out.println(highlight(pos));
            } catch (ParseException e) {
                System.out.println("Expected: highlight <Position> with position of form a6");
            }
        } else {
            System.out.println("Expected: highlight <Position> with position of form a6");
        }
    }

    @Override
    public void notify(ServerMessage notification) {
        if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            gameData = notification.getGame();
            System.out.println(drawBoard(orientationColor));
            if (observing) {
                System.out.print("[observing] >>> ");
            } else {
                System.out.print("[game play] >>> ");
            }
        } else {
            String message = notification.getMessage();
            if (message == null || message.equals("null")) {
                message = notification.getErrorMessage();
            }
            System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_RED + message +
                    EscapeSequences.RESET_TEXT_COLOR +
                    "\n[game play] >>> ");
            Pattern forfeit = Pattern.compile("forfeited");
            Pattern checkmate = Pattern.compile("checkmate");
            Matcher forfeitMatcher = forfeit.matcher(notification.getMessage());
            Matcher checkmateMatcher = checkmate.matcher(notification.getMessage());
            if (forfeitMatcher.find() || checkmateMatcher.find()) {
                gameOngoing = false;
                endedGames.add(gameData.gameID());
            }
        }
    }

    private String clean(String message) {
        if (message.equals("Error: username not found")) {
            return "User does not exist";
        } else if (message.equals("Error: unauthorized")) {
            return "Password incorrect";
        } else {
            return "Something went wrong on our side. Please try again.";
        }
    }

    private String help(String where) {
        return PrintHelp.print(where);
    }

    private String outputGames() {
        StringBuilder output = new StringBuilder();
        if (games.toArray().length == 0) {
            return "No current games";
        }
        ids.clear();
        int i = 1;
        for (GameData game : games) {
            String white = ((game.whiteUsername() != null) ? game.whiteUsername() : "[none]");
            String black = ((game.blackUsername() != null) ? game.blackUsername() : "[none]");
            output.append(String.format("%d. %s - White Player: %s, Black Player: %s %n", i, game.gameName(), white, black));
            ids.put(i,game.gameID());
            i++;
        }
        return output.toString();
    }

    private String drawBoard(String color) {
        return drawBoard(color, null);
    }

    private String drawBoard(String color, ChessPosition highlight) {
        DrawBoard drawBoardObject = new DrawBoard(gameData);
        return drawBoardObject.drawBoard(color, highlight);
    }

    private String highlight(ChessPosition position) {
        return drawBoard(orientationColor,position);
    }
}
