package Helpers;

/**
 * Holds information for the messages sent between the server and the client
 */
public class Message {

    private final int id;
    private MessageCommand command;
    private String messageBody;
    private boolean containsError;
    private String errorMessage;

    /**
     * Creates a message with a message id
     * @param id an id for this message
     */
    public Message(int id) {
        this.id = id;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public int getId() {
        return id;
    }

    public MessageCommand getCommand() {
        return command;
    }

    public void setCommand(MessageCommand command) {
        this.command = command;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public boolean containsError() {
        return containsError;
    }

    /**
     * Sets the errors message, prefixes with "Error: " and sets this message to contain an error.
     * @param errorMessage an error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = "\nError: " + errorMessage;
        this.containsError = true;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
