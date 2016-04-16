
public class Session implements Client{
    private Connection _connection;
    private String _alias;
    private ClientReceiveThread _crt;

    public Session(){
        _connection = null;
        _alias = "Anonymous";
        _crt = new ClientReceiveThread();
    }

    public boolean connect(Connection connection) {
        if(connection.connect()){
            _connection = connection;
            _crt.run();
            return true;
        } else {
            _connection = null;
            return false;
        }
    }

    public boolean isConnected() {
        if(_connection == null || !_connection.isOpen()){
            return false;
        } else {
            return true;
        }
    }

    public boolean disconnect() {
        if(isConnected()){
            _connection = null;
            return _connection.disconnect();
        } else {
            return false;
        }
    }

    public boolean send(Message message) {
        if(isConnected()){
            return _connection.send(message);
        } else {
            return false;
        }
    }

    public String getAlias() {
        return _alias;
    }

    public boolean setAlias(String a) {
        if(a.length() > 15 || a.length() < 1){
            return false;
        } else {
            _alias = a;
            return true;
        }
    }

    private class ClientReceiveThread extends Thread {
        @Override
        public void run() {
            while(isConnected()){
                try{
                    Object o = _connection.receive();
                    if(o != null){
                        Message m = (Message) o;
                        System.out.println(m.getSender() + ": " + m.getText());
                    }
                } catch (ClassCastException e){
                    System.out.println("Error - Message unable to be displayed: "+ e.getMessage());
                } catch (Exception e) {
                    System.out.println("Disconnected - Client Thread Failed: "+ e.getMessage());
                    disconnect();
                    break;
                }
            }
        }
    }
}
