import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.AsynchronousSocketChannel;

public class ClientConnection implements Connection {
    private AsynchronousSocketChannel socket;
    private InputStream input;
    private OutputStream output;

    public ClientConnection(AsynchronousSocketChannel socket, InputStream inputStream, OutputStream outputStream) throws InvalidArgumentException {

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
