package cc.peerapat.yoda.jdbc;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RecodeParser {

    final String body;
    final List<String> lines;

    public RecodeParser(final String body) {
        this.body = body;
        this.lines = Arrays.stream(body.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    public String toPackageName() throws IllegalArgumentException {
        val r = lines.stream().filter(line -> line.contains("@param package_name"))
                .findFirst()
                .map(line -> line.split("=")[1].trim());

        if (r.isEmpty())
            throw new IllegalArgumentException();
        else
            return r.get();
    }

    public String toClassName() throws IllegalArgumentException {
        val r = lines.stream().filter(line -> line.startsWith("public record"))
                .findFirst()
                .map(line -> line.replace("public record", "").split("\\(")[0].trim());

        if (r.isEmpty())
            throw new IllegalArgumentException();
        else
            return r.get();
    }

    public String toTableName() throws IllegalArgumentException {
        val r = lines.stream().filter(line -> line.contains("@param table_name"))
                .findFirst()
                .map(line -> line.split("=")[1].trim());

        if (r.isEmpty())
            throw new IllegalArgumentException();
        else
            return r.get();
    }

    public String toPrimaryKeys() throws IllegalArgumentException {
        val r = lines.stream().filter(line -> line.contains("@param primary_keys"))
                .findFirst()
                .map(line -> line.split("=")[1].trim());

        if (r.isEmpty())
            throw new IllegalArgumentException();
        else
            return r.get();
    }

    public String[] toColumns() {
        val cols = new LinkedList<String>();

        var trigger = false;
        for (val line : lines) {
            if (trigger) {
                val col = Arrays.stream(line.split(" "))
                        .filter(x -> !(ignoreLine(x.trim())))
                        .collect(Collectors.joining(" "))
                        .replace(")", "")
                        .replace("{", "")
                        .replace(",", "")
                        .trim();

                if (col.trim().isEmpty())
                    continue;

                cols.add(col);
            }

            if (line.startsWith("public record"))
                trigger = true;
        }

        return cols.toArray(String[]::new);
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
