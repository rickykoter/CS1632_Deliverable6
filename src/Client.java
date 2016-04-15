public interface Client {
    boolean connect(String ip, int port);
    boolean isConnected();
    boolean disconnect();
    boolean send(Message message);

    String getAlias();
    void setAlias();
}
