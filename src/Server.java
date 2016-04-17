import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;

public class Server implements Runnable {
    private Connection connectionWatch;
    private Set<Connection> connectedClients;
    private Queue<Message> unsentMessages;
    private boolean running = true;

    public Server(Connection connectionWatch, Set<Connection> connectedClients, Queue<Message> unsentMessages) throws IllegalArgumentException {
        if(connectionWatch == null || connectedClients == null || unsentMessages == null) {
            throw new IllegalArgumentException("No arguments can be null");
        }
        this.connectionWatch = connectionWatch;
        this.connectedClients = connectedClients;
        this.unsentMessages = unsentMessages;
    }

    public Collection<Connection> getConnectedClients() {
        return connectedClients;
    }

    public Collection<Message> getUnsentMessages() {
        return unsentMessages;
    }

    // Starts the server
    @Override
    public void run() {
        while(isRunning()) {
            checkForConnections();
            checkForMessages();
            for(Message unsent : getUnsentMessages()) {
                try {
                    sendMessage(unsent);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            getUnsentMessages().clear();
        }
    }

    private synchronized boolean isRunning() {
        return running;
    }

    // Disconnects all of the clients and stops the server
    public synchronized void stop() {
        running = false;
    }

    // Sends a message to all connectedClients
    public void sendMessage(Message message) throws IllegalArgumentException {
        if(message == null) { throw new IllegalArgumentException("message cannot be null"); }
        for(Connection c : connectedClients) {
            c.send(message);
        }
    }

    // Adds new clients to connections and removes disconnected clients
    public void checkForConnections() {
        Collection<Connection> connectionsToClose = getDisconnectedClients();
        closeConnections(connectionsToClose);
        addNewConnections();
    }

    private Collection<Connection> getDisconnectedClients() {
        Collection<Connection> connectionsToClose = new ArrayList<>();
        for(Connection c : getConnectedClients()) {
            if(!c.isOpen()) {
                connectionsToClose.add(c);
            }
        }
        return connectionsToClose;
    }

    private void closeConnections(Collection<Connection> connectionsToClose) {
        for(Connection c : connectionsToClose) {
            getConnectedClients().remove(c);
        }
    }

    private void addNewConnections() {
        Connection c = (Connection)connectionWatch.receive();
        while(c != null) {
            getConnectedClients().add(c);
            c = (Connection)connectionWatch.receive();
        }
    }

    // Checks for new messages and adds any new messages to unsentQueue
    public void checkForMessages() {
        for(Connection c : getConnectedClients()) {
            Message m = (Message)c.receive();
            while(m != null) {
                getUnsentMessages().add(m);
                m = (Message)c.receive();
            }
        }
    }
}
