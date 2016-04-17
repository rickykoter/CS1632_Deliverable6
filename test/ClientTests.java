import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientTests {
    @Test
    public void connectTestFailedConnectionNotOpen() throws IOException {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(false);

        assertFalse(c.connect(conn));
    }

    @Test
    public void connectTestSuccessfulConnection() throws IOException {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(true);

        assertTrue(c.connect(conn));
    }

    @Test
    public void isConnectedTestInitial() {
        Client c = new Session();

        assertFalse(c.isConnected());
    }

    @Test
    public void isConnectedTestAfterSuccessfulConnection() throws IOException {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(true);
        c.connect(conn);

        assertTrue(c.isConnected());
    }

    @Test
    public void isConnectedTestAfterNullConnection() throws IOException {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        c.connect(null);

        assertFalse(c.isConnected());
    }

    @Test
    public void isConnectedTestAfterConnectionClosed() throws IOException {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(false);
        c.connect(conn);

        assertFalse(c.isConnected());
    }

    @Test
    public void disconnectTestFailureBeforeConnection() throws IOException {
        Client c = new Session();

        assertFalse(c.disconnect());
    }

    @Test
    public void disconnectTestSuccessAfterConnection() throws IOException {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(true);
        when(conn.disconnect()).thenReturn(true);
        c.connect(conn);

        assertTrue(c.disconnect());
    }

    @Test
    public void disconnectTestFailureAfterConnection() throws IOException {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.isOpen()).thenReturn(false);
        when(conn.disconnect()).thenReturn(false);
        c.connect(conn);

        assertFalse(c.disconnect());
    }


    @Test
    public void sendTestFailureBeforeConnection() throws IOException {
        Client c = new Session();

        assertFalse(c.send(mock(Message.class)));
    }

    @Test
    public void sendTestFailureAfterConnection() throws IOException {
        Client c = new Session();
        Message m = mock(Message.class);
        Connection conn = mock(Connection.class);
        when(conn.send(m)).thenReturn(false);
        c.connect(conn);

        assertFalse(c.send(m));
    }

    @Test
    public void sendTestSuccessAfterConnection() throws IOException {
        Client c = new Session();
        Message m = mock(Message.class);
        Connection conn = mock(Connection.class);
        when(conn.send(m)).thenReturn(true);
        when(conn.isOpen()).thenReturn(true);
        c.connect(conn);

        assertTrue(c.send(m));
    }

    @Test
    public void getAliasTestInitiallyAnonymous(){
        Client c = new Session();
        assertEquals("Anonymous", c.getAlias());
    }

    @Test
    public void getAliasTestNotEmpty(){
        Client c = new Session();
        c.setAlias("Foobar");

        assertEquals("Foobar", c.getAlias());
    }

    @Test
    public void setAliasTestSuccessNotEmpty(){
        Client c = new Session();

        assertTrue(c.setAlias("Foo Bar"));
        assertEquals("Foo Bar", c.getAlias());
    }

    @Test
    public void setAliasTestFailEmpty(){
        Client c = new Session();

        assertFalse(c.setAlias(""));
        assertEquals("Anonymous", c.getAlias());
    }

    @Test
    public void setAliasTestFailOver15Chars(){
        Client c = new Session();

        assertFalse(c.setAlias("1234567891234567"));
        assertEquals("Anonymous", c.getAlias());
    }

    @Test
    public void beginReceivingTestNoException() throws IOException, ClassNotFoundException {
        Client c = new Session();
        Message m = mock(Message.class);
        Connection conn = mock(Connection.class);
        when(conn.receive()).thenReturn(m);
        when(conn.isOpen()).thenReturn(true);
        c.connect(conn);

        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                if(!c.disconnect()) fail("Error!");
            }
        }, 1, TimeUnit.SECONDS);

        c.beginReceiving();

    }

    @Test
    public void beginReceivingTestException() throws IOException, ClassNotFoundException {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.receive()).thenThrow(new IOException());
        when(conn.isOpen()).thenReturn(true);
        c.connect(conn);

        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                if(!c.disconnect()) return;
                fail("Error - exception did not cause failure to disconnect.");
            }
        }, 1, TimeUnit.SECONDS);
        c.beginReceiving();
        //pass
    }

}
