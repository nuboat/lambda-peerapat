package cc.peerapat.yoda.helper;

import cc.peerapat.yoda.jdbc.JdbcSQLBuilder;
import lombok.val;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public interface Configs {

    String SPACE11 = "           ";

    static String loadTemplate() {
        val is = JdbcSQLBuilder.class.getClassLoader()
                .getResourceAsStream("spring-jdbc-template.txt");
        if (is == null) {
            return "ERROR";
        }
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    static String loadBuild() {
        val is = JdbcSQLBuilder.class.getClassLoader()
                .getResourceAsStream("build.properties");
        if (is == null) {
            return "NA";
        }
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .toString();
    }

}
