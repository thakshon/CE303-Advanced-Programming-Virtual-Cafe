package Helpers.Drinks;

/**
 * Extended data structure for Coffee.
 */
public class Coffee extends Drink {

    /**
     * Creates a Coffee instance setting the {@link DrinkType} and the {@code brewingTime}.
     */
    public Coffee() {
        this.setType(DrinkType.COFFEE);
        this.setBrewingTimeInSeconds(30);
    }
}
