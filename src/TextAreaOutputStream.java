import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

// Used to redirect System.out to a JTextArea. Implementation from:
// http://stackoverflow.com/questions/14706674/system-out-println-to-jtextarea
public class TextAreaOutputStream extends OutputStream {
    private final JTextArea textArea;
    private StringBuilder builder = new StringBuilder();
    public TextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int c) throws IOException {
        if(c == '\n') {
            // write when we hit a newline
            String text = builder.toString() + "\n";
            textArea.append(text);
            builder = new StringBuilder();
        } else {
            // else add to string builder
            builder.append((char)c);
        }
    }
}
