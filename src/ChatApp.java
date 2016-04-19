import javax.net.SocketFactory;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Main class and GUI. Allows for user interaction with client and server creation and messag sending.
 * By Richard Kotermanski and Jon Povirk
 */
public class ChatApp extends JFrame {
    private JPanel mainPanel;
    private JTextField hostTextField;
    private JTextField portTextField;
    private JLabel portLabel;
    private JLabel hostLabel;
    public JButton connectButton;
    private JLabel aliasLabel;
    private JTextField aliasTextField;
    private JScrollPane outputScrollPane;
    private JTextArea chatTextArea;
    public JButton sendMessageButton;
    public JButton startServerButton;
    public JButton disconnectButton;
    private JTextField messageTextArea;
    private JLabel messageLabel;
    private JLabel chatAreaLabel;
    private Client client;
    private SocketFactory socketFactory;
    private Server server;

    // Handles button actions and displaying result to the text output area in th GUI.
    public ChatApp(Client c, SocketFactory sf) {
        if (c == null || sf == null) throw new IllegalArgumentException("Cannot be null");
        System.setOut(new PrintStream(new TextAreaOutputStream(chatTextArea)));
        client = c;
        aliasTextField.setText(client.getAlias());
        socketFactory = sf;

        connectButton.addActionListener(e -> {
            String result = connectToServer(hostTextField.getText(), portTextField.getText(), aliasTextField.getText());
            if (result.length() > 0) {
                System.out.println(result);
            }
        });

        disconnectButton.addActionListener(e -> {
            String result = disconnectFromServer();
            if (result.length() > 0) {
                System.out.println(result);
            }
        });

        sendMessageButton.addActionListener(e -> {
            String result = sendMessageToServer(messageTextArea.getText());
            if (result.length() > 0) {
                System.out.println(result);
            }
            messageTextArea.setText("");
        });

        startServerButton.addActionListener(e -> {
            String result = startServer(hostTextField.getText(), portTextField.getText(), aliasTextField.getText());
            result += "\n";
            result += connectToServer("127.0.0.1", portTextField.getText(), aliasTextField.getText());
            if (result.length() > 0) {
                System.out.println(result);
            }
        });
    }

    // Attempt to create a Client and its Connection to a corresponding Server at the given hostName an portNumber, and
    // set the client's alias if between 1 and 15 characters. Sets button enabled states for GUI.
    public String connectToServer(String hostName, String portNumber, String alias) {
        if (client == null) {
            client = new ChatClient();
        }
        if (!client.setAlias(alias)) {
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            sendMessageButton.setEnabled(false);
            messageTextArea.setEnabled(false);
            return "Error: Alias is invalid. Must be between 1 and 15 characters.";
        }
        int port;
        try {
            port = Integer.parseInt(portNumber);
        } catch (NumberFormatException e) {
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            sendMessageButton.setEnabled(false);
            messageTextArea.setEnabled(false);
            return "Error: Port is not a number!";
        }

        if (hostName == null) {
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            sendMessageButton.setEnabled(false);
            messageTextArea.setEnabled(false);
            return "Error: Host cannot be null!";
        }

        try {
            Socket s = socketFactory.createSocket(hostName, port);
            ChatOutputStream co = new ChatOutputStream(s.getOutputStream());
            co.flush();
            ChatInputStream ci = new ChatInputStream(s.getInputStream());
            Connection c = new ServerConnection(s, co, ci);

            if (!client.connect(c)) {
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                sendMessageButton.setEnabled(false);
                messageTextArea.setEnabled(false);
                return "Error: Unable to connect to desired host and port!";
            }
        } catch (IOException e) {
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            sendMessageButton.setEnabled(false);
            messageTextArea.setEnabled(false);
            return "Error: Unable to connect to desired host and port!";
        }

        connectButton.setEnabled(false);
        disconnectButton.setEnabled(true);
        sendMessageButton.setEnabled(true);
        messageTextArea.setEnabled(true);

        client.beginReceiving();
        return "You have been successfully connected to " + hostName + " at port " + portNumber + ".";
    }

    // Attempts to disconnect from the server if client is already connected and returns success/failure message.
    // Sets button enabled states for GUI.
    public String disconnectFromServer() {
        if (client.isConnected() && client.disconnect()) {
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            sendMessageButton.setEnabled(false);
            messageTextArea.setEnabled(false);
            startServerButton.setEnabled(true);
            client = null;
            if (server != null) {
                server.stop();
                server = null;
            }
            return "You have been successfully disconnected.";
        } else {
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            sendMessageButton.setEnabled(true);
            messageTextArea.setEnabled(true);
            return "Error: You were unable to be disconnected!";
        }
    }

    // Attempts to send messages of length 1 or more to the currently connected Server if connected.
    // Returns empty string if successful, and appropriate error message otherwise. Sets button enabled states for GUI.
    public String sendMessageToServer(String MessageText) {
        if (MessageText.length() <= 0) {
            return "Error: Please provide text to send!";
        }
        if (client.isConnected() && client.send(new Message(client.getAlias(), MessageText))) {
            return "";
        } else {
            return "Error: Unable to send message to the server!";
        }
    }

    // Attempts to create Server for the provided hostName and portNumber, and attempts to create client
    // for the server with the given alias. Returns a status message upon success/failure. Sets button enabled states for GUI.
    public String startServer(String hostName, String portNumber, String alias) {
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        sendMessageButton.setEnabled(false);
        messageTextArea.setEnabled(false);

        if (hostName == null || portNumber == null || alias == null) {
            return "Error: Invalid Host Name or Port Number";
        }
        int port;
        try {
            port = Integer.parseInt(portNumber);
        } catch (NumberFormatException e) {
            return "Error: Port is not a number!";
        }

        try {
            InetSocketAddress hostAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port);
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open().bind(hostAddress);
            ClientConnectionRunner connectionWatch = new ClientConnectionRunner(serverSocketChannel);
            server = new Server(connectionWatch, new HashSet<>(), new LinkedList<>());
            server.run();
        } catch (Exception e) {
            return "Error: Unable to host on desired host and port!";
        }

        System.out.println("Successfully created server \"" + hostName + "\" on port " + port + ".");
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(true);
        sendMessageButton.setEnabled(true);
        messageTextArea.setEnabled(true);
        startServerButton.setEnabled(false);

        return alias + " has started the server " + hostName + " on port " + port + ".";
    }

    public static void main(String[] args) {
        setTheme();
        ChatApp gui = new ChatApp(new ChatClient(), SocketFactory.getDefault());
        JFrame frame = new JFrame("Chat App");
        frame.setContentPane(gui.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static void setTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // if we can't get a nice theme, run anyway
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setAutoscrolls(true);
        panel1.setPreferredSize(new Dimension(700, 400));
        mainPanel.add(panel1, BorderLayout.CENTER);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
        outputScrollPane = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.weighty = 5.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(outputScrollPane, gbc);
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        chatTextArea.setEnabled(false);
        chatTextArea.setLineWrap(true);
        chatTextArea.setText("");
        outputScrollPane.setViewportView(chatTextArea);
        startServerButton = new JButton();
        startServerButton.setText("Start Server");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(startServerButton, gbc);
        disconnectButton = new JButton();
        disconnectButton.setEnabled(false);
        disconnectButton.setText("Disconnect");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(disconnectButton, gbc);
        messageTextArea = new JTextField();
        messageTextArea.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(messageTextArea, gbc);
        aliasTextField = new JTextField();
        aliasTextField.setText("Anonymous");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(aliasTextField, gbc);
        portTextField = new JTextField();
        portTextField.setText("8123");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(portTextField, gbc);
        hostTextField = new JTextField();
        hostTextField.setText("127.0.0.1");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(hostTextField, gbc);
        portLabel = new JLabel();
        portLabel.setText("Port: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel1.add(portLabel, gbc);
        aliasLabel = new JLabel();
        aliasLabel.setText("Alias: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel1.add(aliasLabel, gbc);
        connectButton = new JButton();
        connectButton.setEnabled(true);
        connectButton.setText("Connect");
        connectButton.setToolTipText("Connect to <Host> on port <Port> with the name <Alias>");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.weightx = 0.25;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(connectButton, gbc);
        hostLabel = new JLabel();
        hostLabel.setText("Host: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel1.add(hostLabel, gbc);
        sendMessageButton = new JButton();
        sendMessageButton.setEnabled(false);
        sendMessageButton.setText("Send");
        sendMessageButton.setToolTipText("Disconnect from group server and file server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 7;
        gbc.weightx = 0.25;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(sendMessageButton, gbc);
        chatAreaLabel = new JLabel();
        chatAreaLabel.setText("Chat:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        panel1.add(chatAreaLabel, gbc);
        messageLabel = new JLabel();
        messageLabel.setText("Message:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(messageLabel, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
