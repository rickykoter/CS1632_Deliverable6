import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnection implements Connection {
    private Socket socket;
    private InputStream input;
    private OutputStream output;

    public ClientConnection(Socket socket, InputStream inputStream, OutputStream outputStream) {

    }

    @Override
    public boolean send(Object object) {
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
