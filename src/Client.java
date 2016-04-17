import java.io.IOException;

public interface Client {
    boolean connect(Connection connection) throws IOException;
    boolean isConnected();
    boolean disconnect() throws IOException;
    boolean send(Message message) throws IOException;
    void beginReceiving();

    String getAlias();
    boolean setAlias(String a);
}
