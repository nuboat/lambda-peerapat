package cc.peerapat.yoda.jdbc;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class JdbcSQLBuilderTest {

    @Test
    public void buildWithParsedData() {
        val builder = new JdbcSQLBuilder(new PrintLog());

        val actual = builder.toJdbcClass(
                "cc.peerapat.repo.generated"
                , "cc.peerapat.entites"
                , "AccountGenerated"
                , "AccountEntity"
                , "accounts"
                , "id, client_id"
                , "Long id, Long clientId, String username, String passwordHash".split(","));

        System.out.println(actual);
    }

    @Test
    public void buildWithRecordClass() throws IOException {
        val input = Files.readString(Paths.get("src/test/resources/AccountEntity.java")
                , StandardCharsets.UTF_8);

        val parser = new RecodeParser(input);
        val builder = new JdbcSQLBuilder(new PrintLog());

        val response = builder.toJdbcClass(
                parser.toPackageEntity()
                , parser.toPackageName()
                , parser.toClassName()
                , parser.toEntityName()
                , parser.toTableName()
                , parser.toPrimaryKeys()
                , parser.toColumns()
        );

        System.out.println(response);
    }

}
