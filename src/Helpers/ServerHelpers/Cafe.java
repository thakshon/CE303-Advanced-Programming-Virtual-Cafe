package Helpers.ServerHelpers;

import Helpers.Drinks.Drink;
import Helpers.Drinks.DrinkType;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static Helpers.Drinks.DrinkType.*;

/**
 * A class that contains the 3 areas that drinks can be in and handles the moving of drinks between them, the list
 * of clients currently in the cafe, the number of clients waiting for orders and the information for the drinks.
 * <p>Uses a thread to continuously monitor the {@code waitingArea} and creates more threads to brew each drink.
 *
 */
public class Cafe {

    private final List<Client> clients = new ArrayList<>();

    private final AtomicLong numberOfClientsWaitingForOrders = new AtomicLong();

    private final ArrayList<Drink> waitingArea = new ArrayList<>();
    private final ArrayList<Drink> brewingArea = new ArrayList<>();
    private final ArrayList<Drink> trayArea = new ArrayList<>();

    private final ReentrantLock lock = new ReentrantLock();

    private final Map<String, DrinkType> drinkTypeByString = new HashMap<>();
    private final Map<DrinkType, Integer> maxBrewingCapacityByDrinkType = new HashMap<>();
    private final Map<DrinkType, Integer> currentBrewingByDrinkType = new HashMap<>();
    private int totalBrewingCapacity;

    private final Logging logging = new Logging();

    /**
     * Creates a cafe which initializes the drinks and starts brewing.
     */
    public Cafe() {
        initDrinksSetup();
        passInformationForLogging();
        logging.log("Initial cafe status");
        startBrewing();
    }

    /**
     * Initializes the information for the drinks available in the cafe
     */
    private void initDrinksSetup() {
        //Map used for checking a drink is valid by using isValidDrink(String)
        drinkTypeByString.put("TEA", TEA);
        drinkTypeByString.put("COFFEE", COFFEE);
        drinkTypeByString.put("WATER", WATER);//########################

        //Initialize current brewing map to 0
        for (DrinkType key: DrinkType.values()) {
            currentBrewingByDrinkType.put(key, 0);
        }

        //Set how many drinks can be made by the barista at a time
        maxBrewingCapacityByDrinkType.put(TEA, 2);
        maxBrewingCapacityByDrinkType.put(COFFEE, 2);
        maxBrewingCapacityByDrinkType.put(WATER, 2);//########################

        //Set the total brewing capacity to max capacity of each DrinkType
        for (int capacity: maxBrewingCapacityByDrinkType.values()) {
            totalBrewingCapacity += capacity;
        }
    }

    /**
     * Checks if the drink provided is a recognised drink, also re-checks without the last character if it is 's'.
     * @param drink String of drink to be checked
     * @return true if the drink is in the {@code drinkTypeByString} map
     */
    public boolean isValidDrink(String drink) {
        if (drinkTypeByString.containsKey(drink)) return true;

        //Check if a key exists without last letter (tea/s)
        if (drink.charAt(drink.length() - 1) != 'S') return false;
        drink = drink.substring(0, drink.length() - 1);

        if (drinkTypeByString.containsKey(drink)) return true;
        return false;
    }

    /**
     * Returns a new instance of {@link Drink} dependent on the parameter.
     * <p>The parameterised drink is already known to exist at this point as {@link #isValidDrink(String)} gets called
     * before this method, if the parameter does not exist as a key in the map remove the last character, which will be 's'.
     * @param drink String of drink
     * @return a new {@link Drink} depending on the parameter
     */
    public Drink getTypeOfDrink(String drink) {
        if (!drinkTypeByString.containsKey(drink)) {
            //Removes last character (tea/s)
            drink = drink.substring(0, drink.length() - 1);
        }

        return drinkTypeByString.get(drink).getDrink();
    }

    /**
     * Passes the relevant objects to {@code Logging} for it to be able to create logs.
     */
    private void passInformationForLogging() {
        logging.setClients(clients);
        logging.setNumberOfClientsWaitingForOrders(numberOfClientsWaitingForOrders);
        logging.setWaitingArea(waitingArea);
        logging.setBrewingArea(brewingArea);
        logging.setTrayArea(trayArea);
    }

    /**
     * Continuously loops checking for drinks in {@code waitingArea} that be moved to {@code brewingArea}.
     * Does multiple checks to ensure that the barista can actually process the drink dependent on the max brewing capacity
     * of each drink.
     */
    private void startBrewing() {
        Thread startBrewing = new Thread(() -> {
            while (true) {
                lock.lock();

                //Check if the waitingArea is not empty
                if (!waitingArea.isEmpty()) {

                    //Check if the number of drinks brewing is less than the total brewing capacity
                    if (brewingArea.size() < totalBrewingCapacity) {

                        //For each type of drink in the DrinkType enum
                        for (DrinkType typeOfDrink: DrinkType.values()) {

                            //Check that the current brewing amount is less than the max brewing amount for the current typeOfDrink
                            if (currentBrewingByDrinkType.get(typeOfDrink) < maxBrewingCapacityByDrinkType.get(typeOfDrink)) {

                                //Find the first drink in the waiting area that's type is the same as the current typeOfDrink
                                //If a drink is found pass that drink into startBrewingDrink() and create new Thread
                                //Do nothing otherwise
                                waitingArea.stream()
                                        .filter(drink -> drink.getType() == typeOfDrink)
                                        .findFirst()
                                        .ifPresent(drink -> {
                                            moveDrinkFromWaitingToBrewingArea(drink);
                                            startBrewingDrink(drink);
                                        });
                            }
                        }
                    }
                }
                lock.unlock();
            }
        });
        startBrewing.start();
    }

    /**
     * Move the drink from {@code waitingArea} to {@code brewingArea} as it has been selected to start processing.
     * @param drink Drink to move
     */
    private void moveDrinkFromWaitingToBrewingArea(Drink drink) {
        //Set drink as not waiting and remove it from waitingArea
        drink.setWaiting(false);
        waitingArea.remove(drink);
        logging.log("Removing 1 " + drink + " from the waiting area for " +
                drink.getAssignedTo() + " - Start brewing");

        //Set drink as brewing and add it to the brewingArea
        drink.setBrewing(true);
        brewingArea.add(drink);
        logging.log("Adding 1 " + drink + " to the brewing area for " +
                drink.getAssignedTo() + " - Start brewing");

        //Increment the current brewing amount of the typeOfDrink by 1
        currentBrewingByDrinkType.put(drink.getType(), currentBrewingByDrinkType.get(drink.getType()) + 1);
    }


    /**
     * Creates thread for brewing the drink, uses {@link Thread#sleep} to simulate the brewing. Moves
     * the drink to tray once brewed.
     * @param drink Drink to start brewing
     */
    private void startBrewingDrink(Drink drink) {
        Thread brewDrink = new Thread(() -> {
            try {Thread.sleep(drink.getBrewingTimeInSeconds() * 1000);}
            catch (InterruptedException e) {throw new RuntimeException(e);}

            lock.lock();
            //Removes drink if it is actually still in brewing area (returns true if so)
            //A drink may get removed before brewing is finished if a client leaves before their order is complete
            if (brewingArea.remove(drink)) {
                //Set drink as not brewing
                drink.setBrewing(false);
                logging.log("Removing 1 " + drink + " from the brewing area for " +
                        drink.getAssignedTo() + " - Brewing complete");

                //Set drink as ready and add it to the trayArea
                drink.setReady(true);
                trayArea.add(drink);
                logging.log("Adding 1 " + drink + " to the tray area for " +
                        drink.getAssignedTo() + " - Drink complete");

                checkOrderComplete();
            }

            //Decrement the current brewing amount of the typeOfDrink by 1
            currentBrewingByDrinkType.put(drink.getType(), currentBrewingByDrinkType.get(drink.getType()) - 1);
            lock.unlock();
        });
        brewDrink.start();
    }

    /**
     * Checks if any order has been completed, is called everytime a drink is brewed and added to the tray area, it is also
     * called from whenever a {@link Client} leaves and their drinks get redistributed.
     * <p>If a client's order has been completed it calls {@link #removeCompletedOrderDrinksFromTray(Client)} sends an order completed
     * and sends an order complete action to the client and then from the client to the client's {@link ClientInterface}.
     */
    private void checkOrderComplete() {
        for (Client client : clients) {
            if (client.hasOrder()) {
                boolean allDrinksReady = true;
                List<Drink> clientDrinks = client.getDrinks();

                for (Drink drinkInOrder : clientDrinks) {
                    if (!drinkInOrder.isReady()) {
                        allDrinksReady = false;
                        break;
                    }
                }

                if (allDrinksReady) {
                    System.out.println("All drinks for " + client + " are ready");
                    removeCompletedOrderDrinksFromTray(client);
                    client.orderComplete();
                    logging.log("Setting " + client + "'s order as complete");
                }
            }
        }
    }

    /**
     * Removes all client's drinks from {@code trayArea}.
     * @param client The client whose order is complete
     */
    private void removeCompletedOrderDrinksFromTray(Client client) {
        for (Drink drink : client.getDrinks()) {
            trayArea.remove(drink);
            logging.log("Removing 1 " + drink + " from the tray area for " +
                    client + " - Order complete");
        }
    }

    /**
     * Adds a {@link Client} to the {@code clients} list.
     * @param client The client to add
     */
    public void addClient(Client client) {
        clients.add(client);
        logging.log("Adding " + client + " to the cafe");
    }

    /**
     * Adds all drinks to the waiting area
     * @param newDrinks The drinks to add
     */
    public void addDrinksToWaitingArea(List<Drink> newDrinks) {
        lock.lock();
        logging.log("Adding new order for " + newDrinks.get(0).getAssignedTo());

        for (Drink drink : newDrinks) {
            waitingArea.add(drink);
            logging.log("Adding 1 new " + drink + " to the waiting area for " +
                    drink.getAssignedTo() + " - Add new drink");
        }
        lock.unlock();
    }

    /**
     * Check if client is in the cafe.
     * @param client The client to check
     * @return true if the client is in the cafe
     */
    public boolean contains(Client client) {
        return clients.contains(client);
    }

    /**
     * Removes {@link Client} from the {@code clients} list.
     * @param client The client to remove
     */
    public void removeClient(Client client) {
        this.clients.remove(client);
        logging.log("Removing " + client + " from the cafe");
    }

    /**
     * Removes all {@code clientLeaving}'s drinks from the cafe. Checks afterwards if any orders have been completed.
     * @param clientLeaving The client whose drinks are to be removed
     */
    public void removeClientDrinksFromCafe(Client clientLeaving) {
        lock.lock();
        removeClientDrinksFromWaitingArea(clientLeaving);

        redistributeDrinks(clientLeaving, trayArea);    //Redistribute drinks to order clients from tray area
        redistributeDrinks(clientLeaving, brewingArea); //Redistribute drinks to order clients from brewing area

        removeRemainingClientDrinksFromCafe(clientLeaving);

        checkOrderComplete();
        lock.unlock();
    }

    /**
     * Removes all {@code clientLeaving}'s drinks from the waiting area.
     * @param clientLeaving The client whose drinks are to be removed
     */
    private void removeClientDrinksFromWaitingArea(Client clientLeaving) {
        //If the client has any drinks currently in the waitingArea
        if (clientLeaving.getDrinks().stream().anyMatch(Drink::isWaiting)) {

            System.out.println("Discard all drinks in the waiting area for " + clientLeaving);

            for (Drink drinkInWaiting : clientLeaving.getDrinks()) {
                //If drink has been removed
                if (waitingArea.remove(drinkInWaiting)) {
                    logging.log("Removing 1 " + drinkInWaiting + " from the waiting area for " +
                            drinkInWaiting.getAssignedTo() + " - Discard drink");
                }
            }
            System.out.println("All drinks in the waiting area for " + clientLeaving + " have been discarded");
        }
    }

    /**
     * Redistributes drinks from {@code clientLeaving}'s drinks that are either in the {@code trayArea} or {@code brewingArea}.
     * @param clientLeaving The client whose drinks are to be redistributed
     * @param area The area to redistribute drinks from
     */
    private void redistributeDrinks(Client clientLeaving, ArrayList<Drink> area) {
        ArrayList<Drink> drinksRedistributed = new ArrayList<>();

        //For each Drink in the leaving client that is also in the area parameter (using isReady or isBrewing)
        for (Drink drinkToRedistribute : clientLeaving.getDrinks().stream()
                .filter(area == trayArea ? Drink::isReady : Drink::isBrewing).collect(Collectors.toList())) {

            for (Client client : clients) {

                //For each Drink in the client (that isWaiting and that is the same type of drink as the drink in the area parameter)
                for (Drink drink : client.getDrinks().stream()
                        .filter(Drink::isWaiting)
                        .filter(drink -> drink.getType() == drinkToRedistribute.getType()).collect(Collectors.toList())) {

                    client.replace(drink, drinkToRedistribute);      //Replace current drink with drinkToRedistribute
                    drinksRedistributed.add(drinkToRedistribute);    //Remove drinkToRedistribute from the leaving client

                    //Set the redistributed drink to the current client
                    drinkToRedistribute.setAssignedTo(client);
                    drinkToRedistribute.setWaiting(false);

                    //Remove current drink from the waiting area
                    waitingArea.remove(drink);


                    //Conditionals for different status update and message
                    if (area == trayArea) {
                        drinkToRedistribute.setReady(true);
                        System.out.println("\n1 " + drinkToRedistribute +
                                " in " + clientLeaving + "'s tray has been transferred to " +
                                client + "'s tray");
                    } else {
                        drinkToRedistribute.setBrewing(true);
                        System.out.println("\n1 " + drinkToRedistribute +
                                " currently being brewed for " + clientLeaving + " has been transferred to " +
                                client + "'s order");
                    }

                    logging.log("Removing 1 " + drink + " from the waiting area for " +
                            client + " - Redistributing drink");
                    break;  //Break as this drink has now been redistributed and do not want to distribute again
                }

                //Stop checking other clients if the drink has been redistributed
                if (drinkToRedistribute.getAssignedTo() == client) break;
            }
        }

        //Remove all drinks that got redistributed from the leaving client
        clientLeaving.removeDrinks(drinksRedistributed);
    }

    /**
     * Removes all remaining drinks from the cafe from {@code clientLeaving}'s drinks (if any).
     * @param clientLeaving The client whose drinks are to be removed
     */
    private void removeRemainingClientDrinksFromCafe(Client clientLeaving) {
        //If there are any drinks left to remove
        if (clientLeaving.hasOrder())
            System.out.println("Discard all remaining drinks for " + clientLeaving);

        for (Drink drink : clientLeaving.getDrinks()) {

            //If the drink was removed
            if (brewingArea.remove(drink)) {
                logging.log("Removing 1 " + drink + " from the brewing area for " +
                        clientLeaving + " - Discard drink");

                //Decrement the current brewing amount of the typeOfDrink by 1
                currentBrewingByDrinkType.put(drink.getType(), currentBrewingByDrinkType.get(drink.getType()) - 1);
            }

            //If the drink was removed
            if (trayArea.remove(drink)) {
                logging.log("Removing 1 " + drink + " from the tray area for " +
                        clientLeaving + " - Discard drink");
            }
        }

        System.out.println("All remaining drinks for " + clientLeaving + " have been discarded");
    }
}