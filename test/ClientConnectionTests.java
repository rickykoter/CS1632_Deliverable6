import org.junit.Before;
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
    ObjectInputStream ois;

    byte[] emptyInputBuffer = new byte[0];
    InputStream empty = new ByteArrayInputStream(emptyInputBuffer);
    ObjectInputStream emptyOis;

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ObjectOutputStream oos;

    @Before
    public void setup() throws IOException {
        oos = new ObjectOutputStream(os);
        ois = new ObjectInputStream(is);
        emptyOis = new ObjectInputStream(empty);
    }

    // <editor-fold desc="constructor">
    @Test
    public void constructorNullArgumentsThrowException() {
        try {
            new ClientConnection(null, null, null);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new ClientConnection(asc, ois, null);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new ClientConnection(null, ois, oos);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new ClientConnection(asc, null, oos);
            fail();
        } catch(IllegalArgumentException expected) {
        }
    }

    @Test
    public void constructorValidArgumentsSuccessful() {
        try {
            new ClientConnection(asc, ois, oos);
        } catch(IllegalArgumentException fail) {
            fail();
        }
    }
    // </editor-fold>

    // <editor-fold desc="send">
    @Test
    public void sendWritesToOutputStream() throws IOException, IllegalArgumentException {
        Connection c = new ClientConnection(asc, ois, oos);
        BigInteger expected = new BigInteger("5");
        c.send(expected);

        byte[] serialized = os.toByteArray();
        BigInteger actual = new BigInteger(serialized);
        assertEquals(expected, actual);
    }

    @Test
    public void sendNullArgumentThrowsException() throws IllegalArgumentException {
        Connection c = new ClientConnection(asc, ois, oos);
        try {
            c.send(null);
            fail();
        } catch(Exception expected) {
        }
    }
    // </editor-fold>

    // <editor-fold desc="receive">
    @Test
    public void receiveNullIfNoNew() throws IllegalArgumentException, IOException, ClassNotFoundException {
        Connection c = new ClientConnection(asc, emptyOis, oos);
        Object actual = c.receive();
        assertNull(actual);
    }

    @Test
    public void receiveValidIfNew() throws IllegalArgumentException, IOException, ClassNotFoundException {
        Connection c = new ClientConnection(asc, ois, oos);
        Object actual = c.receive();
        assertNotNull(actual);
    }

    @Test
    public void receiveMultipleIfMultiple() throws IllegalArgumentException, IOException, ClassNotFoundException {
        is.mark(buffSize / 2);
        Connection c = new ClientConnection(asc, ois, oos);
        c.receive();
        Object actual = c.receive();
        assertNotNull(actual);
    }
    // </editor-fold>

    // <editor-fold desc="disconnect">
    @Test
    public void disconnectTrueIfSuccess() throws IllegalArgumentException {
        ClientConnection c = new ClientConnection(asc, ois, oos);
        if(c.isOpen()) {
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
        ClientConnection c = new ClientConnection(asc, ois, oos);
        if(c.isOpen()) {
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
        ClientConnection c = new ClientConnection(asc, ois, oos);
        c.disconnect();
        c.disconnect();
    }
    // </editor-fold>
}
