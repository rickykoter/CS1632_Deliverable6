import java.io.IOException;
import java.net.Socket;
/**
 * ServerConnection that uses sockets, input streams and output streams to communicate with a server.
 * By Richard Kotermanski and Jon Povirk
 */
public class ServerConnection implements Connection{
    private Socket socket;
    private ChatOutputStream output;
    private ChatInputStream input;

    public ServerConnection(Socket s, ChatOutputStream cos, ChatInputStream cis){
        if(s == null || cos == null || cis == null){
            throw new IllegalArgumentException();
        }
        output = cos;
        input = cis;
        socket = s;
    }

    // Sends an object (nullable) through the output stream.
    // Returns true if no exceptions were encountered, false otherwise.
    @Override
    public boolean send(Object message) {
        try {
            output.writeMessage(message);
            return true;
        } catch (IOException e){
            return false;
        }
    }

    // Receives and returns Object through input stream
    // Throws exceptions that may arise due to input stream reading.
    @Override
    public Object receive() throws IOException, ClassNotFoundException {
        return input.readMessage();
    }

    // Returns true if connection to server is established and socket is not null or closed;
    // false otherwise
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

    // Closes the socket and streams and resets them to null after sending a disconnect message;
    // Returns true is disconnect succeeds without exception and false otherwise
    @Override
    public boolean disconnect() {
        if (isOpen()) {
            try {
                send(new Message("","Disconnected"));
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
