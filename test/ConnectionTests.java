import org.junit.Test;
import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class ConnectionTests {
    @Test
    public void constructorNullArgumentsThrowException() {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        try {
            new ServerConnection(null, null, null);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new ServerConnection(s, cos, null);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new ServerConnection(null, cos, cis);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new ServerConnection(s, null, cis);
            fail();
        } catch(IllegalArgumentException expected) {
        }
    }

    @Test
    public void constructorValidArguments() {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        try {
            new ServerConnection(s, cos, cis);

        } catch(IllegalArgumentException expected) {
            fail();
        }
    }


        @Test
    public void sendTestSuccess() throws IOException {
        Message m = mock(Message.class);
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);

        Connection c = new ServerConnection(s, cos, cis);

        assertTrue(c.send(m));
        verify(cos, times(1)).writeMessage(m);
    }

    @Test
    public void sendTestEmpty() throws IOException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);

        Connection c = new ServerConnection(s, cos, cis);

        assertTrue(c.send(null));
        verify(cos, times(1)).writeMessage(null);
    }


    @Test
    public void sendTestException() throws IOException{
        Message m = mock(Message.class);
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        doThrow(new IOException()).when(cos).writeMessage(m);

        Connection c = new ServerConnection(s, cos, cis);
        assertFalse(c.send(m));
        verify(cos, times(1)).writeMessage(anyObject());
    }

    @Test
    public void receiveTestNull() throws IOException, ClassNotFoundException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(cis.readMessage()).thenReturn(null);

        Connection c = new ServerConnection(s, cos, cis);

        assertNull(c.receive());
    }

    @Test
    public void receiveTestException() throws IOException, ClassNotFoundException {

        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(cis.readMessage()).thenThrow(new IOException());

        Connection c = new ServerConnection(s, cos, cis);

        try{
            c.receive();
            fail();
        } catch (Exception e){
            //pass
        }
        verify(cis, times(1)).readMessage();
    }

    @Test
    public void receiveTestMessage() throws IOException, ClassNotFoundException {
        Message m = mock(Message.class);
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(cis.readMessage()).thenReturn(m);

        Connection c = new ServerConnection(s, cos, cis);

        assertEquals(m, c.receive());
        verify(cis, times(1)).readMessage();
    }


    @Test
    public void disconnectTestSuccess() throws IOException, ClassNotFoundException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        Connection c = new ServerConnection(s, cos, cis);
        when(s.isConnected()).thenReturn(true);
        when(s.isClosed()).thenReturn(false);

        assertTrue(c.disconnect());
        verify(cos, times(1)).writeMessage(anyObject());
        verify(cos, times(1)).close();
        verify(cis, times(1)).close();
        verify(s, times(1)).close();
    }

    @Test
    public void disconnectTestNotOpen() throws IOException, ClassNotFoundException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(false);
        when(s.isClosed()).thenReturn(true);

        Connection c = new ServerConnection(s, cos, cis);

        assertFalse(c.disconnect());
    }

    @Test
    public void disconnectTestOutputStreamException() throws IOException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        doThrow(new IOException()).when(cis).close();
        Connection c = new ServerConnection(s, cos, cis);
        assertFalse(c.disconnect());
    }


    @Test
    public void isOpenTestTrue(){
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(true);
        when(s.isClosed()).thenReturn(false);

        Connection c = new ServerConnection(s, cos, cis);

        assertTrue(c.isOpen());
    }


    @Test
    public void isOpenTestFalse(){
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(false);
        when(s.isClosed()).thenReturn(true);

        Connection c = new ServerConnection(s, cos, cis);

        assertFalse(c.isOpen());
    }

}

