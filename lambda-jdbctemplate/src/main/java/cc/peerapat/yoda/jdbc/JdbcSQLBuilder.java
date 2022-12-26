package cc.peerapat.yoda.jdbc;

import lombok.val;

import java.util.Arrays;
import java.util.stream.Collectors;


public abstract class JdbcSQLBuilder {

    /**
     * package=cc.peerapat.accounts<br>
     * class=AccountEntity<br>
     * table=accounts<br>
     * primary_keys=id,client_id<br>
     * columns=Long id, Long client_id, String username, String password_hash<br>
     */
    public String toJdbcClass(final String[] args) throws NumberFormatException {
        val packageId = args[0].trim().replace("package=", "");
        val classname = args[1].trim().replace("class=", "");
        val table = args[2].trim().replace("table=", "");
        val primary_keys = args[3].trim().replace("primary_keys=", "");
        val pks = primary_keys.split(",");
        val columns = args[4].trim().replace("columns=", "").split(",");

        return BASED.replace("__packageId", packageId)
                .replace("__classname", classname)
                .replace("__table", table)
                .replace("__pksCondition", pksCondition(pks))
                .replace("__pksParameters", pksParameters(columns, pks))
                .replace("__primary_keys", primary_keys)
                .replace("__bindings", bindings(columns))
                ;
    }

    String pksCondition(final String[] pks) {
        return Arrays.stream(pks).map(pk -> pk + "=?")
                .collect(Collectors.joining(" AND "));
    }

    String pksParameters(final String[] cols, final String[] pks) {
        return Arrays.stream(pks)
                .map(pk -> pkParameter(cols, pk))
                .collect(Collectors.joining(", "));
    }

    String pkParameter(final String[] cols, final String pk) {
        val colmap = Arrays.stream(cols).map(col -> col.trim().split(" "));
        return colmap.filter(col -> pk.equals(col[1])).findFirst().get()[0] + " " + pk;
    }

    String bindings(final String[] cols) {
        return Arrays.stream(cols).map(col -> binding(col))
                .collect(Collectors.joining(" \n"))
                .replaceFirst("           , ", "");
    }

    String binding(final String col) {
        val arr = col.trim().split(" ");
        return "           , rs.get" + arr[0] + "(\"" + arr[arr.length - 1] + "\")";
    }

    static final String BASED = "" +
            "package __packageId; \n" +
            "\n" +
            "import java.sql.Connection;\n" +
            "import java.sql.ResultSet;\n" +
            "import java.util.Optional;\n" +
            "\n" +
            "public abstract class __tableGenerated { \n" +
            "\n" +
            "   String Q_BY_PKS = \"SELECT * FROM accounts WHERE __pksCondition\"; \n" +
            "   Optional<AccountEntity> find(__pksParameters) { \n" +
            "       return Optional.ofNullable(jdbc().queryForObject(Q_BY_PKS\n" +
            "           , (rs, rowNum) -> parse(rs) \n" +
            "           , __primary_keys)); \n" +
            "   }\n" +
            "\n" +
            "   __classname parse(final ResultSet rs) throws SQLException { \n" +
            "       return new __classname( \n" +
            "           __bindings \n" +
            "       );" +
            "\n}";
}