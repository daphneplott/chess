package chess;

import java.util.ArrayList;
import java.util.Objects;


public class RookMove {
    private ArrayList<ChessMove> moves = new ArrayList<>();

    public RookMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        /*
        General notes I want to copy to all things:
        - Don't move off the board
        - You can't capture a piece of your color

        A rook can move along a straight line
        It only moves until it runs into a piece
        It can capture if the peice is of the other team
        Math
        - +-x in one and zero in the other

         */

        ChessPosition next;
        ChessPiece piece;
        /*
        This first for loop will go with straight up (+, 0)
         */
        for (int x = 1; x < 8; x++) {
            if (position.getRow() + x < 9) {
                next = new ChessPosition(position.getRow() + x, position.getColumn());
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
        This second for loop will go with straight down (-, 0)
         */
        for (int x = 1; x < 8; x++) {
            if (position.getRow() - x >0) {
                next = new ChessPosition(position.getRow() - x, position.getColumn());
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
        This third for loop will do straight right (0, +)
         */
        for (int x = 1; x < 8; x++) {
            if (position.getColumn() +x < 9) {
                next = new ChessPosition(position.getRow(), position.getColumn() + x);
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
        This fourth for loop will do straight left (0,-)
         */
        for (int x = 1; x < 8; x++) {
            if (position.getColumn() -x >0) {
                next = new ChessPosition(position.getRow(), position.getColumn() - x);
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
    public ArrayList<ChessMove> getMoves() {
        return this.moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RookMove rookMove = (RookMove) o;
        return Objects.equals(moves, rookMove.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(moves);
    }
}