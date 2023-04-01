package cc.peerapat.yoda.jdbc;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
public class RecodeParserTest {

    @Test
    public void extractRecordSuccessTest() throws IOException {
        val input = Files.readString(Paths.get("src/test/resources/AccountEntity.java.txt")
                , StandardCharsets.UTF_8);

        val parser = new RecodeParser(input);

        assert "id,client_id".equals(parser.toPrimaryKeys());

        Arrays.stream(parser.toColumns()).forEach(l -> System.out.println("line: " + l));
    }

}
