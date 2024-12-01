package Helpers.Drinks;

/**
 * Extended data structure for Tea.
 */
public class Tea extends Drink {

    /**
     * Creates a Tea instance setting the {@link DrinkType} and the {@code brewingTime}.
     */
    public Tea() {
        this.setType(DrinkType.TEA);
        this.setBrewingTimeInSeconds(45);
    }
}
