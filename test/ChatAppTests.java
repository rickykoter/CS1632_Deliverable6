import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChatAppTests {
    @Test
    public void constructorTestValidString() {
        Client c = mock(Client.class);
        ChatApp ca = new ChatApp(c);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
        assertTrue(ca.startServerButton.isEnabled());
    }

    @Test
    public void connectToServerTestValidString() throws IOException {
        Client c = mock(Client.class);
        Connection cn = mock(Connection.class);
        when(c.connect(cn)).thenReturn(true);
        ChatApp ca = new ChatApp(c);

        String res = ca.connectToServer("foo.bar.0", "12");
        assertEquals("You have been successfully connected to foo.bar.0 at port 12.", res);
        assertTrue(ca.startServerButton.isEnabled());
        assertTrue(ca.disconnectButton.isEnabled());
        assertFalse(ca.connectButton.isEnabled());
        assertTrue(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void connectToServerTestFailedConnection() throws IOException {
        Client c = mock(Client.class);
        Connection cn = mock(Connection.class);
        when(c.connect(cn)).thenReturn(false);
        ChatApp ca = new ChatApp(c);

        String res = ca.connectToServer("in.valid.0", "12");
        assertEquals("Error: Unable to connect to desired host and port!", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
        assertTrue(ca.startServerButton.isEnabled());
    }

    @Test
    public void connectToServerTestIOExceptionHandling() throws IOException {
        Client c = mock(Client.class);
        Connection cn = mock(Connection.class);
        when(c.connect(cn)).thenThrow(new IOException());
        ChatApp ca = new ChatApp(c);

        String res = ca.connectToServer("in.valid.0", "12");
        assertEquals("Error: Unable to connect to desired host and port!", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
        assertTrue(ca.startServerButton.isEnabled());
    }

    @Test
    public void connectToServerTestPortNotANumber() throws IOException {
        Client c = mock(Client.class);
        Connection cn = mock(Connection.class);
        when(c.connect(cn)).thenReturn(true);
        ChatApp ca = new ChatApp(c);

        String res = ca.connectToServer("in.valid.0", "a12");
        assertEquals("Error: Unable to connect to desired host and port!", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
        assertTrue(ca.startServerButton.isEnabled());
    }

    @Test
    public void disconnectFromServerTestClientFailure() throws IOException {
        Client c = mock(Client.class);
        when(c.disconnect()).thenReturn(false);
        ChatApp ca = new ChatApp(c);

        String res = ca.disconnectFromServer();
        assertEquals("Error: You were unable to be disconnected!", res);
        assertTrue(ca.startServerButton.isEnabled());
        assertTrue(ca.disconnectButton.isEnabled());
        assertFalse(ca.connectButton.isEnabled());
        assertTrue(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void disconnectFromServerTestClientSuccess() throws IOException {
        Client c = mock(Client.class);
        when(c.disconnect()).thenReturn(true);
        ChatApp ca = new ChatApp(c);

        String res = ca.disconnectFromServer();
        assertEquals("You have been successfully disconnected.", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
        assertTrue(ca.startServerButton.isEnabled());
    }

    @Test
    public void disconnectFromServerTestClientException() throws IOException {
        Client c = mock(Client.class);
        when(c.disconnect()).thenThrow(new IOException());
        ChatApp ca = new ChatApp(c);

        String res = ca.disconnectFromServer();
        assertEquals("Error: You were unable to be disconnected!", res);
        assertTrue(ca.startServerButton.isEnabled());
        assertTrue(ca.disconnectButton.isEnabled());
        assertFalse(ca.connectButton.isEnabled());
        assertTrue(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void sendMessageToServerTestSuccess() throws IOException {
        Client c = mock(Client.class);
        when(c.send(mock(Message.class))).thenReturn(true);
        ChatApp ca = new ChatApp(c);

        String res = ca.sendMessageToServer("FooBar");
        assertEquals("Sent", res);
        assertTrue(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void sendMessageToServerTestClientFailure() throws IOException {
        Client c = mock(Client.class);
        when(c.send(mock(Message.class))).thenReturn(false);
        ChatApp ca = new ChatApp(c);

        String res = ca.sendMessageToServer("FooBar");
        assertEquals("Error: Unable to send message to the server!", res);
        assertTrue(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void sendMessageToServerTestClientException() throws IOException {
        Client c = mock(Client.class);
        when(c.send(mock(Message.class))).thenThrow(new IOException());
        ChatApp ca = new ChatApp(c);

        String res = ca.sendMessageToServer("FooBar");
        assertEquals("Error: Unable to send message to the server!", res);
        assertTrue(ca.sendMessageButton.isEnabled());
    }

}
