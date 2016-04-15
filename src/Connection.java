public interface Connection {
    boolean send(Object object);
    Object receive();
    boolean connect();
    boolean disconnect();
    boolean isOpen();
}
