package websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();
    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

    public void record(Session session) {
        if (!connections.containsKey(session)) {
            connections.put(session, -1);
        }
    }

    public void add(Session session, Integer gameID) {
        connections.replace(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session exclude, ServerMessage notification, Integer gameID) {
        String msg = gson.toJson(notification);
        for (Session c : connections.keySet(gameID)) {
            if (!connections.get(c).equals(gameID)) {
                continue;
            }
            if (c.isOpen()) {
                if (!c.equals(exclude)) {
                    try {
                        c.getRemote().sendString(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void sendToOne(Session session, ServerMessage notification) {
        String msg = gson.toJson(notification);
        for (Session c : connections.keySet()) {
            if (c.isOpen()) {
                if (c.equals(session)) {
                    try {
                        c.getRemote().sendString(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
