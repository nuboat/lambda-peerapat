package cc.peerapat.yoda.jdbc;

import cc.peerapat.yoda.helper.TextHelper;
import lombok.val;

import java.util.Arrays;
import java.util.stream.Collectors;


public class JdbcSQLBuilder implements TextHelper {

    /**
     * package=cc.peerapat.accounts<br>
     * class=AccountEntity<br>
     * table=accounts<br>
     * primary_keys=id,client_id<br>
     * columns=Long id, Long client_id, String username, String password_hash<br>
     */
    public String toJdbcClass(final String packageId
            , final String classname
            , final String table
            , final String primaryKeys
            , final String[] columns) throws NumberFormatException {
        val pks = primaryKeys.split(",");

//        System.out.println(BASED);

        return BASED.replace("__packageId", packageId)
                .replace("__classname", classname)
                .replace("__table", table)
                .replace("__insertStatement", buildInsertStatement(table, columns))
                .replace("__deleteStatement", buildDeleteStatement(table, pks))
                .replace("__insertParams", insertParams(columns))
                .replace("__pksCondition", pksCondition(pks))
                .replace("__pksParameters", pksParams(columns, pks))
                .replace("__primaryKeys", primaryKeys)
                .replace("__bindings", bindings(columns))
                ;
    }

    String buildInsertStatement(final String table, final String[] cols) {
        val colsJoin = Arrays.stream(cols).map(col -> col.trim().split(" ")[1])
                .collect(Collectors.joining(", "));
        val paramsJoin = Arrays.stream(cols).map(col -> "?")
                .collect(Collectors.joining(","));

        return "INSERT INTO " + table + "(" + colsJoin + ") VALUES (" + paramsJoin + ")";
    }

    String buildDeleteStatement(final String table, final String[] pks) {
        return "DELETE FROM " + table + " WHERE " + pksCondition(pks);
    }

    String pksCondition(final String[] pks) {
        return Arrays.stream(pks).map(pk -> pk + "=?")
                .collect(Collectors.joining(" AND "));
    }

    String pkParameter(final String[] cols, final String pk) {
        val colMap = Arrays.stream(cols).map(col -> col.trim().split(" "));
        return colMap.filter(col -> pk.equals(col[1])).findFirst().get()[0] + " " + pk;
    }

    String insertParams(final String[] cols) {
        return Arrays.stream(cols).map(col -> "e." + snakeToCamel(col.trim().split(" ")[1]) + "()")
                .collect(Collectors.joining("\n           , "));
    }

    String pksParams(final String[] cols, final String[] pks) {
        return Arrays.stream(pks)
                .map(pk -> pkParameter(cols, pk.trim()))
                .collect(Collectors.joining("\n           , "));
    }

    String bindings(final String[] cols) {
        return Arrays.stream(cols).map(this::binding)
                .collect(Collectors.joining(" \n"))
                .replaceFirst("           , ", "");
    }

    String binding(final String col) {
        val arr = col.trim().split(" ");
        if ("Integer".equals(arr[0])) {
            return "           , rs.getInt(\"" + arr[arr.length - 1] + "\")";
        } else if ("LocalDateTime".contentEquals(arr[0])) {
            return "           , rs.getObject(\"" + arr[arr.length - 1] + "\", LocalDateTime.class)";
        } else {
            return "           , rs.get" + arr[0] + "(\"" + arr[arr.length - 1] + "\")";
        }
    }

    static final String BASED = "" +
            "package __packageId; \n" +
            "\n" +
            "import org.springframework.jdbc.core.JdbcTemplate; \n" +
            "\n" +
            "import java.sql.ResultSet;\n" +
            "import java.sql.SQLException; \n" +
            "import java.time.LocalDateTime; \n" +
            "import java.util.Optional; \n" +
            "\n" +
            "public abstract class __tableGenerated { \n" +
            "\n" +
            "   protected abstract JdbcTemplate jdbc();\n" +
            "\n" +
            "   String INSERT = \"__insertStatement\";\n" +
            "   public void insert(final __classname e) { \n" +
            "       jdbc().update(INSERT \n" +
            "           , __insertParams); \n" +
            "   }\n" +
            "\n" +
            "   String DELETE = \"__deleteStatement\"; \n" +
            "   public void delete(__pksParameters) { \n" +
            "       jdbc().update(DELETE \n" +
            "           , __primaryKeys); \n" +
            "   }\n" +
            "\n" +
            "   String Q_BY_PKS = \"SELECT * FROM accounts WHERE __pksCondition\"; \n" +
            "   public Optional<__classname> find(__pksParameters) { \n" +
            "       return Optional.ofNullable(jdbc().queryForObject(Q_BY_PKS \n" +
            "           , (rs, rowNum) -> parse(rs) \n" +
            "           , __primaryKeys)); \n" +
            "   }\n" +
            "\n" +
            "   __classname parse(final ResultSet rs) throws SQLException { \n" +
            "       return new __classname( \n" +
            "           __bindings \n" +
            "       );\n" +
            "   }\n" +
            "}";
}
