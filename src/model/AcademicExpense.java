package model;

import exceptions.InvalidAmountException;

/**
 * AcademicExpense - Dépense d'études.
 * 
 * La matière est obligatoire, mais l'utilisateur peut définir les limites.
 * 
 * @author Membre 1
 */
public class AcademicExpense extends Expense {
    
    private String subjectName;   // OBLIGATOIRE
    private String documentType;  // Book, Printing, Registration, Tuition, Stationery
    
    // Limites configurables
    private static double tuitionLimit = 5000;    // Par défaut 5000 MAD
    private static double bookLimit = 1000;       // Par défaut 1000 MAD
    private static double printingLimit = 200;    // Par défaut 200 MAD
    private static double generalLimit = 2000;    // Pour les autres types
    
    public AcademicExpense(int id, double amount, String date, String description,
                           String category, String subjectName, String documentType) {
        super(id, amount, date, description, category);
        this.subjectName = subjectName;
        this.documentType = documentType;
    }
    
    // Getters/Setters
    public String getSubjectName() { return subjectName; }
    public String getDocumentType() { return documentType; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    // Setters pour les limites
    public static void setTuitionLimit(double limit) { if (limit > 0) tuitionLimit = limit; }
    public static void setBookLimit(double limit) { if (limit > 0) bookLimit = limit; }
    public static void setPrintingLimit(double limit) { if (limit > 0) printingLimit = limit; }
    public static void setGeneralLimit(double limit) { if (limit > 0) generalLimit = limit; }
    
    @Override
    public void validate() throws InvalidAmountException {
        // Règle FIXE : La matière est OBLIGATOIRE (l'utilisateur ne peut pas changer ça)
        if (subjectName == null || subjectName.trim().isEmpty()) {
            throw new InvalidAmountException(
                "La dépense académique doit avoir un nom de matière !"
            );
        }
        
        if (amount <= 0) {
            throw new InvalidAmountException("Le montant doit être positif. Reçu: " + amount);
        }
        
        // Validation selon le type de document avec limites configurables
        switch (documentType.toLowerCase()) {
            case "tuition":
            case "inscription":
                if (amount > tuitionLimit) {
                    throw new InvalidAmountException(
                        String.format("Frais d'inscription trop élevés ! %.2f MAD > limite de %.2f MAD",
                            amount, tuitionLimit)
                    );
                }
                break;
                
            case "book":
            case "livre":
                if (amount > bookLimit) {
                    throw new InvalidAmountException(
                        String.format("Dépense de livre trop élevée ! %.2f MAD > limite de %.2f MAD",
                            amount, bookLimit)
                    );
                }
                break;
                
            case "printing":
            case "impression":
                if (amount > printingLimit) {
                    throw new InvalidAmountException(
                        String.format("Frais d'impression trop élevés ! %.2f MAD > limite de %.2f MAD",
                            amount, printingLimit)
                    );
                }
                break;
                
            default:
                if (amount > generalLimit) {
                    throw new InvalidAmountException(
                        String.format("Dépense académique trop élevée ! %.2f MAD > limite de %.2f MAD",
                            amount, generalLimit)
                    );
                }
                break;
        }
    }
    
    @Override
    public String getSummary() {
        return String.format(
            "[ETUDES] %s - %.2f MAD le %s pour %s (%s)",
            description, amount, date, subjectName, documentType
        );
    }
    
    @Override
    public String toCSV() {
        return String.format("%d,ACADEMIC,%.2f,%s,%s,%s,%s,%s",
            id, amount, date, description, category, subjectName, documentType);
    }
}