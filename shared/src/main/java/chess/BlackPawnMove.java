package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BlackPawnMove {

    ArrayList<ChessMove> moves = new ArrayList<>();
    ArrayList<ChessPiece.PieceType> promotionTypes = new ArrayList<>(Arrays.asList(ChessPiece.PieceType.values()));
    ChessPosition next;
    ChessPiece atNext;
    ChessBoard board;
    ChessGame.TeamColor color;
    ChessPosition pos;

    public BlackPawnMove(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color) {

        promotionTypes.remove(ChessPiece.PieceType.KING);
        promotionTypes.remove(ChessPiece.PieceType.PAWN);
        int row = pos.getRow();
        int col = pos.getColumn();
        this.board = board;
        this.color = color;
        this.pos = pos;

        next = new ChessPosition(row - 1, col);
        if (canGoForward(false)) {
            if (row > 2) {
                moves.add(new ChessMove(pos, next, null));
            } else if (row == 2) {
                addPromotions();
            }
            if (row == 7) {
                addInGoTwoSpaces(row - 2, col);
            }
        }
        next = new ChessPosition(row - 1, col - 1);
        if (canGoForward(true)) {
            if (row > 2) {
                moves.add(new ChessMove(pos, next, null));
            } else if (row == 2) {
                addPromotions();
            }
        }
        next = new ChessPosition(row - 1, col + 1);
        if (canGoForward(true)) {
            if (row > 2) {
                moves.add(new ChessMove(pos, next, null));
            } else if (row == 2) {
                addPromotions();
            }
        }
    }

    private void addPromotions() {
        for (ChessPiece.PieceType promotion : promotionTypes) {
            moves.add(new ChessMove(pos, next, promotion));
        }
    }

    private boolean canGoForward(boolean take) {
        if (next.inRange()) {
            atNext = board.getPiece(next);
            if (!take) {
                return atNext == null;
            } else {
                return atNext != null && atNext.getTeamColor() != color;
            }
        }
        return false;
    }

    private void addInGoTwoSpaces(int adjustedRow, int adjustedCol) {
        next = new ChessPosition(adjustedRow, adjustedCol);
        atNext = board.getPiece(next);
        if (atNext == null) {
            moves.add(new ChessMove(pos, next, null));
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
        BlackPawnMove pawnMove = (BlackPawnMove) o;
        return Objects.equals(moves, pawnMove.moves) && Objects.equals(promotionTypes, pawnMove.promotionTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moves, promotionTypes);
    }
}