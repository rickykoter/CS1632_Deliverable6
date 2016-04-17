import java.io.*;
import java.nio.channels.AsynchronousSocketChannel;

public class ClientConnection implements Connection {
    private AsynchronousSocketChannel socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ClientConnection(AsynchronousSocketChannel socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IllegalArgumentException {
        if(socket == null || inputStream == null || outputStream == null) { throw new IllegalArgumentException("No arguments can be null"); }
        this.socket = socket;
        this.input = inputStream;
        this.output = outputStream;
    }

    @Override
    public boolean send(Object object) {
        boolean success = false;
        if(isOpen()) {
            try {
                output.writeObject(object);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    @Override
    public Object receive() {
        Object data = null;
        try {
            if(isOpen() && input.available() > 0) {
                data = input.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean disconnect() {
        boolean success = false;
        try {
            input.close();
            output.close();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public boolean isOpen() {
        return socket.isOpen();
    }
}
