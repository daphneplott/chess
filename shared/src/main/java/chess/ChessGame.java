package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }


    /**
     * Determines if a particular move is valid.
     * A move is NOT valid if doing so would leave the King able to be checked.
     *
     * @param move is the move in question, piece gives info about the piece moving
     * @return True/False depending on if the move is valid.
     */
    public boolean isValid(ChessMove move, ChessPiece piece) {
        // Preview the move, and then ask if THAT board is in check.
        ChessBoard preview = board.clone();
        preview.removePiece(move.getStartPosition());
        if (move.getPromotionPiece() == null) {
            preview.addPiece(move.getEndPosition(),piece);
        } else {
            preview.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        return !isInCheck(piece.getTeamColor(), preview);
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // Get the piece. If no piece, return null.
        // Get all possible moves for that piece.
        // Call isValid on each move. If valid, add to a new set.
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {return null;}
        var possibleMoves = piece.pieceMoves(board,startPosition);
        ArrayList<ChessMove> valid = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            if (isValid(move,piece)) {
                valid.add(move);
            }
        }
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Get the piece in that position
        // Calls isValid to check if move is valid
        // If valid, remove the piece from start position, and put it in the end position.
        // If it has a promotion, add THAT piece there instead.
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (! isValid(move,piece)) {throw new InvalidMoveException();}
        board.removePiece(move.getStartPosition());
        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getEndPosition(),piece);
        } else {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        if (teamTurn.equals(TeamColor.WHITE)) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Refactor for calling isInCheck
     *
     * @param teamColor denotes the team color in question
     * @param board denotes the chessboard it should look at
     * @return whether that colored king is in check
     */
    private boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        // For all pieces in other team, generate all valid moves.
            // ACTUALLY ... I think it might just be all possible moves.
            // It doesn't matter if moving would put your king in danger if that move takes the other king.
            // ahhhh... infinite recursion avoided.
        // If any of those moves equal where the King is, return true.
        // I guess you'll have to 'find' the King when you iterate through the board.
        ArrayList<ChessPosition> possibleMoves = new ArrayList<>();
        ChessPosition myKing = new ChessPosition(1,1);
        for (Map.Entry<ChessPosition, ChessPiece> entry : board.getEntries()) {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();
            if (piece.getTeamColor() != teamColor) {
                Collection<ChessMove> someMoves = piece.pieceMoves(board,position);
                for (ChessMove move : someMoves) {
                    possibleMoves.add(move.endPosition);
                }
            } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                myKing = position;
            }
        }
        return possibleMoves.contains(myKing);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor,this.board);
    }

    private boolean movesLeft(TeamColor teamColor) {
        for (Map.Entry<ChessPosition, ChessPiece> entry : board.getEntries()) {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();
            if (piece.getTeamColor() == teamColor) {
                Collection<ChessMove> someMoves = validMoves(position);
                if (!someMoves.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // Ask - is in check?
        // See if I have any valid moves
        boolean b1 = isInCheck(teamColor);
        boolean b2 = movesLeft(teamColor);
        return b1 && !b2;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // Ask - is in check?
        // See if I have any valid moves
        boolean b1 = isInCheck(teamColor);
        boolean b2 = movesLeft(teamColor);
        return !b1 && !b2;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
