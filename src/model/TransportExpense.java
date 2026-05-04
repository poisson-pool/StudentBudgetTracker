package model;

import exceptions.InvalidAmountException;

/**
 * TransportExpense - Dépense de transport.
 * 
 * Les limites sont définies par l'utilisateur pour chaque mode de transport.
 * 
 * @author Membre 1
 */
public class TransportExpense extends Expense {
    
    // Attributs spécifiques
    private String transportMode;  // Bus, Train, Taxi, Metro, Fuel, Bike
    private double distanceKm;
    
    // Limites configurables par mode de transport (statiques)
    private static double taxiLimit = 300;   // Par défaut 300 MAD
    private static double fuelLimit = 600;   // Par défaut 600 MAD
    private static double generalLimit = 1000; // Pour tous les autres modes
    
    public TransportExpense(int id, double amount, String date, String description,
                            String category, String transportMode, double distanceKm) {
        super(id, amount, date, description, category);
        this.transportMode = transportMode;
        this.distanceKm = distanceKm;
    }
    
    // Getters/Setters
    public String getTransportMode() { return transportMode; }
    public double getDistanceKm() { return distanceKm; }
    public void setTransportMode(String transportMode) { this.transportMode = transportMode; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    
    // Setters pour les limites (appelés par l'utilisateur via ExpenseManager)
    public static void setTaxiLimit(double limit) { if (limit > 0) taxiLimit = limit; }
    public static void setFuelLimit(double limit) { if (limit > 0) fuelLimit = limit; }
    public static void setGeneralLimit(double limit) { if (limit > 0) generalLimit = limit; }
    
    // Getters pour les limites
    public static double getTaxiLimit() { return taxiLimit; }
    public static double getFuelLimit() { return fuelLimit; }
    public static double getGeneralLimit() { return generalLimit; }
    
    @Override
    public void validate() throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Le montant doit être positif. Reçu: " + amount);
        }
        
        // Validation selon le mode de transport avec limites configurables
        switch (transportMode.toLowerCase()) {
            case "taxi":
                if (amount > taxiLimit) {
                    throw new InvalidAmountException(
                        String.format("Taxi trop cher ! %.2f MAD > limite de %.2f MAD", 
                            amount, taxiLimit)
                    );
                }
                break;
                
            case "fuel":
            case "essence":
                if (amount > fuelLimit) {
                    throw new InvalidAmountException(
                        String.format("Essence trop chère ! %.2f MAD > limite de %.2f MAD",
                            amount, fuelLimit)
                    );
                }
                break;
                
            default:
                if (amount > generalLimit) {
                    throw new InvalidAmountException(
                        String.format("Dépense transport trop élevée ! %.2f MAD > limite de %.2f MAD",
                            amount, generalLimit)
                    );
                }
                break;
        }
    }
    
    @Override
    public String getSummary() {
        return String.format(
            "[TRANSPORT] %s - %.2f MAD le %s en %s (%.1f km)",
            description, amount, date, transportMode, distanceKm
        );
    }
    
    @Override
    public String toCSV() {
        return String.format("%d,TRANSPORT,%.2f,%s,%s,%s,%s,%.2f",
            id, amount, date, description, category, transportMode, distanceKm);
    }
}