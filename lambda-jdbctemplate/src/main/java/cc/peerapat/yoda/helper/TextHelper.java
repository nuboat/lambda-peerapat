package cc.peerapat.yoda.helper;

import lombok.val;

import java.util.regex.Pattern;


public interface TextHelper {

    default String snakeToCamel(final String snake) {
        val camel  = Pattern.compile("_([a-z])")
                .matcher(snake)
                .replaceAll(m -> m.group(1).toUpperCase());

        return camel;
    }

}
