package Helpers.CustomerHelpers;

import Helpers.Message;
import com.google.gson.Gson;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;

import static Helpers.MessageCommand.*;

/**
 * This class serves as the interface between the server and the customer program, it performs a handshake to connect
 * to the server. It delegates the main listening and writing responsibilities to {@link ServerListener} and {@link ServerWriter}
 * and it processes the messages received by the listener.
 */
public class ServerInterface implements AutoCloseable {

    private final static int port = 8888;
    private final static Socket socket = new Socket();

    private Scanner reader;
    private PrintWriter writer;

    private boolean connectedToServer = false;
    public boolean connectionIsValid = false;
    private ServerWriter serverWriter;

    private Message serverResponse;

    private String clientName;

    /**
     * Creates a server interface that attempts to establish a connection to the server.
     */
    public ServerInterface() {
        try {
            System.out.println("Establishing a connecting to the server");
            SocketAddress address = new InetSocketAddress("localhost", port);
            socket.connect(address, 2000);

            reader = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);

            connectedToServer = true;
            System.out.println("Connection established to the server");

        } catch (Exception e) {
            System.out.println("Error: Failed to establish a connection to the server");
        }
    }

    /**
     * Attempts to perform a handshake to get a valid connection to the server.
     */
    public void performHandshake() {
        if (connectedToServer) {
            try {
                String messageIn = reader.nextLine();

                if (Objects.equals(messageIn, "NAME")) {

                    getClientNameFromUserInput();
                    String welcomeMessage = reader.nextLine();

                    connectionIsValid = true;
                    //Outputs welcome message
                    System.out.println(welcomeMessage);
                    writer.println("ACKNOWLEDGED");
                } else {
                    errorInvalidServerResponse(messageIn);
                }
            } catch (Exception e) {
                errorLostConnection();
            }
        }
    }

    /**
     * Keep requesting a name from the user until a non-empty input is given.
     */
    private void getClientNameFromUserInput() {
        Scanner scanner = new Scanner(System.in);

        clientName = "";

        while (clientName.isBlank()) {
            System.out.println("What is your name?");
            System.out.print("> ");
            clientName = scanner.nextLine();
        }

        clientName = clientName.trim();
        clientName = clientName.toLowerCase();

        //Capitalize first letter
        clientName = clientName.substring(0, 1).toUpperCase() + clientName.substring(1);
        writer.println(clientName);
    }

    /**
     * Initialize {@link ServerListener} on a new thread.
     */
    public void initServerListener() {
        Thread serverListener = new Thread(new ServerListener(this, reader));
        serverListener.start();
    }

    /**
     * Initialize {@link ServerWriter}.
     */
    public void initServerWriter() {
        serverWriter = new ServerWriter(this, writer);
    }

    /**
     * Split the user's input by whitespace character and send to the {@link ServerWriter}.
     * @param input text input from the user
     */
    public void sendCommand(String input) {
        //Regex matches any sequence of whitespace characters
        String[] split = input.toUpperCase().split("\\s+");
        serverWriter.parseInputAndSendMessageToServer(split);
    }

    /**
     * Confirms that a message has been received as this is called after a message has been sent from {@link ServerWriter}
     * as each message sent to the server from {@code ServerWriter} should get a response.
     * <p>Outputs the relevant text to the terminal depending on the serverResponse.
     */
    public void waitForResponse() {
        while (serverResponse == null)
            Thread.onSpinWait();

        if (connectionIsValid) {
            if (serverResponse.containsError()) {
                System.out.println(serverResponse.getErrorMessage());

            } else if (serverResponse.getCommand() == ORDER_COMPLETE) {
                System.out.println("\n" + serverResponse.getMessageBody());
                System.out.print("> ");

            } else if (serverResponse.getCommand() == ORDER_STATUS) {
                System.out.println("\n" + serverResponse.getMessageBody());

            } else if (serverResponse.getCommand() == ORDER_ACKNOWLEDGED) {
                System.out.println("\n" + serverResponse.getMessageBody());
            }
        }

        serverResponse = null;
    }

    public void setServerResponse(Message serverResponse) {
        this.serverResponse = serverResponse;
    }

    /**
     * Returns if the connection to the server is valid.
     * @return if the connection to the server is valid
     */
    public boolean connectionIsValid() {
        return connectionIsValid;
    }

    /**
     * Output error message.
     */
    public void errorLostConnection() {
        errorMessage("Lost connection to the server");
    }

    /**
     * Output error message.
     */
    public void errorWriterIsNull() {
        errorMessage("Writer is null");
    }

    /**
     * Output error message.
     */
    private void errorInvalidServerResponse(String serverResponse) {
        errorMessage("Received invalid server response '" + serverResponse + "'");
    }

    /**
     * Outputs error message and sets connection to be invalid.
     * @param errorMessage error message to be displayed.
     */
    public void errorMessage(String errorMessage) {
        System.out.println("Error: " + errorMessage);
        connectionIsValid = false;
    }

    /**
     * Shutdown hook runs when either the program exits normally (user types in 'exit') or a SIGTERM signal is sent by
     * the user. The hook sends an EXIT {@link Message} to the server, prints a message to the terminal and
     * closes the socket and the reader and writer.
     */
    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Message exitMessage = new Message(-1);
            exitMessage.setCommand(EXIT);

            Gson gson = new Gson();
            String messageJson = gson.toJson(exitMessage);
            writer.println(messageJson);

            System.out.println("\n" + clientName + " has exited the cafe");
            close();
        }));
    }

    /**
     * Overridden method for {@link AutoCloseable} to automatically close resources in a try-catch with resources block.
     */
    @Override
    public void close() {
        if (reader != null) reader.close();
        if (writer != null) writer.close();

        try {
            if (socket.isConnected()) socket.close();
        } catch (Exception e) {
            System.out.println("Error: Failed to close socket");
        }
    }
}