/**
 * ChatClient manages a Connection by receiving (listening) and sending messages with an associated alias.
 * By Richard Kotermanski and Jon Povirk
 */
public class ChatClient implements Client{
    private Connection _connection;
    private String _alias;
    private ClientReceiveThread _crt;

    public ChatClient(){
        _connection = null;
        _alias = "Anonymous";
        _crt = new ClientReceiveThread();
    }
    
    // Returns true and sets the connection to given Connection if it is open and not null; 
    // otherwise returns false.
    public boolean connect(Connection connection) {
        if(connection != null && connection.isOpen()){
             _connection = connection;
            return true;
        } else {
            _connection = null;
            return false;
        }
    }
    
    // Returns true if the current connection is not null and is open; returns false otherwise.
    public synchronized boolean isConnected() {
        if(_connection == null || !_connection.isOpen()){
            return false;
        } else {
            return true;
        }
    }
    
    // If the current connection is not null and open, then return true, disconnect the connection, 
    // and set the current connection to null. If not connected, then return false.
    public synchronized boolean disconnect() {
        if(isConnected()){
            boolean res =_connection.disconnect();
            _connection = null;
            return res;
        } else {
            return false;
        }
    }
    
    // Sends a message using the current connection. Returns true if the current Connection is 
    // connected and returns true for its send function. Otherwise returns false.
    public boolean send(Message message) {
        if(isConnected()){
            return _connection.send(message);
        } else {
            return false;
        }
    }

    // Returns the alias of the client
    public String getAlias() {
        return _alias;
    }
    
    // If the desired alias "a" is beween 1 and 15 characters, then set the Clients alias to it and return true;
    // otherwise, return false.
    public boolean setAlias(String a) {
        if(a == null || a.length() > 15 || a.length() < 1){
            return false;
        } else {
            _alias = a;
            return true;
        }
    }

    // Starts and returns a thread that is listening for messages for the current Connection while it is connected
    // and without exception.
    public Thread beginReceiving() {
        _crt.start();
        return _crt;
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
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
