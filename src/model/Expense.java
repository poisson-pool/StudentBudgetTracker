/**
 * CLASSE ABSTRAITE Expense - Le fondement de toutes les dépenses du projet
 * ============================================================================
 * 
 * À QUOI SERT CETTE CLASSE ?
 * -------------------------
 * Expense est une classe abstraite qui sert de "modèle de base" pour tous les
 * types de dépenses que l'étudiant peut enregistrer (nourriture, transport,
 * études, loisirs).
 CE QU'ELLE CONTIENT (attributs)
 * -------------------------------
 * - id           : Numéro unique pour identifier chaque dépense
 * - amount       : Montant dépensé en MAD (Dirhams marocains)
 * - date         : Date de la dépense (format YYYY-MM-DD)
 * - description  : Description textuelle (ex: "Pizza du soir")
 * - category     : Catégorie (NOURRITURE, TRANSPORT, ETUDES, LOISIRS)
 *  * CE QU'ELLE OBLIGE LES SOUS-CLASSES À FAIRE (méthodes abstraites)
 * ----------------------------------------------------------------
 * 1. validate()   → Chaque type de dépense doit vérifier ses propres règles
 *                   (ex: FoodExpense refuse les montants > 500 MAD)
 * 
 * 2. getSummary() → Chaque type de dépense doit savoir comment s'afficher
 *                   (ex: "[Food] Pizza - 80 MAD on 2026-05-03")
 * 
 * 3. toCSV()      → Chaque type de dépense doit savoir se convertir en CSV
 *                   pour la sauvegarde dans les fichiers
 * @version 1.0
 */

package model;

import exceptions.InvalidAmountException;

public abstract class Expense {
    // les attributs
    protected int id;
    protected double amount;
    protected String date;      // Format: YYYY-MM-DD
    protected String description;
    protected String category;
    
    // Constructeurs
    public Expense(int id, double amount, String date, String description, String category) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    public double getAmount() {
        return amount;
    }
    public String getDate() {
        return date;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() {
        return category;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    
    // Les methodes abtraits que - chaque sous-classe doit implementer !
    public abstract void validate() throws InvalidAmountException;
    public abstract String getSummary();
    public abstract String toCSV();
    
    @Override
    public String toString() {
        return getSummary();
    }
}
