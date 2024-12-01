package Helpers.ServerHelpers;

import Helpers.Drinks.Drink;
import Helpers.Message;

import java.util.LinkedList;
import java.util.List;

import static Helpers.MessageCommand.ORDER_ACKNOWLEDGED;

/**
 * Helper class that constructs a {@link Message} to be sent back to the client as an order acknowledgement.
 */
public class ActionOrder {

    private String[] orderArgs;

    private final Client client;
    private final Cafe cafe;
    private final Message messageOut;
    private final Message messageIn;

    /**
     * Creates an ActionOrder object.
     * @param client The {@link Client} that has placed the order
     * @param cafe Reference to {@link Cafe} for adding new drinks and checking valid drinks
     * @param messageOut The {@link Message} that will be sent to the client
     * @param messageIn The {@link Message} from the client that has placed the order
     */
    public ActionOrder(Client client, Cafe cafe, Message messageOut, Message messageIn) {
        this.client = client;
        this.cafe = cafe;
        this.messageOut = messageOut;
        this.messageIn = messageIn;
    }

    /**
     * Checks that the order is valid, if it is valid add drinks to the {@link Cafe} and construct
     * order acknowledgement
     */
    public void placeOrder() {
        checkOrder();

        if (!messageOut.containsError()) {
            addDrinksToClientAndCafe();
            orderAcknowledgement();
        }

        messageOut.setCommand(ORDER_ACKNOWLEDGED);
    }

    /**
     * Adds the drinks to the {@link Client} and to the {@link Cafe}.
     */
    private void addDrinksToClientAndCafe() {
        List<Drink> drinks = new LinkedList<>();

        for (int i = 0; i < Integer.parseInt(orderArgs[0]); i++) {
            Drink drink = cafe.getTypeOfDrink(orderArgs[1]);
            drink.setAssignedTo(client);
            drinks.add(drink);
        }
        if (orderArgs.length == 5) {
            for (int i = 0; i < Integer.parseInt(orderArgs[3]); i++) {
                Drink drink = cafe.getTypeOfDrink(orderArgs[4]);
                drink.setAssignedTo(client);
                drinks.add(drink);
            }
        }

        client.getDrinks().addAll(drinks);
        cafe.addDrinksToWaitingArea(drinks);
    }

    /**
     * Construct order acknowledgement to be sent to the client.
     */
    private void orderAcknowledgement() {
        String sb = "order received for " + client.getClientName() +
                " (" + String.join(" ", orderArgs).toLowerCase() + ")";

        messageOut.setMessageBody(sb);
    }

    /**
     * Check that the {@code orderArgs} are in the correct format, set relevant error message
     * if not in the correct format.
     */
    private void checkOrder() {
        orderArgs = messageIn.getMessageBody().split(" ");

        checkOrderArgLengths();
        if (!messageOut.containsError()) {
            checkOrderArgValues();
        }
    }

    /**
     * Checks that the orderArgs lengths are correct for an order command.
     */
    private void checkOrderArgLengths() {
        if (orderArgs.length != 2 && orderArgs.length != 5) {
            StringBuilder orderArgsLengthError = new StringBuilder();

            orderArgsLengthError.append("Incorrect arguments for the order command, provided ");
            orderArgsLengthError.append(orderArgs.length);
            orderArgsLengthError.append(", requires either 2 or 5\n");
            orderArgsLengthError.append("order ");
            orderArgsLengthError.append(messageIn.getMessageBody());

            appendExpectedOrderFormatToStringBuilder(orderArgsLengthError);

            messageOut.setErrorMessage(orderArgsLengthError.toString());
        }
    }


    /**
     * Checks that the orderArgs are correct, error messages set if any checks fail.
     * <li>index 0 - Has to be a number
     * <li>index 1 - Has to be a valid drink
     * <p><p>If a second drink has been ordered
     * <li>index 2 - Has to be 'and'
     * <li>index 3 - Has to be a number
     * <li>index 4 - Has to be a valid drink
     */
    private void checkOrderArgValues() {
        if (!isStringAnInteger(orderArgs[0])) {
            messageOut.setErrorMessage("Expecting a number at 1st argument\n" +
                    surroundArgWithQuotesAtIndex(0));

        } else if (!cafe.isValidDrink(orderArgs[1])) {
            messageOut.setErrorMessage("Unknown drink '" + orderArgs[1] + "' at 2nd argument\n" +
                    surroundArgWithQuotesAtIndex(1));
        }

        if (orderArgs.length == 5) {
            if (!orderArgs[2].matches("AND")) {
                messageOut.setErrorMessage("Expecting 'AND' at 3rd argument \n" +
                        surroundArgWithQuotesAtIndex(2));

            } else if (!isStringAnInteger(orderArgs[3])) {
                messageOut.setErrorMessage("Expecting a number at 4th argument\n" +
                        surroundArgWithQuotesAtIndex(3));

            } else if (!cafe.isValidDrink(orderArgs[4])) {
                messageOut.setErrorMessage("Unknown drink '" + orderArgs[4] + "' at 5th argument\n" +
                        "order " + surroundArgWithQuotesAtIndex(4));
            }
        }
    }

    /**
     * Surrounds the specified index of {@code orderArgs} with single quotes.
     * @param index An index number for the argument to be wrapped in single quotes
     * @return A {@link String} with single quotes around the indexed argument
     */
    private String surroundArgWithQuotesAtIndex(int index) {
        StringBuilder result = new StringBuilder();

        result.append("order ");

        for (int i = 0; i < orderArgs.length; i++) {
            if (i == index) result.append("'");
            result.append(orderArgs[i]);
            if (i == index) result.append("'");
            result.append(" ");
        }

        appendExpectedOrderFormatToStringBuilder(result);

        return result.toString();
    }

    /**
     * Append some example order commands to a {@link StringBuilder}.
     * @param stringBuilder The StringBuilder to append to
     */
    private void appendExpectedOrderFormatToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append("\n\nExpected formats:\n");
        stringBuilder.append("order 2 teas\n");
        stringBuilder.append("order 2 teas and 1 coffee\n");
        stringBuilder.append("order 1 tea and 3 coffees");
    }

    /**
     * Checks that a {@link String} is an integer, returning true if the String can be parsed into an
     * int and false if not.
     * @param string The String to check if it is an integer
     * @return true if the String can be parsed into an int and false if not
     */
    private boolean isStringAnInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
