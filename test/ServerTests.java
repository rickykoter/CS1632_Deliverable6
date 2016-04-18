import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ServerTests {
    private ClientConnectionRunner connectionWatch = mock(ClientConnectionRunner.class);
    private Set<Connection> connectedClients = new HashSet<>();
    private Queue<Message> unsentMessages = new LinkedList<>();

    // <editor-fold desc="constructor">
    @Test
    public void constructorNullArgumentsThrowException() {
        try {
            new Server(null, null, null);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new Server(connectionWatch, connectedClients, null);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new Server(null, connectedClients, unsentMessages);
            fail();
        } catch(IllegalArgumentException expected) {
        }

        try {
            new Server(connectionWatch, null, unsentMessages);
            fail();
        } catch(IllegalArgumentException expected) {
        }
    }

    @Test
    public void constructorValidArgumentsSuccessful() {
        try {
            new Server(connectionWatch, connectedClients, unsentMessages);
        } catch(IllegalArgumentException fail) {
            fail();
        }
    }
    // </editor-fold>

    // <editor-fold desc="stop">
    @Test(timeout=5000)
    public void stopStopsServerWithNoConnections() throws InterruptedException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Thread t = new Thread(server);
        t.start();

        server.stop();

        t.join();
    }

    @Test(timeout=5000)
    public void stopStopsServerWithConnections() throws InterruptedException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        connectedClients.add(mock(Connection.class));
        connectedClients.add(mock(Connection.class));
        unsentMessages.add(mock(Message.class));
        unsentMessages.add(mock(Message.class));
        unsentMessages.add(mock(Message.class));
        unsentMessages.add(mock(Message.class));
        Thread t = new Thread(server);
        t.start();

        server.stop();

        t.join();
    }
    // </editor-fold">

    // <editor-fold desc="sendMessage">
    @Test
    public void sendMessageNullArgumentThrowsException() {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        try {
            server.sendMessage(null);
            fail();
        } catch(IllegalArgumentException expected) {
        }
    }

    @Test
    public void sendMessageSendsToAllConnectedClients() {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnectionA = mock(Connection.class);
        Connection mockConnectionB = mock(Connection.class);
        Message mockMessage = mock(Message.class);
        connectedClients.add(mockConnectionA);
        connectedClients.add(mockConnectionB);

        server.sendMessage(mockMessage);

        verify(mockConnectionA, times(1)).send(mockMessage);
        verify(mockConnectionB, times(1)).send(mockMessage);
    }
    // </editor-fold>

    // <editor-fold desc="checkForConnections">
    @Test
    public void checkForConnectionsDoesNotAddIfNoNew() throws IOException, ClassNotFoundException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        when(connectionWatch.receive()).thenReturn(null);

        server.checkForConnections();

        assertEquals(0, connectedClients.size());
    }

    @Test
    public void checkForConnectionsAddsConnectionIfNew() throws IOException, ClassNotFoundException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isOpen()).thenReturn(true).thenReturn(false);
        when(connectionWatch.receive()).thenReturn(mockConnection).thenReturn(null);

        server.checkForConnections();

        assertTrue(connectedClients.contains(mockConnection));
    }

    @Test
    public void checkForConnectionsAddsConnectionIfNewSizeCorrect() throws IOException, ClassNotFoundException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isOpen()).thenReturn(true).thenReturn(false);
        when(connectionWatch.receive()).thenReturn(mockConnection).thenReturn(null);

        server.checkForConnections();

        assertEquals(1, connectedClients.size());
    }

    @Test
    public void checkForConnectionsAddsMultipleConnectionsIfMultiple() throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnectionA = mock(Connection.class);
        when(mockConnectionA.isOpen()).thenReturn(true);

        Connection mockConnectionB = mock(Connection.class);
        when(mockConnectionB.isOpen()).thenReturn(true);
        when(connectionWatch.receive()).thenReturn(mockConnectionA, mockConnectionB, null);

        server.checkForConnections();
        server.checkForConnections();

        assertTrue(connectedClients.contains(mockConnectionA));
        assertTrue(connectedClients.contains(mockConnectionB));
    }

    @Test
    public void checkForConnectionsAddsMultipleConnectionsIfMultipleSizeCorrect() throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnectionA = mock(Connection.class);
        when(mockConnectionA.isOpen()).thenReturn(true);

        Connection mockConnectionB = mock(Connection.class);
        when(mockConnectionB.isOpen()).thenReturn(true);
        when(connectionWatch.receive()).thenReturn(mockConnectionA, mockConnectionB, null);

        server.checkForConnections();
        server.checkForConnections();

        assertEquals(2, connectedClients.size());
    }

    @Test
    public void checkForConnectionsRemovesDisconnectedClients() {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnectionA = mock(Connection.class);
        when(mockConnectionA.isOpen()).thenReturn(false);
        Connection mockConnectionB = mock(Connection.class);
        when(mockConnectionB.isOpen()).thenReturn(true);
        Connection mockConnectionC = mock(Connection.class);
        when(mockConnectionC.isOpen()).thenReturn(false);
        connectedClients.addAll(Arrays.asList(mockConnectionA, mockConnectionB, mockConnectionC));

        server.checkForConnections();

        assertFalse(connectedClients.contains(mockConnectionA));
        assertTrue(connectedClients.contains(mockConnectionB));
        assertFalse(connectedClients.contains(mockConnectionC));
    }

    @Test
    public void checkForConnectionsRemovesDisconnectedClientsSizeCorrect() {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnectionA = mock(Connection.class);
        when(mockConnectionA.isOpen()).thenReturn(false);
        Connection mockConnectionB = mock(Connection.class);
        when(mockConnectionB.isOpen()).thenReturn(true);
        Connection mockConnectionC = mock(Connection.class);
        when(mockConnectionC.isOpen()).thenReturn(false);
        connectedClients.addAll(Arrays.asList(mockConnectionA, mockConnectionB, mockConnectionC));

        server.checkForConnections();

        assertEquals(1, connectedClients.size());
    }
    // </editor-fold>

    // <editor-fold desc="checkForNewMessages">
    @Test
    public void checkForNewMessagesDoesNotAddIfNoNew() throws IOException, ClassNotFoundException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.receive()).thenReturn(null);
        connectedClients.add(mockConnection);

        server.checkForMessages();

        assertEquals(0, unsentMessages.size());
    }

    @Test
    public void checkForNewMessagesAddsConnectionIfNew() throws IOException, ClassNotFoundException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnection = mock(Connection.class);
        Message mockMessage = mock(Message.class);
        when(mockConnection.receive()).thenReturn(mockMessage).thenReturn(null);
        connectedClients.add(mockConnection);

        server.checkForMessages();

        assertTrue(unsentMessages.contains(mockMessage));
    }

    @Test
    public void checkForNewMessagesAddsConnectionIfNewSizeCorrect() throws IOException, ClassNotFoundException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnection = mock(Connection.class);
        Message mockMessage = mock(Message.class);
        when(mockConnection.receive()).thenReturn(mockMessage).thenReturn(null);
        connectedClients.add(mockConnection);

        server.checkForMessages();

        assertEquals(1, unsentMessages.size());
    }

    @Test
    public void checkForNewMessagesAddsMultipleConnectionsIfMultiple() throws IOException, ClassNotFoundException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnection = mock(Connection.class);
        Message mockMessageA = mock(Message.class);
        Message mockMessageB = mock(Message.class);
        when(mockConnection.receive()).thenReturn(mockMessageA, mockMessageB, null);
        connectedClients.add(mockConnection);

        server.checkForMessages();
        server.checkForMessages();

        assertTrue(unsentMessages.contains(mockMessageA));
        assertTrue(unsentMessages.contains(mockMessageB));
    }

    @Test
    public void checkForNewMessagesAddsMultipleConnectionsIfMultipleSizeCorrect() throws IOException, ClassNotFoundException {
        Server server = new Server(connectionWatch, connectedClients, unsentMessages);
        Connection mockConnection = mock(Connection.class);
        Message mockMessageA = mock(Message.class);
        Message mockMessageB = mock(Message.class);
        when(mockConnection.receive()).thenReturn(mockMessageA, mockMessageB, null);
        connectedClients.add(mockConnection);

        server.checkForMessages();
        server.checkForMessages();

        assertEquals(2, unsentMessages.size());
    }
    // </editor-fold>
}
