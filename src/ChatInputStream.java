import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ChatInputStream extends ObjectInputStream{

    public ChatInputStream(InputStream in) throws IOException {
        super(in);
    }

    public Object readMessage() throws IOException, ClassNotFoundException {
        return this.readObject();
    }
}
