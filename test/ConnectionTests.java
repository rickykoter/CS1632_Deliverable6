import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class ConnectionTests {
    Connection conn;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(conn);
        Socket s = mock(Socket.class);
        ObjectOutputStream oos = mock(ObjectOutputStream.class);

        when(s.getOutputStream()).thenReturn(oos);
        when(s.getInputStream()).thenReturn(mock(ObjectInputStream.class));

        conn = new ServerConnection(s);
    }

    @Test
    public void connectTestFailedException() throws IOException {
        Socket s = mock(Socket.class);
        when(s.getOutputStream()).thenThrow(new IOException("Connection Error Mock"));
        when(s.getInputStream()).thenReturn(mock(ObjectInputStream.class));

        Connection c = new ServerConnection(s);


        assertFalse(c.connect());
    }

    @Test
    public void connectTestPassed() throws IOException {
        Socket s = mock(Socket.class);
        when(s.getOutputStream()).thenReturn(mock(ObjectOutputStream.class));
        when(s.getInputStream()).thenReturn(mock(ObjectInputStream.class));

        Connection c = new ServerConnection(s);

        assertTrue(c.connect());
    }

    @Test
    public void sendTestSuccess() throws IOException {
        Message m = mock(Message.class);

        assertTrue(conn.send(m));
    }

    @Test
    public void sendTestEmpty(){
        Object m = null;

        assertTrue(conn.send(m));
    }

    @Test
    public void sendTestException() throws IOException{
        Socket s = mock(Socket.class);
        ObjectOutputStream oos = mock(ObjectOutputStream.class);
        doThrow(new IOException()).when(oos).writeObject(null);
        when(s.getOutputStream()).thenReturn(oos);
        when(s.getInputStream()).thenReturn(mock(ObjectInputStream.class));

        Connection c = new ServerConnection(s);
        assertFalse(c.send(null));
    }

    @Test
    public void receiveTestNull() throws IOException, ClassNotFoundException {
        Socket s = mock(Socket.class);
        ObjectInputStream ois = mock(ObjectInputStream.class);
        when(ois.readObject()).thenReturn(null);
        when(s.getOutputStream()).thenReturn(mock(ObjectOutputStream.class));
        when(s.getInputStream()).thenReturn(ois);

        Connection c = new ServerConnection(s);
        assertNull(c.receive());
    }

    @Test
    public void receiveTestException() throws IOException, ClassNotFoundException {
        Socket s = mock(Socket.class);
        ObjectInputStream ois = mock(ObjectInputStream.class);
        Message m = mock(Message.class);
        when(ois.readObject()).thenThrow(new IOException());
        when(s.getOutputStream()).thenReturn(mock(ObjectOutputStream.class));
        when(s.getInputStream()).thenReturn(ois);

        Connection c = new ServerConnection(s);
        try{
            c.receive();
            fail();
        } catch (Exception e){
            //pass
        }
    }

    @Test
    public void receiveTestMessage() throws IOException, ClassNotFoundException {
        Socket s = mock(Socket.class);
        ObjectInputStream ois = mock(ObjectInputStream.class);
        Message m = mock(Message.class);
        when(ois.readObject()).thenReturn(m);
        when(s.getOutputStream()).thenReturn(mock(ObjectOutputStream.class));
        when(s.getInputStream()).thenReturn(ois);

        Connection c = new ServerConnection(s);
        assertEquals(m, c.receive());
    }
}
