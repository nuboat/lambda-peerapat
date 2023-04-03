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
public class JdbcSQLBuilderTest {

    @Test
    public void buildWithParsedData() {
        val builder = new JdbcSQLBuilder();
        val actual = builder.toJdbcClass("cc.peerapat.accounts"
        , "AccountEntity"
        , "accounts"
        , "id,client_id"
        , "Long id, Long clientId, String username, String passwordHash".split(","));

        System.out.println(actual);
    }

    @Test
    public void buildWithRecordClass() throws IOException {
        val input = Files.readString(Paths.get("src/test/resources/AccountEntity.java.txt")
                , StandardCharsets.UTF_8);

        val parser = new RecodeParser(input);
        val builder = new JdbcSQLBuilder();

        val response = builder.toJdbcClass(parser.toPackageName()
                , parser.toClassName()
                , parser.toTableName()
                , parser.toPrimaryKeys()
                , parser.toColumns()
        );

        System.out.println(response);
    }

}
