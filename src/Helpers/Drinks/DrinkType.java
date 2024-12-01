package Helpers.Drinks;

/**
 * Enumerator list for the types of drinks in the VirtualCafe
 */
public enum DrinkType {
    TEA("tea"),
    COFFEE("coffee"),
    WATER("water");//########################

    private final String typeOfDrink;

    /**
     * Creates a drink type and updates the string representation of the type of drink.
     * @param typeOfDrink the string of the type of drink
     */
    DrinkType(String typeOfDrink) {
        this.typeOfDrink = typeOfDrink;
    }

    /**
     * Returns a new instance of a {@link Drink} based on the drink type
     * @return a new instance of {@link Drink} based on the drink type
     */
    public Drink getDrink() {
        switch (this) {
            case TEA -> {
                return new Tea();
            }
            case COFFEE -> {
                return new Coffee();
            }
            case WATER -> {//########################
                return new Water();//########################
            }//########################
        }
        return null;
    }

    /**
     * Returns the string representation of the type of drink.
     * @return the string representation of the type of drink
     */
    @Override
    public String toString() {
        return typeOfDrink;
    }
}