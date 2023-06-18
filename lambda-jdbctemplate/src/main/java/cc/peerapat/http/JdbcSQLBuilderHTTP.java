package cc.peerapat.http;

import cc.peerapat.yoda.helper.Configs;
import cc.peerapat.yoda.helper.TextHelper;
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

/**
 * cc.peerapat.http.JdbcSQLBuilderHTTP::handleRequest
 */
public class JdbcSQLBuilderHTTP implements Configs
        , TextHelper
        , RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Override
    public APIGatewayV2HTTPResponse handleRequest(
            final APIGatewayV2HTTPEvent request
            , final Context context) {
        val log = context.getLogger();
        val start = System.currentTimeMillis();

        try {
            val parser = new RecodeParser(request.getBody());
            val builder = new JdbcSQLBuilder(log);

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

            return buildResponse(500
                    , f("Error: %s\n Cause: %s\n%s", e.getMessage(), e.getCause().getMessage(), sw)
                    , end - start);
        }
    }

    private APIGatewayV2HTTPResponse buildResponse(
            final Integer code
            , final String body
            , final Long processTime) {
        val headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/plain");
        headers.put("X-Processing-ms", processTime.toString());
        headers.put("X-Build-Id", Configs.BUILD);

        val response = new APIGatewayV2HTTPResponse();
        response.setIsBase64Encoded(false);
        response.setStatusCode(code);
        response.setHeaders(headers);
        response.setBody(body);

        return response;
    }

}
