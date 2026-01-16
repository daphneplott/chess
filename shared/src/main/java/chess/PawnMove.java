package chess;

import java.util.ArrayList;
import java.util.Arrays;

public class PawnMove {

    ArrayList<ChessMove> moves = new ArrayList<>();
    ArrayList<ChessPiece.PieceType> promotion_options = new ArrayList<>(Arrays.asList(ChessPiece.PieceType.values()));


    public PawnMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        /*
        Here are a pawn's move:
            1. If they haven't moved yet (so are on row 2 for white or 7 for black), they can move one or two spaces up
            2. Otherwise, they can move one space up for white, one space down for black
            3. If the end position will be the end (8 for white, 1 for black), they also get to promote.
                3.1 The promotion should yield all possible promotion options, ie 6 moves
            4. A pawn can move diagonal if they can capture a piece
            5. AND they can't move on top of another piece.
         Move Math:
         - "Up" - position.row + 1
         - "Down" - position.row - 1
         - "Left" - position.col - 1
         - "Right" - position.col + 1
         */
        promotion_options.remove(ChessPiece.PieceType.KING);
        promotion_options.remove(ChessPiece.PieceType.PAWN);
        if (color == ChessGame.TeamColor.WHITE) {
            if (position.getRow() == 2) {
                moves.add(new ChessMove(position, new ChessPosition(position.getRow() + 1, position.getColumn()),null));
                moves.add(new ChessMove(position, new ChessPosition(position.getRow() + 2, position.getColumn()),null));
            } else if (position.getRow() == 7) {
                for (ChessPiece.PieceType promotion : promotion_options) {
                    moves.add(new ChessMove(position, new ChessPosition(position.getRow() + 1, position.getColumn()),promotion));
                }
            }
        } else if (color == ChessGame.TeamColor.BLACK) {

        }
    }
}
