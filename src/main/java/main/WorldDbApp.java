package main;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import atlantafx.base.theme.PrimerLight;
import db.DatabaseConnection;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import operations.DeleteOperation;
import operations.InsertOperation;
import operations.SelectOperation;
import operations.UpdateOperation;

public class WorldDbApp extends Application {
    private Connection con;
    private TableView<ObservableList<String>> table = new TableView<>();
    private ComboBox<String> tableSelector = new ComboBox<>();
    private List<String> currentColumns = new ArrayList<>();
    private List<String> databaseView = new ArrayList<>();

    private Button btnSelect = new Button("Atlasīt (SELECT)");
    private Button btnInsert = new Button("Pievienot (INSERT)");
    private Button btnUpdate = new Button("Atjaunināt (UPDATE)");
    private Button btnDelete = new Button("Dzēst (DELETE)");

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        
        try {
            con = DatabaseConnection.getConnection();
            databaseView = operations.ViewManager.getDatabaseViews(con);
        } catch (Exception e) {
            showAlert("Savienojuma kļūda: ", e.getMessage());
            return;
        }

        tableSelector.getItems().add("--- Tabulas ---");
        tableSelector.getItems().addAll("city", "country", "countrylanguage");

        if (databaseView.isEmpty()) {
            tableSelector.getItems().add("--- NAV ATRASTS NEVIENS SKATS --");
            showAlert("Informācija", "Datu bāzē pagaidām nav neviens skats");
        } else {
            tableSelector.getItems().add("--- SKATI ---");
            tableSelector.getItems().addAll(databaseView);
        }

        btnSelect.setOnAction(e -> loadData());
        btnInsert.setOnAction(e -> showInsertUpdateDialog(false));
        btnUpdate.setOnAction(e -> showInsertUpdateDialog(true));
        btnDelete.setOnAction(e -> performDelete());

        tableSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isSeparator = (newVal == null || newVal.startsWith("---"));
            btnSelect.setDisable(isSeparator);
            btnInsert.setDisable(isSeparator);
            btnUpdate.setDisable(isSeparator);
            btnDelete.setDisable(isSeparator);
        });

        tableSelector.getSelectionModel().select(1);

        HBox topBox = new HBox(10, new Label("Tabula/Skats:"), tableSelector, btnSelect);
        topBox.setPadding(new Insets(10));
        HBox bottomBox = new HBox(10, btnInsert, btnUpdate, btnDelete);
        VBox root = new VBox(10, topBox, table, bottomBox);

        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("World DB APP");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png.png")));
        stage.show();
        loadData();
    }

    private void loadData() {
        String selectedTable = tableSelector.getValue();
        if (selectedTable == null || selectedTable.startsWith("---")) return;
        try {
            currentColumns = SelectOperation.populateTable(con, selectedTable, table);
        } catch (Exception e) {
            showAlert("Kļūda atlasot datus", e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInsertUpdateDialog(boolean isUpdate) {
        String tableName = tableSelector.getValue();
        if (tableName == null || tableName.startsWith("---")) return;

        if (databaseView.contains(tableName)) {
            showAlert("Uzmanibu!", "Izveletais objekts ir skats! Datus taja mainit un izveidot nevar!");
            return;
        }

        loadData();

        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle(isUpdate ? "Update " + tableName : "INSERT " + tableName);
        dialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setHeaderText(isUpdate ? "Aizpildi laukus, lai atjauninatu:" : "Aizpildi laukus, lai pievienotu:");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        List<String> activeColumns = new ArrayList<>();
        List<TextField> textFields = new ArrayList<>();

        int index = 0;
        for (int i = 0; i < currentColumns.size(); i++) {
            String colName = currentColumns.get(i);
            if (!isUpdate && colName.equalsIgnoreCase("id")) {
                continue;
            }
            activeColumns.add(colName);

           
            int columnGroup = index % 2; 
            int row = index / 2;       
            
            int labelColumn = columnGroup * 2;
            int fieldColumn = labelColumn + 1;

            grid.add(new Label(colName + ":"), labelColumn, row);

            TextField tf = new TextField();
            if (isUpdate && i == 0) {
                tf.setPromptText("Ievadi esosu ID / Valsts kodu");
            }
            textFields.add(tf);
            grid.add(tf, fieldColumn, row);

            index++;
        }

        dialog.getDialogPane().setContent(grid);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                List<String> values = new ArrayList<>();
                for (int i = 0; i < textFields.size(); i++) {
                    values.add(textFields.get(i).getText());
                }

                if (isUpdate) {
                    int rowAffected = UpdateOperation.updateRecord(con, tableName, currentColumns, values);
                    if (rowAffected > 0) {
                        showAlert("Veiksmigi", "Ieraksts tika atjauninats");
                        loadData();
                    } else {
                        String pkColumn = currentColumns.get(0);
                        String pkValue = values.get(0);
                        showAlert("Informacija", "Ieraksts ar " + pkColumn + " = " + pkValue + " netika atrasts!");
                        event.consume();
                    }
                } else {
                    InsertOperation.insertRecord(con, tableName, activeColumns, values);
                    showAlert("Veiksmigi!", "Jauns ieraksts ievietots!");
                    loadData();
                }
            } catch (Exception e) {
                showAlert("SQL kluda", e.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void performDelete() {
        String tableName = tableSelector.getValue();
        if (tableName == null || tableName.startsWith("---")) return;

        if (databaseView.contains(tableName)) {
            showAlert("Uzmanibu!", " No skata dzest nedrikst!");
            return;
        }

        loadData();

        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("DELETE FROM " + tableName);
        dialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        List<String> pkColumns = new ArrayList<>();
        List<TextField> textFields = new ArrayList<>();

        boolean isCountryLanguage = "countrylanguage".equalsIgnoreCase(tableName);

        if (isCountryLanguage) {
            pkColumns.add(currentColumns.isEmpty() ? "CountryCode" : currentColumns.get(0));
            pkColumns.add("Language");
            dialog.setHeaderText("Ievadi valsts kodu un valodu lai dzestu:");
        } else {
            String pkColumn = currentColumns.isEmpty() ? "ID" : currentColumns.get(0);
            pkColumns.add(pkColumn);
            dialog.setHeaderText("Ievadi " + pkColumn.toLowerCase() + " lai dzestu:");
        }

        for (int i = 0; i < pkColumns.size(); i++) {
            String colName = pkColumns.get(i);
            grid.add(new Label(colName + ": "), 0, i);
            TextField tf = new TextField();
            textFields.add(tf);
            grid.add(tf, 1, i);
        }

        dialog.getDialogPane().setContent(grid);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                List<String> validPkValues = new ArrayList<>();
                for (int i = 0; i < pkColumns.size(); i++) {
                    validPkValues.add(textFields.get(i).getText());
                }

                int rowsAffected;
                try (java.sql.Statement stmt = con.createStatement()) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
                }
                
                rowsAffected = DeleteOperation.deleteRecord(con, tableName, pkColumns, validPkValues);
                
                try (java.sql.Statement stmt = con.createStatement()) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
                }

                if (rowsAffected > 0) {
                    showAlert("Veiksmigi dzests!", "Dzesto ierakstu skaits: " + rowsAffected);
                    loadData();
                } else {
                    showAlert("Dzesana nenotika!", "Noraditais ieraksts netika atrasts!");
                    event.consume();
                }
            } catch (IllegalArgumentException e) {
                showAlert("Ievades datu kluda!", e.getMessage());
                event.consume();
            } catch(Exception e) {
                try (java.sql.Statement stmt = con.createStatement()) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
                } catch (Exception ignored) {}
                
                showAlert("SQL kluda", e.getMessage());
                event.consume();
            }
        });
        dialog.showAndWait();
    }

	
	public static void main(String[] args) {
		launch(args);
	}
}