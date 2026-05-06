package model;

import exceptions.InvalidAmountException;

/**
 * EntertainmentExpense - entertainment expense.
 */
public class EntertainmentExpense extends Expense {

    private String activityType;
    private static double maxLimit = 400;

    /**
     * Creates an entertainment expense.
     */
    public EntertainmentExpense(int id, double amount, String date, String description,
                                String category, String activityType) {
        super(id, amount, date, description, category);
        this.activityType = activityType;
    }

    /**
     * Returns the activity type.
     */
    public String getActivityType() {
        return activityType;
    }

    /**
     * Updates the activity type.
     */
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    /**
     * Sets the shared entertainment limit.
     */
    public static void setMaxLimit(double limit) {
        if (limit > 0) {
            maxLimit = limit;
            System.out.println("OK: entertainment limit set to " + limit + " MAD");
        } else {
            System.out.println("Warning: invalid limit. Keeping " + maxLimit + " MAD");
        }
    }

    /**
     * Returns the shared entertainment limit.
     */
    public static double getMaxLimit() {
        return maxLimit;
    }

    /**
     * Validates the expense amount and activity type.
     */
    @Override
    public void validate() throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("The amount must be positive. Got: " + amount);
        }

        if (activityType == null || activityType.trim().isEmpty()) {
            throw new InvalidAmountException("Please specify an activity type.");
        }

        if (amount > maxLimit) {
            throw new InvalidAmountException(
                String.format("Entertainment expense too high! %.2f MAD > limit of %.2f MAD", amount, maxLimit)
            );
        }
    }

    /**
     * Returns a readable summary.
     */
    @Override
    public String getSummary() {
        return String.format(
            "[LOISIRS] %s - %.2f MAD le %s (%s)",
            description, amount, date, activityType
        );
    }

    /**
     * Converts the expense to CSV.
     */
    @Override
    public String toCSV() {
        return String.format("%d,ENTERTAINMENT,%.2f,%s,%s,%s,%s",
            id, amount, date, description, category, activityType);
    }
}
