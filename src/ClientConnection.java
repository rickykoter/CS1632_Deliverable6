import java.io.*;
import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class ClientConnection implements Connection {
    private Channel socket;
    private ChatInputStream input;
    private ChatOutputStream output;

    public ClientConnection(Channel socket, ChatInputStream inputStream, ChatOutputStream outputStream) throws IllegalArgumentException {
        if(socket == null || inputStream == null || outputStream == null) { throw new IllegalArgumentException("No arguments can be null"); }
        this.socket = socket;
        this.input = inputStream;
        this.output = outputStream;
    }

    public static ClientConnection create(SocketChannel socket) {
        ClientConnection connection = null;
        try {
            ChatOutputStream cos = new ChatOutputStream(Channels.newOutputStream(socket));
            cos.flush();
            ChatInputStream cis = new ChatInputStream(Channels.newInputStream(socket));
            connection = new ClientConnection(socket, cis, cos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public synchronized boolean send(Object object) {
        boolean success = false;
        if(isOpen() && object != null) {
            try {
                output.writeMessage(object);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    @Override
    public synchronized Object receive() {
        Object data = null;
        try {
            if(isOpen()) {  //  && input.available() > 0
                data = input.readMessage();
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
            input.close();
            output.close();
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
