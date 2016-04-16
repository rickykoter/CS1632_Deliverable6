import com.sun.javaws.exceptions.InvalidArgumentException;
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

    @Test
    public void constructorNullArgumentsThrowException() {
        try {
            new ClientConnection(null, null, null);
            fail();
        } catch(InvalidArgumentException expected) {
        }

        try {
            new ClientConnection(asc, is, null);
            fail();
        } catch(InvalidArgumentException expected) {
        }

        try {
            new ClientConnection(null, is, os);
            fail();
        } catch(InvalidArgumentException expected) {
        }

        try {
            new ClientConnection(asc, null, os);
            fail();
        } catch(InvalidArgumentException expected) {
        }
    }

    @Test
    public void constructorValidArgumentsSuccessful() {
        try {
            new ClientConnection(asc, is, os);
        } catch(InvalidArgumentException fail) {
            fail();
        }
    }

    @Test
    public void sendWritesToOutputStream() throws IOException, InvalidArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        BigInteger expected = new BigInteger("5");
        c.send(expected);

        byte[] serialized = os.toByteArray();
        BigInteger actual = new BigInteger(serialized);
        assertEquals(expected, actual);
    }

    @Test
    public void sendNullArgumentThrowsException() throws InvalidArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        try {
            c.send(null);
            fail();
        } catch(Exception expected) {
        }
    }

    @Test
    public void receiveNullIfNoNew() throws InvalidArgumentException {
        Connection c = new ClientConnection(asc, empty, os);
        Object actual = c.receive();
        assertNull(actual);
    }

    @Test
    public void receiveValidIfNew() throws InvalidArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        Object actual = c.receive();
        assertNotNull(actual);
    }

    @Test
    public void receiveMultipleIfMultiple() throws InvalidArgumentException {
        is.mark(buffSize / 2);
        Connection c = new ClientConnection(asc, is, os);
        c.receive();
        Object actual = c.receive();
        assertNotNull(actual);
    }

    @Test
    public void connectTrueIfSuccess() throws InvalidArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        boolean isConnected = c.connect();
        if(isConnected) {
            assertTrue(c.isOpen());
        } else {
            fail();
        }
    }

    @Test
    public void connectFalseIfFail() throws InvalidArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        boolean isConnected = c.connect();
        if(!isConnected) {
            assertFalse(c.isOpen());
        } else {
            fail();
        }
    }

    @Test
    public void multipleConnectOk() throws InvalidArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        c.connect();
        c.connect();
        c.disconnect();
    }

    @Test
    public void disconnectTrueIfSuccess() throws InvalidArgumentException {
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
    public void disconnectFalseIfFail() throws InvalidArgumentException {
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
    public void multipleDisconnectOk() throws InvalidArgumentException {
        Connection c = new ClientConnection(asc, is, os);
        c.connect();
        c.disconnect();
        c.disconnect();
    }
}
