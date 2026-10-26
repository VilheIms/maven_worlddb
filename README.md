# 🌍 World DB App

A small JavaFX desktop application for browsing and editing the MySQL **`world`** sample database. Pick a table or view from the dropdown, load it into a grid, and perform `INSERT`, `UPDATE` and `DELETE` operations through simple dialogs.

> The user interface is in Latvian (e.g. *Atlasīt* = Select, *Pievienot* = Insert, *Atjaunināt* = Update, *Dzēst* = Delete).
> 
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

## Prerequisites

1. **JDK 21+** installed and available on your `PATH`
   ```bash
   java -version
   ```
2. **MySQL 8.0** running locally with the [`world` sample database](https://dev.mysql.com/doc/index-other.html) imported
3. The `maven_worlddb.jar` file

## Running the App

```bash
java -cp maven_worlddb.jar main.Launcher
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

- This app is intended for learning and demonstration with the `world` sample database.
- `INSERT`, `UPDATE` and `DELETE` modify data directly, so use a test copy of the database.
- Deleting from `country` also removes dependent rows in `countrylanguage` and `city` to avoid foreign-key conflicts.
- The bundled JavaFX natives are Windows (`.dll`) builds, so the JAR runs as-is on Windows. On macOS/Linux, rebuild with the matching JavaFX platform classifier.
