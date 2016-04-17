import org.junit.Test;

import java.io.*;
import java.math.BigInteger;
import java.nio.channels.AsynchronousSocketChannel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientConnectionTests {
    AsynchronousSocketChannel asc = mock(AsynchronousSocketChannel.class);
    final int buffSize = 10;
    byte[] inputBuffer = new byte[buffSize];
    InputStream is = new ByteArrayInputStream(inputBuffer);
    byte[] emptyInputBuffer = new byte[0];
    InputStream empty = new ByteArrayInputStream(emptyInputBuffer);
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    // <editor-fold desc="constructor">
    @Test
    public void constructorNullArgumentsThrowException() {
        try {
            new ClientConnection(null, null, null);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new ClientConnection(asc, is, null);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new ClientConnection(null, is, os);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new ClientConnection(asc, null, os);
            fail();
        } catch(IllegalArgumentException expected) {
        }
    }

    @Test
    public void constructorValidArgumentsSuccessful() {
        try {
            new ClientConnection(asc, is, os);
        } catch(IllegalArgumentException fail) {
            fail();
        }
    }
    // </editor-fold>

    // <editor-fold desc="send">
    @Test
    public void sendWritesToOutputStream() throws IOException, IllegalArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        BigInteger expected = new BigInteger("5");
        c.send(expected);

        byte[] serialized = os.toByteArray();
        BigInteger actual = new BigInteger(serialized);
        assertEquals(expected, actual);
    }

    @Test
    public void sendNullArgumentThrowsException() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        try {
            c.send(null);
            fail();
        } catch(Exception expected) {
        }
    }
    // </editor-fold>

    // <editor-fold desc="receive">
    @Test
    public void receiveNullIfNoNew() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, empty, os);
        Object actual = c.receive();
        assertNull(actual);
    }

    @Test
    public void receiveValidIfNew() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        Object actual = c.receive();
        assertNotNull(actual);
    }

    @Test
    public void receiveMultipleIfMultiple() throws IllegalArgumentException {
        is.mark(buffSize / 2);
        Connection c = new ClientConnection(asc, is, os);
        c.receive();
        Object actual = c.receive();
        assertNotNull(actual);
    }
    // </editor-fold>

    // <editor-fold desc="connect">
    @Test
    public void connectTrueIfSuccess() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        boolean isConnected = c.connect();
        if(isConnected) {
            assertTrue(c.isOpen());
        } else {
            fail();
        }
    }

    @Test
    public void connectFalseIfFail() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        boolean isConnected = c.connect();
        if(!isConnected) {
            assertFalse(c.isOpen());
        } else {
            fail();
        }
    }

    @Test
    public void connectMultipleTimesOk() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        c.connect();
        c.connect();
        c.disconnect();
    }
    // </editor-fold>

    // <editor-fold desc="disconnect">
    @Test
    public void disconnectTrueIfSuccess() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        boolean isConnected = c.connect();
        if(isConnected) {
            if(c.disconnect()) {
                assertFalse(c.isOpen());
            } else {
                fail();
            }
        } else {
            fail();
        }
    }

    @Test
    public void disconnectFalseIfFail() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        boolean isConnected = c.connect();
        if(isConnected) {
            if(!c.disconnect()) {
                assertTrue(c.isOpen());
            } else {
                fail();
            }
        } else {
            fail();
        }
    }

    @Test
    public void disconnectMultipleTimesOk() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        c.connect();
        c.disconnect();
        c.disconnect();
    }
    // </editor-fold>
}
