public class Message {
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
