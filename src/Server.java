import java.util.Collection;
import java.util.Queue;
import java.util.Set;

public class Server {
    private Connection connectionWatch;
    private Set<Connection> connectedClients;
    private Queue<Message> unsentMessages;

    public Server(Connection connectionWatch, Set<Connection> connectedClients, Queue<Message> unsentMessages) {

    }

    public Collection<Connection> getConnectedClients() {
        return null;
    }

    public Collection<Message> getUnsentMessages() {
        return null;
    }

    // Starts the server
    public void run() {

    }

    // Disconnects all of the clients and stops the server
    public void stop() {

    }

    // Sends a message to all connectedClients
    public void sendMessage(Message message) {
    }

    // Adds new connections to and removes disconnected clients from connectedClients
    public void checkForConnections() {
    }

    // Checks for new messages and adds any new messages to unsentQueue
    public void checkForMessages() {
    }
}
