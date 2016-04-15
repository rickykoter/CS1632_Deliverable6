
public class ServerConnection implements Connection{
    @Override
    public boolean send(Message message) {
        return false;
    }

    @Override
    public Message receive() {
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
