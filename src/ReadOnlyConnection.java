import java.io.IOException;

public interface ReadOnlyConnection {
    Object receive() throws IOException, ClassNotFoundException;  // blocks
    boolean isOpen();
    boolean disconnect();
}
