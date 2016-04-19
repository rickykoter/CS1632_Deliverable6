import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the ChatApp class and thus the GUI.
 * By Richard Kotermanski and Jon Povirk
 */
public class ChatAppTests {
    @Mock
    SocketFactory sf = mock(SocketFactory.class);
    ByteArrayInputStream bi;

    @Before
    public void setUp() throws IOException {
        // Create a mocked socket factory used in most test cases that uses a ByteArrayOutputStream
        // to work with the ObjectOutputStream's constructor since a mocked object does not work.
        MockitoAnnotations.initMocks(sf);
        Socket s = mock(Socket.class);
        InputStream is = mock(InputStream.class);
        when(is.read()).thenReturn(1);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream co = new ObjectOutputStream(bo);
        co.writeObject("FooBar");
        byte[] ser = bo.toByteArray();
        bi = new ByteArrayInputStream(ser);

        when(s.getInputStream()).thenReturn(bi);
        when(s.getOutputStream()).thenReturn(mock(ChatOutputStream.class));

        when(sf.createSocket("foo.bar.0", 0)).thenReturn(s);
    }
    
    // Tests that the constructor sets the GUI's buttons to their default state.
    @Test
    public void constructorTestValidString() {
        Client c = mock(Client.class);

        ChatApp ca = new ChatApp(c,sf);

        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
        assertTrue(ca.startServerButton.isEnabled());
    }

    // Tests that the constructor throws an exception for any combination of null arguments.
    @Test
    public void constructorTestValidStringNull() {
        Client c = mock(Client.class);
        try {
            ChatApp ca = new ChatApp(null,null);
            fail("Did not experience an exception for both being null!");
        } catch (IllegalArgumentException e){
            //pass
        }
        try {
            ChatApp ca = new ChatApp(c,null);
            fail("Did not experience an exception for just a null SocketFactory!");
        } catch (IllegalArgumentException e){
            //pass
        }
        try {
            ChatApp ca = new ChatApp(null,sf);
            fail("Did not experience an exception for just a null Client!");
        } catch (IllegalArgumentException e){
            //pass
        }
    }
    
    // Tests that the connectToServer function returns success text in the case
    // of a successful connection and valid alias name. Also, ensures that the GUI buttons
    // are enabled/disabled for a connected configuration.
    @Test
    public void connectToServerTestValid() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        when(c.connect(any(Connection.class))).thenReturn(true);

        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer("foo.bar.0", "0", "FooBar");
        verify(c, times(1)).connect(any(Connection.class));
        assertEquals("You have been successfully connected to foo.bar.0 at port 0.", res);
        assertTrue(ca.startServerButton.isEnabled());
        assertTrue(ca.disconnectButton.isEnabled());
        assertFalse(ca.connectButton.isEnabled());
        assertTrue(ca.sendMessageButton.isEnabled());
    }
    
    // For the connectToServer function, this tests that the correct error message is 
    // returned for an empty string alias argument, and
    // tests that the GUI buttons are enabled/disabled for a disconnected configuration.
    @Test
    public void connectToServerTestInvalidAliasEmpty() throws IOException {
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

    // For the connectToServer function, this tests that the correct error message is
    // returned for a null alias argument, and
    // tests that the GUI buttons are enabled/disabled for a disconnected configuration.
    @Test
    public void connectToServerTestInvalidAliasNull() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        when(c.connect(any(Connection.class))).thenReturn(true);

        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer("foo.bar.0", "0", null);
        assertEquals("Error: Alias is invalid. Must be between 1 and 15 characters.", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    // TFor the connectToServer function, this tests that thecorrect error message is returned 
    // for an alias argument that is over 15 characters, and
    // tests that the GUI buttons are enabled/disabled for a disconnected configuration.
    @Test
    public void connectToServerTestInvalidAliasOver15() throws IOException {
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
    
    // For the connectToServer function, this tests that the correct error message is returned 
    // in the case that the Client's connect function returns false, and
    // tests that the GUI buttons are enabled/disabled for a disconnected configuration.
    @Test
    public void connectToServerTestFailedConnection() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        when(c.connect(any(Connection.class))).thenReturn(false);

        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer("foo.bar.0", "0", "FooBar");
        verify(c, times(1)).connect(any(Connection.class));
        assertEquals("Error: Unable to connect to desired host and port!", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    // For the connectToServer function, this tests that the correct error message is returned 
    // in the case that the socket factory throws an exception 
    // when attempting to be make a socket for the desired hostName and portNumber, and
    // tests that the GUI buttons are enabled/disabled for a disconnected configuration.
    @Test
    public void connectToServerTestIOExceptionHandling() throws IOException {
        Client c = mock(Client.class);
        Socket s = mock(Socket.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        when(s.getInputStream()).thenReturn(bi);
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
    
    // For the connectToServer function, this tests that the correct error message is returned for 
    // a portNumber (string) that is not a parsable integer value, 
    // and tests that the GUI buttons are enabled/disabled for a disconnected configuration.
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

    // For the connectToServer function, this tests that the correct error message is returned for
    // a portNumber that is null, and tests that the GUI buttons are enabled/disabled for a disconnected configuration.
    @Test
    public void connectToServerTestPortNotANumberNull() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer("in.valid.0", null, "FooBar");
        assertEquals("Error: Port is not a number!", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    // For the connectToServer function, this tests that the correct error message is returned for
    // a hostName that is null, and tests that the GUI buttons are enabled/disabled for a disconnected configuration.
    @Test
    public void connectToServerTestHostNameNull() throws IOException {
        Client c = mock(Client.class);
        when(c.setAlias("FooBar")).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.connectToServer(null, "0", "FooBar");
        assertEquals("Error: Host cannot be null!", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }
    
    // Tests that the disconnectFromServer function returns an appropriate error message when
    // the set Client returns false for it's disconnect function, and
    // tests that the GUI buttons are enabled/disabled for a connected configuration.
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

    // Tests that the disconnectFromServer function returns an appropriate success message when
    // the set Client returns true for it's disconnect function, and
    // tests that the GUI buttons are enabled/disabled for a disconnected configuration.
    @Test
    public void disconnectFromServerTestClientSuccess() throws IOException {
        Client c = mock(Client.class);
        when(c.disconnect()).thenReturn(true);
        when(c.isConnected()).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.disconnectFromServer();
        verify(c, times(1)).disconnect();
        assertEquals("You have been successfully disconnected.", res);
        assertTrue(ca.connectButton.isEnabled());
        assertFalse(ca.disconnectButton.isEnabled());
        assertFalse(ca.sendMessageButton.isEnabled());
    }

    // Tests that the sendMessageToServer function returns a success message of an empty string ("")
    // when the Client is connected (isConnected returns true).
    @Test
    public void sendMessageToServerTestSuccess() throws IOException {
        Client c = mock(Client.class);
        when(c.send(any(Message.class))).thenReturn(true);
        when(c.isConnected()).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.sendMessageToServer("FooBar");
        verify(c, times(1)).send(any(Message.class));
        assertEquals("", res);
    }

    // Tests that the sendMessageToServer function returns the appropriate error message
    // when the Client is not connected (isConnected returns false).
    @Test
    public void sendMessageToServerTestFailureNotConnected() throws IOException {
        Client c = mock(Client.class);
        when(c.send(any(Message.class))).thenReturn(true);
        when(c.isConnected()).thenReturn(false);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.sendMessageToServer("FooBar");
        assertEquals("Error: Unable to send message to the server!", res);
    }

    // Tests that the sendMessageToServer function returns the appropriate error message
    // when the Client's send function returns false (unable to send message).
    @Test
    public void sendMessageToServerTestClientFailure() throws IOException {
        Client c = mock(Client.class);
        when(c.send(any())).thenReturn(false);
        when(c.isConnected()).thenReturn(true);
        ChatApp ca = new ChatApp(c, sf);

        String res = ca.sendMessageToServer("FooBar");
        assertEquals("Error: Unable to send message to the server!", res);
    }

    // Tests that the sendMessageToServer function returns the appropriate error message
    // when the input message text is empty ("").
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
