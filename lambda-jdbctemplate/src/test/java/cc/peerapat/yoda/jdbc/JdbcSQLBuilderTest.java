package cc.peerapat.yoda.jdbc;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.testng.annotations.*;

@Slf4j
class JdbcSQLBuilderTest extends JdbcSQLBuilder  {

    @BeforeClass
    public void setUp() {
        // code that will be invoked when this test is instantiated
    }

    @Test(groups = { "success" })
    public void buildSuccessTest() {
        val input = "package=cc.peerapat.accounts \n" +
                "class=AccountEntity \n" +
                "table=accounts \n" +
                "primary_keys=id,client_id \n" +
                "columns=Long id, Long client_id, String username, String password_hash \n" +
                "";

        val actual = toJdbcClass(input.split("\n"));

        System.out.println(actual);
    }

}
