package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        try {
            url = url.replace("http", "ws");
            URI socket = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this,socket);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = gson.fromJson(message,ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void enterGame(String username, Integer gameID, String color) throws IOException {
        // UserGameCommand -> commandType, authToken, gameID, username, color, move
        // connect
        // Needs to know: String color -> observing, white, black
        //              String username, Integer gameID,
        var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT,"",gameID,username,color);
        this.session.getBasicRemote().sendText(gson.toJson(action));
    }

    public void leaveGame(String username, Integer gameID) throws IOException {
        var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE,"",gameID, username,"");
        this.session.getBasicRemote().sendText(gson.toJson(action));
    }

    public void resign(String username, Integer gameID) throws IOException {
        // REsign
        // username, gameID
        var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN,"",gameID,username,"");
        this.session.getBasicRemote().sendText(gson.toJson(action));
    }

    public void makeMove(String username, Integer gameID, String authToken, ChessMove move) throws IOException {
        // username, gameID, authToken, move
        var action = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE,authToken,gameID,username,"",move);
        this.session.getBasicRemote().sendText(gson.toJson(action));
    }


}
