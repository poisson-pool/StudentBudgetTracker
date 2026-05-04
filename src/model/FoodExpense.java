package model;

import exceptions.InvalidAmountException;

/**
 * FoodExpense - Dépense de nourriture.
 * 
 * La limite maximale est définie par l'utilisateur via ExpenseManager.
 * 
 * @author Membre 1
 */
public class FoodExpense extends Expense {
    
    // Attributs spécifiques
    private String mealType;      // Breakfast, Lunch, Dinner, Snack
    private boolean isRestaurant; // true = restaurant, false = supermarché
    
    // Attribut STATIQUE pour la limite (partagée par TOUTES les FoodExpense)
    private static double maxLimit = 500; // Valeur par défaut, modifiable par l'utilisateur
    
    public FoodExpense(int id, double amount, String date, String description,
                       String category, String mealType, boolean isRestaurant) {
        super(id, amount, date, description, category);
        this.mealType = mealType;
        this.isRestaurant = isRestaurant;
    }
    
    // Getters/Setters spécifiques
    public String getMealType() { return mealType; }
    public boolean isRestaurant() { return isRestaurant; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public void setRestaurant(boolean isRestaurant) { this.isRestaurant = isRestaurant; }
    
    /**
     * Définit la limite maximale pour TOUTES les dépenses de nourriture
     * @param limit Nouvelle limite en MAD
     */
    public static void setMaxLimit(double limit) {
        if (limit > 0) {
            maxLimit = limit;
        }
    }
    
    /**
     * Retourne la limite maximale actuelle
     */
    public static double getMaxLimit() {
        return maxLimit;
    }
    
    @Override
    public void validate() throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Le montant doit être positif. Reçu: " + amount
            );
        }
        
        // Utilise la limite définie par l'utilisateur
        if (amount > maxLimit) {
            throw new InvalidAmountException(
                String.format("Dépense nourriture trop élevée ! %.2f MAD > limite de %.2f MAD",
                    amount, maxLimit)
            );
        }
    }
    
    @Override
    public String getSummary() {
        String location = isRestaurant ? "Restaurant" : "Supermarché";
        return String.format(
            "[NOURRITURE] %s - %.2f MAD le %s (%s) - %s",
            description, amount, date, location, mealType
        );
    }
    
    @Override
    public String toCSV() {
        return String.format("%d,FOOD,%.2f,%s,%s,%s,%s,%b",
            id, amount, date, description, category, mealType, isRestaurant);
    }
}