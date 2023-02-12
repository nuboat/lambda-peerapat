package cc.peerapat.yoda.jdbc;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Slf4j
class TxnSQLBuilderTest extends JdbcSQLBuilder  {

    @BeforeClass
    public void setUp() {
        // code that will be invoked when this test is instantiated
    }

    @Test(groups = { "success" })
    public void buildSuccessTest() {
        val input = "package=com.kiatnakinbank.ibn.repos.based \n" +
                "class=TxnEntity \n" +
                "table=transactions \n" +
                "primary_keys=id, client_id \n" +
                "columns=String id, Long client_id, Long account_id, Integer txn_state_id, Integer txn_type_id, Integer document_type_id" +
                "      , Integer kyc_method, Integer channel_id, String idp_level, String branch_code, String device_id, String citizen_id" +
                "      , String full_name_en, String full_name_th, String birth_date, String mobile_no, String email, Boolean is_export" +
                "      , String ref_a, String ref_b, String ref_c, LocalDateTime expired, Long creator_id, LocalDateTime created\n" +
                "";

        val actual = toJdbcClass(input.split("\n"));

        System.out.println(actual);
    }

}

//    full_name_en VARCHAR(128),
//    full_name_th VARCHAR(128),
//    birth_date VARCHAR(8),
//    mobile_no VARCHAR(16),
//    email VARCHAR(256),
//    is_export BOOLEAN,
//    ref_a VARCHAR(32),
//    ref_b VARCHAR(32),
//    ref_c VARCHAR(32),
//    expired TIMESTAMP WITH TIME ZONE NOT NULL,
//    creator_id BIGINT,
//    created TIMESTAMP WITH TIME ZONE NOT NULL,
