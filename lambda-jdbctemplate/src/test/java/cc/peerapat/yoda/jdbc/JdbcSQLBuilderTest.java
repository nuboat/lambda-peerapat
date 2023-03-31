package cc.peerapat.yoda.jdbc;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.testng.annotations.Test;

@Slf4j
public class JdbcSQLBuilderTest extends JdbcSQLBuilder  {

    @Test
    public void buildSuccessTest() {
        val actual = toJdbcClass("cc.peerapat.accounts"
        , "AccountEntity"
        , "accounts"
        , "id,client_id"
        , "Long id, Long client_id, String username, String password_hash".split(","));

        System.out.println(actual);
    }

}
