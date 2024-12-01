package Helpers.ServerHelpers;

import Helpers.Drinks.Drink;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure to hold information relating to each client
 */
public class Client {

    private final String clientName;
    private final long clientId;
    private final ClientInterface clientInterface;
    private List<Drink> drinks = new ArrayList<>();

    /**
     * Creates a client.
     * @param clientId an id for the client
     * @param clientName the name of the client
     * @param clientInterface the reference to the relevant {@link ClientInterface}.
     *                        Required as the {@link ClientInterface} holds the writer that can
     *                        send messages to the client.
     */
    public Client(int clientId, String clientName, ClientInterface clientInterface) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientInterface = clientInterface;
    }

    public List<Drink> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
    }

    public String getClientName() {
        return clientName;
    }

    /**
     * Passing on the order complete action to the relevant {@link ClientInterface} as it
     * contains the writer that can send messages to the client.
     */
    public void orderComplete() {
        clientInterface.orderComplete();
    }

    public boolean hasOrder() {
        return !drinks.isEmpty();
    }

    public void removeDrinks(ArrayList<Drink> drinksToRemove) {
        drinks.removeAll(drinksToRemove);
    }

    public void replace(Drink drink, Drink drinksRedistributed) {
        drinks.remove(drink);
        drinks.add(drinksRedistributed);
    }

    /**
     * Returns whether the object is the same as this object based on the {@code clientId}s.
     * @param obj the other object to compare to
     * @return true only if this {@code clientId} is equal to the other object's {@code clientId}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Client) {
            Client other = (Client) obj;
            return this.clientId == other.clientId;
        }

        return false;
    }

    /**
     * Returns the {@code clientId} and {@code clientName} (if present).
     * @return the {@code clientId} and {@code clientName} (if present)
     */
    @Override
    public String toString() {
        if (clientName.isEmpty()) {
            return "" + clientId;
        } else {
            return clientId + ":" + clientName;
        }
    }
}
