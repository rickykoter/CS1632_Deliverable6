public interface Connection {
    void send(Message message);
    Message receive();  // blocks
}
