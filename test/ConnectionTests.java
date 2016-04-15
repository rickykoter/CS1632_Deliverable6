import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionTests {
    @Test
    public void connectTestFailedConnection() {
        Client c = new Session();
        Connection conn = mock(Connection.class);
        when(conn.connect()).thenReturn(false);

        assertFalse(c.connect(conn));
    }

}
