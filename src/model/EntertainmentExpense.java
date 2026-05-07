package model;

import exceptions.InvalidAmountException;

public class EntertainmentExpense extends Expense {
    // les attributs
    private String activityType;
    private static double maxLimit = 400;

    //Constructeur
    public EntertainmentExpense(int id, double amount, String date, String description,
                                String category, String activityType) {
        super(id, amount, date, description, category);
        this.activityType = activityType;
    }

    //Getters & setters
    public String getActivityType() {
        return activityType;
    }
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public static double getMaxLimit() {
        return maxLimit;
    }
    public static void setMaxLimit(double limit) {
        if (limit > 0) {
            maxLimit = limit;
            System.out.println("OK: entertainment limit set to " + limit + " MAD");
        } else {
            System.out.println("Warning: invalid limit. Keeping " + maxLimit + " MAD");
        }
    }

    //Validation
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

    //Resume
    @Override
    public String getSummary() {
        return String.format(
            "[LOISIRS] %s - %.2f MAD le %s (%s)",
            description, amount, date, activityType
        );
    }

    //Serialisation - commun entre les classes
    @Override
    public String toCSV() {
        return String.format("%d,ENTERTAINMENT,%.2f,%s,%s,%s,%s",
            id, amount, date, description, category, activityType);
    }
}
