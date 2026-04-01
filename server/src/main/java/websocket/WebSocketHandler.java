package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDaoInterface;
import dataaccess.BadDataRequestException;
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
    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
    private final GameDaoInterface gameDao;
    private final AuthDaoInterface authDao;
    private ArrayList<Integer> endedGames;

    public WebSocketHandler(GameDaoInterface gameDao, AuthDaoInterface authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
        endedGames = new ArrayList<>();
    }

    public void clear() {
        endedGames = new ArrayList<>();
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        connections.record(ctx.session);
        UserGameCommand action = gson.fromJson(ctx.message(),UserGameCommand.class);
        String username;
        if (action.getUsername() != null && !action.getUsername().equals("null")) {
            username = action.getUsername();
        } else {
            try {
                username = authDao.getAuth(action.getAuthToken()).username();
            } catch (DataAccessException e) {
                var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null ,"Error: Could not connect to database");
                connections.sendToOne(ctx.session, notification);
                return;
            } catch (BadDataRequestException e) {
                var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null,"Error: AuthData does not exist");
                connections.sendToOne(ctx.session,notification);
                return;
            }
        }

        GameData game;
        String color;
        ChessGame.TeamColor teamColor;
        try {
            game = gameDao.getGame(action.getGameID());
            if (username.equals(game.whiteUsername())) {
                color = "white";
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(game.blackUsername())) {
                color = "black";
                teamColor = ChessGame.TeamColor.BLACK;
            } else {
                color = "observer";
                teamColor = null;
            }
        } catch (DataAccessException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error: Could not connect to database");
            connections.sendToOne(ctx.session, notification);
            return;
        } catch (BadDataRequestException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error: Game ID not found");
            connections.sendToOne(ctx.session, notification);
            return;
        }
        switch (action.getCommandType()) {
            case CONNECT -> connect(username,action.getGameID(), color,ctx.session);
            case MAKE_MOVE -> makeMove(username,action.getGameID(),action.getAuthToken(), action.getMove(), ctx.session,teamColor);
            case LEAVE -> leave(username, action.getGameID(),ctx.session);
            case RESIGN -> resign(username, action.getGameID(),ctx.session, color);
        }
    }

    private void resign(String username, Integer gameID, Session session, String color) {
        if ("observer".equals(color)) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null,"Error: observer cannot resign");
            connections.sendToOne(session, notification);
            return;
        }

        if (endedGames.contains(gameID)) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,"Error: game has already ended");
            connections.sendToOne(session, notification);
            return;
        }

        endedGames.add(gameID);

        String message = String.format("%s has forfeited", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message,null);
        connections.broadcast(null, notification, gameID);
    }

    private void leave(String username, Integer gameID, Session session) {
        var message = String.format("%s has exited the game",username);
        try {
            GameData currentGame = gameDao.getGame(gameID);
            GameData updatedGame;
            if (username.equals(currentGame.whiteUsername())) {
                updatedGame = new GameData(currentGame.gameID(),null,currentGame.blackUsername(),currentGame.gameName(),currentGame.game());
                gameDao.updateGame(updatedGame,gameID);
            } else if (username.equals(currentGame.blackUsername())) {
                updatedGame = new GameData(currentGame.gameID(),currentGame.whiteUsername(),null,currentGame.gameName(),currentGame.game());
                gameDao.updateGame(updatedGame,gameID);
            }
        } catch (DataAccessException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,"Error: Could not connect to database");
            connections.broadcast(null, notification,gameID);
            return;
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message,null);
        connections.broadcast(session, notification, gameID);
        connections.remove(session);
    }

    private void makeMove(String username, Integer gameID, String authToken, ChessMove move, Session session, ChessGame.TeamColor color) {
        if (endedGames.contains(gameID)) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null,"Error: game has ended");
            connections.sendToOne(session, notification);
            return;
        }
        try {
            GameData gameData = gameDao.getGame(gameID);
            ChessGame game = gameData.game();
            if (!game.getTeamTurn().equals(color)) {
                var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null,"Error: not your turn");
                connections.sendToOne(session, notification);
                return;
            }
            game.makeMove(move);
            gameDao.updateGame(gameData,gameID);
            String start = parsePosition(move.getStartPosition());
            String end = parsePosition(move.getEndPosition());
            String message1 = String.format("%s moved the piece from %s to %s",username,start,end);
            String otherUser = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            String message2 = "";
            if (game.isInCheckmate(game.getTeamTurn())) {
                message2 = String.format("%s is in checkmate", otherUser);
            } else if (game.isInCheck(game.getTeamTurn())) {
                message2 = String.format("%s is in check", otherUser);
            }
            var notification3 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,gameData);
            var notification1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,message1,null);

            connections.broadcast(session, notification1, gameID);
            connections.broadcast(null, notification3, gameID); // Load Game

            if (!message2.isEmpty()) {
                var notification2 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,message2,null);
                connections.broadcast(null, notification2, gameID);
            }
        } catch (DataAccessException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, "Error: Could not connect to database");
            connections.sendToOne(session, notification);
        } catch (BadDataRequestException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null,"Error: Game ID not found");
            connections.sendToOne(session,notification);
        } catch (InvalidMoveException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null,"Error: Invalid move");
            connections.sendToOne(session, notification);
        }
    }

    private String parsePosition(ChessPosition pos) {
        String[] change = {"a","b","c","d","e","f","g","h"};
        String positionName = "";
        positionName += change[pos.getColumn() - 1];
        positionName += pos.getRow();
        return positionName;
    }

    private void connect(String username, Integer gameID, String color, Session session) {
        connections.add(session, gameID);
        String message;

        GameData game;
        try {
            game = gameDao.getGame(gameID);
        } catch (DataAccessException e) {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null,"Error: could not access database");
            connections.sendToOne(session, notification);
            return;
        }

        if (color.equals("observer")) {
            message = String.format("%s is now observing", username);
        } else {
            message = String.format("%s is now playing as %s", username, color);
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message,null);
        var notification1 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcast(session, notification, gameID);
        connections.sendToOne(session, notification1);
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
