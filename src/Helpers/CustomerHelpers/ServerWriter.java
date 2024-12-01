package Helpers.CustomerHelpers;

import Helpers.Message;
import com.google.gson.Gson;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Objects;

import static Helpers.MessageCommand.*;

/**
 * Class that handles all messages sent to the server after handshake is completed and connection is valid.
 */
public class ServerWriter {

    private final ServerInterface serverInterface;
    private final PrintWriter writer;

    private String[] input;
    private int messageId;
    private Message message;
    private boolean sendMessage;

    /**
     * Creates a server writer.
     * @param serverInterface reference to {@link ServerInterface}, used for checking the connection and
     *                        checking that messages are being received
     * @param writer using the same writer as the writer used by {@link ServerInterface} for the handshake
     */
    public ServerWriter(ServerInterface serverInterface, PrintWriter writer) {
        this.serverInterface = serverInterface;
        this.writer = writer;
    }

    /**
     * Initialize a new message.
     */
    private void initMessage() {
        messageId++;
        sendMessage = true;
        message = new Message(messageId);
    }

    /**
     * Constructs a message from the user input, checks the connection, sends the message to the server and
     * checks if the message has been received.
     * @param input the text input from the customer
     */
    public void parseInputAndSendMessageToServer(String[] input) {
        this.input = input;
        initMessage();

        checkConnection();

        if (serverInterface.connectionIsValid()) {
            if (writer != null) {

                parseInputAndSendMessageToServer();

            } else {
                serverInterface.errorWriterIsNull();
            }
        }

        if (sendMessage) {
            checkReceived();
        }
    }

    /**
     * Tests connection by sending a PING request.
     */
    private void checkConnection() {
        try {
            if (writer != null) {
                message.setCommand(PING);
                sendMessageToServer();
                checkReceived();
            } else {
                serverInterface.errorWriterIsNull();
            }
        } catch (Exception e) {
            serverInterface.errorLostConnection();
        }
    }

    /**
     * Calls for the {@link ServerInterface} to check that a message has been received from the
     * server.
     */
    private void checkReceived() {
        serverInterface.waitForResponse();
    }

    /**
     * Calls {@link #parseAndSetCommandToMessage} to parse and set the command. If the input is an order,
     * call {@link #setMessageBodyForOrderCommand} to add the number of drinks and drink/s to the message body.
     * If no errors have been detected, send the message to the server.
     */
    private void parseInputAndSendMessageToServer() {
        parseAndSetCommandToMessage();

        if (message.getCommand() == ORDER) {
            setMessageBodyForOrderCommand();
        }


        if (message.containsError()) {
            System.out.println(message.getErrorMessage());
            sendMessage = false;

        } else {
            sendMessageToServer();
        }
    }

    /**
     * Sets the parsed command to the {@link Message}, sets the error message if the command is unknown.
     */
    private void parseAndSetCommandToMessage() {
        switch (input[0]) {
            case "ORDER" -> parseAndSetOrderCommand();
            case "EXIT" -> message.setCommand(EXIT);
            default -> message.setErrorMessage("Invalid command: '" + input[0]  + "'. Valid commands: PING, EXIT, ORDER, ORDER STATUS"  + "'\nmessage not sent");
        }
    }

    /**
     * Checks if the command is 'order n DRINK/s' or 'order status'.
     */
    private void parseAndSetOrderCommand() {
        if (input.length > 1) {
            if (Objects.equals(input[1], "STATUS")) {
                message.setCommand(ORDER_STATUS);

            } else {
                message.setCommand(ORDER);
            }

        } else {
            message.setCommand(ORDER);
        }
    }

    /**
     * Remove 'order' from {@code input}, join the rest and set the message body.
     */
    private void setMessageBodyForOrderCommand() {
        String[] inputExcludingCommand = Arrays.copyOfRange(input, 1, input.length);
        String messageBody = String.join(" ", inputExcludingCommand);

        message.setMessageBody(messageBody);
    }

    /**
     * Sends the {@link Message} to the server.
     */
    private void sendMessageToServer() {
        Gson gson = new Gson();
        String messageJson = gson.toJson(message);
        writer.println(messageJson);
    }
}
