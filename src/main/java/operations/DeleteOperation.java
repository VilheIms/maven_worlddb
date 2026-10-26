package operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DeleteOperation {

    public static int deleteRecord(Connection con, String tableName, List<String> pkColumns, List<String> pkValues) throws SQLException {
        
        if ("country".equalsIgnoreCase(tableName)) {
            String countryCode = pkValues.get(0);
            
            deleteFromDependentTable(con, "countrylanguage", "CountryCode", countryCode);
            deleteFromDependentTable(con, "city", "CountryCode", countryCode);
        }

        if ("countrylanguage".equalsIgnoreCase(tableName)) {
            String sql = "DELETE FROM countrylanguage WHERE " + pkColumns.get(0) + " = ? AND " + pkColumns.get(1) + " = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pkValues.get(0));
                ps.setString(2, pkValues.get(1));
                return ps.executeUpdate();
            }
        } else {
            String sql = "DELETE FROM " + tableName + " WHERE " + pkColumns.get(0) + " = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pkValues.get(0));
                return ps.executeUpdate();
            }
        }
    }

    private static void deleteFromDependentTable(Connection con, String table, String fkColumn, String value) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE " + fkColumn + " = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.executeUpdate();
        }
    }
}
