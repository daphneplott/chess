package chess;

import java.util.ArrayList;
import java.util.Objects;

public class QueenMove {

    private ArrayList<ChessMove> moves = new ArrayList<>();

    public QueenMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        /*
        General notes I want to copy to all things:
        - Don't move off the board
        - You can't capture a piece of your color

        So, a queen can move anywhere that a Rook can more or a bishop can move, right?


         */

        BishopMove bishopMove = new BishopMove(board,position,color);
        RookMove rookMove = new RookMove(board, position,color);
        moves.addAll(bishopMove.getMoves());
        moves.addAll(rookMove.getMoves());
    }

    public ArrayList<ChessMove> getMoves() {
        return this.moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueenMove queenMove = (QueenMove) o;
        return Objects.equals(moves, queenMove.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(moves);
    }
}
