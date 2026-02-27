package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class KnightMove {
    private ArrayList<ChessMove> moves;

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
        ArrayList<KingMove.Pair> potentialMath = new ArrayList<KingMove.Pair>(Arrays.asList(
                new KingMove.Pair(1,2), new KingMove.Pair(1,-2), new KingMove.Pair(-1,2),new KingMove.Pair(-1,-2),
                new KingMove.Pair(2,1), new KingMove.Pair(2, -1), new KingMove.Pair(-2, 1), new KingMove.Pair(-2, -1)
        ));

        moves = KingMove.getMovesWithSpecificSubset(potentialMath,board,position,color);

    }

    public ArrayList<ChessMove> getMoves() {
        return this.moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KnightMove that = (KnightMove) o;
        return Objects.equals(moves, that.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(moves);
    }
}
