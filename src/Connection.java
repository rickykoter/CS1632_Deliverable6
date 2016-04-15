public interface Connection {
    boolean send(Message message);
    Message receive();  // blocks
    boolean connect();
    boolean disconnect();
    boolean isOpen();
}
