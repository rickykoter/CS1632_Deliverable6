import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionTests {
    @Test
    public void connectTestFailedConnection() {
        Connection c = new ServerConnection("",0);

        assertFalse(c.connect());
    }

}
