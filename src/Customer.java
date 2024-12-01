import Helpers.CustomerHelpers.ServerInterface;

import java.util.*;

/**
 * Customer is the client and handles the interface between the user and the server, passing
 * all inputs to the {@link ServerInterface}.
 */
public class Customer {

    private final Scanner in = new Scanner(System.in);

    /**
     * Entry point for the client.
     */
    public static void main(String[] args) {
        System.out.println("Client");
        Customer customer = new Customer();
        customer.startCustomerProgram();
    }

    /**
     * Attempts to complete a handshake with the server from {@link ServerInterface}.
     * Initializes the connection if the connection is valid and gets inputs from the
     * user and passes it to {@link ServerInterface#sendCommand(String)} until they exit.
     */
    public void startCustomerProgram() {
        try (ServerInterface server = new ServerInterface()) {
            server.performHandshake();

            //Initializes connection if the handshake was completed
            if (server.connectionIsValid()) {
                initConnection(server);
            }

            //Read inputs from user
            while (server.connectionIsValid()) {
                System.out.print("> ");
                server.sendCommand(getInput());
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the server writer and server listener and adds a shut-down hook.
     * @param server The connection to the server
     */
    private void initConnection(ServerInterface server) {
        server.initServerListener();
        server.initServerWriter();
        server.addShutdownHook();
    }

    /**
     * Loops until a non-blank input has been input by the customer.
     * @return the text the user types into the terminal
     */
    public String getInput() {
        String input = "";

        while (input.isBlank()) {
            if (in.hasNext()) {
                input = in.nextLine();
            }
        }

        return input;
    }
}