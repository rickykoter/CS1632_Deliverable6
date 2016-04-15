import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class ChatAppTests {
    @Test
    public void connectTestFail() {
        Connection c = new ServerConnection("",0);

        assertFalse(c.connect());
    }

}
