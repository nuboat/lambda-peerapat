package cc.peerapat.http;

import cc.peerapat.yoda.jdbc.JdbcSQLBuilder;
import cc.peerapat.yoda.jdbc.RecodeParser;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * cc.peerapat.http.JdbcSQLBuilderHTTP::handleRequest
 */
public class JdbcSQLBuilderHTTP implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Override
    public APIGatewayV2HTTPResponse handleRequest(final APIGatewayV2HTTPEvent request, final Context context) {
        val log = context.getLogger();
        val start = System.currentTimeMillis();

        try {
            val parser = new RecodeParser(request.getBody());
            val builder = new JdbcSQLBuilder(Optional.of(log));

            val response = builder.toJdbcClass(
                    parser.toPackageName()
                    , parser.toPackageEntity()
                    , parser.toClassName()
                    , parser.toEntityName()
                    , parser.toTableName()
                    , parser.toPrimaryKeys()
                    , parser.toColumns()
            );
            val end = System.currentTimeMillis();

            return buildResponse(200, response, (end - start));

        } catch (final Exception e) {
            val end = System.currentTimeMillis();
            val sw = new StringWriter();
            val pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            return buildResponse(500, "Error: " + e.getMessage() + "\n"
                    + " Cause:" + e.getCause() + "\n"
                    + sw, (end - start));
        }
    }

    private APIGatewayV2HTTPResponse buildResponse(final Integer code, final String body, final Long processTime) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put("X-Processing-ms", processTime.toString());

        val response = new APIGatewayV2HTTPResponse();
        response.setIsBase64Encoded(false);
        response.setStatusCode(code);
        response.setHeaders(headers);
        response.setBody(body);

        return response;
    }

}
