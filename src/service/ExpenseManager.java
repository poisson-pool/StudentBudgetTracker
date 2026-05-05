package service;

import exceptions.BudgetExceededException;
import exceptions.ExpenseNotFoundException;
import exceptions.InvalidAmountException;
import interfaces.Alertable;
import model.Budget;
import model.Expense;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExpenseManager — the "brain" of the application.
 *
 * Owns two core collections:
 *   - List<Expense>         : all recorded expenses (ArrayList)
 *   - Map<String, Budget>   : one budget per category (HashMap, key = category name)
 *
 * Responsibilities: CRUD on expenses, budget tracking, search, sort, filter.
 *
 * OOP concepts used here:
 *   ─ Polymorphism  : all methods work on the abstract Expense type
 *   ─ Collections   : ArrayList + HashMap + Collections.sort()
 *   ─ Custom exceptions : InvalidAmountException, ExpenseNotFoundException,
 *                         BudgetExceededException
 *   ─ String class  : toLowerCase(), contains(), trim(), String.format()
 *   ─ Interfaces    : Budget implements Alertable; called via checkAlert()
 */
public class ExpenseManager {

    // ── Collections ──────────────────────────────────────────────────────────

    /** Master list of all expenses. Polymorphic: holds any Expense subtype. */
    private List<Expense> expenses = new ArrayList<>();

    /** Maps category name → Budget object. O(1) lookup on every add. */
    private Map<String, Budget> budgets = new HashMap<>();

    // ── Expense CRUD ─────────────────────────────────────────────────────────

    /**
     * Add a new expense to the tracker.
     * After adding, checks the category budget and fires an alert if needed.
     *
     * @throws InvalidAmountException  if the expense amount is zero or negative
     * @throws BudgetExceededException if the category budget is exceeded after add
     */
    public void addExpense(Expense expense) throws InvalidAmountException, BudgetExceededException {
        // Polymorphism: validate() is abstract in Expense; each subclass enforces its own rules
        expense.validate();

        expenses.add(expense);
        System.out.println("  ✔ Expense added: " + expense.getSummary());

        // Check the budget for this category if one was set
        String cat = expense.getCategory();
        if (budgets.containsKey(cat)) {
            Budget budget = budgets.get(cat);
            budget.addSpent(expense.getAmount());

            // Budget implements Alertable — checkAlert() prints a warning if near/over the limit
            ((Alertable) budget).checkAlert();
        }
    }

    /**
     * Delete an expense by its ID.
     *
     * @throws ExpenseNotFoundException if no expense with that ID exists
     */
    public void deleteExpense(String id) throws ExpenseNotFoundException {
        Expense target = findById(id); // throws if not found
        expenses.remove(target);
        System.out.println("  ✔ Expense removed: " + id);
    }

    /**
     * Edit the description and amount of an existing expense.
     *
     * @throws ExpenseNotFoundException if the ID doesn't exist
     * @throws InvalidAmountException   if the new amount is invalid
     */
    public void editExpense(String id, String newDescription, double newAmount)
            throws ExpenseNotFoundException, InvalidAmountException {

        if (newAmount <= 0) {
            throw new InvalidAmountException("Amount must be positive. Got: " + newAmount);
        }

        Expense target = findById(id);
        target.setDescription(newDescription);
        target.setAmount(newAmount);
        System.out.println("  ✔ Expense updated: " + id);
    }

    /**
     * Display all expenses to the console.
     * Uses polymorphism: getSummary() is overridden in each subclass.
     */
    public void listAll() {
        if (expenses.isEmpty()) {
            System.out.println("  No expenses recorded yet.");
            return;
        }
        System.out.println("\n  ── All expenses (" + expenses.size() + ") ───────────────────");
        for (Expense e : expenses) {
            // Polymorphism: the runtime type determines which getSummary() is called
            System.out.println("  " + e.getSummary());
        }
        System.out.println("  ─────────────────────────────────────────────\n");
    }

    // ── Filtering ────────────────────────────────────────────────────────────

    /**
     * Returns all expenses belonging to a given category.
     * String handling: case-insensitive comparison with toLowerCase().
     */
    public List<Expense> filterByCategory(String category) {
        // String: normalize to lowercase before comparing
        String target = category.trim().toLowerCase();

        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.getCategory().toLowerCase().equals(target)) {
                result.add(e);
            }
        }

        System.out.println("  Found " + result.size() + " expense(s) in category: " + category);
        return result;
    }

    /**
     * Returns all expenses recorded in a specific month/year.
     */
    public List<Expense> filterByMonth(int month, int year) {
        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            LocalDate d = e.getDate();
            if (d.getMonthValue() == month && d.getYear() == year) {
                result.add(e);
            }
        }
        System.out.println("  Found " + result.size() + " expense(s) for " + month + "/" + year);
        return result;
    }

    // ── Search ───────────────────────────────────────────────────────────────

    /**
     * Full-text keyword search across description, category, and subclass-specific fields.
     * String handling: toLowerCase() + contains() for case-insensitive matching.
     *
     * @param keyword search term (case-insensitive)
     * @return list of matching expenses
     */
    public List<Expense> search(String keyword) {
        // String: trim whitespace, convert to lowercase for comparison
        String kw = keyword.trim().toLowerCase();

        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            // String.contains() — core String class usage
            boolean descMatch     = e.getDescription().toLowerCase().contains(kw);
            boolean categoryMatch = e.getCategory().toLowerCase().contains(kw);
            // getSummary() is polymorphic — catches subclass-specific fields like artist/subject
            boolean summaryMatch  = e.getSummary().toLowerCase().contains(kw);

            if (descMatch || categoryMatch || summaryMatch) {
                result.add(e);
            }
        }

        System.out.println("  Search \"" + keyword + "\": " + result.size() + " result(s).");
        return result;
    }

    // ── Sorting ──────────────────────────────────────────────────────────────

    /**
     * Sorts the master expense list by date (ascending — oldest first).
     * Uses Collections.sort() with a lambda Comparator.
     */
    public void sortByDate() {
        Collections.sort(expenses, Comparator.comparing(Expense::getDate));
        System.out.println("  ✔ Expenses sorted by date.");
    }

    /**
     * Sorts the master expense list by amount (descending — most expensive first).
     */
    public void sortByAmount() {
        // Collections.sort() with reversed Comparator
        Collections.sort(expenses, Comparator.comparingDouble(Expense::getAmount).reversed());
        System.out.println("  ✔ Expenses sorted by amount (highest first).");
    }

    // ── Budget Management ────────────────────────────────────────────────────

    /**
     * Sets or replaces a monthly spending limit for a category.
     *
     * @param category  expense category name (e.g. "food", "transport")
     * @param limit     maximum amount in MAD for the month
     * @param month     target month (1–12)
     * @param year      target year
     */
    public void setBudget(String category, double limit, int month, int year) {
        // HashMap.put() — creates or replaces the budget for this category
        budgets.put(category.toLowerCase(), new Budget(category, limit, month, year));
        System.out.printf("  ✔ Budget set: %s → %.2f MAD (%d/%d)%n", category, limit, month, year);
    }

    /**
     * Displays all configured budgets and their current usage.
     */
    public void listBudgets() {
        if (budgets.isEmpty()) {
            System.out.println("  No budgets configured.");
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

    // ── Statistics ───────────────────────────────────────────────────────────

    /**
     * Calculates total spending for a given month.
     */
    public double totalForMonth(int month, int year) {
        double total = 0;
        for (Expense e : filterByMonth(month, year)) {
            total += e.getAmount();
        }
        return total;
    }

    /**
     * Finds the most expensive single expense.
     *
     * @throws ExpenseNotFoundException if the list is empty
     */
    public Expense mostExpensive() throws ExpenseNotFoundException {
        if (expenses.isEmpty()) {
            throw new ExpenseNotFoundException("No expenses to compare.");
        }
        // Collections.max() with amount comparator
        return Collections.max(expenses, Comparator.comparingDouble(Expense::getAmount));
    }

    // ── Accessors for FileManager (Member 4) ─────────────────────────────────

    /**
     * Returns the raw expense list.
     * Called by FileManager to persist all expenses to CSV.
     */
    public List<Expense> getAllExpenses() {
        return expenses;
    }

    /**
     * Replaces the entire expense list.
     * Called by FileManager after loading expenses from CSV on startup.
     */
    public void setExpenses(List<Expense> loaded) {
        this.expenses = loaded;
    }

    /**
     * Returns all configured budgets.
     * Called by FileManager to persist budgets.
     */
    public Map<String, Budget> getBudgets() {
        return budgets;
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    /**
     * Finds an expense by its ID.
     * String handling: trim() + equals() for reliable comparison.
     *
     * @throws ExpenseNotFoundException if no match is found
     */
    private Expense findById(String id) throws ExpenseNotFoundException {
        String target = id.trim(); // String.trim() to remove accidental whitespace
        for (Expense e : expenses) {
            if (e.getId().equals(target)) {
                return e;
            }
        }
        // Custom exception — not a generic RuntimeException
        throw new ExpenseNotFoundException("No expense found with ID: " + id);
    }
}