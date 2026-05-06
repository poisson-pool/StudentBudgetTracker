package model;

import exceptions.InvalidAmountException;

/**
 * FoodExpense - food expense.
 */
public class FoodExpense extends Expense {

    private String mealType;
    private boolean isRestaurant;
    private static double maxLimit = 500;

    /**
     * Creates a food expense.
     */
    public FoodExpense(int id, double amount, String date, String description,
                       String category, String mealType, boolean isRestaurant) {
        super(id, amount, date, description, category);
        this.mealType = mealType;
        this.isRestaurant = isRestaurant;
    }

    /**
     * Returns the meal type.
     */
    public String getMealType() { return mealType; }

    /**
     * Returns whether the expense happened at a restaurant.
     */
    public boolean isRestaurant() { return isRestaurant; }

    /**
     * Updates the meal type.
     */
    public void setMealType(String mealType) { this.mealType = mealType; }

    /**
     * Updates the restaurant flag.
     */
    public void setRestaurant(boolean isRestaurant) { this.isRestaurant = isRestaurant; }

    /**
     * Sets the shared food limit.
     */
    public static void setMaxLimit(double limit) {
        if (limit > 0) {
            maxLimit = limit;
            System.out.println("OK: food limit set to " + limit + " MAD");
        } else {
            System.out.println("Warning: invalid limit. Keeping " + maxLimit + " MAD");
        }
    }

    /**
     * Returns the shared food limit.
     */
    public static double getMaxLimit() {
        return maxLimit;
    }

    /**
     * Validates the expense amount and meal type.
     */
    @Override
    public void validate() throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("The amount must be positive. Got: " + amount);
        }

        if (mealType == null || mealType.trim().isEmpty()) {
            throw new InvalidAmountException("Please specify a meal type.");
        }

        if (amount > maxLimit) {
            throw new InvalidAmountException(
                String.format("Food expense too high! %.2f MAD > limit of %.2f MAD", amount, maxLimit)
            );
        }
    }

    /**
     * Returns a readable summary.
     */
    @Override
    public String getSummary() {
        String location = isRestaurant ? "Restaurant" : "Supermarche";
        return String.format(
            "[NOURRITURE] %s - %.2f MAD le %s (%s) - %s",
            description, amount, date, location, mealType
        );
    }

    /**
     * Converts the expense to CSV.
     */
    @Override
    public String toCSV() {
        return String.format("%d,FOOD,%.2f,%s,%s,%s,%s,%b",
            id, amount, date, description, category, mealType, isRestaurant);
    }
}
