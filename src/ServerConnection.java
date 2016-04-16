import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection implements Connection{
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String server;
    private int port;

    public ServerConnection(Socket s){
        server = s.getRemoteSocketAddress().toString();
        port = s.getPort();
    }
    @Override
    public boolean send(Object message) {
        return false;
    }

    @Override
    public Object receive() {
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
