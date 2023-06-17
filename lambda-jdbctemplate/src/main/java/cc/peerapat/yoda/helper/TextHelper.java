package cc.peerapat.yoda.helper;

import java.util.regex.Pattern;

public interface TextHelper {

    default String snakeToCamel(final String snake) {
        return Pattern.compile("_([a-z])")
                .matcher(snake)
                .replaceAll(m -> m.group(1).toUpperCase());
    }

    default String camelToSnake(final String camel) {
        return camel.replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

    default String f(final String txt, final Object... args) {
        return String.format(txt, args);
    }

}
