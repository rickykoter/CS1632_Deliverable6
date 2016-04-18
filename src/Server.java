import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;

public class Server implements Runnable {
    private ClientConnectionRunner connectionWatch;
    private Set<Connection> connectedClients;
    private Object clientsLock = new Object();
    private Queue<Message> unsentMessages;
    private Object messagesLock = new Object();
    private boolean running = true;
    Thread senderThread = null;
    Thread connectionThread = null;

    public Server(ClientConnectionRunner connectionWatch, Set<Connection> connectedClients, Queue<Message> unsentMessages) throws IllegalArgumentException {
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
        senderThread = new Thread() {
            public void run() {
                while(isRunning()) {
                    Message message;
                    boolean moreMessages;
                    do {
                        synchronized (messagesLock) {
                            message = unsentMessages.poll();
                            moreMessages = !unsentMessages.isEmpty();
                        }
                        if(message != null) {
                            sendMessage(message);
                        }
                    } while(moreMessages);
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        senderThread.start();

        connectionThread = new Thread() {
            public void run() {
                while(isRunning()) {
                    checkForConnections();
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        connectionThread.start();
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
        synchronized (clientsLock) {
            for(Connection c : connectedClients) {
                c.send(message);
            }
        }
    }

    // Adds new clients to connections and removes disconnected clients
    public void checkForConnections() {
        Collection<Connection> connectionsToClose = getDisconnectedClients();
        closeConnections(connectionsToClose);
        checkForNewConnection();
    }

    private Collection<Connection> getDisconnectedClients() {
        Collection<Connection> connectionsToClose = new ArrayList<>();
        synchronized(clientsLock) {
            for(Connection c : getConnectedClients()) {
                if(!c.isOpen()) {
                    connectionsToClose.add(c);
                }
            }
        }
        return connectionsToClose;
    }

    private void closeConnections(Collection<Connection> connectionsToClose) {
        for(Connection c : connectionsToClose) {
            synchronized (clientsLock) {
                getConnectedClients().remove(c);
            }
        }
    }

    private void checkForNewConnection() {
        Connection socket = safeReceive(connectionWatch);
        if(socket != null && socket.isOpen()) {
            synchronized (clientsLock) {
                getConnectedClients().add(socket);
            }

            Thread clientThread = new Thread() {
                public void run() {
                    while(isRunning() && socket.isOpen()) {
                        Message m = safeReceive(socket);
                        if(m != null) {
                            synchronized (messagesLock) {
                                unsentMessages.add(m);
                            }
                        }
                        try {
                            sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(socket.isOpen()) {
                        socket.disconnect();
                    }
                }
            };
            clientThread.start();
        }
    }

    private static <T> T safeReceive(ReadOnlyConnection connection) {
        T receivedData = null;
        try {
            receivedData = (T)connection.receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receivedData;
    }

    // Checks for new messages and adds any new messages to unsentQueue
    public void checkForMessages() {
        for(Connection c : getConnectedClients()) {
            Message m = safeReceive(c);
            while(m != null) {
                getUnsentMessages().add(m);
                m = safeReceive(c);
            }
        }
    }
}
