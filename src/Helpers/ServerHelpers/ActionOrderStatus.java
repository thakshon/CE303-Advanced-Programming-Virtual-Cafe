package Helpers.ServerHelpers;

import Helpers.Drinks.Drink;
import Helpers.Drinks.DrinkType;
import Helpers.Message;

import java.util.Map;
import java.util.stream.Collectors;

import static Helpers.MessageCommand.ORDER_STATUS;


/**
 * Helper class that constructs a {@link Message} to be sent back to the client with their order status information.
 */
public class ActionOrderStatus {

    private final Client client;
    private final Message messageOut;

    /**
     * Creates an ActionOrderStatus object.
     * @param client The {@link Client} that has requested an order status
     * @param messageOut The {@link Message} that will be sent to the client
     */
    public ActionOrderStatus(Client client, Message messageOut) {
        this.client = client;
        this.messageOut = messageOut;
    }

    /**
     * Constructs the {@link Message} to be sent to the client.
     */
    public void constructOrderStatusMessage() {
        if (client.hasOrder()) {

            Map<String, Map<DrinkType, Long>> numberOfDrinksByDrinkTypeByArea;

            //Count number of drinks in each area
            //Mapped by status (area) and type of drink
            numberOfDrinksByDrinkTypeByArea = client.getDrinks().stream()
                    .collect(Collectors.groupingBy(Drink::getStatus,
                            Collectors.groupingBy(Drink::getType, Collectors.counting())));


            StringBuilder messageBody = new StringBuilder();
            messageBody.append("Order status for ").append(client.getClientName()).append(":\n");

            //Get text for the number and type of drinks in each area
            if (numberOfDrinksByDrinkTypeByArea.containsKey("waiting")) {
                messageBody.append(getAreaStatusDetails(numberOfDrinksByDrinkTypeByArea, "waiting"));
            }
            if (numberOfDrinksByDrinkTypeByArea.containsKey("brewing")) {
                messageBody.append(getAreaStatusDetails(numberOfDrinksByDrinkTypeByArea, "brewing"));
            }
            if (numberOfDrinksByDrinkTypeByArea.containsKey("ready")) {
                messageBody.append(getAreaStatusDetails(numberOfDrinksByDrinkTypeByArea, "ready"));
            }

            messageOut.setMessageBody(messageBody.toString());

        } else {
            messageOut.setMessageBody("No order found for " + client.getClientName());
        }

        messageOut.setCommand(ORDER_STATUS);
    }

    /**
     * Generates a {@link String} from {@code numberOfDrinksByDrinkTypeByArea}. The resulting {@code String}
     * contains the number and type of drinks from each area that contains drinks for the client, area
     * information does not get generated if no drinks are in that area.
     * @param numberOfDrinksByDrinkTypeByArea number of drinks mapped by type and then area
     * @param area name of area
     * @return a {@code String} that consists of the details of each area
     */
    private String getAreaStatusDetails(Map<String, Map<DrinkType, Long>> numberOfDrinksByDrinkTypeByArea, String area) {
        StringBuilder areaDetails = new StringBuilder();
        areaDetails.append(" - ");

        //For each DrinkCountByDrinkType in the area
        for (Map.Entry<DrinkType, Long> drinksInArea : numberOfDrinksByDrinkTypeByArea.get(area).entrySet()) {
            areaDetails.append(drinksInArea.getValue()).append(" ");
            areaDetails.append(drinksInArea.getKey());

            if (drinksInArea.getValue() > 1) areaDetails.append("s");   //If plural
            areaDetails.append(" and ");
        }

        //Removing the last ' and '
        for (int i = 0; i < 4; i++) areaDetails.deleteCharAt(areaDetails.length() - 1);

        //Add string to the end of the details depending on the area
        switch (area) {
            case "waiting" -> areaDetails.append("in waiting area");
            case "brewing" -> areaDetails.append("currently being prepared");
            case "ready" -> areaDetails.append("currently in the tray");
        }
        areaDetails.append("\n");

        return areaDetails.toString();
    }
}
