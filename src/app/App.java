package app;

import exceptions.BudgetExceededException;
import exceptions.ExpenseNotFoundException;
import exceptions.FileIOException;
import exceptions.InvalidAmountException;
import export.ReportExporter;
import model.EntertainmentExpense;
import model.Expense;
import model.FoodExpense;
import model.OtherExpense;
import model.Student;
import model.TransportExpense;
import persistence.FileManager;
import service.ExpenseManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {

    // Parametres d'affichage de l'interface console.
    private static final int MENU_WIDTH = 56;
    private static final int COLUMN_GAP = 6;
    private static final String TITLE_COLOR = "\033[1;33m";
    private static final String RESET = "\033[0m";
    private static final String[] WALLET_ART = new String[] {
        "        -=",
        "      -******",
        "   -***********",
        " -****************",
        "+******************",
        "+******************",
        "+*****        *****+",
        "+*****        *****+",
        "+******************",
        " :................:"
    };
    private static final String[] NAME_ART = new String[] {
        "   __  __      _ _       __      ____     __ ",
        "  / / / /___  (_) |     / /___ _/ / /__  / /_",
        " / / / / __ \\/ /| | /| / / __ `/ / / _ \\/ __/",
        "/ /_/ / / / / / | |/ |/ / /_/ / / /  __/ /_  ",
        "\\____/_/ /_/_/  |__/|__/\\__,_/_/_/\\___/\\__/  "
    };
    private static final int LOGO_COLOR_LINES = WALLET_ART.length + 1 + NAME_ART.length;

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {
        // Boucle principale de navigation entre les differents modules.
        Scanner scanner = new Scanner(System.in);
        ExpenseManager manager = new ExpenseManager();
        FileManager fileManager = new FileManager();
        ReportExporter exporter = new ReportExporter(manager);
        Student student = null;
        int nextExpenseId = 1;
        boolean quitter = false;

        while (!quitter) {
            clearScreen();
            printMainMenu();

            int choix = readInt(scanner, "    Votre choix: ", 1, 6);

            switch (choix) {
                case 1 -> nextExpenseId = menuDepenses(scanner, manager, nextExpenseId);
                case 2 -> menuBudgets(scanner, manager);
                case 3 -> menuRapports(scanner, manager, exporter);
                case 4 -> nextExpenseId = menuDonnees(scanner, manager, fileManager, nextExpenseId);
                case 5 -> student = menuEtudiant(scanner, fileManager, student);
                case 6 -> quitter = true;
                default -> System.out.println("\tChoix invalide.");
            }

        }

        clearScreen();
        System.out.println("\033[32mAu revoir!\033[0m");
        scanner.close();
    }

    private static String[] buildLogoLines() {
        // Construit le bloc logo/titre/auteurs affiche dans la colonne droite.
        List<String> lines = new ArrayList<>();
        int width = 0;
        for (String line : WALLET_ART) {
            width = Math.max(width, visibleWidth(line));
        }
        for (String line : NAME_ART) {
            width = Math.max(width, visibleWidth(line));
        }

        for (String line : WALLET_ART) {
            lines.add(center(line, width));
        }
        lines.add("");
        int nameWidth = 0;
        for (String line : NAME_ART) {
            nameWidth = Math.max(nameWidth, visibleWidth(line));
        }
        for (String line : NAME_ART) {
            lines.add(center(line, width));
        }
        lines.add("");
        lines.add(center("A STUDENT BUDGET TRACKER", nameWidth));
        lines.add("");
        return lines.toArray(new String[0]);
    }

    private static void showMenu(String title, String[] options) {
        // Assemble puis affiche menu (gauche) et logo (droite) cote a cote.
        String[] logo = buildLogoLines();
        String[] menu = buildMenuLines(title, options);
        printSideBySide(menu, logo);
    }

    private static String center(String text, int width) {
        int textWidth = visibleWidth(text);
        if (textWidth >= width) {
            return text;
        }
        int left = (width - textWidth) / 2;
        int right = width - textWidth - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }

    private static void printMainMenu() {
        showMenu("MENU PRINCIPAL", new String[] {
            "Gestion des depenses",
            "Gestion des budgets",
            "Rapports et export",
            "Sauvegarde et chargement",
            "Profil etudiant",
            "Quitter"
        });
    }

    private static String[] buildMenuLines(String title, String[] options) {
        // Genere le cadre du menu avec son titre et ses options.
        List<String> lines = new ArrayList<>();
        String line = "-".repeat(MENU_WIDTH);
        lines.add(line);
        lines.add(center(title, MENU_WIDTH));
        lines.add(line);
        lines.add("");
        for (int i = 0; i < options.length; i++) {
            String opt = String.format("[%d] %s", i + 1, options[i]);
            // Ajoute une petite indentation a gauche pour aligner visuellement les options et l'invite.
            lines.add("    " + opt);
            if (i < options.length - 1) {
                lines.add("");
            }
        }
        lines.add("");
        return lines.toArray(new String[0]);
    }

    private static String centerLeft(String text, int width) {
        int textWidth = visibleWidth(text);
        if (textWidth >= width) {
            return text;
        }
        int left = (width - textWidth) / 2;
        return " ".repeat(left) + text;
    }

    private static void printSideBySide(String[] left, String[] right) {
        // Centre verticalement le menu de gauche par rapport au logo de droite.
        // Force la colonne gauche a MENU_WIDTH pour aligner correctement les options et l'invite.
        int leftWidth = MENU_WIDTH;
        int total = Math.max(left.length, right.length);
        int leftStart = (total - left.length) / 2;
        int rightStart = (total - right.length) / 2;
        String gap = " ".repeat(COLUMN_GAP);
        for (int i = 0; i < total; i++) {
            int leftIndex = i - leftStart;
            int rightIndex = i - rightStart;
            String leftLine = (leftIndex >= 0 && leftIndex < left.length) ? rstrip(left[leftIndex]) : "";
            String rightLine = (rightIndex >= 0 && rightIndex < right.length) ? rstrip(right[rightIndex]) : "";
            String paddedLeft = padRight(leftLine, leftWidth);

            // Applique la couleur du titre/logo a la colonne droite (le logo est a droite).
            if (rightIndex >= 0 && rightIndex < LOGO_COLOR_LINES && !rightLine.isEmpty()) {
                rightLine = TITLE_COLOR + rightLine + RESET;
            }

            System.out.println(paddedLeft + gap + rightLine);
        }
        // Fin de l'affichage cote a cote.
    }

    private static int visibleWidth(String text) {
        return stripAnsi(rstrip(text)).length();
    }

    private static String stripAnsi(String text) {
        return text.replaceAll("\\u001B\\[[;\\d]*m", "");
    }

    private static String rstrip(String text) {
        int end = text.length();
        while (end > 0 && text.charAt(end - 1) == ' ') {
            end--;
        }
        return text.substring(0, end);
    }

    private static String padRight(String text, int width) {
        int pad = width - visibleWidth(text);
        if (pad <= 0) {
            return text;
        }
        return text + " ".repeat(pad);
    }

    private static int menuDepenses(Scanner scanner, ExpenseManager manager, int nextExpenseId) {
        // Sous-menu de gestion des depenses.
        boolean retour = false;

        while (!retour) {
            clearScreen();
            showMenu("MENU DEPENSES", new String[] {
                "Ajouter une depense",
                "Modifier une depense",
                "Supprimer une depense",
                "Lister toutes les depenses",
                "Filtrer les depenses",
                "Rechercher",
                "Trier",
                "Statistiques",
                "Retour"
            });

            int choix = readInt(scanner, "    Votre choix: ", 1, 9);

            switch (choix) {
                case 1 -> nextExpenseId = ajouterDepense(scanner, manager, nextExpenseId);
                case 2 -> modifierDepense(scanner, manager);
                case 3 -> supprimerDepense(scanner, manager);
                case 4 -> manager.listAll();
                case 5 -> filtrerDepenses(scanner, manager);
                case 6 -> rechercherDepenses(scanner, manager);
                case 7 -> trierDepenses(scanner, manager);
                case 8 -> statistiques(scanner, manager);
                case 9 -> retour = true;
                default -> System.out.println("\tChoix invalide.");
            }

            if (!retour) {
                pause(scanner);
            }
        }

        return nextExpenseId;
    }

    private static void menuBudgets(Scanner scanner, ExpenseManager manager) {
        // Sous-menu de gestion des budgets.
        boolean retour = false;

        while (!retour) {
            clearScreen();
            showMenu("MENU BUDGETS", new String[] {
                "Definir un budget",
                "Lister les budgets",
                "Retour"
            });

            int choix = readInt(scanner, "    Votre choix: ", 1, 3);

            switch (choix) {
                case 1 -> definirBudget(scanner, manager);
                case 2 -> manager.listBudgets();
                case 3 -> retour = true;
                default -> System.out.println("\tChoix invalide.");
            }

            if (!retour) {
                pause(scanner);
            }
        }
    }

    private static void menuRapports(Scanner scanner, ExpenseManager manager, ReportExporter exporter) {
        // Sous-menu de consultation et d'export des rapports.
        boolean retour = false;

        while (!retour) {
            clearScreen();
            showMenu("MENU RAPPORTS", new String[] {
                "Afficher le rapport complet",
                "Exporter le rapport complet",
                "Exporter un rapport mensuel",
                "Retour"
            });

            int choix = readInt(scanner, "    Votre choix: ", 1, 4);

            switch (choix) {
                case 1 -> exporter.printToConsole(manager.getAllExpenses());
                case 2 -> exporterRapport(scanner, exporter);
                case 3 -> exporterRapportMensuel(scanner, exporter);
                case 4 -> retour = true;
                default -> System.out.println("\tChoix invalide.");
            }

            if (!retour) {
                pause(scanner);
            }
        }
    }

    private static int menuDonnees(Scanner scanner, ExpenseManager manager, FileManager fileManager, int nextExpenseId) {
        // Sous-menu de sauvegarde et chargement des donnees.
        boolean retour = false;

        while (!retour) {
            clearScreen();
            showMenu("MENU SAUVEGARDE", new String[] {
                "Sauvegarder les depenses",
                "Charger les depenses",
                "Sauvegarder les budgets",
                "Charger les budgets",
                "Retour"
            });

            int choix = readInt(scanner, "    Votre choix: ", 1, 5);

            switch (choix) {
                case 1 -> sauvegarderDepenses(scanner, manager, fileManager);
                case 2 -> {
                    chargerDepenses(scanner, manager, fileManager);
                    nextExpenseId = updateNextId(manager, nextExpenseId);
                }
                case 3 -> sauvegarderBudgets(scanner, manager, fileManager);
                case 4 -> chargerBudgets(scanner, manager, fileManager);
                case 5 -> retour = true;
                default -> System.out.println("\tChoix invalide.");
            }

            if (!retour) {
                pause(scanner);
            }
        }

        return nextExpenseId;
    }

    private static Student menuEtudiant(Scanner scanner, FileManager fileManager, Student student) {
        // Sous-menu de creation, affichage et persistance du profil etudiant.
        boolean retour = false;
        Student current = student;

        while (!retour) {
            clearScreen();
            showMenu("MENU ETUDIANT", new String[] {
                "Creer ou modifier le profil",
                "Afficher le profil",
                "Sauvegarder le profil",
                "Charger le profil",
                "Retour"
            });

            int choix = readInt(scanner, "    Votre choix: ", 1, 5);

            switch (choix) {
                case 1 -> current = creerOuModifierEtudiant(scanner, current);
                case 2 -> afficherEtudiant(current);
                case 3 -> sauvegarderEtudiant(scanner, fileManager, current);
                case 4 -> current = chargerEtudiant(scanner, fileManager);
                case 5 -> retour = true;
                default -> System.out.println("\tChoix invalide.");
            }

            if (!retour) {
                pause(scanner);
            }
        }

        return current;
    }

    private static int ajouterDepense(Scanner scanner, ExpenseManager manager, int nextExpenseId) {
        clearScreen();
        showMenu("TYPE DE DEPENSE", new String[] {
            "Nourriture",
            "Transport",
            "Loisirs",
            "Autre",
            "Retour"
        });

        int choix = readInt(scanner, "    Votre choix: ", 1, 5);
        if (choix == 5) {
            return nextExpenseId;
        }

        double amount = readDouble(scanner, "\tMontant (MAD): ");
        String date = readDate(scanner, "\tDate (YYYY-MM-DD): ");
        String description = readNonEmpty(scanner, "\tDescription: ");

        try {
            Expense expense;
            switch (choix) {
                case 1 -> {
                    String mealType = readNonEmpty(scanner, "\tType de repas: ");
                    boolean isRestaurant = readYesNo(scanner, "\tRestaurant (o/n): ");
                    expense = new FoodExpense(nextExpenseId, amount, date, description, "nourriture", mealType, isRestaurant);
                }
                case 2 -> {
                    String transportMode = readNonEmpty(scanner, "\tMode de transport: ");
                    expense = new TransportExpense(nextExpenseId, amount, date, description, "transport", transportMode);
                }
                case 3 -> {
                    String activityType = readNonEmpty(scanner, "\tType d'activite: ");
                    expense = new EntertainmentExpense(nextExpenseId, amount, date, description, "loisirs", activityType);
                }
                case 4 -> {
                    String subType = readNonEmpty(scanner, "\tSous-type: ");
                    expense = new OtherExpense(nextExpenseId, amount, date, description, "autre", subType);
                }
                default -> {
                    System.out.println("\tChoix invalide.");
                    return nextExpenseId;
                }
            }

            manager.addExpense(expense);
            return nextExpenseId + 1;
        } catch (InvalidAmountException | BudgetExceededException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
            return nextExpenseId;
        }
    }

    private static void modifierDepense(Scanner scanner, ExpenseManager manager) {
        String id = readNonEmpty(scanner, "\tID de la depense: ");
        String description = readNonEmpty(scanner, "\tNouvelle description: ");
        double amount = readDouble(scanner, "\tNouveau montant (MAD): ");

        try {
            manager.editExpense(id, description, amount);
        } catch (InvalidAmountException | ExpenseNotFoundException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static void supprimerDepense(Scanner scanner, ExpenseManager manager) {
        String id = readNonEmpty(scanner, "\tID de la depense: ");
        try {
            manager.deleteExpense(id);
        } catch (ExpenseNotFoundException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static void filtrerDepenses(Scanner scanner, ExpenseManager manager) {
        clearScreen();
        showMenu("FILTRER DEPENSES", new String[] {
            "Filtrer par categorie",
            "Filtrer par mois",
            "Retour"
        });

        int choix = readInt(scanner, "    Votre choix: ", 1, 3);
        if (choix == 3) {
            return;
        }

        if (choix == 1) {
            String category = readNonEmpty(scanner, "\tCategorie: ");
            List<Expense> result = manager.filterByCategory(category);
            printExpenseList(result);
        } else if (choix == 2) {
            int month = readInt(scanner, "\tMois (1-12): ", 1, 12);
            int year = readInt(scanner, "\tAnnee (ex: 2026): ", 2000, 2100);
            List<Expense> result = manager.filterByMonth(month, year);
            printExpenseList(result);
        }
    }

    private static void rechercherDepenses(Scanner scanner, ExpenseManager manager) {
        String keyword = readNonEmpty(scanner, "\tMot-cle: ");
        List<Expense> result = manager.search(keyword);
        printExpenseList(result);
    }

    private static void trierDepenses(Scanner scanner, ExpenseManager manager) {
        clearScreen();
        showMenu("TRIER DEPENSES", new String[] {
            "Trier par date",
            "Trier par montant",
            "Retour"
        });

        int choix = readInt(scanner, "    Votre choix: ", 1, 3);
        if (choix == 1) {
            manager.sortByDate();
        } else if (choix == 2) {
            manager.sortByAmount();
        }
    }

    private static void statistiques(Scanner scanner, ExpenseManager manager) {
        int month = readInt(scanner, "\tMois (1-12): ", 1, 12);
        int year = readInt(scanner, "\tAnnee (ex: 2026): ", 2000, 2100);
        double total = manager.totalForMonth(month, year);
        System.out.printf("\tTotal %02d/%d: %.2f MAD%n", month, year, total);

        try {
            Expense mostExpensive = manager.mostExpensive();
            System.out.println("\tDepense la plus chere: " + mostExpensive.getSummary());
        } catch (ExpenseNotFoundException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static void definirBudget(Scanner scanner, ExpenseManager manager) {
        String category = readNonEmpty(scanner, "\tCategorie: ");
        double limit = readDouble(scanner, "\tLimite (MAD): ");
        int month = readInt(scanner, "\tMois (1-12): ", 1, 12);
        int year = readInt(scanner, "\tAnnee (ex: 2026): ", 2000, 2100);
        manager.setBudget(category, limit, month, year);
    }

    private static void exporterRapport(Scanner scanner, ReportExporter exporter) {
        String path = readNonEmpty(scanner, "\tChemin du fichier: ");
        try {
            exporter.exportToFile(path);
            System.out.println("\tRapport exporte.");
        } catch (FileIOException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static void exporterRapportMensuel(Scanner scanner, ReportExporter exporter) {
        int month = readInt(scanner, "\tMois (1-12): ", 1, 12);
        int year = readInt(scanner, "\tAnnee (ex: 2026): ", 2000, 2100);
        String path = readNonEmpty(scanner, "\tChemin du fichier: ");
        try {
            exporter.generateMonthlyReport(month, year, path);
            System.out.println("\tRapport mensuel exporte.");
        } catch (FileIOException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static void sauvegarderDepenses(Scanner scanner, ExpenseManager manager, FileManager fileManager) {
        String path = readNonEmpty(scanner, "\tChemin du fichier: ");
        try {
            fileManager.saveExpenses(manager.getAllExpenses(), path);
            System.out.println("\tDepenses sauvegardees.");
        } catch (FileIOException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static void chargerDepenses(Scanner scanner, ExpenseManager manager, FileManager fileManager) {
        String path = readNonEmpty(scanner, "\tChemin du fichier: ");
        try {
            manager.setExpenses(fileManager.loadExpenses(path));
            System.out.println("\tDepenses chargees.");
        } catch (FileIOException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static void sauvegarderBudgets(Scanner scanner, ExpenseManager manager, FileManager fileManager) {
        String path = readNonEmpty(scanner, "\tChemin du fichier: ");
        try {
            fileManager.saveBudgets(manager.getBudgets(), path);
            System.out.println("\tBudgets sauvegardes.");
        } catch (FileIOException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static void chargerBudgets(Scanner scanner, ExpenseManager manager, FileManager fileManager) {
        String path = readNonEmpty(scanner, "\tChemin du fichier: ");
        try {
            Map<String, model.Budget> loaded = fileManager.loadBudgets(path);
            manager.getBudgets().clear();
            manager.getBudgets().putAll(loaded);
            System.out.println("\tBudgets charges.");
        } catch (FileIOException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static Student creerOuModifierEtudiant(Scanner scanner, Student current) {
        String id = readNonEmpty(scanner, "\tID etudiant: ");
        String name = readNonEmpty(scanner, "\tNom: ");
        String email = readNonEmpty(scanner, "\tEmail: ");
        double income = readDouble(scanner, "\tRevenu mensuel (MAD): ");
        return new Student(id, name, email, income);
    }

    private static void afficherEtudiant(Student student) {
        if (student == null) {
            System.out.println("\tAucun profil etudiant.");
            return;
        }
        System.out.println("\t" + student);
    }

    private static void sauvegarderEtudiant(Scanner scanner, FileManager fileManager, Student student) {
        if (student == null) {
            System.out.println("\tAucun profil a sauvegarder.");
            return;
        }
        String path = readNonEmpty(scanner, "\tChemin du fichier: ");
        try {
            fileManager.saveStudent(student, path);
            System.out.println("\tProfil sauvegarde.");
        } catch (FileIOException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
        }
    }

    private static Student chargerEtudiant(Scanner scanner, FileManager fileManager) {
        String path = readNonEmpty(scanner, "\tChemin du fichier: ");
        try {
            Student student = fileManager.loadStudent(path);
            if (student == null) {
                System.out.println("\tAucun profil trouve.");
            } else {
                System.out.println("\tProfil charge.");
            }
            return student;
        } catch (FileIOException ex) {
            System.out.println("\tErreur: " + ex.getMessage());
            return null;
        }
    }

    private static int updateNextId(ExpenseManager manager, int fallback) {
        // Recalcule le prochain identifiant apres un chargement de donnees.
        int max = 0;
        for (Expense e : manager.getAllExpenses()) {
            if (e.getId() > max) {
                max = e.getId();
            }
        }
        return Math.max(1, max + 1);
    }

    private static void printExpenseList(List<Expense> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("\tAucune depense.");
            return;
        }
        for (Expense e : list) {
            System.out.println("\t" + e.getSummary());
        }
    }

    private static void pause(Scanner scanner) {
        System.out.print("\n\tAppuyez sur Entree pour continuer...");
        scanner.nextLine();
    }

    private static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("\tValeur vide.");
        }
    }

    private static int readInt(Scanner scanner, int min, int max) {
        // Lecture d'un choix numerique deja affiche dans le menu.
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("\tChoix invalide.");
                    continue;
                }
                System.out.println();
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("\tEntrez un nombre valide.");
            }
        }
    }

    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        // Lecture d'un choix numerique avec invite affichant le curseur juste apres le prompt.
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("\tChoix invalide.");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("\tEntrez un nombre valide.");
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.println("\tEntrez un nombre valide.");
            }
        }
    }

    private static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("o") || input.equals("oui") || input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("non") || input.equals("no")) {
                return false;
            }
            System.out.println("\tReponse invalide.");
        }
    }

    private static String readDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                LocalDate.parse(input);
                return input;
            } catch (Exception ex) {
                System.out.println("\tDate invalide (YYYY-MM-DD).");
            }
        }
    }
}
