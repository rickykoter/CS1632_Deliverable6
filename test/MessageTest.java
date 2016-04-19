import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests of the Message class.
 * By Richard Kotermanski and Jon Povirk
 */
public class MessageTest {
    // Tests that the sender and text of a message are set by the respective arguments of the constructor if
    // non-null strings.
    @Test
    public void constructorTest(){
        Message m = new Message("FooBar", "BarFoo");
        assertEquals("FooBar", m.getSender());
        assertEquals("BarFoo", m.getText());
    }

    // Tests that the sender and text of a message are set by the respective arguments of the constructor in the case of
    // null arguments.
    @Test
    public void constructorTestNulls(){
        Message m = new Message(null, null);
        assertNull(m.getText());
        assertNull(m.getSender());
    }

    // Tests that the getMessage function returns null after being set that by the constructor.
    @Test
    public void getSenderTestNull(){
        Message m = new Message(null, "BarFoo");
        assertEquals("BarFoo", m.getText());
        assertNull(m.getSender());
    }

    // Tests that the getSender function returns the sender's name (FooBar) after being set that by the constructor.
    @Test
    public void getSenderTest(){
        Message m = new Message("FooBar", "");
        assertEquals("FooBar", m.getSender());
    }

    // Tests that the getSender function returns an empty string after being set that by the constructor.
    @Test
    public void getSenderEmpty(){
        Message m = new Message("", "");
        assertEquals("", m.getSender());
    }

    // Tests that the getMessage function returns the message's text (FooBar) after being set that by the constructor.
    @Test
    public void getTextTest(){
        Message m = new Message("", "FooBar");
        assertEquals("FooBar", m.getText());
    }

    // Tests that the getMessage function returns an empty string after being set that by the constructor.
    @Test
    public void getTextTestEmpty(){
        Message m = new Message("", "");
        assertEquals("", m.getText());
    }

    // Tests that the getText function returns null after being set that by the constructor.
    @Test
    public void getTextTestNull(){
        Message m = new Message("FooBar", null);
        assertNull(m.getText());
        assertEquals("FooBar", m.getSender());
    }
}
