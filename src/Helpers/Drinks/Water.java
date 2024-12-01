package Helpers.Drinks;

/**
 * Extended data structure for Water.
 */
public class Water extends Drink {

    /**
     * Creates a Water instance setting the {@link DrinkType} and the {@code brewingTime}.
     */
    public Water() {
        this.setType(DrinkType.WATER);
        this.setBrewingTimeInSeconds(10);
    }
}
