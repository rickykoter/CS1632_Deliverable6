import java.io.IOException;

public interface Connection {
    boolean send(Object message);
    Object receive() throws IOException, ClassNotFoundException;  // blocks
    boolean isOpen();
    boolean disconnect();
}
