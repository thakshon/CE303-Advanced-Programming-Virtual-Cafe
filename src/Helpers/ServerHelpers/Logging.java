package Helpers.ServerHelpers;

import Helpers.Drinks.Drink;
import Helpers.Drinks.DrinkType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * For handling the logging to terminal and logging to Log.json
 */
public class Logging {

    private List<Client> clients;
    private ArrayList<Drink> waitingArea;
    private ArrayList<Drink> brewingArea;
    private ArrayList<Drink> trayArea;

    private AtomicLong numberOfClientsWaitingForOrders;

    private String currentDateAndTime;

    /**
     * Generates a log to be printed out in the terminal.
     * @param event text of the event that changed the state
     */
    public void log(String event) {
        currentDateAndTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date());

        StringBuilder log = new StringBuilder();
        log.append("\n").append(event).append("\n");
        log.append("Current cafe status: ").append(currentDateAndTime);
        log.append("\nNumber of clients: ").append(clients.size()).append("\n");


        //Count number of clients that are waiting for orders
        numberOfClientsWaitingForOrders.set(clients.stream()
                .filter(Client::hasOrder).count());

        log.append("Number of clients waiting for orders: ").append(numberOfClientsWaitingForOrders).append("\n");

        log.append("Waiting area:  ");
        log.append(getNumberAndTypeOfDrinksInArea(waitingArea));

        log.append("\nBrewing area:  ");
        log.append(getNumberAndTypeOfDrinksInArea(brewingArea));

        log.append("\nTray area:     ");
        log.append(getNumberAndTypeOfDrinksInArea(trayArea));
        log.append("\n");

        System.out.println(log);

        logToJson(event);
    }

    /**
     * Gets number of drinks and type of drinks from area
     * @param area the area of drinks to be mapped
     * @return a {@link String} of the number of drinks and type of drink from area
     */
    private String getNumberAndTypeOfDrinksInArea(ArrayList<Drink> area) {
        //Count number of drinks in area
        //Mapped by type of drink
        Map<DrinkType, Long> numberOfDrinksByArea = area.stream()
                .collect(Collectors.groupingBy(Drink::getType, Collectors.counting()));


        if (numberOfDrinksByArea.size() == 0) return "no drinks";

        StringBuilder numberAndTypeOfDrinksInArea = new StringBuilder();

        for (DrinkType drinkType : numberOfDrinksByArea.keySet()){
            numberAndTypeOfDrinksInArea.append(numberOfDrinksByArea.get(drinkType)).append(" ").append(drinkType.toString());

            if (numberOfDrinksByArea.get(drinkType) > 1) numberAndTypeOfDrinksInArea.append("s"); //If plural
            numberAndTypeOfDrinksInArea.append(" and ");
        }
        //Remove last " and "
        for (int i = 0; i < 5; i++) numberAndTypeOfDrinksInArea.deleteCharAt(numberAndTypeOfDrinksInArea.length() - 1);

        return numberAndTypeOfDrinksInArea.toString();
    }

    /**
     * Creates and populates a {@link Log} object then serializes it and appends it to Log.json.
     * <p>Reads the existing Log.json file and deserializes into a map of logs mapped by date/time adds the new log to
     * the map and then serializes the list and writes it back to Log.json.
     * @param event text of the event that changed the state
     */
    private void logToJson(String event) {
        Log log = new Log();

        //Set the fields of the Log object
        log.setEvent(event);
        log.setNumberOfClients(clients.size());
        log.setNumberOfClientsWaitingForOrder(numberOfClientsWaitingForOrders.intValue());
        log.setWaitingArea(getListOfDrinksByDrinkTypeFromArea(waitingArea));
        log.setBrewingArea(getListOfDrinksByDrinkTypeFromArea(brewingArea));
        log.setTrayArea(getListOfDrinksByDrinkTypeFromArea(trayArea));


        try {
            //Reads existing logs from Log.json
            String fileContents;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader("Log.json"))) {
                fileContents = bufferedReader.readLine();
            }

            //Deserialize the logs into a map of logsByDateTime
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Log>>(){}.getType();
            Map<String, Log> logsByDateTime = gson.fromJson(fileContents, type);

            //If no logs in file (first log created at start of server)
            if (logsByDateTime == null) logsByDateTime = new HashMap<>();

            //Put new log in the map with the date/time as the key
            logsByDateTime.put(currentDateAndTime, log);

            //Serialize map and write to file
            FileWriter file = new FileWriter("Log.json");
            gson.toJson(logsByDateTime, file);
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets list of drinks mapped by DrinkType from the area.
     * @param area the area of drinks to be mapped
     * @return map of listOfDrinksByDrinkTypeFromArea
     */
    private Map<DrinkType, List<Drink>> getListOfDrinksByDrinkTypeFromArea(ArrayList<Drink> area) {
        return area.stream().collect(Collectors.groupingBy(Drink::getType));
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public void setWaitingArea(ArrayList<Drink> waitingArea) {
        this.waitingArea = waitingArea;
    }

    public void setBrewingArea(ArrayList<Drink> brewingArea) {
        this.brewingArea = brewingArea;
    }

    public void setTrayArea(ArrayList<Drink> trayArea) {
        this.trayArea = trayArea;
    }

    public void setNumberOfClientsWaitingForOrders(AtomicLong numberOfClientsWaitingForOrders) {
        this.numberOfClientsWaitingForOrders = numberOfClientsWaitingForOrders;
    }
}
