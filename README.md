# 🌍 World DB App

A small JavaFX desktop application for browsing and editing the MySQL **`world`** sample database. Pick a table or view from the dropdown, load it into a grid, and perform `INSERT`, `UPDATE` and `DELETE` operations through simple dialogs.

> The user interface is in Latvian (e.g. *Atlasīt* = Select, *Pievienot* = Insert, *Atjaunināt* = Update, *Dzēst* = Delete).

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Running the App](#running-the-app)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [Configuration](#configuration)
- [Notes](#notes)

## Features

- 📋 Browse the `city`, `country` and `countrylanguage` tables, plus any database views (e.g. `largest_districts`)
- ➕ Insert new rows through an auto-generated form
- ✏️ Update existing rows
- 🗑️ Delete rows by key (e.g. `CountryCode`), with a confirmation of how many rows were removed
- 🎨 Modern look using the [AtlantaFX](https://github.com/mkpaz/atlantafx) theme
- 📦 Single runnable "fat" JAR with JavaFX and the MySQL driver bundled

## Screenshots

### Update a record

Fill in the fields to update a row in `country`. A confirmation dialog appears when the record has been updated.

![Update country dialog](images/update-country.png)

### Insert a record

Adding a new row to `city`. The `ID` column is auto-increment, so only `Name`, `CountryCode`, `District` and `Population` are required.

![Insert city dialog](images/insert-city.png)

### Delete a record

Enter the `CountryCode` of the rows to remove from `countrylanguage`.

![Delete dialog](images/delete-countrylanguage-dialog.png)

The app reports how many rows were deleted.

![Delete result](images/delete-countrylanguage-result.png)

### Database views

Views are listed in the same dropdown as tables. The `largest_districts` view shows the most populous districts per country.

![largest_districts view](images/largest-districts-view.png)

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 21 or newer |
| JavaFX | 23.0.1 |
| AtlantaFX (theme) | 2.0.1 |
| MySQL Connector/J | bundled in the JAR |
| Database | MySQL 8.0 (`world` schema) |
| Build tool | Maven |

## Prerequisites

1. **JDK 21+** installed and available on your `PATH`
   ```bash
   java -version
   ```
2. **MySQL 8.0** running locally with the [`world` sample database](https://dev.mysql.com/doc/index-other.html) imported
3. The `maven_worlddb.jar` file

## Database Setup

By default the app connects to:

```
jdbc:mysql://localhost:3307/world
```

- **Host:** `localhost`
- **Port:** `3307`
- **Schema:** `world`
- **User:** `root`

Make sure your MySQL server is listening on port `3307` (or change the port, see [Configuration](#configuration)) and that the `world` schema exists.

To try the `largest_districts` view shown above, create it in the `world` schema first, for example:

```sql
CREATE OR REPLACE VIEW largest_districts AS
SELECT co.Name AS country_name,
       ci.District,
       COUNT(*) AS city_count,
       SUM(ci.Population) AS district_population
FROM city ci
JOIN country co ON co.Code = ci.CountryCode
GROUP BY co.Name, ci.District
ORDER BY district_population DESC;
```

## Running the App

```bash
java -jar maven_worlddb.jar
```

The JAR's entry point is `main.WorldDbApp`. A `main.Launcher` class is also included as a fallback launcher for JavaFX.

If the window does not appear, try:

```bash
java -cp maven_worlddb.jar main.Launcher
```

## Project Structure

```
maven_worlddb.jar
├── db/
│   ├── DatabaseConnection   # JDBC connection settings
│   └── TestConnection       # Quick connectivity check
├── main/
│   ├── WorldDbApp           # JavaFX application (UI)
│   └── Launcher             # Alternate entry point
├── operations/
│   ├── SelectOperation      # SELECT * FROM <table>, fills the grid
│   ├── InsertOperation      # INSERT INTO <table> ...
│   ├── UpdateOperation      # UPDATE <table> ... WHERE ...
│   ├── DeleteOperation      # DELETE FROM <table> WHERE ...
│   └── ViewManager          # Lists tables and views from the schema
└── resources/
    └── icon.png             # Application icon
```

## Usage

1. Launch the app.
2. Choose a table or view in the **Tabula/Skats** dropdown.
3. Click **Atlasīt (SELECT)** to load its data.
4. Use the buttons at the bottom:
   - **Pievienot (INSERT)** – opens a form for a new row
   - **Atjaunināt (UPDATE)** – opens a form to edit a row
   - **Dzēst (DELETE)** – asks for the key of the row(s) to remove
5. Confirm with **OK**. A success dialog shows the result.

## Configuration

The connection settings are defined in `db/DatabaseConnection`:

```
jdbc:mysql://localhost:3307/world?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Riga&characterEncoding=utf8&useUnicode=true
```

To use a different host, port, user or password, change them in `DatabaseConnection.java` and rebuild the JAR with Maven.

> ⚠️ **Security:** Never commit real database passwords to a public repository. Use a dedicated MySQL user with limited privileges, or load credentials from environment variables.

## Notes

- This app is intended for learning and demonstration with the `world` sample database.
- `INSERT`, `UPDATE` and `DELETE` modify data directly, so use a test copy of the database.
- Deleting from `country` also removes dependent rows in `countrylanguage` and `city` to avoid foreign-key conflicts.
- The bundled JavaFX natives are Windows (`.dll`) builds, so the JAR runs as-is on Windows. On macOS/Linux, rebuild with the matching JavaFX platform classifier.
