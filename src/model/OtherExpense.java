package model;

import exceptions.InvalidAmountException;

/**
 * OtherExpense - other expense category.
 */
public class OtherExpense extends Expense {

    private String subType;
    private static double maxLimit = 1000;

    /**
     * Creates an other expense.
     */
    public OtherExpense(int id, double amount, String date, String description,
                        String category, String subType) {
        super(id, amount, date, description, category);
        this.subType = subType;
    }

    /**
     * Returns the subtype.
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Updates the subtype.
     */
    public void setSubType(String subType) {
        this.subType = subType;
    }

    /**
     * Sets the shared other-expense limit.
     */
    public static void setMaxLimit(double limit) {
        if (limit > 0) {
            maxLimit = limit;
            System.out.println("OK: other expense limit set to " + limit + " MAD");
        } else {
            System.out.println("Warning: invalid limit. Keeping " + maxLimit + " MAD");
        }
    }

    /**
     * Returns the shared other-expense limit.
     */
    public static double getMaxLimit() {
        return maxLimit;
    }

    /**
     * Validates the expense amount and subtype.
     */
    @Override
    public void validate() throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("The amount must be positive. Got: " + amount);
        }

        if (subType == null || subType.trim().isEmpty()) {
            throw new InvalidAmountException("Please specify a subtype.");
        }

        if (amount > maxLimit) {
            throw new InvalidAmountException(
                String.format("Other expense too high! %.2f MAD > limit of %.2f MAD for other expenses", amount, maxLimit)
            );
        }
    }

    /**
     * Returns a readable summary.
     */
    @Override
    public String getSummary() {
        return String.format(
            "[AUTRE] %s - %.2f MAD le %s (%s)",
            description, amount, date, subType
        );
    }

    /**
     * Converts the expense to CSV.
     */
    @Override
    public String toCSV() {
        return String.format("%d,OTHER,%.2f,%s,%s,%s,%s",
            id, amount, date, description, category, subType);
    }
}
