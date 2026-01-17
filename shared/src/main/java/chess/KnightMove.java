package chess;

import java.util.ArrayList;
import java.util.Arrays;

public class KnightMove {
    private ArrayList<ChessMove> moves = new ArrayList<>();

    record Pair(int x, int y) {}

    public KnightMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        /*
        General notes I want to copy to all things:
        - Don't move off the board
        - You can't capture a piece of your color

        A knight moves in an L shape.
            - Math?
                This is a +- 2 in one direction, and a +- 1 in the other direction.
        A knight can capture any piece if it normally moves there - but only if it's of the other color.
        A knight doesn't care if pieces are 'in the way'
        Make sure the knight doesn't move off the board

         */
        ArrayList<Pair> potential_math = new ArrayList<Pair>(Arrays.asList(
                new Pair(1,2), new Pair(1,-2), new Pair(-1,2),new Pair(-1,-2),
                new Pair(2,1), new Pair(2, -1), new Pair(-2, 1), new Pair(-2, -1)
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
}
