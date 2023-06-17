package cc.peerapat.yoda.jdbc;

import cc.peerapat.yoda.helper.Configs;
import cc.peerapat.yoda.helper.TextHelper;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import lombok.val;

import java.util.Arrays;
import java.util.stream.Collectors;

public class JdbcSQLBuilder implements Configs, TextHelper {

    private final LambdaLogger log;
    private static final String TEMPLATE = Configs.loadTemplate();

    public JdbcSQLBuilder(final LambdaLogger log) {
        this.log = log;
    }

    public String toJdbcClass(final String packageId
            , final String packageEntity
            , final String entityName
            , final String table
            , final String primaryKeys
            , final String[] columns) throws NumberFormatException {

        val pks = primaryKeys.split(",");
        val pkCamelSet = Arrays.stream(pks).map(pk -> snakeToCamel(pk.trim()))
                .collect(Collectors.joining(", "));
        log.log(Arrays.toString(pks));

        return TEMPLATE
                .replace("__packageId", packageId)
                .replace("__packageEntity", packageEntity)
                .replace("__entityName", entityName)
                .replace("__table", table)
                .replace("__insertStatement", buildInsertStatement(table, columns))
                .replace("__deleteStatement", buildDeleteStatement(table, pks))
                .replace("__insertParams", insertParams(columns))
                .replace("__pksCondition", pksCondition(pks))
                .replace("__pksParameters", pksParams(columns, pks))
                .replace("__primaryKeys", pkCamelSet)
                .replace("__bindings", bindings(columns));
    }

    String buildInsertStatement(final String table, final String[] cols) {
        val colsJoin = Arrays.stream(cols).map(col ->
                        camelToSnake(col.trim().split(" ")[1]))
                .collect(Collectors.joining(", "));
        val paramsJoin = Arrays.stream(cols).map(col -> "?")
                .collect(Collectors.joining(", "));

        return f("INSERT INTO %s (%s) VALUES (%s)", table, colsJoin, paramsJoin);
    }

    String buildDeleteStatement(final String table, final String[] pks) {
        return f("DELETE FROM  %s WHERE %s", table, pksCondition(pks));
    }

    String pksCondition(final String[] pks) {
        return Arrays.stream(pks).map(pk -> f("%s = ?", pk.trim()))
                .collect(Collectors.joining(" AND "));
    }

    String insertParams(final String[] cols) {
        return Arrays.stream(cols)
                .map(col -> f("e.%s()", snakeToCamel(col.trim().split(" ")[1])))
                .collect(Collectors.joining(f("\n%s, ", SPACE11)));
    }

    String pksParams(final String[] cols, final String[] pks) {
        return Arrays.stream(pks)
                .map(pk -> pkParameter(cols, pk.trim()))
                .collect(Collectors.joining(", "));
    }

    String bindings(final String[] cols) {
        return Arrays.stream(cols).map(this::binding)
                .collect(Collectors.joining(" \n"))
                .replaceFirst(f("%s, ", SPACE11), "");

    }

    private String pkParameter(final String[] cols, final String pk) {
        val c = Arrays.stream(cols).map(col -> col.trim().split(" "))
                .filter(col -> pk.equals(camelToSnake(col[1])))
                .findFirst();

        if (c.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return f("final %s %s", c.get()[0], snakeToCamel(pk));
    }

    private String binding(final String col) {
        val arr = col.trim().split(" ");
        if ("Integer".equals(arr[0])) {
            return f("%s, rs.getInt(\"%s\")", SPACE11, camelToSnake(arr[arr.length - 1]));
        } else if ("LocalDateTime".contentEquals(arr[0])) {
            return f("%s, rs.getObject(\"%s\", LocalDateTime.class)", SPACE11, camelToSnake(arr[arr.length - 1]));
        } else {
            return f("%s, rs.get%s(\"%s\")", SPACE11, arr[0], camelToSnake(arr[arr.length - 1]));
        }
    }

}
