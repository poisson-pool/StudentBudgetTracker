package model;


public class Student {
    // les attributs
    private String id;
    private String name;
    private String email;
    private double monthlyIncome;

    //Constructeur
    public Student(String id, String name, String email, double monthlyIncome) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.monthlyIncome = monthlyIncome;
    }

    //Getters & Setters
    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    //Serialisation - commun entre les classes
    public String toCSV() {
        return String.join(",", id, name, email, String.valueOf(monthlyIncome));
    }
    public static Student fromCSV(String csvLine) {
        // String handling: split on comma, trim each field
        String[] parts = csvLine.split(",");
        String id            = parts[0].trim();
        String name          = parts[1].trim();
        String email         = parts[2].trim();
        double monthlyIncome = Double.parseDouble(parts[3].trim());
        return new Student(id, name, email, monthlyIncome);
    }

    //Optionelle - En cas d'affichage direct
    @Override
    public String toString() {
        return String.format("Student[id=%s | name=%s | email=%s | income=%.2f MAD]",
                id, name, email, monthlyIncome);
    }
}
