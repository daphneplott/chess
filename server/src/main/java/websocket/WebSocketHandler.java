package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDaoInterface;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

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
        // Get game with id
        // Check if move is legal. If not, send only client error
        // Make move in game
        // update database
        // Check if in check/checkmate
        // Send load_game
        // Send message about what move was made
        // Send check/checkmate info
        try {
            GameData gameData = gameDao.getGame(gameID);
            ChessGame game = gameData.game();
            game.makeMove();
        } catch (DataAccessException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Could not connect to database");
            connections.broacast(null, notification,gameID);
        }

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
