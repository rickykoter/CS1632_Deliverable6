import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection implements Connection{
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String server;
    private int port;

    public ServerConnection(String serverAddress, int portNumber){
        server = serverAddress;
        port = portNumber;
    }
    @Override
    public boolean send(Message message) {
        return false;
    }

    @Override
    public Message receive() {
        return null;
    }

    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public boolean disconnect() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }
}
