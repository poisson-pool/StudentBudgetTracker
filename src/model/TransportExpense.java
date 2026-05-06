package model;

import exceptions.InvalidAmountException;

/**
 * TransportExpense - Dépense de transport.
 * 
 * Un étudiant peut dépenser pour différents modes de transport :
 * - Bus, Métro, Tramway, Taxi, VTC, Essence (pour voiture), Vélo, etc.
 * 
 * Règles métier : L'utilisateur peut définir des limites par mode de transport.
 * 
 * @author Membre 1
 */
public class TransportExpense extends Expense {
    
    // Attribut spécifique : le mode de transport utilisé
    private String transportMode;  // Bus, Metro, Taxi, Fuel, etc.
    
    // Limites configurables par l'utilisateur (static = partagé entre toutes les dépenses)
    private static double taxiLimit = 300;      // Limite pour les taxis/VTC
    private static double fuelLimit = 600;      // Limite pour l'essence
    private static double publicTransportLimit = 50;  // Limite pour bus/métro/tram
    private static double otherLimit = 200;     // Limite pour les autres modes
    
    /**
     * Constructeur
     * @param id Identifiant unique
     * @param amount Montant dépensé en MAD
     * @param date Date au format YYYY-MM-DD
     * @param description Description (ex: "Trajet maison -> fac")
     * @param category Catégorie (généralement "TRANSPORT")
     * @param transportMode Mode de transport (Bus, Taxi, Metro, Fuel, etc.)
     */
    public TransportExpense(int id, double amount, String date, String description,
                            String category, String transportMode) {
        super(id, amount, date, description, category);
        this.transportMode = transportMode;
    }
    
    // ========== GETTERS ET SETTERS ==========
    
    public String getTransportMode() {
        return transportMode;
    }
    
    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }
    
    /**
     * Définit la limite pour les taxis et VTC
     */
    public static void setTaxiLimit(double limit) {
        if (limit > 0) {
            taxiLimit = limit;
            System.out.println("✓ Limite des taxis/VTC fixée à " + limit + " MAD");
        }
    }
    
    /**
     * Définit la limite pour l'essence
     */
    public static void setFuelLimit(double limit) {
        if (limit > 0) {
            fuelLimit = limit;
            System.out.println("✓ Limite d'essence fixée à " + limit + " MAD");
        }
    }
    
    /**
     * Définit la limite pour les transports en commun (bus, métro, tram)
     */
    public static void setPublicTransportLimit(double limit) {
        if (limit > 0) {
            publicTransportLimit = limit;
            System.out.println("✓ Limite des transports en commun fixée à " + limit + " MAD");
        }
    }
    
    /**
     * Définit la limite pour les autres modes de transport
     */
    public static void setOtherLimit(double limit) {
        if (limit > 0) {
            otherLimit = limit;
            System.out.println("✓ Limite des autres transports fixée à " + limit + " MAD");
        }
    }
    
    // Getters pour les limites (utiles pour l'affichage)
    public static double getTaxiLimit() { return taxiLimit; }
    public static double getFuelLimit() { return fuelLimit; }
    public static double getPublicTransportLimit() { return publicTransportLimit; }
    public static double getOtherLimit() { return otherLimit; }
    
    // ========== MÉTHODES OBLIGATOIRES ==========
    
    /**
     * Valide la dépense de transport selon les règles :
     * 1. Le montant doit être positif
     * 2. Le mode de transport ne peut pas être vide
     * 3. Le montant ne doit pas dépasser la limite selon le mode choisi
     */
    @Override
    public void validate() throws InvalidAmountException {
        // Règle 1 : Montant positif
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Le montant de la dépense transport doit être positif. Reçu: " + amount
            );
        }
        
        // Règle 2 : Mode de transport non vide
        if (transportMode == null || transportMode.trim().isEmpty()) {
            throw new InvalidAmountException(
                "Veuillez préciser le mode de transport (Bus, Taxi, Metro, Fuel, etc.)"
            );
        }
        
        // Règle 3 : Vérification des limites selon le mode
        String mode = transportMode.toLowerCase();
        double limit = getLimitForMode(mode);
        
        if (amount > limit) {
            throw new InvalidAmountException(
                String.format("Dépense %s trop élevée ! %.2f MAD > limite de %.2f MAD",
                    transportMode, amount, limit)
            );
        }
        
           }
    
    /**
     * Retourne la limite selon le mode de transport
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
     * Retourne un résumé formaté pour l'affichage
     * Exemple : "[TRANSPORT] Trajet maison-fac - 8.00 MAD le 2026-05-06 (Bus)"
     */
    @Override
    public String getSummary() {
        return String.format(
            "[TRANSPORT] %s - %.2f MAD le %s (%s)",
            description, amount, date, transportMode
        );
    }
    
    /**
     * Convertit la dépense en ligne CSV pour la sauvegarde
     * Format: id,TRANSPORT,amount,date,description,category,transportMode
     */
    @Override
    public String toCSV() {
        return String.format("%d,TRANSPORT,%.2f,%s,%s,%s,%s",
            id, amount, date, description, category, transportMode);
    }
}