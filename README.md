
# Library Management System

A desktop Library Management System built with Java Swing and MySQL.  
Developed as a DBMS project demonstrating JDBC-driven workflows and a clean separation between UI and database logic.

---

## Overview

Manage books, borrowers, categories, and checkouts via a Java Swing GUI backed by MySQL.  
Supports issuing/returning books, availability tracking, overdue detection, and borrower fine management.

This project emphasizes practical use of relational databases, SQL queries, and JDBC connectivity.

---

## Key Features

- Admin authentication and tabbed dashboard  
- Add / edit / delete / view books and categories  
- Borrower management with fine tracking  
- Issue and return books with availability updates  
- Automatic overdue detection (30 days) and fine application  
- Simple, themeable Swing UI with readable tables and dialogs

---

## Overdue & Fine Calculation

- Overdue detection: A checkout is considered overdue when returned more than 30 days after checkout.  
- Fine logic (current implementation): returning an overdue book adds a fixed fine of 50 units to the borrower's `current_fine`.  
- Recommendation: Make the overdue window and fine amount configurable (via `config.properties` or environment variables) before publishing a runnable artifact.

---

## Tech Stack

- Frontend: Java Swing  
- Database: MySQL  
- DB access: JDBC (mysql-connector-j)  
- Tools: VS Code, MySQL Workbench

---

## Project Structure

src/  
├─ admin/ (AdminLogin, AdminDashboard, BooksPanel, BorrowersPanel, CategoriesPanel, CheckoutsPanel)  
└─ db/ (DBConnection.java)

lib/  
└─ mysql-connector-j-9.4.0.jar

---

## Code Overview

**DBConnection.java**  
Manages JDBC connectivity and acts as a centralized database access layer.

**AdminLogin.java**  
Handles admin authentication before allowing dashboard access.

**AdminDashboard.java**  
Main control panel with tabbed navigation between modules.

**BooksPanel.java / BorrowersPanel.java / CategoriesPanel.java / CheckoutsPanel.java**  
CRUD and workflow logic for books, borrowers, categories and checkouts (includes overdue detection and fine updates).

---

## How to Run (Windows PowerShell)

1. Install MySQL and create the required schema/tables.  
2. Update database credentials in `src/db/DBConnection.java` or move them to a config file (recommended).  
3. Ensure `lib/mysql-connector-j-9.4.0.jar` is present.

Copy-paste PowerShell to compile and run:
```powershell
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp ".\lib\mysql-connector-j-9.4.0.jar" -d .\bin $files
if ($LASTEXITCODE -eq 0) { java -cp ".\bin;.\lib\mysql-connector-j-9.4.0.jar" admin.AdminLogin } else { Write-Error "Compilation failed"; exit $LASTEXITCODE }
```

Notes:
- Do NOT commit credentials. Move DB URL/USER/PASS to `config.properties` or environment variables when sharing binaries.  
- To create a runnable JAR, add a manifest with `Main-Class: admin.AdminLogin` and include the JDBC connector on the classpath or bundle as a fat JAR.

---

## What This Project Demonstrates

- Practical JDBC usage and SQL query design  
- UI theming for desktop apps (dark theme, table styling, dialogs)  
- Real-world workflows: checkouts, returns, availability and fine handling

---

## Author

Ajay — BTech Computer Science Engineering
