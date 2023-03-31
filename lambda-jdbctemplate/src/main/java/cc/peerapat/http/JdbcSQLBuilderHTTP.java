package cc.peerapat.http;

import cc.peerapat.yoda.jdbc.JdbcSQLBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * cc.peerapat.http.JdbcSQLBuilderHTTP::handleRequest
 */
public class JdbcSQLBuilderHTTP implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Override
    public APIGatewayV2HTTPResponse handleRequest(final APIGatewayV2HTTPEvent request, final Context context) {
        val start = System.currentTimeMillis();
        val builder = new JdbcSQLBuilder();
        val lines = Arrays.stream(request.getBody().split("\n"))
                .map(String::trim)
                .collect(Collectors.toList());

        val response = builder.toJdbcClass(toPackageName(lines)
                , toClassName(lines)
                , toTableName(lines)
                , toPrimaryKeys(lines)
                , toColumns(lines)
        );
        val end = System.currentTimeMillis();

        return buildResponse(response, (end - start));
    }

    private APIGatewayV2HTTPResponse buildResponse(final String body, final Long processTime) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put("X-Processing-ms", processTime.toString());

        final APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setIsBase64Encoded(false);
        response.setStatusCode(200);
        response.setHeaders(headers);
        response.setBody(body);

        return response;
    }

    private String toPackageName(final List<String> lines) throws IllegalArgumentException {
        val r = lines.stream().filter(line -> line.startsWith("package"))
                .findFirst()
                .map(line -> line.replace("package", "").replace(";", "").trim());

        if (r.isEmpty())
            throw new IllegalArgumentException();
        else
            return r.get();
    }

    private String toClassName(final List<String> lines) throws IllegalArgumentException {
        val r = lines.stream().filter(line -> line.startsWith("public record"))
                .findFirst()
                .map(line -> line.replace("public record", "").split("\\(")[0].trim());

        if (r.isEmpty())
            throw new IllegalArgumentException();
        else
            return r.get();
    }

    private String toTableName(final List<String> lines) throws IllegalArgumentException {
        val r = lines.stream().filter(line -> line.contains("@param table_name"))
                .findFirst()
                .map(line -> line.split("=")[1].trim());

        if (r.isEmpty())
            throw new IllegalArgumentException();
        else
            return r.get();
    }

    private String toPrimaryKeys(final List<String> lines) throws IllegalArgumentException {
        val r = lines.stream().filter(line -> line.contains("@param primary_keys"))
                .findFirst()
                .map(line -> line.split("=")[1].trim());

        if (r.isEmpty())
            throw new IllegalArgumentException();
        else
            return r.get();
    }

    private String[] toColumns(final List<String> lines) {
        val cols = new LinkedList<String>();

        var trigger = false;
        for (val line : lines) {
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
