package regressions;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
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

            log.info("Status code: " + response.statusCode());
            log.info("Headers: " + response.headers().allValues("content-type"));

            if (response.statusCode() == 200)
                Files.write(Paths.get(TARGET + output + ".java"), response.body().getBytes());
            else
                log.error("Build {} got Error: {}", output, response.body());
        } catch (IOException | InterruptedException e) {
            log.error("Build {} got Error: {}", output, e.getMessage());
        }
    }
}
