import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageTest {
    @Test
    public void constructorTest(){
        Message m = new Message("FooBar", "BarFoo");
        assertEquals("FooBar", m.getSender());
        assertEquals("BarFoo", m.getText());
    }
    @Test
    public void getSenderTest(){
        Message m = new Message("FooBar", "");
        assertEquals("FooBar", m.getSender());
    }

    @Test
    public void getSenderEmpty(){
        Message m = new Message("", "");
        assertEquals("", m.getSender());
    }

    @Test
    public void getTextTest(){
        Message m = new Message("", "FooBar");
        assertEquals("FooBar", m.getText());
    }

    @Test
    public void getTextTestEmpty(){
        Message m = new Message("", "");
        assertEquals("", m.getText());
    }





}
