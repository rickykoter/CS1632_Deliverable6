
public class Session implements Client{
    private Connection _connection;
    private String _alias;
    public Session(){
        _connection = null;
        _alias = "Anonymous";
    }

    public boolean connect(Connection connection) {
        if(connection.connect()){
            _connection = connection;
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
}
