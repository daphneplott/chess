package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDaoInterface;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.ArrayList;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    Gson gson = new Gson();
    private GameDaoInterface gameDao;

    public WebSocketHandler(GameDaoInterface gameDao) {
        this.gameDao = gameDao;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        UserGameCommand action = gson.fromJson(ctx.message(),UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(action.getUsername(),action.getGameID(), action.getColor(),ctx.session);
            case MAKE_MOVE -> makeMove(action.getUsername(),action.getGameID(),action.getAuthToken(), action.getMove(), ctx.session);
            case LEAVE -> leave(action.getUsername(), action.getGameID(),ctx.session);
            case RESIGN -> resign(action.getUsername(), action.getGameID(),ctx.session);
        }
    }

    private void resign(String username, Integer gameID, Session session) {
        String message = String.format("%s has forfeited", username);
        try {
            GameData currentGame = gameDao.getGame(gameID);
            GameData updatedGame;
            if (currentGame.whiteUsername().equals(username)) {
                updatedGame = new GameData(currentGame.gameID(),null,currentGame.blackUsername(),currentGame.gameName(),currentGame.game());
            } else {
                updatedGame = new GameData(currentGame.gameID(),currentGame.whiteUsername(),null,currentGame.gameName(),currentGame.game());
            }
            gameDao.updateGame(updatedGame,gameID);
        } catch (DataAccessException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Could not connect to database");
            connections.broadcast(null, notification,gameID);
            return;
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification, gameID);
    }

    private void leave(String username, Integer gameID, Session session) {
        var message = String.format("%s has exited the game",username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification, gameID);
        connections.remove(session);
    }

    private void makeMove(String username, Integer gameID, String authToken, ChessMove move, Session session) {
        try {
            GameData gameData = gameDao.getGame(gameID);
            ChessGame game = gameData.game();
            game.makeMove(move);
            gameDao.updateGame(gameData,gameID);
            String start = parsePosition(move.getStartPosition());
            String end = parsePosition(move.getEndPosition());
            String message1 = String.format("%s moved the piece from %s to %s",username,start,end);
            String teamName = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? "White" : "Black";
            String message2 = "";
            if (game.isInCheckmate(game.getTeamTurn())) {
                message2 = String.format("%s is in checkmate", teamName);
            } else if (game.isInCheck(game.getTeamTurn())) {
                message2 = String.format("%s is in check", teamName);
            }
            var notification3 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,gameData);
            var notification1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,message1);
            connections.broadcast(null, notification3, gameID);
            connections.broadcast(null, notification1, gameID);

            if (!message2.isEmpty()) {
                var notification2 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,message2);
                connections.broadcast(null, notification2, gameID);
            }
        } catch (DataAccessException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Could not connect to database");
            connections.broadcast(null, notification,gameID);
        } catch (InvalidMoveException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: Invalid move");
            connections.sendToOne(session, notification,gameID);
        }
    }

    private String parsePosition(ChessPosition pos) {
        String[] change = {"a","b","c","d","e","f","g","h"};
        String positionName = "";
        positionName += change[pos.getRow() - 1];
        positionName += pos.getColumn();
        return positionName;
    }

    private void connect(String username, Integer gameID, String color, Session session) {
        connections.add(session, gameID);
        String message;
        if (color.equals("observer")) {
            message = String.format("%s is now observing", username);
        } else {
            message = String.format("%s is now playing as %s", username, color);
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification, gameID);
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
