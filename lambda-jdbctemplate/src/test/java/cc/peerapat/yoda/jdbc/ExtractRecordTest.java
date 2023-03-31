package cc.peerapat.yoda.jdbc;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Slf4j
public class ExtractRecordTest {

    @Test
    public void extractRecordSuccessTest() throws IOException {
        val input = Files.readString(Paths.get("src/test/resources/AccountEntity.java.txt"), StandardCharsets.UTF_8);
        val list = Arrays.stream(input.split("\n"))
                .map(String::trim)
                .collect(Collectors.toList());

        val packageName = list.stream().filter(line -> line.startsWith("package"))
                .findFirst()
                .map(line -> line.replace("package", "").replace(";", "").trim())
                .orElse(null);
        assert "cc.peerapat.entity".equals(packageName) : "package name should be cc.peerapat.entity";

        val tableName = list.stream().filter(line -> line.contains("@param table_name"))
                .findFirst()
                .map(line -> line.split("=")[1].trim())
                .orElse(null);
        assert "accounts".equals(tableName);

        val primaryKeys = list.stream().filter(line -> line.contains("@param primary_keys"))
                .findFirst()
                .map(line -> line.split("=")[1].trim())
                .orElse(null);
        assert "id,client_id".equals(primaryKeys);

        val className = list.stream().filter(line -> line.startsWith("public record"))
                .findFirst()
                .map(line -> line.replace("public record", "").split("\\(")[0].trim())
                .orElse(null);
        assert "AccountEntity".equals(className) : "class name should be AccountEntity";

        var trigger = false;
        val cols = new LinkedList<String>();
        for (val line : list) {
            if (trigger)
                cols.add(Arrays.stream(line.split(" "))
                        .filter(x -> !(ignoreLine(x.trim())))
                        .collect(Collectors.joining(" "))
                        .replace(")", "")
                        .replace("{", "")
                        .replace(",", "")
                        .trim());

            if (line.startsWith("public record"))
                trigger = true;
        }

        cols.forEach(System.out::println);
    }

    private boolean ignoreLine(final @NotNull String line) {
        return line.isEmpty()
                || line.startsWith(",")
                || line.startsWith("@")
                || line.startsWith("import")
                || line.startsWith("/")
                || line.startsWith("}")
                || line.startsWith("*");
    }
}
