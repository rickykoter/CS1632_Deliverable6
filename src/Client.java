public interface Client {
    boolean connect(Connection connection);
    boolean isConnected();
    boolean disconnect();
    boolean send(Message message);


    String getAlias();
    boolean setAlias(String a);
}
