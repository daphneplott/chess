package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
            4. A pawn can move diagonal if they can capture a piece of the other color.
            5. AND they can't move on top of another piece.
            6. AND make sure they don't move off the board
         Move Math:
         - "Up" - position.row + 1
         - "Down" - position.row - 1
         - "Left" - position.col - 1
         - "Right" - position.col + 1
         */
        promotion_options.remove(ChessPiece.PieceType.KING);
        promotion_options.remove(ChessPiece.PieceType.PAWN);
        ChessPosition next;
        ChessPiece piece;
        if (color == ChessGame.TeamColor.WHITE) {
            if (position.getRow() == 7) {
                next = new ChessPosition(position.getRow() + 1, position.getColumn());
                if (board.getPiece(next) == null) {
                    for (ChessPiece.PieceType promotion : promotion_options) {
                        moves.add(new ChessMove(position, next, promotion));
                    }
                }
                next = new ChessPosition(position.getRow() + 1, position.getColumn() - 1);
                piece = board.getPiece(next);
                if (position.getColumn() !=1 && piece != null && piece.getTeamColor() != color) {
                    for (ChessPiece.PieceType promotion : promotion_options) {
                        moves.add(new ChessMove(position, next, promotion));
                    }
                }
                next = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
                piece = board.getPiece(next);
                if (position.getColumn() != 8 && piece != null && piece.getTeamColor() != color) {
                    for (ChessPiece.PieceType promotion : promotion_options) {
                        moves.add(new ChessMove(position, next, promotion));
                    }
                }
            } else {
                next = new ChessPosition(position.getRow()+1,position.getColumn());
                if (board.getPiece(next) == null) {
                    moves.add(new ChessMove(position, next, null));
                    if (position.getRow() == 2) {
                        next = new ChessPosition(position.getRow()+2,position.getColumn());
                        if (board.getPiece(next) == null)
                            moves.add(new ChessMove(position, next, null));
                        }
                    }
                }
                next = new ChessPosition(position.getRow()+1,position.getColumn()-1);
                piece = board.getPiece(next);
                if (position.getColumn() != 1 && piece != null && piece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, next, null));
                }
                next = new ChessPosition(position.getRow()+1,position.getColumn()+1);
                piece = board.getPiece(next);
                if (position.getColumn() != 8 && piece != null && piece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, next, null));
                }
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (position.getRow() == 2) {
                next = new ChessPosition(position.getRow() - 1, position.getColumn());
                if (board.getPiece(next) == null) {
                    for (ChessPiece.PieceType promotion : promotion_options) {
                        moves.add(new ChessMove(position, next, promotion));
                    }
                }
                next = new ChessPosition(position.getRow() - 1, position.getColumn() - 1);
                piece = board.getPiece(next);
                if (position.getColumn() != 1 && piece != null && piece.getTeamColor() != color) {
                    for (ChessPiece.PieceType promotion : promotion_options) {
                        moves.add(new ChessMove(position, next, promotion));
                    }
                }
                next = new ChessPosition(position.getRow() - 1, position.getColumn() + 1);
                piece = board.getPiece(next);
                if (position.getColumn() != 8 &&  piece != null && piece.getTeamColor() != color) {
                    for (ChessPiece.PieceType promotion : promotion_options) {
                        moves.add(new ChessMove(position, next, promotion));
                    }
                }
            } else {
                next = new ChessPosition(position.getRow()-1,position.getColumn());
                if (board.getPiece(next) == null) {
                    moves.add(new ChessMove(position, next, null));
                    if (position.getRow() == 7) {
                        next = new ChessPosition(position.getRow()-2,position.getColumn());
                        if (board.getPiece(next) == null)
                            moves.add(new ChessMove(position, next, null));
                    }
                }
            }
            next = new ChessPosition(position.getRow()-1,position.getColumn()-1);
            piece = board.getPiece(next);
            if (position.getColumn() != 1 && piece != null && piece.getTeamColor() != color) {
                moves.add(new ChessMove(position, next, null));
            }
            next = new ChessPosition(position.getRow()-1,position.getColumn()+1);
            piece = board.getPiece(next);
            if (position.getColumn() != 8 && piece != null && piece.getTeamColor() != color) {
                moves.add(new ChessMove(position, next, null));
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
        PawnMove pawnMove = (PawnMove) o;
        return Objects.equals(moves, pawnMove.moves) && Objects.equals(promotion_options, pawnMove.promotion_options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moves, promotion_options);
    }
}
