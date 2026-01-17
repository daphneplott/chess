package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor color;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        /* The basic outline of this will be as follows:
        1. Ask what type of piece this is
        2. Call the associated class constructor, ie BishopMove
        2.1. The class BishopMove will need the board and position and color
        3. The class BishopMove will do all the math
        3.1 The class BishopMove will create all the instances of ChessMove as it sees fit
        4. The class BishopMove will have some method or attribute that will return a Collection<ChessMove>
         */
        if (this.type == PieceType.PAWN) {
            PawnMove pawnMove = new PawnMove(board,myPosition,this.color);
            return pawnMove.getMoves();
        } else if (this.type == PieceType.QUEEN) {
            QueenMove queenMove = new QueenMove(board, myPosition,this.color);
            return queenMove.getMoves();
        } else if (this.type == PieceType.ROOK) {
            RookMove rookMove = new RookMove(board,myPosition,this.color);
            return rookMove.getMoves();
        } else if (this.type == PieceType.KING) {
            KingMove kingMove = new KingMove(board,myPosition,this.color);
            return kingMove.getMoves();
        } else if (this.type == PieceType.KNIGHT) {
            KnightMove knightMove = new KnightMove(board, myPosition,this.color);
            return knightMove.getMoves();
        } else { // ie: if (this.type == PieceType.BISHOP)
            BishopMove bishopMove = new BishopMove(board, myPosition,this.color);
            return bishopMove.getMoves();
        } // I don't know HOW it would happen ... but if our piece has some other type than these, this function will break.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                color +
                ", " + type +
                '}';
    }
}
