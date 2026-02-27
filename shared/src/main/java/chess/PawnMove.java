package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PawnMove {

    ArrayList<ChessMove> moves = new ArrayList<>();

    public PawnMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        /*
        Here are a pawn's move:
            1. If they haven't moved yet (so are on row 2 for white or 7 for black), they can move one or two spaces up
            2. Otherwise, they can move one space up for white, one space down for black
            3. If the end position will be the end (8 for white, 1 for black), they also get to promote.
                3.1 The promotion should yield all possible promotion options, ie 6 moves
            4. A pawn can move diagonal if they can capture a piece of the other color.
            5. AND they can't move on top of another piece.
            6. AND make sure they don't move off the board
         Move Math:
         - "Up" - position.row + 1
         - "Down" - position.row - 1
         - "Left" - position.col - 1
         - "Right" - position.col + 1
         */

        if (color == ChessGame.TeamColor.WHITE) {
            WhitePawnMove whitePawnMove = new WhitePawnMove(board,position,color);
            moves = whitePawnMove.getMoves();
        }
        if (color == ChessGame.TeamColor.BLACK) {
            BlackPawnMove blackPawnMove = new BlackPawnMove(board, position,color);
            moves = blackPawnMove.getMoves();
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
        PawnMove pawnMove = (PawnMove) o;
        return Objects.equals(moves, pawnMove.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moves);
    }
}
