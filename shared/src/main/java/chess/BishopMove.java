package chess;

import java.util.ArrayList;
import java.util.Arrays;

public class BishopMove {
    private ArrayList<ChessMove> moves = new ArrayList<>();

    public BishopMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        /*
        General notes I want to copy to all things:
        - Don't move off the board
        - You can't capture a piece of your color

        A bishop moves on a diagonal
        A bishop can't move past objects, but can capture a piece in its path
        Math
        - +-x paired with +-x

         */

        ChessPosition next;
        ChessPiece piece;
        /*
        This first for loop with go diagonals up and to the right (both positive)
         */
        for (int x = 1; x < 8; x++) {
            if (position.getRow() + x < 9 && position.getColumn() + x < 9) {
                next = new ChessPosition(position.getRow() + x, position.getColumn() + x);
                piece = board.getPiece(next);
                if (piece == null) {
                    moves.add(new ChessMove(position, next, null));
                } else if (piece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, next, null));
                    break;
                } else {break;}
            } else {break;}
        }
        /*
        This second for loop will go with diagonals up and to the left (+, -)
         */
        for (int x = 1; x < 8; x++) {
            if (position.getRow() + x < 9 && position.getColumn() - x > 0) {
                next = new ChessPosition(position.getRow() + x, position.getColumn() - x);
                piece = board.getPiece(next);
                if (piece == null) {
                    moves.add(new ChessMove(position, next, null));
                } else if (piece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, next, null));
                    break;
                } else {break;}
            } else {break;}
        }
        /*
        This third for loop will do diagonals down and to the right (-, +)
         */
        for (int x = 1; x < 8; x++) {
            if (position.getRow() - x > 0 && position.getColumn() + x < 9) {
                next = new ChessPosition(position.getRow() - x, position.getColumn() + x);
                piece = board.getPiece(next);
                if (piece == null) {
                    moves.add(new ChessMove(position, next, null));
                } else if (piece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, next, null));
                    break;
                } else {break;}
            } else {break;}
        }
        /*
        This fourth for loop will do diagonals down and to the left (both negative)
         */
        for (int x = 1; x < 8; x++) {
            if (position.getRow() -x >0 && position.getColumn() -x >0) {
                next = new ChessPosition(position.getRow() - x, position.getColumn() - x);
                piece = board.getPiece(next);
                if (piece == null) {
                    moves.add(new ChessMove(position, next, null));
                } else if (piece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, next, null));
                    break;
                } else {break;}
            } else {break;}
        }
    }
}
