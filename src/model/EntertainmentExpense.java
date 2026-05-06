package model;

import exceptions.InvalidAmountException;

/**
 * EntertainmentExpense - Dépense de loisirs.
 * 
 * Un étudiant peut dépenser pour différentes activités de loisirs :
 * - Cinema, Concert, Sport, Jeux vidéo, Café/Bar, Sortie entre amis, etc.
 * 
 * Règle métier : L'utilisateur peut définir une limite maximale par dépense.
 * 
 * @author Membre 1
 */
public class EntertainmentExpense extends Expense {
    
    // Attribut spécifique : le type d'activité de loisir
    private String activityType;  // Cinema, Concert, Sport, Gaming, Cafe, Party, etc.
    
    // Limite maximale par dépense (configurable par l'utilisateur)
    // static = la même limite pour TOUTES les dépenses de loisirs
    private static double maxLimit = 400;  // Valeur par défaut : 400 MAD
    
    /**
     * Constructeur
     * @param id Identifiant unique
     * @param amount Montant dépensé en MAD
     * @param date Date au format YYYY-MM-DD
     * @param description Description (ex: "Film Avatar au Megarama")
     * @param category Catégorie (généralement "LOISIRS")
     * @param activityType Type d'activité (Cinema, Concert, etc.)
     */
    public EntertainmentExpense(int id, double amount, String date, String description,
                                String category, String activityType) {
        super(id, amount, date, description, category);
        this.activityType = activityType;
    }
    
    // ========== GETTERS ET SETTERS ==========
    
    public String getActivityType() {
        return activityType;
    }
    
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    
    /**
     * Définit la limite maximale pour TOUTES les dépenses de loisirs
     * @param limit Nouvelle limite en MAD (doit être > 0)
     */
    public static void setMaxLimit(double limit) {
        if (limit > 0) {
            maxLimit = limit;
            System.out.println("✓ Limite des dépenses de loisirs fixée à " + limit + " MAD");
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
     * Valide la dépense de loisirs selon les règles :
     * 1. Le montant doit être positif
     * 2. Le type d'activité ne peut pas être vide
     * 3. Le montant ne doit pas dépasser la limite configurée
     */
    @Override
    public void validate() throws InvalidAmountException {
        // Règle 1 : Montant positif
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Le montant de la dépense loisir doit être positif. Reçu: " + amount
            );
        }
        
        // Règle 2 : Type d'activité non vide
        if (activityType == null || activityType.trim().isEmpty()) {
            throw new InvalidAmountException(
                "Veuillez préciser le type d'activité (Cinema, Concert, Sport, etc.)"
            );
        }
        
        // Règle 3 : Respect de la limite configurée
        if (amount > maxLimit) {
            throw new InvalidAmountException(
                String.format("Dépense loisir trop élevée ! %.2f MAD > limite de %.2f MAD",
                    amount, maxLimit)
            );
        }
        
            }
    
    /**
     * Retourne un résumé formaté pour l'affichage
     * Exemple : "[LOISIRS] Film Avatar - 60.00 MAD le 2026-05-03 (Cinema)"
     */
    @Override
    public String getSummary() {
        return String.format(
            "[LOISIRS] %s - %.2f MAD le %s (%s)",
            description, amount, date, activityType
        );
    }
    
    /**
     * Convertit la dépense en ligne CSV pour la sauvegarde
     * Format: id,ENTERTAINMENT,amount,date,description,category,activityType
     */
    @Override
    public String toCSV() {
        return String.format("%d,ENTERTAINMENT,%.2f,%s,%s,%s,%s",
            id, amount, date, description, category, activityType);
    }
}