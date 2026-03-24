package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();
    Gson gson = new Gson();

    public void add(Session session, Integer gameID) {
        connections.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session exclude, ServerMessage notification, Integer gameID) throws IOException {
        String msg = gson.toJson(notification);
        for (Session c : connections.keySet(gameID)) {
            if (c.isOpen()) {
                if (!c.equals(exclude)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
