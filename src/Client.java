import java.io.IOException;

public interface Client {
    boolean connect(Connection connection);
    boolean isConnected();
    boolean disconnect();
    boolean send(Message message);
    void beginReceiving();

    String getAlias();
    boolean setAlias(String a);
}
