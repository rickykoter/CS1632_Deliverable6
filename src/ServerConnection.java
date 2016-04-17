import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection implements Connection{
    private Socket socket;
    private ChatOutputStream output;
    private ChatInputStream input;
    private String server;
    private int port;

    public ServerConnection(Socket s, ChatOutputStream cos, ChatInputStream cis){
        if(s == null || cos == null || cis == null){
            throw new IllegalArgumentException();
        }
        output = cos;
        input = cis;
        socket = s;
    }

    @Override
    public boolean send(Object message) {
        try {
            output.writeMessage(message);
            return true;
        } catch (IOException e){
            return false;
        }
    }

    @Override
    public Object receive() throws IOException, ClassNotFoundException {
        return input.readMessage();
    }

    @Override
    public boolean isOpen() {
        if (socket == null || socket.isClosed()) {
            return false;
        } else if (socket.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean disconnect() {
        if (isOpen()) {
            try {
                send(null);
                socket.close();
                input.close();
                output.close();
                socket = null;
                input = null;
                output = null;
                return true;
            } catch (IOException e){
                return false;
            }
        }
        return false;
    }
}
