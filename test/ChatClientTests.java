import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the ChatClient class (implementation of Client)
 * by Richard Kotermanski and Jon Povirk
 */

public class ChatClientTests {

    //Tests that the connect function returns False if the connection is not open.
    @Test
    public void connectTestFailedConnectionNotOpen() {
        Client c = new ChatClient();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(false);

        assertFalse(c.connect(conn));
    }

    //Tests that the connect function returns True if the connection is open.
    @Test
    public void connectTestSuccessfulConnection() {
        Client c = new ChatClient();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(true);

        assertTrue(c.connect(conn));
    }

    // Tests that the isConnected function returns false prior to a connection being made. (no connection)
    @Test
    public void isConnectedTestInitial() {
        Client c = new ChatClient();

        assertFalse(c.isConnected());
    }

    // Tests that the isConnected function returns true after a successful connection has been made that
    // returns true for it's isOpen function. (no connection)
    @Test
    public void isConnectedTestAfterSuccessfulConnection() {
        Client c = new ChatClient();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(true);
        c.connect(conn);

        assertTrue(c.isConnected());
    }

    // Tests that the isConnected function returns false if no connection is given (null).
    @Test
    public void isConnectedTestAfterNullConnection() {
        Client c = new ChatClient();
        c.connect(null);

        assertFalse(c.isConnected());
    }

    // Tests that the isConnected function returns false after a successful connection has been made but
    // said connection is then disconnected. (no connection)
    @Test
    public void isConnectedTestAfterDisconnected() {
        Client c = new ChatClient();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(true);
        c.connect(conn);
        c.disconnect();

        assertFalse(c.isConnected());
    }

    // Tests that the disconnect function returns false if no connection has been made yet.
    @Test
    public void disconnectTestFailureBeforeConnection() {
        Client c = new ChatClient();

        assertFalse(c.disconnect());
    }

    // Tests that the disconnect function returns true if a connection has been made, and the said connection is open
    // and it's respective disconnect function returns true.
    @Test
    public void disconnectTestSuccessAfterConnection() {
        Client c = new ChatClient();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(true);
        when(conn.disconnect()).thenReturn(true);
        c.connect(conn);

        assertTrue(c.disconnect());
    }


    // Tests that the disconnect function returns false if a connection has been made, and the said connection is not open
    // and it's respective disconnect function returns false.
    @Test
    public void disconnectTestFailureAfterConnection() {
        Client c = new ChatClient();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(false);
        when(conn.disconnect()).thenReturn(false);
        c.connect(conn);

        assertFalse(c.disconnect());
    }

    // Tests that the send function returns false if a connection has yet to be made.
    @Test
    public void sendTestFailureBeforeConnection() {
        Client c = new ChatClient();

        assertFalse(c.send(mock(Message.class)));
    }

    // Tests that the send function returns false if a connection has been made,
    // but said connection returns false when its send function is called.
    @Test
    public void sendTestFailureAfterConnection() {
        Client c = new ChatClient();
        Message m = mock(Message.class);
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(true);
        when(conn.send(m)).thenReturn(false);
        c.connect(conn);

        assertFalse(c.send(m));
    }

    // Tests that the send function returns true if a connection has been made,
    // and said connection returns true when its send function is called.
    @Test
    public void sendTestSuccessAfterConnection() {
        Client c = new ChatClient();
        Message m = mock(Message.class);
        Connection conn = mock(Connection.class);
        when(conn.send(m)).thenReturn(true);
        when(conn.isOpen()).thenReturn(true);
        c.connect(conn);

        assertTrue(c.send(m));
    }

    // Tests that the getAlias returns a default alias (Anonymous) when called prior to setting it.
    @Test
    public void getAliasTestInitiallyAnonymous(){
        Client c = new ChatClient();
        assertEquals("Anonymous", c.getAlias());
    }

    // Tests that the setAlias sets the alias (returned by getAlias) to a valid string and returns true.
    @Test
    public void getAliasTestNotEmpty(){
        Client c = new ChatClient();
        c.setAlias("Foobar");

        assertEquals("Foobar", c.getAlias());
    }

    // Tests that the setAlias sets the alias (returned by getAlias) to a valid string with a space and returns true.
    @Test
    public void setAliasTestSpaceNotEmpty(){
        Client c = new ChatClient();

        assertTrue(c.setAlias("Foo Bar"));
        assertEquals("Foo Bar", c.getAlias());
    }

    // Tests that the setAlias does not sets the alias (returned by getAlias and is still the default)
    // to an invalid, empty string and returns false.
    @Test
    public void setAliasTestFailEmpty(){
        Client c = new ChatClient();

        assertFalse(c.setAlias(""));
        assertEquals("Anonymous", c.getAlias());
    }

    // Tests that the setAlias does not sets the alias (returned by getAlias and is still the default)
    // to an invalid, over-15-character string and returns false.
    @Test
    public void setAliasTestFailOver15Chars(){
        Client c = new ChatClient();

        assertFalse(c.setAlias("1234567891234567"));
        assertEquals("Anonymous", c.getAlias());
    }

    // Tests that the thread returned by beginReceiving finishes once disconnect is called.
    @Test
    public void beginReceivingTestNoException() throws IOException, ClassNotFoundException, InterruptedException {
        Client c = new ChatClient();
        Message m = mock(Message.class);
        Connection conn = mock(Connection.class);
        when(conn.receive()).thenReturn(m);
        when(conn.isOpen()).thenReturn(true);
        when(conn.disconnect()).thenReturn(true);
        c.connect(conn);

        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);

        Thread th = c.beginReceiving();
        Thread.sleep(1000);
        c.disconnect();
        Thread.sleep(1000);
        verify(conn, atLeast(1)).receive();
        if(th.isAlive()){
            fail("Receiver still alive.");
        }
        //pass
    }

    // Tests that the thread returned by beginReceiving finishes if an exception occurs while receiving.
    @Test
    public void beginReceivingTestException() throws IOException, ClassNotFoundException, InterruptedException {
        Client c = new ChatClient();
        Connection conn = mock(Connection.class);
        when(conn.receive()).thenThrow(new IOException());
        when(conn.isOpen()).thenReturn(true);
        when(conn.disconnect()).thenReturn(true);
        c.connect(conn);

        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);

        Thread th = c.beginReceiving();
        Thread.sleep(1000);
        verify(conn, atLeast(1)).receive();
        if(th.isAlive()){
            c.disconnect();
            fail("Receiver still alive.");
        }
        //pass
    }

}
