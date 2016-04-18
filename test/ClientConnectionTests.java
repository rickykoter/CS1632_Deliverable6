import org.junit.Test;

import java.io.*;
import java.nio.channels.AsynchronousSocketChannel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientConnectionTests {
    AsynchronousSocketChannel asc = mock(AsynchronousSocketChannel.class);
    ChatInputStream is = mock(ChatInputStream.class);
    ChatOutputStream os = mock(ChatOutputStream.class);
    ClientConnection connection = new ClientConnection(asc, is, os);

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
    // </editor-fold>

    // <editor-fold desc="send">
    @Test
    public void sendWritesToOutputStream() throws IOException, IllegalArgumentException {
        when(asc.isOpen()).thenReturn(true);
        Object expected = new Object();
        connection.send(expected);

        verify(os, times(1)).writeMessage(expected);
    }

    @Test
    public void sendNullArgumentReturnsFalse() throws IllegalArgumentException {
        when(asc.isOpen()).thenReturn(true);
        boolean actual = connection.send(null);

        assertFalse(actual);
    }

    @Test
    public void sendExceptionReturnsFalse() throws IOException {
        doThrow(new IOException()).when(os).writeMessage(anyObject());

        assertFalse(connection.send(new Object()));
    }
    // </editor-fold>

    // <editor-fold desc="receive">
    @Test
    public void receiveNullIfNoNew() throws IllegalArgumentException, IOException, ClassNotFoundException {
        when(is.available()).thenReturn(0);
        Object actual = connection.receive();
        assertNull(actual);
    }

    @Test
    public void receiveValidIfNew() throws IllegalArgumentException, IOException, ClassNotFoundException {
        when(asc.isOpen()).thenReturn(true);
        Object expected = new Object();
        when(is.available()).thenReturn(4);
        when(is.readMessage()).thenReturn(expected);

        Object actual = connection.receive();

        assertEquals(actual, expected);
    }

    @Test
    public void receiveMultipleIfMultiple() throws IllegalArgumentException, IOException, ClassNotFoundException {
        when(asc.isOpen()).thenReturn(true);
        Object expected = new Object();
        when(is.available()).thenReturn(4);
        when(is.readMessage()).thenReturn(new Object()).thenReturn(expected).thenReturn(null);

        connection.receive();
        Object actual = connection.receive();
        assertEquals(actual, expected);
    }

    @Test
    public void receiveExceptionReturnsNull() throws IOException, ClassNotFoundException {
        when(is.readMessage()).thenThrow(new IOException());

        assertNull(connection.receive());
    }
    // </editor-fold>

    // <editor-fold desc="disconnect">
    @Test
    public void disconnectMultipleTimesNoException() throws IllegalArgumentException {
        connection.disconnect();
        connection.disconnect();
    }

    @Test
    public void disconnectExceptionReturnsFalse() throws IOException {
        doThrow(new IOException()).when(is).close();

        assertFalse(connection.disconnect());
    }
    // </editor-fold>
}
