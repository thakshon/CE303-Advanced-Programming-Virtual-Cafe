import Helpers.ServerHelpers.Cafe;
import Helpers.ServerHelpers.ClientInterface;

import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Barista listens for incoming connections and creates a {@link ClientInterface} on a new thread for
 * each connection assigning a new incrementing {@code clientId}.
 */
public class Barista {

    private final int port = 8888;

    /**
     * Entry point for the server.
     */
    public static void main(String[] args) {
        System.out.println("Barista");

        Barista barista = new Barista();
        barista.createNewLogFile();
        barista.startServer();
    }

    /**
     * Starts continuously listening for and accepting connections on the {@code port}, giving the new connections
     * the socket they are connected on, a link to the cafe and an incremental client id.
     */
    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            //Initialize cafe and clientId
            Cafe cafe = new Cafe();
            int clientId = 0;

            System.out.println("Cafe open - Waiting for clients...");
            while (true) {
                Socket socket = serverSocket.accept();

                clientId++;
                Thread newClient = new Thread(new ClientInterface(socket, cafe, clientId));
                newClient.start();

                System.out.println("Client " + clientId + " attempting to connect");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Closing server");
        }
    }

    /**
     * Create/overwrite Log.json in the working directory.
     */
    private void createNewLogFile() {
        try {
            System.out.println("Creating/overwriting Log.json");
            new FileWriter("Log.json").close();
        } catch (Exception e) {
            System.out.println("Error creating Log.Json");
        }
    }
}