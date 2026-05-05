package model;

/**
 * Represents a student user of the budget tracker.
 * Stores identity info and monthly income for budget context.
 */
public class Student {

    // ── Fields ──────────────────────────────────────────────────────────────
    private String id;
    private String name;
    private String email;
    private double monthlyIncome;

    // ── Constructor ──────────────────────────────────────────────────────────
    public Student(String id, String name, String email, double monthlyIncome) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.monthlyIncome = monthlyIncome;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    // ── Serialization ────────────────────────────────────────────────────────

    /**
     * Converts this student to a CSV line.
     * Format: id,name,email,monthlyIncome
     * Used by FileManager (Member 4) to persist data.
     */
    public String toCSV() {
        return String.join(",", id, name, email, String.valueOf(monthlyIncome));
    }

    /**
     * Reconstructs a Student from a CSV line.
     * Inverse of toCSV().
     */
    public static Student fromCSV(String csvLine) {
        // String handling: split on comma, trim each field
        String[] parts = csvLine.split(",");
        String id            = parts[0].trim();
        String name          = parts[1].trim();
        String email         = parts[2].trim();
        double monthlyIncome = Double.parseDouble(parts[3].trim());
        return new Student(id, name, email, monthlyIncome);
    }

    // ── Display ──────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Student[id=%s | name=%s | email=%s | income=%.2f MAD]",
                id, name, email, monthlyIncome);
    }
}
