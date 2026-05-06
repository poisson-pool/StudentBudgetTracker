package model;

import exceptions.InvalidAmountException;

/**
 * FoodExpense - Dépense de nourriture.
 * 
 * Permet à l'étudiant de suivre ses dépenses alimentaires :
 * - Repas au restaurant
 * - Courses au supermarché
 * - Snacks, livraisons, etc.
 * 
 * L'utilisateur peut définir sa propre limite maximale.
 * 
 * @author Membre 1
 */
public class FoodExpense extends Expense {
    
    // Attributs spécifiques à la nourriture
    private String mealType;      // Breakfast, Lunch, Dinner, Snack
    private boolean isRestaurant; // true = restaurant, false = supermarché/courses
    
    // Limite maximale configurable par l'utilisateur
    private static double maxLimit = 500;  // Valeur par défaut : 500 MAD
    
    /**
     * Constructeur
     * @param id Identifiant unique
     * @param amount Montant dépensé en MAD
     * @param date Date au format YYYY-MM-DD
     * @param description Description (ex: "Pizza du soir", "Courses Carrefour")
     * @param category Catégorie (généralement "NOURRITURE")
     * @param mealType Type de repas (Breakfast, Lunch, Dinner, Snack)
     * @param isRestaurant True si au restaurant, false si courses
     */
    public FoodExpense(int id, double amount, String date, String description,
                       String category, String mealType, boolean isRestaurant) {
        super(id, amount, date, description, category);
        this.mealType = mealType;
        this.isRestaurant = isRestaurant;
    }
    
    // ========== GETTERS ET SETTERS ==========
    
    public String getMealType() {
        return mealType;
    }
    
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    
    public boolean isRestaurant() {
        return isRestaurant;
    }
    
    public void setRestaurant(boolean isRestaurant) {
        this.isRestaurant = isRestaurant;
    }
    
    /**
     * Définit la limite maximale pour TOUTES les dépenses alimentaires
     * @param limit Nouvelle limite en MAD (doit être > 0)
     */
    public static void setMaxLimit(double limit) {
        if (limit > 0) {
            maxLimit = limit;
            System.out.println("✓ Limite des dépenses alimentaires fixée à " + limit + " MAD");
        } else {
            System.out.println("⚠️ Limite invalide. Gardée à " + maxLimit + " MAD");
        }
    }
    
    /**
     * Retourne la limite maximale actuelle
     */
    public static double getMaxLimit() {
        return maxLimit;
    }
    
    // ========== MÉTHODES OBLIGATOIRES ==========
    
    /**
     * Valide la dépense alimentaire selon les règles :
     * 1. Le montant doit être positif
     * 2. Le type de repas ne peut pas être vide
     * 3. Le montant ne doit pas dépasser la limite configurée
     */
    @Override
    public void validate() throws InvalidAmountException {
        // Règle 1 : Montant positif
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Le montant de la dépense alimentaire doit être positif. Reçu: " + amount
            );
        }
        
        // Règle 2 : Type de repas non vide
        if (mealType == null || mealType.trim().isEmpty()) {
            throw new InvalidAmountException(
                "Veuillez préciser le type de repas (Breakfast, Lunch, Dinner, Snack)"
            );
        }
        
        // Règle 3 : Respect de la limite configurée
        if (amount > maxLimit) {
            throw new InvalidAmountException(
                String.format("Dépense alimentaire trop élevée ! %.2f MAD > limite de %.2f MAD",
                    amount, maxLimit)
            );
        }
    }
    
    /**
     * Retourne un résumé formaté pour l'affichage
     * Exemple : "[NOURRITURE] Pizza Margherita - 80.00 MAD le 2026-05-06 (Restaurant) - Dinner"
     */
    @Override
    public String getSummary() {
        String location = isRestaurant ? "Restaurant" : "Supermarché";
        return String.format(
            "[NOURRITURE] %s - %.2f MAD le %s (%s) - %s",
            description, amount, date, location, mealType
        );
    }
    
    /**
     * Convertit la dépense en ligne CSV pour la sauvegarde
     * Format: id,FOOD,amount,date,description,category,mealType,isRestaurant
     */
    @Override
    public String toCSV() {
        return String.format("%d,FOOD,%.2f,%s,%s,%s,%s,%b",
            id, amount, date, description, category, mealType, isRestaurant);
    }
}