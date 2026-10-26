package operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class UpdateOperation {

    public static int updateRecord(Connection con, String tableName, List<String> columns, List<String> values) throws SQLException {
        boolean isCompositeKey = tableName.equalsIgnoreCase("countrylanguage");
        int pkCount = isCompositeKey ? 2 : 1;

        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        for (int i = pkCount; i < columns.size(); i++) {
            sql.append(columns.get(i)).append(" = ?");
            if (i < columns.size() - 1) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE ");
        for (int i = 0; i < pkCount; i++) {
            sql.append(columns.get(i)).append(" = ?");
            if (i < pkCount - 1) {
                sql.append(" AND ");
            }
        }

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            for (int i = pkCount; i < values.size(); i++) {
                String value = values.get(i);
                if (value == null || value.equalsIgnoreCase("null")) {
                    ps.setNull(paramIndex++, Types.VARCHAR);
                } else {
                    ps.setString(paramIndex++, value);
                }
            }

            for (int i = 0; i < pkCount; i++) {
                ps.setString(paramIndex++, values.get(i));
            }

            return ps.executeUpdate();
        }
    }
}
