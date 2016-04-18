import org.junit.Test;
import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the ServerConnection class.
 * Created by Richard Kotermanski and Jon Povirk
 */

public class ServerConnectionTests {

    // Check if any combination of null arguments do cause an IllegalArgumentException
    // Fail if exception is not thrown.
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

    // Tests if the constructor accepts valid, non-null arguments
    // fails if IllegalArgumentException is thrown
    @Test
    public void constructorValidArguments() {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        try {
            new ServerConnection(s, cos, cis);
        } catch(IllegalArgumentException unexpected) {
            fail();
        }
    }

    // Tests if the send function returns true when the output stream
    // does not throw any exceptions, and verifies that the output stream's writeMessage
    // function is called once.
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

    // Tests if the send function returns true when the output stream
    // does not throw any exceptions even if the message input is null, and verifies that the output stream's writeMessage
    // function is called once.
    @Test
    public void sendTestEmpty() throws IOException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);

        Connection c = new ServerConnection(s, cos, cis);

        assertTrue(c.send(null));
        verify(cos, times(1)).writeMessage(null);
    }

    // Tests if the send function returns false when the output stream
    // does throws an exception, and verifies that the output stream's writeMessage
    // function is called once in the process.
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

    // Tests if the receive function returns null when the input stream
    // returns null for readMessage() calls, and verifies that readMessage() was called once.
    @Test
    public void receiveTestNull() throws IOException, ClassNotFoundException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(cis.readMessage()).thenReturn(null);

        Connection c = new ServerConnection(s, cos, cis);

        assertNull(c.receive());
        verify(cis, times(1)).readMessage();
    }

    // Tests if the receive function throws an IOException if the input stream throws an IOException when
    // readMessage is called, and verifies that readMessage() was called once.
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

    // Tests if the receive function returns a given message when the input stream
    // returns said message for readMessage() calls, and verifies that readMessage() was called once.
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

    // Tests if a disconnect message is sent, the socket and in/output streams are closed, and that the return value
    // is true for the disconnect function given that no exceptions occur for closing calls.
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

    // Tests if the disconnect function returns false if the socket is not connected/already is closed.
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

    // Tests if the disconnect function returns false if an exception occurs for the input stream's close function.
    @Test
    public void disconnectTestInputStreamException() throws IOException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(true);
        when(s.isClosed()).thenReturn(false);
        doThrow(new IOException()).when(cis).close();

        Connection c = new ServerConnection(s, cos, cis);

        assertFalse(c.disconnect());
    }

    // Tests if the disconnect function returns false if an exception occurs for the output stream's close function.
    @Test
    public void disconnectTestOutputStreamException() throws IOException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(true);
        when(s.isClosed()).thenReturn(false);
        doThrow(new IOException()).when(cos).close();

        Connection c = new ServerConnection(s, cos, cis);

        assertFalse(c.disconnect());
    }

    // Tests if the disconnect function returns false if an exception occurs for the socket's close function.
    @Test
    public void disconnectTestSocketStreamException() throws IOException {
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(true);
        when(s.isClosed()).thenReturn(false);
        doThrow(new IOException()).when(s).close();

        Connection c = new ServerConnection(s, cos, cis);

        assertFalse(c.disconnect());
    }

    // Tests that the isOpen function returns true when the socket is open, connected, and not null.
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

    // Tests that the isOpen function returns false when the socket is open and not null but is not connected,.
    @Test
    public void isOpenTestNotConnectedFalse(){
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(false);
        when(s.isClosed()).thenReturn(false);

        Connection c = new ServerConnection(s, cos, cis);

        assertFalse(c.isOpen());
    }

    // Tests that the isOpen function returns false when the socket is connected and not null but is not open,.
    @Test
    public void isOpenTestNotOpenFalse(){
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(true);
        when(s.isClosed()).thenReturn(true);

        Connection c = new ServerConnection(s, cos, cis);

        assertFalse(c.isOpen());
    }

    // Tests that the isOpen function returns false when the socket is null after disconnecting.
    @Test
    public void isOpenTestDisconnectedFalse(){
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(true);
        when(s.isClosed()).thenReturn(false);

        Connection c = new ServerConnection(s, cos, cis);
        c.disconnect();

        assertFalse(c.isOpen());
    }

    // Tests that the isOpen function returns false when the socket is not null but is not open, not connected.
    @Test
    public void isOpenTestNotConnectedOrOpenFalse(){
        Socket s = mock(Socket.class);
        ChatInputStream cis = mock(ChatInputStream.class);
        ChatOutputStream cos = mock(ChatOutputStream.class);
        when(s.isConnected()).thenReturn(false);
        when(s.isClosed()).thenReturn(true);

        Connection c = new ServerConnection(s, cos, cis);

        assertFalse(c.isOpen());
    }

}

