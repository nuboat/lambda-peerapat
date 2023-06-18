package regressions;

import lombok.val;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

public class Main {

    private static final String PATH = "src/test/resources/";
    private static final String TARGET = "target/";

    public static void main(final String[] args) {
        process("AccountEntity", "accountsGenerated");
    }

    public static void process(final String file, final String output) {
        val httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        try {
            val input = Files.readString(Paths.get(PATH + file + ".java"));
            val request = HttpRequest.newBuilder()
                    .header("Content-Type", "text/plain")
//                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("User-Agent", "Lambda-JDBC")
                    .POST(HttpRequest.BodyPublishers.ofString(input))
                    .uri(URI.create("https://buildjdbctemplate.peerapat.cc/"))
                    .build();
            val response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("Status code: " + response.statusCode());
            System.out.println(("Headers: " + response.headers().allValues("content-type")));
            System.out.println(("Headers: " + response.headers().allValues("X-Processing-ms")));
            System.out.println(("Headers: " + response.headers().allValues("X-Build-Id")));
            System.out.println(response.body());

            if (response.statusCode() == 200)
                Files.write(Paths.get(TARGET + output + ".java"), response.body().getBytes());
            else
                System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
