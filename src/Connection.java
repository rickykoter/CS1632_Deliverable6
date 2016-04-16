public interface Connection {
    boolean send(Object message);
    Object receive();  // blocks
    boolean connect();
    boolean disconnect();
    boolean isOpen();
}
