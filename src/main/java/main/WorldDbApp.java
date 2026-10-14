package main;

import javafx.scene.control.Button;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import atlantafx.base.theme.PrimerLight;
import db.DatabaseConnection;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class WorldDbApp extends Application {
	private Connection con;
	
	private TableView<ObservableList<String>> table = new TableView<>();
	
	private ComboBox<String> tableSelector = new ComboBox<>();
	
	private List<String> currentColumns = new ArrayList<>();
	private List<String> databaseView = new ArrayList<>();
	
	private Button btnSelect = new Button("Atlasit (SELECT)");
	private Button btnInsert = new Button("Pievienot (INSERT)");
	private Button btnUpdate = new Button("Atjauninat (UPDATE)");
	private Button btnDelete = new Button("Izdzest (DELETE)");
	
	@Override
	public void start(Stage stage) {
		Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
		//PrimerDark(), NordDark()
		try {
			con = DatabaseConnection.getConnection();
			//databaseView = ViewManager.getDatabaseViews(con);
			
			
		}catch (Exception e) {
			showAlert("Savienojuma kluda:", e.getMessage());
			return;
		}
		tableSelector.getItems().add("---Tabulas---");
		tableSelector.getItems().addAll("city");
		tableSelector.getItems().addAll("country");
		tableSelector.getItems().addAll("countrylanguage");
		
		if(databaseView.isEmpty()) {
			tableSelector.getItems().add("---NAV ATRASTS NEVIENS SKATS---");
			showAlert("Informacija", "Datu baze pagaidam nav neviens skats");
		} else {
			tableSelector.getItems().add("---SKATI---");
			tableSelector.getItems().addAll(databaseView);
		}
		
	}
	
	private void showAlert(String title, String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
