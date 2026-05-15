# UniWallet
### Student Budget Tracker

Application console Java développée dans le cadre du cours de **Programmation Orientée Objet**.  
Elle permet à un étudiant de suivre ses dépenses quotidiennes par catégorie, de définir des budgets mensuels, de recevoir des alertes en cas de dépassement, et d'exporter des rapports mensuels.

---

## Contexte

Projet réalisé en réponse à la fiche projet POO développement d'une application console Java sans interface graphique, mettant en œuvre les concepts fondamentaux de la POO : héritage, polymorphisme, interfaces, exceptions personnalisées, collections et persistance fichier.

---

## Diagrammes UML

![Class Diagram](./UML_diagrammes/CLASS%20-%20Uniwallet.png)

<details>
<summary>📋 Use Case</summary>

![Use Case](./UML_diagrammes/USE%20CASE.png)

</details>

<details>
<summary>🔄 Sequence Diagrams</summary>

![Ajouter une dépense](./UML_diagrammes/SEQUENCE%20-%20Ajouter%20une%20depense.png)
![Exporter le rapport](./UML_diagrammes/SEQUENCE%20-%20Exporter%20le%20rapport.png)

</details>

<details>
<summary>⚡ Activity Diagrams</summary>

![Ajouter une dépense](./UML_diagrammes/ACTIVITE%20-%20Ajouter%20une%20depense.png)
![Générer le rapport](./UML_diagrammes/ACTIVITE%20-%20Generer%20le%20rapport.png)

</details>

<details>
<summary>🔁 State Diagrams</summary>

![Budget](./UML_diagrammes/ETAT%20-%20budget.png)
![Dépense](./UML_diagrammes/ETAT%20-%20depense.png)

</details>
---

## Environnement de développement

| Outil | Version / Détail |
|-------|-----------------|
| **Java** | JDK 14 |
| **IDE** | IntelliJ IDEA · Visual Studio Code |

---

## Architecture

Le projet suit une architecture modulaire avec séparation claire des responsabilités :

```
src/
├── model/
│   ├── Expense.java                  ← classe abstraite
│   ├── FoodExpense.java
│   ├── TransportExpense.java
│   ├── EntertainmentExpense.java
│   ├── OtherExpense.java
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
└── App.java                          ← Point d'entrée principal : [main]
    
```

---

## Concepts POO utilisés

### Héritage & Polymorphisme
`Expense` est une classe abstraite. `FoodExpense`, `TransportExpense`, `EntertainmentExpense` et `OtherExpense` en héritent et redéfinissent chacune `getSummary()`, `validate()` et `toCSV()`. L'`ExpenseManager` manipule toutes ces sous-classes via le type `Expense` polymorphisme pur.

### Classes abstraites
`Expense` regroupe les champs communs (`id` : int, `amount`, `date`, `description`, `category`) et déclare les méthodes abstraites que chaque sous-classe est obligée d'implémenter : `validate()`, `getSummary()`, `toCSV()` et `toString()`.

### Interfaces
- **`Alertable`** implémentée par `Budget`. Définit `checkAlert()`.
- **`Exportable`** implémentée par `ReportExporter`. Définit `exportToFile(String path)` pour la génération de rapports.

### Exceptions personnalisées

| Exception | Type | Déclenchée quand |
|-----------|------|-----------------|
| `BudgetExceededException` | Checked (`Exception`) | Les dépenses dépassent la limite |
| `InvalidAmountException` | Unchecked (`RuntimeException`) | Montant invalide (≤ 0 ou hors plage) |
| `ExpenseNotFoundException` | Unchecked (`RuntimeException`) | ID introuvable lors d'un edit/delete |
| `FileIOException` | Unchecked (`RuntimeException`) | Erreur de lecture/écriture fichier |

### Collections
`ExpenseManager` utilise:
- `List<Expense>` pour stocker les dépenses.
- `Map<String, Budget>` (clé = catégorie) pour un accès instantané aux budgets.

### Persistance fichier
`FileManager` lit et écrit trois fichiers CSV (`expenses.csv`, `budgets.csv`, `students.csv`) via `BufferedReader` et `PrintWriter`. Chaque entité sait se sérialiser via `toCSV()` et se reconstruire via `fromCSV()`.

### Traitement de chaînes
- `String.split(",")` et `String.trim()` pour parser les CSV
- `String.format()` pour l'alignement des colonnes dans les rapports
- `String.toLowerCase().contains()` pour la recherche insensible à la casse

---

## Détail des classes

### Modèle (`model/`)

**`Expense` (abstraite)**
- Champs : 
`#id : int`, 
`#amount : double`, 
`#date : String`, 
`#description : String`, 
`#category : String`
- Méthodes abstraites : 
`+validate() : void`, 
`+getSummary() : String`, 
`+toCSV() : String`, 
`+toString() : String`

**`FoodExpense`**
- Champs supplémentaires : 
`-mealType : String`, 
`-isRestaurant : boolean`, 
`-maxLimit : double`
- Méthodes : 
`getMealType()`, 
`setMealType()`,
`getRestaurant()`,
`setRestaurant()`,
`getMaxLimit()`,
`setMaxLimit()`,
`validate()`,
`getSummary()`,
`toCSV()`

**`TransportExpense`**
- Champs supplémentaires : 
`-transportMode : String`, 
`-taxiLimit : double`, 
`-fuelLimit : double`, 
`-publicTransportLimit : double`, 
`-otherLimit : double`
- Méthodes : 
`getTransportMode()`, 
`setTransportMode()`, 
`getTaxiLimit()`, 
`setTaxiLimit()`, 
`getFuelLimit()`, 
`setFuelLimit()`, 
`getPublicTransportLimit()`, 
`setPublicTransportLimit()`, 
`getOtherLimit()`, 
`setOtherLimit()`, 
`validate()`, 
`getSummary()`, 
`toCSV()`, 
`getLimitForMode(mode: String)`

**`EntertainmentExpense`**
- Champs supplémentaires : 
`-activityType : String`, 
`-maxLimit : double`
- Méthodes : 
`getActivityType()`, 
`setActivityType()`, 
`getMaxLimit()`, 
`setMaxLimit()`, 
`validate()`, 
`getSummary()`, 
`toCSV()`

**`OtherExpense`**
- Champs supplémentaires : `-subType : String`, `-maxLimit : double`
- Méthodes : `getSubType()`, `setSubType()`, `getMaxLimit()`, `setMaxLimit()`, `validate()`, `getSummary()`, `toCSV()`

**`Budget` (implements `Alertable`)**
- Champs : `-category : String`, `-limit : double`, `-month : String`, `-currentSpending : double`
- Méthodes : `getCategory()`, `setCategory()`, `getLimit()`, `setLimit()`, `getCurrentSpending()`, `setCurrentSpending()`, `getMonth()`, `setMonth()`, `addSpent(amount: double)`, `getSpent()`, `checkAlert()`, `toCSV()`, `fromCSV(line: String) : Budget`

**`Student`**
- Champs : `-id : String`, `-name : String`, `-email : String`, `-monthlyIncome : double`
- Méthodes : `getId()`, `getName()`, `setName()`, `getEmail()`, `setEmail()`, `getMonthlyIncome()`, `setMonthlyIncome()`, `toCSV()`, `fromCSV(line: String) : Student`, `toString()`

### Service (`service/`)

**`ExpenseManager`**
- Champs : `-expenses : List<Expense>`, `-budgets : Map<String, Budget>`
- Méthodes : `addExpense(expense: Expense)`, `deleteExpense(id: String)`, `editExpenseId(id: String, newDescription: String, newAmount: double)`, `listAll()`, `filterByCategory(category: String) : List<Expense>`, `filterByMonth(month: String, year: int) : List<Expense>`, `searchKeyword(keyword: String) : List<Expense>`, `sortByDate()`, `sortByAmount()`, `setBudget(category: String, limit: double, month: int, year: int)`, `listBudgets()`, `totalForMonth(month: int, year: int) : double`, `mostExpensive() : Expense`, `getAllExpenses() : List<Expense>`, `setExpenses(loaded: List<Expense>)`, `getBudgets() : Map<String, Budget>`, `findById(id: String) : Expense`

### Persistance (`persistence/`)

**`FileManager`**
- Méthodes : `saveExpenses(expenses: List<Expense>, path: String)`, `loadExpenses(path: String) : List<Expense>`, `saveBudgets(budgets: Map<String, Budget>, path: String)`, `loadBudgets(path: String) : Map<String, Budget>`, `saveStudent(student: Student, path: String)`, `loadStudent(path: String) : Student`, `ensureParentDirectory(file: File)`

### Export (`export/`)

**`ReportExporter` (implements `Exportable`)**
- Champs : `-manager : ExpenseManager`
- Méthodes : `ReportExporter(manager: ExpenseManager)`, `exportToFile(path: String)`, `formatRow(expense: Expense) : String`, `generateMonthlyReport(month: int, year: int, path: String)`, `printToConsole(list: List<Expense>)`, `writeReport(expenses: List<Expense>, writer: PrintWriter)`, `ensureParentDirectory(file: File)`

### Point d'entrée (`app/`)

**`App`** — classe principale contenant le `main`
- Lance la boucle de navigation principale entre les différents modules.
- Gère l'affichage console côte à côte (menu + logo ASCII).
- Délègue chaque action aux méthodes de service et de persistance correspondantes.

---

## Fonctionnalités

- Ajouter, modifier, supprimer et afficher des dépenses
- Filtrer par catégorie ou par mois
- Rechercher par mot-clé dans la description
- Trier les dépenses par date ou par montant
- Définir un budget mensuel par catégorie
- Recevoir une alerte à 80% du budget, exception à 100%
- Exporter un rapport mensuel formaté dans un fichier texte
- Sauvegarde et chargement automatiques au démarrage/fermeture

---

## Lancer l'application

```bash
cd src/ 
java App.java

```

> **Requis :** JDK 14 ou supérieur.

---

## Répartition du travail

| Membre | Couche | Fichiers |
|--------|--------|----------|
| 1 | Modèle | `Expense.java` + 4 sous-classes (`FoodExpense`, `TransportExpense`, `EntertainmentExpense`, `OtherExpense`) |
| 2 | Interfaces & Exceptions | `Alertable`, `Exportable`, `Budget`, 4 exceptions |
| 3 | Service | `ExpenseManager`, `Student` |
| 4 | Persistance & Export | `FileManager`, `ReportExporter` |
| 5 | Point d'entrée | `App.java` |

---

## Credits

> *Elaboré par:*
>
> - **Ahrabar Ahmed monir** — `@Ahmedahrabar-hub`
> - **Faiz Adnane** — `@Mazicus`
> - **Sbia Youness** — `@Lamprofony`
> - **Telouani Amine** — `@atel12345`
> - **Toukam Abderrahman** — `@beeOverflow`

---

## Diagramme UML

Le diagramme de classes est disponible à la racine du projet : `diagramme_de_classe.png`
<img width="2895" height="1499" alt="Diag_class_StudentBudgetTracker" src="https://github.com/user-attachments/assets/da50b561-0b66-4728-ab1c-b5b63f39c845" />
