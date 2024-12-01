package Helpers.ServerHelpers;

import Helpers.Drinks.Drink;
import Helpers.Drinks.DrinkType;

import java.util.List;
import java.util.Map;

/**
 * Data structure to hold the cafe status information which is used to serialize to Json and store int Log.json.
 */
public class Log {

    private Map<DrinkType, List<Drink>> waitingArea;
    private Map<DrinkType, List<Drink>> brewingArea;
    private Map<DrinkType, List<Drink>> trayArea;
    private String event;
    private int NumberOfClients;
    private int numberOfClientsWaitingForOrder;


    public void setWaitingArea(Map<DrinkType, List<Drink>> waitingArea) {
        this.waitingArea = waitingArea;
    }

    public void setBrewingArea(Map<DrinkType, List<Drink>> brewingArea) {
        this.brewingArea = brewingArea;
    }

    public void setTrayArea(Map<DrinkType, List<Drink>> trayArea) {
        this.trayArea = trayArea;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setNumberOfClients(int numberOfClients) {
        NumberOfClients = numberOfClients;
    }

    public void setNumberOfClientsWaitingForOrder(int numberOfClientsWaitingForOrder) {
        this.numberOfClientsWaitingForOrder = numberOfClientsWaitingForOrder;
    }
}
