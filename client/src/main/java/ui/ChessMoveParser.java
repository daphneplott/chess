package ui;

import chess.ChessPiece;
import chess.ChessPosition;

import java.text.ParseException;

public class ChessMoveParser {

    public static ChessPosition parsePosition(String pos) throws ParseException {
        if (pos.length() != 2) {
            throw new ParseException("Could not parse",2);
        }
        String col = String.valueOf(pos.charAt(0));
        int rowInt;
        char row = pos.charAt(1);
        int colInt;
        try {
            rowInt = Integer.parseInt(String.valueOf(row));
        } catch (NumberFormatException e) {
            throw new ParseException("could not parse",2);
        }
        switch (col) {
            case "a" -> colInt = 1;
            case "b" -> colInt = 2;
            case "c" -> colInt = 3;
            case "d" -> colInt = 4;
            case "e" -> colInt = 5;
            case "f" -> colInt = 6;
            case "g" -> colInt = 7;
            case "h" -> colInt = 8;
            default -> throw new ParseException("could not parse",2);
        }
        return new ChessPosition(rowInt,colInt);
    }

    public static ChessPiece.PieceType parsePromotion(String promotion) throws ParseException {
        promotion = promotion.toLowerCase();
        return switch (promotion) {
            case "null" -> null;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            default -> throw new ParseException("Could not parse", 2);
        };
    }
}
