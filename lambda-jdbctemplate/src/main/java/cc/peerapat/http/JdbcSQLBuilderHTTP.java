//package cc.peerapat.http;
//
//import cc.peerapat.yoda.jdbc.JdbcSQLBuilder;
//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
//import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * cc.peerapat.http.JdbcSQLBuilderHTTP::handleRequest
// */
//public class JdbcSQLBuilderHTTP implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
//
//    final Map<String, String> headers = new HashMap<>();
//
//    public JdbcSQLBuilderHTTP() {
//        headers.put("Content-Type", "text/plain");
//    }
//
//    @Override
//    public APIGatewayV2HTTPResponse handleRequest(final APIGatewayV2HTTPEvent request, final Context context) {
//        return buildResponse(JdbcSQLBuilder.toText(request.getBody().split("\n")));
//    }
//
//    private APIGatewayV2HTTPResponse buildResponse(final String body) {
//        final APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
//        response.setIsBase64Encoded(false);
//        response.setStatusCode(200);
//        response.setHeaders(headers);
//        response.setBody(body);
//        return response;
//    }
//
//}
