package model;

import exceptions.InvalidAmountException;

/**
 * OtherExpense - Dépense pour tout autre type non spécifique.
 * 
 * Cette classe permet à l'étudiant de gérer des dépenses qui ne rentrent pas
 * dans les catégories principales (Nourriture, Transport, Loisirs).
 * 
 * Exemples d'utilisation :
 * - Abonnements (Netflix, magazine, salle de sport)
 * - Achats divers (vêtements, électronique, fournitures)
 * - Soins médicaux (pharmacie, médecin)
 * - Logement (loyer, électricité, eau, internet)
 * - Frais bancaires, etc.
 * 
 * L'utilisateur peut définir sa propre limite et son propre type.
 * 
 * @author Membre 1
 */
public class OtherExpense extends Expense {
    
    // Attribut spécifique : le sous-type de dépense (libre)
    private String subType;  // "magazine", "Loyer", "Salle de sport", etc.
    
    // Limite maximale configurable par l'utilisateur
    private static double maxLimit = 1000;  // Valeur par défaut : 1000 MAD
    
    /**
     * Constructeur
     * @param id Identifiant unique
     * @param amount Montant dépensé en MAD
     * @param date Date au format YYYY-MM-DD
     * @param description Description de la dépense
     * @param category Catégorie (généralement "AUTRE")
     * @param subType Sous-type (ex: "Netflix", "Loyer", "Médecin")
     */
    public OtherExpense(int id, double amount, String date, String description,
                        String category, String subType) {
        super(id, amount, date, description, category);
        this.subType = subType;
    }
    
    // ========== GETTERS ET SETTERS ==========
    
    public String getSubType() {
        return subType;
    }
    
    public void setSubType(String subType) {
        this.subType = subType;
    }
    
    /**
     * Définit la limite maximale pour TOUTES les dépenses autres
     * @param limit Nouvelle limite en MAD (doit être > 0)
     */
    public static void setMaxLimit(double limit) {
        if (limit > 0) {
            maxLimit = limit;
            System.out.println("✓ Limite des dépenses diverses fixée à " + limit + " MAD");
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
     * Valide la dépense selon les règles :
     * 1. Le montant doit être positif
     * 2. Le sous-type ne peut pas être vide
     * 3. Le montant ne doit pas dépasser la limite configurée
     */
    @Override
    public void validate() throws InvalidAmountException {
        // Règle 1 : Montant positif
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Le montant de la dépense doit être positif. Reçu: " + amount
            );
        }
        
        // Règle 2 : Sous-type non vide
        if (subType == null || subType.trim().isEmpty()) {
            throw new InvalidAmountException(
                "Veuillez préciser le type de dépense (Netflix, Loyer, Médical, etc.)"
            );
        }
        
        // Règle 3 : Respect de la limite configurée
        if (amount > maxLimit) {
            throw new InvalidAmountException(
                String.format("Dépense trop élevée ! %.2f MAD > limite de %.2f MAD pour les dépenses diverses",
                    amount, maxLimit)
            );
        }
    }
    
    /**
     * Retourne un résumé formaté pour l'affichage
     * Exemple : "[AUTRE] Netflix mensuel - 15.99 MAD le 2026-05-06 (Netflix)"
     */
    @Override
    public String getSummary() {
        return String.format(
            "[AUTRE] %s - %.2f MAD le %s (%s)",
            description, amount, date, subType
        );
    }
    
    /**
     * Convertit la dépense en ligne CSV pour la sauvegarde
     * Format: id,OTHER,amount,date,description,category,subType
     */
    @Override
    public String toCSV() {
        return String.format("%d,OTHER,%.2f,%s,%s,%s,%s",
            id, amount, date, description, category, subType);
    }
}