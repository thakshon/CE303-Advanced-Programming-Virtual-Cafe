package Helpers.CustomerHelpers;

import Helpers.Message;
import Helpers.MessageCommand;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static Helpers.MessageCommand.*;

/**
 * Class that continuously listens for messages from the server as long as the connection is valid.
 */
public class ServerListener implements Runnable{

    private final ServerInterface serverInterface;
    private final Scanner reader;
    public static Map<String, MessageCommand> MessageCommandsByCommand = new HashMap<>();

    /**
     * Creates a server listener and maps known server responses.
     * @param serverInterface reference to {@link ServerInterface}, used for
     *                        checking the connection and setting the {@code serverResponse} to the incoming
     *                        message.
     * @param reader using the same reader as the reader used by {@link ServerInterface} for the handshake
     */
    public ServerListener(ServerInterface serverInterface, Scanner reader) {
        this.serverInterface = serverInterface;
        this.reader = reader;

        //Mapping known server responses
        MessageCommandsByCommand.put("EXIT_OK", EXIT_OK);
        MessageCommandsByCommand.put("ORDER_ACKNOWLEDGED", ORDER_ACKNOWLEDGED);
        MessageCommandsByCommand.put("PING_OK", PING_OK);
        MessageCommandsByCommand.put("ORDER_STATUS", ORDER_STATUS);
        MessageCommandsByCommand.put("UNKNOWN_COMMAND", UNKNOWN_COMMAND);
    }

    /**
     * Continuously listening for incoming messages from the server while the connection is valid, sets the
     * connection to be invalid as to not listen again for the next input and block the customer program.
     * <p>After the {@code serverResponse} has been set the {@link ServerInterface#waitForResponse()} method
     * is called from {@link ServerWriter} this will grab the serverResponse, process and clear it.
     */
    @Override
    public void run() {
        while (serverInterface.connectionIsValid()) {

            //Read message from server
            if (reader.hasNext()) {
                String serverMessageJson = reader.nextLine();

                Message messageIn = deserializeMessage(serverMessageJson);

                MessageCommand command = messageIn.getCommand();

                serverInterface.setServerResponse(messageIn);

                //Set connection as invalid to stop listening for messages from the server
                if (command == EXIT_OK) {
                    serverInterface.connectionIsValid = false;
                } else if (command == ORDER_COMPLETE) {
                    serverInterface.waitForResponse();
                }
            }
        }
    }

    /**
     * Deserialize {@link Message} from Json format to {@link Message}.
     * @param messageJson   Message to deserialize into {@link Message}
     * @return  Deserialized {@link Message}
     */
    private Message deserializeMessage(String messageJson) {
        Gson gson = new Gson();
        return gson.fromJson(messageJson, Message.class);
    }
}