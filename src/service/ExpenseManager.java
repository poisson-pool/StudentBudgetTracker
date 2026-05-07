package service;

import exceptions.BudgetExceededException;
import exceptions.ExpenseNotFoundException;
import exceptions.InvalidAmountException;
import model.Budget;
import model.Expense;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseManager {
    //Collections
    private List<Expense> expenses = new ArrayList<>();
    private Map<String, Budget> budgets = new HashMap<>();

    //Expense CRUD
    ///creation
    public void addExpense(Expense expense) throws InvalidAmountException, BudgetExceededException {
        // Polymorphism
        expense.validate();
        expenses.add(expense);
        System.out.println("Expense ajoute: " + expense.getSummary());

        //Verification de budget de categorie
        String cat = expense.getCategory().toLowerCase();
        if (budgets.containsKey(cat)) {
            Budget budget = budgets.get(cat);
            budget.addSpent(expense.getAmount());
            // Method de l'implimentation Alertable
            budget.checkAlert();
        }
    }
    ///suppression
    public void deleteExpense(String id) throws ExpenseNotFoundException {
        Expense target = findById(id); // throws si non trouve
        expenses.remove(target);
        System.out.println("Expense supprime: " + id);
    }
    ///modification
    public void editExpense(String id, String newDescription, double newAmount)
            throws ExpenseNotFoundException, InvalidAmountException {

        if (newAmount <= 0) {
            throw new InvalidAmountException("le montnat doit etre positive: " + newAmount);
        }

        Expense target = findById(id);
        target.setDescription(newDescription);
        target.setAmount(newAmount);
        System.out.println("Expense modifie: " + id);
    }

    ///Affichage
    public void listAll() {
        if (expenses.isEmpty()) {
            System.out.println("  accune depense trouvee.");
            return;
        }
        System.out.println("\n  ── All expenses (" + expenses.size() + ") ───────────────────");
        for (Expense e : expenses) {
            System.out.println("  " + e.getSummary());
        }
        System.out.println("  ─────────────────────────────────────────────\n");
    }

    //Filtrage
    ///Categorie
    public List<Expense> filterByCategory(String category) {
        // String: normalize to lowercase before comparing
        String target = category.trim().toLowerCase();

        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.getCategory().toLowerCase().equals(target)) {
                result.add(e);
            }
        }

        System.out.println("  trouve " + result.size() + " expense(s) en categorie: " + category);
        return result;
    }
    ///mois
    public List<Expense> filterByMonth(int month, int year) {
        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            LocalDate d = LocalDate.parse(e.getDate());
            if (d.getMonthValue() == month && d.getYear() == year) {
                result.add(e);
            }
        }
        System.out.println("  trouve " + result.size() + " expense(s) en " + month + "/" + year);
        return result;
    }

    //Recherche
    public List<Expense> search(String keyword) {
        String kw = keyword.trim().toLowerCase();

        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            // String.contains()
            boolean descMatch     = e.getDescription().toLowerCase().contains(kw);
            boolean categoryMatch = e.getCategory().toLowerCase().contains(kw);
            // getSummary() est polymorphique
            boolean summaryMatch  = e.getSummary().toLowerCase().contains(kw);

            if (descMatch || categoryMatch || summaryMatch) {
                result.add(e);
            }
        }
        System.out.println("  Recherche \"" + keyword + "\": " + result.size() + " resultat(s).");
        return result;
    }
    private Expense findById(String id) throws ExpenseNotFoundException {
        String target = id.trim();
        for (Expense e : expenses) {
            if (String.valueOf(e.getId()).equals(target)) {
                return e;
            }
        }
        throw new ExpenseNotFoundException("accune depnse trouve avec ID de: " + id);
    }

    //Triage
    ///Date
    public void sortByDate() {
        Collections.sort(expenses, Comparator.comparing(Expense::getDate));
        System.out.println("Expenses trie par la date.");
    }
    ///montant
    public void sortByAmount() {
        // Collections.sort() with reversed Comparator
        Collections.sort(expenses, Comparator.comparingDouble(Expense::getAmount).reversed());
        System.out.println("Expenses trie par le monatant descendent.");
    }

    //gestion de Budget
    public void setBudget(String category, double limit, int month, int year) {
        budgets.put(category.toLowerCase(), new Budget(category, limit, Integer.toString(month)));
        System.out.printf("  ✔ Budget set: %s → %.2f MAD (%d/%d)%n", category, limit, month, year);
    }

    //Affichage des Budgets
    public void listBudgets() {
        if (budgets.isEmpty()) {
            System.out.println("  accune budget configuree.");
            return;
        }
        System.out.println("\n  ── Budgets ────────────────────────────────────");
        // HashMap iteration: entrySet() gives key-value pairs
        for (Map.Entry<String, Budget> entry : budgets.entrySet()) {
            Budget b = entry.getValue();
            System.out.printf("  %-20s spent: %.2f / %.2f MAD%n",
                    b.getCategory(), b.getSpent(), b.getLimit());
        }
        System.out.println("  ─────────────────────────────────────────────\n");
    }

    //Statistique
    ///total par mois
    public double totalForMonth(int month, int year) {
        double total = 0;
        for (Expense e : filterByMonth(month, year)) {
            total += e.getAmount();
        }
        return total;
    }
    ///les plus chers
    public Expense mostExpensive() throws ExpenseNotFoundException {
        if (expenses.isEmpty()) {
            throw new ExpenseNotFoundException("accune depense a compare.");
        }
        // Collections.max() with amount comparator
        return Collections.max(expenses, Comparator.comparingDouble(Expense::getAmount));
    }

    //Getters & setters
    ///list depenses
    public List<Expense> getAllExpenses() {
        return expenses;
    }
    public void setExpenses(List<Expense> loaded) {
        this.expenses = loaded;
    }
    ///Budgets
    public Map<String, Budget> getBudgets() {
        return budgets;
    }
}
