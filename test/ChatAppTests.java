import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChatAppTests {
    @Mock
    SocketFactory sf = mock(SocketFactory.class);

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(sf);
        Socket s = mock(Socket.class);
        InputStream is = mock(InputStream.class);
        when(is.read()).thenReturn(1);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream co = new ObjectOutputStream(bo);
        co.writeObject("FooBar");
        byte[] ser = bo.toByteArray();
        ByteArrayInputStream bi = new ByteArrayInputStream(ser);

        when(s.getInputStream()).thenReturn(bi);
        when(s.getOutputStream()).thenReturn(mock(ChatOutputStream.class));

        when(sf.createSocket("foo.bar.0", 0)).thenReturn(s);
    }

    @Test
    public void constructorTestValidString() {
        Client c = mock(Client.class);

        ChatApp ca = new ChatApp(c,sf);

        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
        assertTrue(ca.startServerButton.isEnabled());
    }

    @Test
    public void connectToServerTestValid() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        when(c.connect(any(Connection.class))).thenReturn(true);

        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer("foo.bar.0", "0", "FooBar");
        assertEquals("You have been successfully connected to foo.bar.0 at port 0.", res);
        assertTrue(ca.startServerButton.isEnabled());
        assertTrue(ca.disconnectButton.isEnabled());
        assertFalse(ca.connectButton.isEnabled());
        assertTrue(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void connectToServerTestInValidAliasEmpty() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        when(c.connect(any(Connection.class))).thenReturn(true);

        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer("foo.bar.0", "0", "");
        assertEquals("Error: Alias is invalid. Must be between 1 and 15 characters.", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void connectToServerTestInValidAliasOver15() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("123456789123456")).thenReturn(false);
        when(c.connect(any(Connection.class))).thenReturn(true);

        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer("foo.bar.0", "0", "123456789123456");
        assertEquals("Error: Alias is invalid. Must be between 1 and 15 characters.", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void connectToServerTestFailedConnection() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        when(c.connect(any(Connection.class))).thenReturn(false);

        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer("foo.bar.0", "0", "FooBar");
        assertEquals("Error: Unable to connect to desired host and port!", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void connectToServerTestIOExceptionHandling() throws IOException {
        Client c = mock(Client.class);
        Socket s = mock(Socket.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        when(s.getInputStream()).thenReturn(mock(ChatInputStream.class));
        when(s.getOutputStream()).thenReturn(mock(ChatOutputStream.class));
        SocketFactory sf2 = mock(SocketFactory.class);
        when(sf2.createSocket(any(String.class), any(Integer.class))).thenThrow(new IOException());

        ChatApp ca = new ChatApp(c,sf2);

        String res = ca.connectToServer("foo.bar.0", "0", "FooBar");
        assertEquals("Error: Unable to connect to desired host and port!", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void connectToServerTestPortNotANumber() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer("in.valid.0", "a12", "FooBar");
        assertEquals("Error: Port is not a number!", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void disconnectFromServerTestClientFailure() throws IOException {
        Client c = mock(Client.class);
        when(c.disconnect()).thenReturn(false);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.disconnectFromServer();
        assertEquals("Error: You were unable to be disconnected!", res);
        assertTrue(ca.disconnectButton.isEnabled());
        assertFalse(ca.connectButton.isEnabled());
        assertTrue(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void disconnectFromServerTestClientSuccess() throws IOException {
        Client c = mock(Client.class);
        when(c.disconnect()).thenReturn(true);
        when(c.isConnected()).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.disconnectFromServer();
        assertEquals("You have been successfully disconnected.", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    @Test
    public void sendMessageToServerTestSuccess() throws IOException {
        Client c = mock(Client.class);
        when(c.send(any(Message.class))).thenReturn(true);
        when(c.isConnected()).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.sendMessageToServer("FooBar");
        assertEquals("", res);
    }

    @Test
    public void sendMessageToServerTestFailureNotConnected() throws IOException {
        Client c = mock(Client.class);
        when(c.send(any(Message.class))).thenReturn(true);
        when(c.isConnected()).thenReturn(false);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.sendMessageToServer("FooBar");
        assertEquals("Error: Unable to send message to the server!", res);
    }

    @Test
    public void sendMessageToServerTestClientFailure() throws IOException {
        Client c = mock(Client.class);
        when(c.send(any())).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.sendMessageToServer("FooBar");
        assertEquals("Error: Unable to send message to the server!", res);
    }

    @Test
    public void sendMessageToServerTestEmptyFailure() throws IOException {
        Client c = mock(Client.class);
        when(c.send(any(Message.class))).thenReturn(true);
        when(c.isConnected()).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.sendMessageToServer("");
        assertEquals("Error: Please provide text to send!", res);
    }
}
