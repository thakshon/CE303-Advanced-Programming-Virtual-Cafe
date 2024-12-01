package Helpers.Drinks;

import Helpers.ServerHelpers.Client;

/**
 * Parent data structure class for the drinks the cafe can use.
 */
public class Drink {

    private DrinkType type;

    private transient long brewingTimeInSeconds;

    private transient boolean waiting;

    private transient boolean brewing;
    private transient boolean ready;
    private transient Client assignedTo;
    private String client;  //Used for Json logging

    /**
     * Creates a drink with the default state of {@code waiting} set to true.
     */
    public Drink() {
        waiting = true;
    }

    public DrinkType getType() {
        return type;
    }

    public void setType(DrinkType type) {
        this.type = type;
    }

    public boolean isBrewing() {
        return brewing;
    }

    public void setBrewing(boolean brewing) {
        this.brewing = brewing;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public Client getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Client assignedTo) {
        this.assignedTo = assignedTo;
        this.client = assignedTo.getClientName();
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public long getBrewingTimeInSeconds() {
        return brewingTimeInSeconds;
    }

    public void setBrewingTimeInSeconds(long brewingTimeInSeconds) {
        this.brewingTimeInSeconds = brewingTimeInSeconds;
    }

    /**
     * Returns the current status of the drink in string format.
     * @return the current status of the drink in string format.
     */
    public String getStatus() {
        if (ready) return "ready";
        if (brewing) return "brewing";
        return "waiting";
    }

    /**
     * Returns {@link DrinkType#toString()}
     * @return {@link DrinkType#toString()}
     */
    @Override
    public String toString() {
        return type.toString();
    }
}
