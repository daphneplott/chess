package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;

import java.util.*;


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
                default -> System.out.println(help("login"));
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

        ServerFacade.listGamesResponse listGamesResponse = server.list(auth);
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
                    System.out.println(outputGames());
                }
                case "play" -> {
                    if (tokens.length == 3) {
                        ServerFacade.logoutJoinObserveResponse response = server.join(tokens, auth);
                        if (response.responseCode() == 200) {
                            try {
                                joinedID = Integer.parseInt(tokens[1]);
                                joinedColor = tokens[2];
                                return "gameplay";
                            } catch (NumberFormatException e) {
                                System.out.println("Expected: join <ID> [White|Black]");
                            }
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
                        try {
                            int gameID = Integer.parseInt(tokens[1]);
                            if (gameID <= games.toArray().length) {
                                joinedID = gameID;
                                joinedColor = "white";
                                return "gameplay";
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Expected: observe <ID>");
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
                default -> System.out.println(help("in game"));
            }
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
                    "join <Id> [White|Black] - join a game\n" +
                    "observe <Id> - view a game\n" +
                    "logout - log out user\n" +
                    "quit - exit application\n" +
                    "help - view possible commands";
        } else if (Objects.equals(where, "in game")) {
            return "quit - exit current game\nhelp - view possible commands";
        } else {
            return "position not recognized";
        }
    }

    private String outputGames() {
        String output = "";
        for (GameData game : games) {
            String white = ((game.whiteUsername() != null) ? game.whiteUsername() : "[none]");
            String black = ((game.blackUsername() != null) ? game.blackUsername() : "[none]");
            output += String.format("%d. %s - White Player: %s, Black Player: %s %n",game.gameID(),game.gameName(), white,black);
        }
        return output;
    }

    private String drawBoard(String color) {
        // To change from white to black, reverse each line, then reverse the order of each line
        String[][] board = new String[10][10];
        String background = EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_DARK_GREY;
        String white = EscapeSequences.SET_BG_COLOR_WHITE + EscapeSequences.SET_TEXT_COLOR_MAGENTA;
        String black = EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_BLUE;
        String letter;
        HashMap<ChessPiece, String> pieceMapper = new HashMap<>();
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING),EscapeSequences.WHITE_KING);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN),EscapeSequences.WHITE_QUEEN);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),EscapeSequences.WHITE_BISHOP);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), EscapeSequences.WHITE_KNIGHT);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK), EscapeSequences.WHITE_ROOK);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),EscapeSequences.WHITE_PAWN);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING),EscapeSequences.BLACK_KING);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN),EscapeSequences.BLACK_QUEEN);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),EscapeSequences.BLACK_BISHOP);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), EscapeSequences.BLACK_KNIGHT);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),EscapeSequences.BLACK_PAWN);
        pieceMapper.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK),EscapeSequences.BLACK_ROOK);

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
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(i,j));
                if ((i+j)%2 == 0) {
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

        String output = "";

        if (Objects.equals(color, "white")) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    output += board[9-i][j];
                }
            }
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    output += board[i][9-j];
                }
            }
        }
        return output;
    }
}
