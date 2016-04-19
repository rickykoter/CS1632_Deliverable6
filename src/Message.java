import java.io.Serializable;

/**
 * Message class that contains fields for text and its associated sender.
 * By Richard Kotermanski and Jon Povirk
 */
public class Message implements Serializable {
    String text;
    String sender;
    public Message(String sndr, String txt){
        text = txt;
        sender = sndr;
    }
    public String getSender(){
        return sender;
    }
    public String getText(){
        return text;
    }
}
