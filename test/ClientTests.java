import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientTests {
    @Test
    public void connectTestFailedConnection() {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(false);

        assertFalse(c.connect(conn));
    }

    @Test
    public void connectTestSuccessfulConnection() {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(true);

        assertTrue(c.connect(conn));
    }

    @Test
    public void isConnectedTestInitial() {
        Client c = new Session();

        assertFalse(c.isConnected());
    }

    @Test
    public void isConnectedTestAfterSuccessfulConnection() {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(true);
        when(conn.isOpen()).thenReturn(true);
        c.connect(conn);

        assertTrue(c.isConnected());
    }

    @Test
    public void isConnectedTestAfterFailedConnection() {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(false);
        when(conn.isOpen()).thenReturn(true);
        c.connect(conn);

        assertFalse(c.isConnected());
    }

    @Test
    public void isConnectedTestAfterConnectionClosed() {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(true);
        when(conn.isOpen()).thenReturn(false);
        c.connect(conn);

        assertFalse(c.isConnected());
    }

    @Test
    public void disconnectTestFailureBeforeConnection() {
        Client c = new Session();

        assertFalse(c.disconnect());
    }

    @Test
    public void disconnectTestSuccessAfterConnection() {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(true);
        when(conn.isOpen()).thenReturn(true);
        when(conn.disconnect()).thenReturn(true);
        c.connect(conn);

        assertTrue(c.disconnect());
    }

    @Test
    public void disconnectTestFailureAfterConnection() {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(true);
        when(conn.isOpen()).thenReturn(false);
        when(conn.disconnect()).thenReturn(false);
        c.connect(conn);

        assertFalse(c.disconnect());
    }


    @Test
    public void sendTestFailureBeforeConnection() {
        Client c = new Session();

        assertFalse(c.send(mock(Message.class)));
    }

    @Test
    public void sendTestFailureAfterConnection() {
        Client c = new Session();
        Message m = mock(Message.class);
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(true);
        when(conn.send(m)).thenReturn(false);
        c.connect(conn);

        assertFalse(c.send(m));
    }

    @Test
    public void sendTestSuccessAfterConnection() {
        Client c = new Session();
        Message m = mock(Message.class);
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(true);
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

}
