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

import java.text.ParseException;
import java.util.*;


public class ChessClient implements NotificationHandler {

    private ServerFacade server;
    private WebSocketFacade ws;
    private Scanner scanner;
    private boolean authenticated = false;
    private AuthData auth;
    private int joinedID;
    private String joinedColor;
    private GameData gameData;
    private ArrayList<GameData> games;
    private HashMap<Integer, Integer> ids = new HashMap<>();
    private boolean observing = false;
    private boolean gameOngoing = true;


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        System.out.println("Welcome to Chess");
        System.out.println(help("login"));

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
                System.out.println("Success! You've logged in.");
                next = postLogin();
            } else if (next.equals("logged out")) {
                System.out.println("Success! You've logged out.");
                next = preLogin();
            } else if (next.equals("gameplay")) {
                next = gamePlay();
            } else if (next.equals("quit game")) {
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
                        authenticated = true;
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
            default -> System.out.println(help("login"));
        }
        return "none";
    }

    private String postLogin() {
        // Supports help, logout, quit
        // create game, list games, join game, observe game
        // Returns 'quit' or 'logged out' or 'gameplay'
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
                    int gameID = -1;
                    try {
                        gameID = Integer.parseInt(tokens[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Expected: observe <ID>");
                        return "none";
                    }
                    if (gameID <= games.toArray().length && gameID > 0) {
                        joinedID = gameID;
                        joinedColor = "white";
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
            int gameId = -1;
            try {
                gameId = Integer.parseInt(tokens[1]);
                if (! (gameId <= games.toArray().length && gameId > 0) ) {
                    System.out.println("ID not found");
                    return "none";
                }
                if ( (Objects.equals(games.get(gameId-1).blackUsername(), auth.username()) && tokens[2].equals("black")) ||
                        (Objects.equals(games.get(gameId-1).whiteUsername(), auth.username()) && tokens[2].equals("white"))) {
                    joinedID = gameId;
                    joinedColor = tokens[2];
                    return "gameplay";
                }
                else {
                    String[] newTokens = tokens.clone();
                    newTokens[1] = String.valueOf(ids.get(gameId));
                    ServerFacade.LogoutJoinResponse response = server.join(newTokens, auth);
                    if (response.responseCode() == 200) {
                        joinedID = gameId;
                        joinedColor = tokens[2];
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
        ws.enterGame();
        String input;
        String[] tokens;
        String cmd;
        gameData = games.get(joinedID - 1);
        System.out.println(drawBoard(joinedColor));
        if (observing) {
            while (true) {
                System.out.print("[observing] >>> ");
                input = scanner.nextLine();
                tokens = input.toLowerCase().split(" ");
                cmd = ((tokens.length > 0) ? tokens[0] : "help");
                switch (cmd) {
                    case "quit" -> {
                        observing = false;
                        ws.leaveGame();
                        return "quit game";
                    }
                    case "redraw" -> {
                        System.out.println(drawBoard(joinedColor));
                    }
                    case "highlight" -> {
                        if (tokens.length == 2) {
                            try {
                                ChessPosition pos = parsePosition(tokens[1]);
                                System.out.println(highlight(pos));
                            } catch (ParseException e) {
                                System.out.println("Expected: highlight <Position> with position of form a6");
                            }
                        } else {
                            System.out.println("Expected: highlight <Position> with position of form a6");
                        }
                    }
                    case "help" -> {
                        System.out.println(help("observing"));
                    }
                }
            }
        }
        gameOngoing = true;
        while (gameOngoing) {
            System.out.print("[game play] >>> ");
            input = scanner.nextLine();
            tokens = input.toLowerCase().split(" ");
            cmd = ((tokens.length > 0) ? tokens[0] : "help");
            switch (cmd) {
                case "quit" -> {
                    observing = false;
                    ws.leaveGame();
                    return "quit game";
                }
                case "redraw" -> {
                    System.out.println(drawBoard(joinedColor));
                }
                case "resign" -> {
                    System.out.print("You are about to resign, confirm? [y/n]: ");
                    String confirm = scanner.nextLine();
                    if (confirm.toLowerCase().startsWith("y")) {
                        TO DO; // Mark game as finished.
                        ws.resign();
                        gameOngoing = false;
                    }
                } case "move" -> {
                    ChessGame.TeamColor teamColor = (joinedColor.equals("white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                    if (!gameData.game().getTeamTurn().equals(teamColor)) {
                        System.out.println("Error: not your turn");
                    }
                    if (tokens.length == 3) {
                        ChessPosition start;
                        ChessPosition end;
                        try {
                            start = parsePosition(tokens[1]);
                            end = parsePosition(tokens[2]);
                            ws.makeMove();
                        } catch (ParseException e) {
                            System.out.println("Expected: move <Start> <End> with positions of form a6");
                        }
                    } else {
                        System.out.println("Expected: move <Start> <End> with positions of form a6");
                    }
                } case "highlight" -> {
                    if (tokens.length == 2) {
                        try {
                            ChessPosition pos = parsePosition(tokens[1]);
                            System.out.println(highlight(pos));
                        } catch (ParseException e) {
                            System.out.println("Expected: highlight <Position> with position of form a6");
                        }
                    } else {
                        System.out.println("Expected: highlight <Position> with position of form a6");
                    }
                }
                default -> System.out.println(help("in game"));
            }
        }
        while (true) {
            System.out.print("[observing] >>> ");
            input = scanner.nextLine();
            tokens = input.toLowerCase().split(" ");
            cmd = ((tokens.length > 0) ? tokens[0] : "help");
            switch (cmd) {
                case "quit" -> {
                    observing = false;
                    ws.leaveGame();
                    return "quit game";
                }
                case "help" -> {
                    System.out.println("post resign");
                }
            }
        }
    }

    private ChessPosition parsePosition(String pos) throws ParseException {
        if (pos.length() != 2) {
            throw new ParseException("Could not parse",2);
        }
        String row = String.valueOf(pos.charAt(0));
        int rowInt;
        char col = pos.charAt(1);
        int colInt;
        try {
            colInt = Integer.parseInt(String.valueOf(col));
        } catch (NumberFormatException e) {
            throw new ParseException("could not parse",2);
        }
        switch (row) {
            case "a" -> rowInt = 1;
            case "b" -> rowInt = 2;
            case "c" -> rowInt = 3;
            case "d" -> rowInt = 4;
            case "e" -> rowInt = 5;
            case "f" -> rowInt = 6;
            case "g" -> rowInt = 7;
            case "h" -> rowInt = 8;
            default -> {throw new ParseException("could not parse",2);}
        }
        return new ChessPosition(rowInt,colInt);
    }

    @Override
    public void notify(ServerMessage notification) {
        if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            gameData = notification.getGame();
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + notification.getMessage()+
                    EscapeSequences.RESET_TEXT_COLOR);
            // IF resign, gameOngoing = false;
            // IF checkmate, gameOngoing = false;
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
        if (Objects.equals(where, "login")) {
            return "register <Username> <Password> <Email> - to create an account\n" +
                    "login <Username> <Password> - to play chess\n" +
                    "quit - exit application\n" +
                    "help - view possible commands";
        } else if (Objects.equals(where, "authenticated")) {
            return "create <Name> - create a new game\n" +
                    "list - list games\n" +
                    "play <Id> [White|Black] - join a game\n" +
                    "observe <Id> - view a game\n" +
                    "logout - log out user\n" +
                    "quit - exit application\n" +
                    "help - view possible commands";
        } else if (Objects.equals(where, "in game")) {
            return "quit - exit current game" +
                    "\nhelp - view possible commands" +
                    "\nredraw - redraw chess board" +
                    "\nmove <Start> <End> - move a peice from the start point to the end point, " +
                    "positions should be of the form a1 or e6" +
                    "\nresign - forfeit and end game" +
                    "\nhighlight <Position> - highlights the legal moves of the selected position," +
                    "positions should be of the form a1 or e6";
        } else if (Objects.equals(where,"post resign")) {
            return "quit - exit current game" +
                    "\nhelp - view possible commands";
        } else if (Objects.equals(where, "observing")) {
            return "quit - exit current game" +
                    "\nredraw - redraw chess board" +
                    "\nhighlight <Position> - highlights the legal moves of the selected position," +
                    "positions should be of the form a1 or e6" +
                    "help - view possible commands";
        } else {
            return "position not recognized";
        }
    }

    private String outputGames() {
        String output = "";
        if (games.toArray().length == 0) {
            return "No current games";
        }
        ids.clear();
        int i = 1;
        for (GameData game : games) {
            String white = ((game.whiteUsername() != null) ? game.whiteUsername() : "[none]");
            String black = ((game.blackUsername() != null) ? game.blackUsername() : "[none]");
            output += String.format("%d. %s - White Player: %s, Black Player: %s %n",i,game.gameName(), white,black);
            ids.put(i,game.gameID());
            i++;
        }
        return output;
    }

    private String drawBoard(String color) {
        return drawBoard(color, null);
    }

    private String drawBoard(String color, ChessPosition highlight) {
        // To change from white to black, reverse each line, then reverse the order of each line
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (highlight != null) {
            validMoves = gameData.game().validMoves(highlight);
        }
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove c : validMoves) {
            endPositions.add(c.getEndPosition());
        }

        String[][] board = new String[10][10];
        String background = EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_DARK_GREY;
        String white = EscapeSequences.SET_BG_COLOR_WHITE;
        String black = EscapeSequences.SET_BG_COLOR_BLACK;
        String yellow = EscapeSequences.SET_BG_COLOR_YELLOW;
        String green = EscapeSequences.SET_BG_COLOR_GREEN;
        String letter;
        HashMap<ChessPiece, String> pieceMapper = new HashMap<>();
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING),
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + EscapeSequences.WHITE_KING);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN),
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + EscapeSequences.WHITE_QUEEN);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + EscapeSequences.WHITE_BISHOP);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + EscapeSequences.WHITE_KNIGHT);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK),
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + EscapeSequences.WHITE_ROOK);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + EscapeSequences.WHITE_PAWN);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING),
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_KING);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN),
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_QUEEN);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_BISHOP);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                EscapeSequences.SET_TEXT_COLOR_BLUE +  EscapeSequences.BLACK_KNIGHT);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                EscapeSequences.SET_TEXT_COLOR_BLUE+ EscapeSequences.BLACK_PAWN);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK),
                EscapeSequences.SET_TEXT_COLOR_BLUE+ EscapeSequences.BLACK_ROOK);

        for (int i = 0; i < 10; i++) {
            switch(i) {
                case(1) -> letter = "\u2003a ";
                case(2) -> letter = "\u2003b ";
                case(3) -> letter = "\u2003c ";
                case(4) -> letter = "\u2003d ";
                case(5) -> letter = "\u2003e ";
                case(6) -> letter = "\u2003f ";
                case(7) -> letter = "\u2003g ";
                case(8) -> letter = "\u2003h ";
                default -> letter = background + EscapeSequences.EMPTY;
            }
            board[0][i] = letter;
            board[9][i] = letter;
            if (i < 9 && i > 0) {
                board[i][0] = background + String.format("\u2003%d ",i);
                board[i][9] = background + String.format("\u2003%d ",i);
            }
        }

        var chessBoard = gameData.game().getBoard();

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = chessBoard.getPiece(position);
                if (position.equals(highlight)) {
                    if (piece == null) {
                        board[i][j] = yellow + EscapeSequences.EMPTY;
                    } else {
                        board[i][j] = yellow + pieceMapper.get(piece);
                    }
                } else if (endPositions.contains(position)) {
                    if (piece == null) {
                        board[i][j] = green + EscapeSequences.EMPTY;
                    } else {
                        board[i][j] = green + pieceMapper.get(piece);
                    }
                } else {
                    if ((i + j) % 2 == 0) {
                        if (piece == null) {
                            board[i][j] = black + EscapeSequences.EMPTY;
                        } else {
                            board[i][j] = black + pieceMapper.get(piece);
                        }
                    } else {
                        if (piece == null) {
                            board[i][j] = white + EscapeSequences.EMPTY;
                        } else {
                            board[i][j] = white + pieceMapper.get(piece);
                        }
                    }
                }
            }
        }

        String output = "";

        if (Objects.equals(color, "white")) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    output += board[9-i][j];
                }
                output += EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + "\n";
            }
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    output += board[i][9-j];
                }
                output += EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + "\n";
            }
        }
        output += EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;
        return output;
    }

    private String highlight(ChessPosition position) {
        return drawBoard(joinedColor,position);
    }
}
