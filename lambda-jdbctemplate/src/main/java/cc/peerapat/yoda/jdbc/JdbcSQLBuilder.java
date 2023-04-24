package cc.peerapat.yoda.jdbc;

import cc.peerapat.yoda.helper.TextHelper;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import lombok.val;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;


public class JdbcSQLBuilder implements TextHelper {

    private final Optional<LambdaLogger> log;

    public JdbcSQLBuilder(final Optional<LambdaLogger> log) {
        this.log = log;
    }

    private static final String SPACE11 = "           ";
    private static final String TEMPLATE = template();

    /**
     * packageId=cc.peerapat.repos.generated<br>
     * packageEntity=cc.peerapat.entites<br>
     * class=AccountEntity<br>
     * table=accounts<br>
     * primary_keys=id,client_id<br>
     * columns=Long id, Long client_id, String username, String password_hash<br>
     */
    public String toJdbcClass(final String packageId
            , final String packageEntity
            , final String className
            , final String entityName
            , final String table
            , final String primaryKeys
            , final String[] columns) throws NumberFormatException {

        val pks = primaryKeys.split(",");
        val pkCamelSet = Arrays.stream(pks).map(pk -> snakeToCamel(pk.trim()))
                .collect(Collectors.joining(", "));
        log.ifPresent(l -> l.log(Arrays.toString(pks)));

        return TEMPLATE
                .replace("__packageId", packageId)
                .replace("__packageEntity", packageEntity)
                .replace("__className", className)
                .replace("__entityName", entityName)
                .replace("__insertStatement", buildInsertStatement(table, columns))
                .replace("__deleteStatement", buildDeleteStatement(table, pks))
                .replace("__insertParams", insertParams(columns))
                .replace("__pksCondition", pksCondition(pks))
                .replace("__pksParameters", pksParams(columns, pks))
                .replace("__primaryKeys", pkCamelSet)
                .replace("__bindings", bindings(columns));
    }

    String buildInsertStatement(final String table, final String[] cols) {
        val colsJoin = Arrays.stream(cols).map(col -> camelToSnake(col.trim().split(" ")[1]))
                .collect(Collectors.joining(", "));
        val paramsJoin = Arrays.stream(cols).map(col -> "?")
                .collect(Collectors.joining(", "));

        return "INSERT INTO " + table + "(" + colsJoin + ") VALUES (" + paramsJoin + ")";
    }

    String buildDeleteStatement(final String table, final String[] pks) {
        return "DELETE FROM " + table + " WHERE " + pksCondition(pks);
    }

    String pksCondition(final String[] pks) {
        return Arrays.stream(pks).map(pk -> pk + "=?")
                .collect(Collectors.joining(" AND "));
    }

    String insertParams(final String[] cols) {
        return Arrays.stream(cols).map(col -> "e." + snakeToCamel(col.trim().split(" ")[1]) + "()")
                .collect(Collectors.joining("\n" + SPACE11 + ", "));
    }

    String pksParams(final String[] cols, final String[] pks) {
        return Arrays.stream(pks)
                .map(pk -> pkParameter(cols, pk.trim()))
                .collect(Collectors.joining(", "));
    }

    String bindings(final String[] cols) {
        return Arrays.stream(cols).map(this::binding)
                .collect(Collectors.joining(" \n"))
                .replaceFirst(SPACE11 + ", ", "");
    }

    private static String template() {
        val is = JdbcSQLBuilder.class.getClassLoader()
                .getResourceAsStream("spring-jdbc-template.txt");
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private String pkParameter(final String[] cols, final String pk) {
        val c = Arrays.stream(cols).map(col -> col.trim().split(" "))
                .filter(col -> pk.equals(camelToSnake(col[1])))
                .findFirst();
        if (c.isEmpty())
            throw new IllegalArgumentException();

        return "final " + c.get()[0] + " " + snakeToCamel(pk);
    }

    private String binding(final String col) {
        val arr = col.trim().split(" ");
        if ("Integer".equals(arr[0])) {
            return SPACE11 + ", rs.getInt(\"" + camelToSnake(arr[arr.length - 1]) + "\")";
        } else if ("LocalDateTime".contentEquals(arr[0])) {
            return SPACE11 + ", rs.getObject(\"" + camelToSnake(arr[arr.length - 1]) + "\", LocalDateTime.class)";
        } else {
            return SPACE11 + ", rs.get" + arr[0] + "(\"" + camelToSnake(arr[arr.length - 1]) + "\")";
        }
    }

}
