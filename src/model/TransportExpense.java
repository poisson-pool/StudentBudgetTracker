package model;

import exceptions.InvalidAmountException;

/**
 * TransportExpense - transport expense.
 */
public class TransportExpense extends Expense {

    private String transportMode;
    private static double taxiLimit = 300;
    private static double fuelLimit = 600;
    private static double publicTransportLimit = 50;
    private static double otherLimit = 200;

    /**
     * Creates a transport expense.
     */
    public TransportExpense(int id, double amount, String date, String description,
                            String category, String transportMode) {
        super(id, amount, date, description, category);
        this.transportMode = transportMode;
    }

    /**
     * Returns the transport mode.
     */
    public String getTransportMode() {
        return transportMode;
    }

    /**
     * Updates the transport mode.
     */
    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    /**
     * Sets the taxi/VTC limit.
     */
    public static void setTaxiLimit(double limit) {
        if (limit > 0) {
            taxiLimit = limit;
            System.out.println("OK: taxi/VTC limit set to " + limit + " MAD");
        }
    }

    /**
     * Sets the fuel limit.
     */
    public static void setFuelLimit(double limit) {
        if (limit > 0) {
            fuelLimit = limit;
            System.out.println("OK: fuel limit set to " + limit + " MAD");
        }
    }

    /**
     * Sets the public transport limit.
     */
    public static void setPublicTransportLimit(double limit) {
        if (limit > 0) {
            publicTransportLimit = limit;
            System.out.println("OK: public transport limit set to " + limit + " MAD");
        }
    }

    /**
     * Sets the other transport limit.
     */
    public static void setOtherLimit(double limit) {
        if (limit > 0) {
            otherLimit = limit;
            System.out.println("OK: other transport limit set to " + limit + " MAD");
        }
    }

    /**
     * Returns the taxi/VTC limit.
     */
    public static double getTaxiLimit() { return taxiLimit; }

    /**
     * Returns the fuel limit.
     */
    public static double getFuelLimit() { return fuelLimit; }

    /**
     * Returns the public transport limit.
     */
    public static double getPublicTransportLimit() { return publicTransportLimit; }

    /**
     * Returns the other transport limit.
     */
    public static double getOtherLimit() { return otherLimit; }

    /**
     * Validates the expense.
     */
    @Override
    public void validate() throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("The amount must be positive. Got: " + amount);
        }

        if (transportMode == null || transportMode.trim().isEmpty()) {
            throw new InvalidAmountException("Please specify a transport mode.");
        }

        String mode = transportMode.toLowerCase();
        double limit = getLimitForMode(mode);

        if (amount > limit) {
            throw new InvalidAmountException(
                String.format("Transport expense too high! %.2f MAD > limit of %.2f MAD", amount, limit)
            );
        }
    }

    /**
     * Returns the configured limit for a mode.
     */
    private double getLimitForMode(String mode) {
        switch (mode) {
            case "taxi":
            case "vtc":
                return taxiLimit;
            case "fuel":
            case "essence":
                return fuelLimit;
            case "bus":
            case "metro":
            case "tram":
            case "tramway":
                return publicTransportLimit;
            default:
                return otherLimit;
        }
    }

    /**
     * Returns a readable summary.
     */
    @Override
    public String getSummary() {
        return String.format(
            "[TRANSPORT] %s - %.2f MAD le %s (%s)",
            description, amount, date, transportMode
        );
    }

    /**
     * Converts the expense to CSV.
     */
    @Override
    public String toCSV() {
        return String.format("%d,TRANSPORT,%.2f,%s,%s,%s,%s",
            id, amount, date, description, category, transportMode);
    }
}
