import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ChatOutputStream extends ObjectOutputStream{

    public ChatOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    public void writeMessage(Object o) throws IOException{
        this.writeObject(o);
    }
}
