package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class KingMove {
    private ArrayList<ChessMove> moves = new ArrayList<>();

    record Pair(int x, int y) {}

    public KingMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        /*
        General notes I want to copy to all things:
        - Don't move off the board
        - You can't capture a piece of your color

        A king can move one square in any direction, include diagonal.
        Math:
         - All pairs of +-1 or 0

         */
        ArrayList<Pair> potential_math = new ArrayList<>(Arrays.asList(
                new Pair(1,0), new Pair(1,1), new Pair(1,-1),
                new Pair(-1,0),new Pair(-1,1), new Pair(-1,-1),
                new Pair(0,1), new Pair(0,-1)
        ));
        ChessPosition next;
        ChessPiece piece;
        for (Pair pair : potential_math) {
            if (position.getColumn() + pair.y() < 9 && position.getColumn() + pair.y() >0 &&
                    position.getRow() + pair.x() < 9&& position.getRow()+pair.x() > 0
            ) {
                next = new ChessPosition(position.getRow() + pair.x(), position.getColumn() + pair.y());
                piece = board.getPiece(next);
                if (piece == null || piece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, next, null));
                }
            }
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
        KingMove kingMove = (KingMove) o;
        return Objects.equals(moves, kingMove.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(moves);
    }
}
