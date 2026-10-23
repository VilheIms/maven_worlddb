package operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SelectOperation {
	public static List<String> populateTable(Connection con, String tableName, TableView<ObservableList<String>> table) throws SQLException {
		List<String> columnNames = new ArrayList<>();
		table.getColumns().clear();
		table.getItems().clear();
		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
		
		String sql = "SELECT * FROM " + tableName;
		try (Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			ResultSetMetaData meta = rs.getMetaData();
			int colCount = meta.getColumnCount();
			
			for (int i = 1; i <= colCount; i++) {
				final int j = i - 1;
				String colName = meta.getColumnName(i);
				int colType = meta.getColumnType(i);
				columnNames.add(colName);
				
				TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
				col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
				
				
				if (isNumericType(colType)) {
					col.setComparator((String s1, String s2) -> {
						if (s1 == null || s1.trim().isEmpty()) return (s2 == null || s2.trim().isEmpty()) ? 0 : -1;
						if (s2 == null || s2.trim().isEmpty()) return 1;
						
						try {
							
							double d1 = Double.parseDouble(s1.trim());
							double d2 = Double.parseDouble(s2.trim());
							return Double.compare(d1, d2);
						} catch (NumberFormatException e) {
							
							return s1.compareTo(s2);
						}
					});
				}
				
				table.getColumns().add(col);
			}
			
			while (rs.next()) {
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= colCount; i++) {
					row.add(rs.getString(i) == null ? "" : rs.getString(i));
				}
				data.add(row);
			}
			table.setItems(data);
		}
		
		return columnNames;
	}
	
	// Palīgmetode, kas nosaka, vai SQL tips ir skaitlisks
	private static boolean isNumericType(int type) {
		return type == Types.INTEGER  || 
		       type == Types.BIGINT   || 
		       type == Types.SMALLINT || 
		       type == Types.TINYINT  || 
		       type == Types.DECIMAL  || 
		       type == Types.NUMERIC  || 
		       type == Types.DOUBLE   || 
		       type == Types.FLOAT    || 
		       type == Types.REAL;
	}
}
