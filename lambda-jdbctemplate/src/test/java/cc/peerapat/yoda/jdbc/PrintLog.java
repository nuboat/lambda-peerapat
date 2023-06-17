package cc.peerapat.yoda.jdbc;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class PrintLog implements LambdaLogger {

    @Override
    public void log(String s) {
        System.out.println(s);
    }

    @Override
    public void log(byte[] bytes) {
        System.out.println(new String(bytes));
    }

}
