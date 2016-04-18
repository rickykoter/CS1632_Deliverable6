public interface Connection extends ReadOnlyConnection {
    boolean send(Object message);
}
