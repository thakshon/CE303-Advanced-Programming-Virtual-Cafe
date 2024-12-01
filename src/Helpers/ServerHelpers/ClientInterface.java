package Helpers.ServerHelpers;

import Helpers.Drinks.Drink;
import Helpers.Drinks.DrinkType;
import Helpers.Message;
import com.google.gson.Gson;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import static Helpers.MessageCommand.*;

/**
 * A {@code ClientInterface} is created on its own thread for every new client that joins the cafe,
 * it performs a handshake with a {@code Customer} which is determined by the passed {@code socket} from {@code Barista}.
 * After the handshake is successful it will continuously listen for and process incoming {@link Message}'s
 * <p>Also uses its {@code socket} to send messages back to the client.
 */
public class ClientInterface implements Runnable {

    private final Cafe cafe;

    private final Socket socket;
    private boolean connectionIsValid = false;
    private PrintWriter writer;
    private Scanner reader;

    private final int clientId;
    private String clientName;
    private Client client;
    private int messageId;

    /**
     * Creates a new {@link ClientInterface}.
     *
     * @param socket    The socket for communicating with the client
     * @param cafe      Link to the cafe to access the 3 production areas
     * @param clientId  Id of the client on this connection, assigned from {@code Barista}
     */
    public ClientInterface(Socket socket, Cafe cafe, int clientId) {
        this.socket = socket;
        this.cafe = cafe;
        this.clientId = clientId;
    }

    /**
     * Initializes the reader ({@link Scanner}) and the writer ({@link PrintWriter})
     * for the client connection.
     * @throws Exception
     */
    private void init() throws Exception {
        reader = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Entry point for new {@link ClientInterface}.
     */
    @Override
    public void run() {
        try {
            init();
            performHandshake();
            listenForIncomingClientMessages();

        } catch (Exception e) {
            clientExited();
        }
    }

    /**
     * Request "NAME" from the client, read the response as the clients name and
     * respond with a welcome message.
     */
    private void performHandshake() {
        //Send "NAME" as the first message to the client
        writer.println("NAME");

        //Read the client's response as the client's name
        clientName = reader.nextLine();

        //Send welcome message to client
        writer.println("Hii " + clientName + ", what can I do for you?");

        acknowledgeConnection();
    }

    /**
     * Check that the client has acknowledged the connection by
     * waiting for an "ACKNOWLEDGED" message from the client.
     */
    private void acknowledgeConnection() {
        String message = reader.nextLine();

        if (Objects.equals(message, "ACKNOWLEDGED")) {
            //Flag the connection as valid
            connectionIsValid = true;

            //Create new client and log that they have connected
            client = new Client(clientId, clientName, this);
            System.out.println("Client " + client + " valid connection established");

            //Add the new client to the cafe
            cafe.addClient(client);

        } else {
            //Flag the connection as not valid
            connectionIsValid = false;
        }
    }

    /**
     * Listen for incoming messages ({@link Message}) from the client while the connection is valid.
     * <p>Runs relevant code and responds with a relevant {@link Message}.
     */
//    private void listenForIncomingClientMessages() {
//        while (connectionIsValid) {
//            if (reader.hasNext()) {
//                String messageJson = reader.nextLine();
//
//                Message messageIn = deserializeMessage(messageJson);
//                //Get message id to respond with a message with the same id
//                messageId = messageIn.getId();
//                Message messageOut = new Message(messageId);
//
//                switch (messageIn.getCommand()) {
//                    case PING -> {
//                        System.out.println("\nPING from client " + client);
//
//                        messageOut.setCommand(PING_OK);
//                    }
//                    case EXIT -> {
//                        System.out.println("EXIT from client " + client);
//
//                        connectionIsValid = false;
//                        messageOut.setCommand(EXIT_OK);
//                    }
//                    case ORDER -> {
//                        System.out.println("ORDER from client " + client);
//
//                        ActionOrder order = new ActionOrder(client, cafe, messageOut, messageIn);
//                        order.placeOrder();
//                    }
//                    case ORDER_STATUS -> {
//                        System.out.println("ORDER_STATUS from client " + client);
//
//                        ActionOrderStatus orderStatus = new ActionOrderStatus(client, messageOut);
//                        orderStatus.constructOrderStatusMessage();
//                    }
//                    default -> {
//                        System.out.println("Unknown command: " + messageJson + " from client " + client);
//
//                        messageOut.setCommand(UNKNOWN_COMMAND);
//                    }
//                }
//
//                System.out.println("Responding with " + messageOut.getCommand());
//                String messageOutJson = serializeMessage(messageOut);
//                sendMessage(messageOutJson);
//            }
//        }
//
//        clientExited();
//    }

//    ***********************************************************************************************************
//    private void listenForIncomingClientMessages() {
//        while (connectionIsValid) {
//            if (reader.hasNext()) {
//                String messageJson = reader.nextLine();
//
//                Message messageIn = deserializeMessage(messageJson);
//                // Get message ID to respond with a message with the same ID
//                messageId = messageIn.getId();
//                Message messageOut = new Message(messageId);
//
//                String command = messageIn.getCommand();
//                switch (messageIn.getCommand()) {
//                    case PING -> {
//                        System.out.println("\nPING from client " + client);
//                        messageOut.setCommand(PING_OK);
//                    }
//                    case EXIT -> {
//                        System.out.println("EXIT from client " + client);
//                        connectionIsValid = false;
//                        messageOut.setCommand(EXIT_OK);
//                    }
//                    case ORDER -> {
//                        System.out.println("ORDER from client " + client);
//                        ActionOrder order = new ActionOrder(client, cafe, messageOut, messageIn);
//                        order.placeOrder();
//                    }
//                    case ORDER_STATUS -> {
//                        System.out.println("ORDER_STATUS from client " + client);
//                        ActionOrderStatus orderStatus = new ActionOrderStatus(client, messageOut);
//                        orderStatus.constructOrderStatusMessage();
//                    }
//                    default -> {
//                        System.out.println("Unknown command: " + messageIn.getCommand().name() + " from client " + client);
//                        System.out.println("Valid commands are: PING, EXIT, ORDER, ORDER_STATUS");
//                        messageOut.setCommand(UNKNOWN_COMMAND);
//                        messageOut.setErrorMessage("Invalid command: '" + messageIn.getCommand().name() + "'. Valid commands: PING, EXIT, ORDER, ORDER_STATUS");
//                    }
//                }
//
//
//                System.out.println("Responding with " + messageOut.getCommand());
//                String messageOutJson = serializeMessage(messageOut);
//                sendMessage(messageOutJson);
//            }
//        }
//
//        clientExited();
//    }


    private void listenForIncomingClientMessages() {
        while (connectionIsValid) {
            if (reader.hasNext()) {
                String messageJson = reader.nextLine();

                Message messageIn = deserializeMessage(messageJson);
                messageId = messageIn.getId();
                Message messageOut = new Message(messageId);

                try {
                    // Use name() to get the enum name and normalize it
                    String commandInput = messageIn.getCommand().name(); // Enum name() returns the constant name
                    Helpers.MessageCommand command = Helpers.MessageCommand.valueOf(commandInput);

                    switch (command) {
                        case PING -> {
                            System.out.println("\nPING from client " + client);
                            messageOut.setCommand(PING_OK);
                        }
                        case EXIT -> {
                            System.out.println("EXIT from client " + client);
                            connectionIsValid = false;
                            messageOut.setCommand(EXIT_OK);
                        }
                        case ORDER -> {
                            System.out.println("ORDER from client " + client);
                            ActionOrder order = new ActionOrder(client, cafe, messageOut, messageIn);
                            order.placeOrder();
                        }
                        case ORDER_STATUS -> {
                            System.out.println("ORDER_STATUS from client " + client);
                            ActionOrderStatus orderStatus = new ActionOrderStatus(client, messageOut);
                            orderStatus.constructOrderStatusMessage();
                        }
                        default -> throw new IllegalArgumentException("Unhandled command");
                    }
                } catch (IllegalArgumentException e) {
                    // Handle unknown commands
                    System.out.println("Error: Unknown command '" + messageIn.getCommand() + "'");
                    System.out.println("Valid commands are: " + Arrays.toString(Helpers.MessageCommand.values()));
                    System.out.println("Invalid command: '" + messageIn.getCommand() + "'. Valid commands: PING, EXIT, ORDER, ORDER_STATUS");
                    messageOut.setCommand(UNKNOWN_COMMAND);
                    messageOut.setErrorMessage("Invalid command: '" + messageIn.getCommand() + "'. Valid commands: PING, EXIT, ORDER, ORDER_STATUS");
                }

                System.out.println("Responding with " + messageOut.getCommand());
                String messageOutJson = serializeMessage(messageOut);
                sendMessage(messageOutJson);
            }
        }

        clientExited();
    }



    /**
     * Deserialize {@link Message} from Json format to {@link Message}.
     * @param messageJson   Message to deserialize into {@link Message}
     * @return  Deserialized {@link Message}
     */
    private Message deserializeMessage(String messageJson) {
        return new Gson().fromJson(messageJson, Message.class);
    }

    /**
     * Serialize {@link Message} to Json format
     * @param message   Message to serialize
     * @return  Serialized {@link Message} as {@link String}
     */
    private String serializeMessage(Message message) {
        return new Gson().toJson(message);
    }

    /**
     * Sends {@link Message} in Json format to the client.
     * @param messageJson   Json message to sent
     */
    private void sendMessage(String messageJson) {
        if (writer != null) {
            writer.println(messageJson);
        } else {
            System.out.println("Writer is null");
        }
    }

    /**
     * Outputs that the client has disconnected/failed to connect, removes client and their drinks from cafe and
     * closes the reader, writer and socket.
     */
    private void clientExited() {
        //If the client has performed handshake successfully
        if (cafe.contains(client)) {
            System.out.println("Client " + client + " disconnected");

            //Remove client from the cafe
            cafe.removeClient(client);

            //Remove drinks from the cafe for the client
            if (client.hasOrder()) {
                cafe.removeClientDrinksFromCafe(client);
            }

            try {
                reader.close();
                writer.close();
                socket.close();
            } catch (Exception e) {

            }

        } else {
            System.out.println("Client " + clientId + " failed to connect");
        }
    }

    /**
     * Called from {@link Client#orderComplete()} responds with a {@link Message} containing the
     * number and type of drink that the client has ordered.
     */
    public void orderComplete() {
        Map<DrinkType, Integer> numberOfDrinksByDrinkType = getNumberOfDrinksByDrinkType();

        //Create new message to be sent to the client
        messageId++;
        Message messageOut = new Message(messageId);
        messageOut.setCommand(ORDER_COMPLETE);

        //Get and set message body
        String messageBody = getMessageBodyForOrderComplete(numberOfDrinksByDrinkType);
        messageOut.setMessageBody(messageBody);

        System.out.println("Sending ORDER_COMPLETE to " + client);

        //Serialize and send message
        String messageJson = serializeMessage(messageOut);
        sendMessage(messageJson);

        //Clear the client's drinks
        client.setDrinks(new ArrayList<>());
    }

    /**
     * Counts the number of drinks that the client has and maps to {@link DrinkType}.
     * @return  Number of drinks mapped by {@link DrinkType
     */
    private Map<DrinkType, Integer> getNumberOfDrinksByDrinkType() {
        Map<DrinkType, Integer> numberOfDrinksByDrinkType = new HashMap<>();

        for (Drink drink : client.getDrinks()) {
            numberOfDrinksByDrinkType.putIfAbsent(drink.getType(), 0);

            //Increase count of drink
            numberOfDrinksByDrinkType.put(drink.getType(), numberOfDrinksByDrinkType.get(drink.getType()) + 1);
        }

        return numberOfDrinksByDrinkType;
    }

    /**
     * Creates and returns the text (using a {@link StringBuilder}) for a message body for an {@code ORDER_COMPLETE} {@link Message},
     * using the {@code numberOfDrinksByDrinkType} map.
     * @param numberOfDrinksByDrinkType Map that contains the number of drinks mapped by {@link DrinkType}
     * @return  the text for the body of the {@code ORDER_COMPLETE} {@link Message}
     */
    private String getMessageBodyForOrderComplete(Map<DrinkType, Integer> numberOfDrinksByDrinkType) {
        StringBuilder messageBody = new StringBuilder();

        messageBody.append("order delivered to ");
        messageBody.append(client.getClientName());
        messageBody.append(" (");

        //Append number and type of drinks
        for (DrinkType drink : numberOfDrinksByDrinkType.keySet()) {
            messageBody.append(numberOfDrinksByDrinkType.get(drink));
            messageBody.append(" ");
            messageBody.append(drink.toString());

            if (numberOfDrinksByDrinkType.get(drink) > 1) messageBody.append("s");  //If plural

            messageBody.append(" and ");
        }
        //Remove last ' and '
        for (int i = 0; i < 5; i++) messageBody.deleteCharAt(messageBody.length() - 1);

        messageBody.append(")");

        return messageBody.toString();
    }
}
