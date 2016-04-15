
public class Session implements Client{
    public boolean connect(Connection connection) {
        return false;
    }

    public boolean isConnected() {
        return false;
    }

    public boolean disconnect() {
        return false;
    }

    public boolean send(Message message) {
        return false;
    }

    public String getAlias() {
        return null;
    }

    public boolean setAlias(String a) {
        return false;
    }
}
