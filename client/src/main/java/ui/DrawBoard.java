package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class DrawBoard {

    private final GameData gameData;

    public DrawBoard(GameData gameData) {
        this.gameData = gameData;
    }

    public String drawBoard(String color, ChessPosition highlight) {
        // To change from white to black, reverse each line, then reverse the order of each line
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (highlight != null) {
            validMoves = gameData.game().validMoves(highlight);
            if (validMoves == null) {
                validMoves = new ArrayList<>();
            }
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
        HashMap<ChessPiece, String> pieceMapper = createPieceMapper();
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
                    if ((i + j) % 2 == 0 && piece == null) {
                        board[i][j] = black + EscapeSequences.EMPTY;
                    } else if ((i + j) % 2 == 0 && piece != null) {
                        board[i][j] = black + pieceMapper.get(piece);
                    } else if (piece == null) {
                        board[i][j] = white + EscapeSequences.EMPTY;
                    } else {
                        board[i][j] = white + pieceMapper.get(piece);
                    }
                }
            }
        }
        StringBuilder output = new StringBuilder();
        if (Objects.equals(color, "white")) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    output.append(board[9 - i][j]);
                }
                output.append(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + "\n");
            }
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    output.append(board[i][9 - j]);
                }
                output.append(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + "\n");
            }
        }
        output.append(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        return output.toString();
    }

    private HashMap<ChessPiece, String> createPieceMapper() {
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
        return pieceMapper;
    }
}
