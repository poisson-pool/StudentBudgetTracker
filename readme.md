# Student Budget Tracker

Application console Java développée dans le cadre du cours de **Programmation Orientée Objet**.  
Elle permet à un étudiant de suivre ses dépenses quotidiennes par catégorie, de définir des budgets mensuels, de recevoir des alertes en cas de dépassement, et d'exporter des rapports mensuels.

---

## Contexte

Projet réalisé en réponse à la fiche projet POO — développement d'une application console Java sans interface graphique, mettant en œuvre les concepts fondamentaux de la POO : héritage, polymorphisme, interfaces, exceptions personnalisées, collections et persistance fichier.

---

## Architecture

Le projet suit une architecture modulaire avec séparation claire des responsabilités :

```
src/
├── model/
│   ├── Expense.java                  ← classe abstraite
│   ├── FoodExpense.java
│   ├── TransportExpense.java
│   ├── AcademicExpense.java
│   ├── EntertainmentExpense.java
│   ├── Budget.java                   ← implements Alertable
│   └── Student.java
├── interfaces/
│   ├── Alertable.java
│   └── Exportable.java
├── exceptions/
│   ├── BudgetExceededException.java
│   ├── ExpenseNotFoundException.java
│   ├── InvalidAmountException.java
│   └── FileIOException.java
├── service/
│   └── ExpenseManager.java
├── persistence/
│   └── FileManager.java
├── export/
│   └── ReportExporter.java           ← implements Exportable
└── Main.java
```

---

## Concepts POO utilisés

### Héritage & Polymorphisme
`Expense` est une classe abstraite. `FoodExpense`, `TransportExpense`, `AcademicExpense` et `EntertainmentExpense` en héritent et redéfinissent chacune `getSummary()`, `validate()` et `toCSV()`. L'`ExpenseManager` manipule toutes ces sous-classes via le type `Expense` — polymorphisme pur.

### Classes abstraites
`Expense` regroupe les champs communs (`id`, `amount`, `date`, `description`, `category`) et déclare les méthodes abstraites que chaque sous-classe est obligée d'implémenter.

### Interfaces
- **`Alertable`** — implémentée par `Budget`. Définit `checkAlert()` : avertissement à 80% du budget, exception à 100%.
- **`Exportable`** — implémentée par `ReportExporter`. Définit `exportToFile(String path)` pour la génération de rapports.

### Exceptions personnalisées

| Exception | Type | Déclenchée quand |
|-----------|------|-----------------|
| `BudgetExceededException` | Checked | Les dépenses dépassent la limite |
| `InvalidAmountException` | Checked | Montant invalide (≤ 0 ou hors plage) |
| `ExpenseNotFoundException` | Unchecked | ID introuvable lors d'un edit/delete |
| `FileIOException` | Unchecked | Erreur de lecture/écriture fichier |

### Collections
`ExpenseManager` utilise une `List<Expense>` pour stocker les dépenses et une `Map<String, Budget>` (clé = catégorie) pour un accès instantané aux budgets.

### Persistance fichier
`FileManager` lit et écrit trois fichiers CSV (`expenses.csv`, `budgets.csv`, `students.csv`) via `BufferedReader` et `PrintWriter`. Chaque entité sait se sérialiser via `toCSV()` et se reconstruire via `fromCSV()`.

### Traitement de chaînes
- `String.split(",")` et `String.trim()` pour parser les CSV
- `String.format()` pour l'alignement des colonnes dans les rapports
- `String.toLowerCase().contains()` pour la recherche insensible à la casse

---

## Fonctionnalités

- Ajouter, modifier, supprimer et afficher des dépenses
- Filtrer par catégorie ou par mois
- Rechercher par mot-clé dans la description
- Définir un budget mensuel par catégorie
- Recevoir une alerte à 80% du budget, exception à 100%
- Exporter un rapport mensuel formaté dans un fichier texte
- Sauvegarde et chargement automatiques au démarrage/fermeture

---

## Répartition du travail

| Membre | Couche | Fichiers |
|--------|--------|----------|
| 1 | Modèle | `Expense.java` + 4 sous-classes |
| 2 | Interfaces & Exceptions | `Alertable`, `Exportable`, `Budget`, 4 exceptions |
| 3 | Service | `ExpenseManager`, `Student` |
| 4 | Persistance & Export | `FileManager`, `ReportExporter` |
| 5 | Point d'entrée | `Main.java` |

---

## Lancer l'application

```bash
javac -d out src/**/*.java
java -cp out Main
```

---

## 📄 Diagramme UML

Le diagramme de classes est disponible à la racine du projet : `diagramme_de_classe.png`
![Diagramme de classes](src/diagramme_de_classe.png)
