import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public class ClientConnectionRunner implements ReadOnlyConnection {
    private ServerSocketChannel socket;

    public ClientConnectionRunner(ServerSocketChannel socket) throws IllegalArgumentException {
        if(socket == null) { throw new IllegalArgumentException("No arguments can be null"); }
        this.socket = socket;
    }

    @Override
    public synchronized Object receive() {
        Object data = null;
        try {
            if(isOpen()) {
                data = ClientConnection.create(socket.accept());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public synchronized boolean disconnect() {
        boolean success = false;
        try {
            socket.close();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public synchronized boolean isOpen() {
        return socket.isOpen();
    }
}
