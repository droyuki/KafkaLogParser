package kafka.dns;

/**
 * Created by WeiChen on 2015/7/28.
 */
import java.util.Locale;

public class ParserFactory {
    private static Class<? extends Parser> lookup(String name) {
        try {
            return ParserType.valueOf(name.toUpperCase(Locale.ENGLISH))
                    .getParserClass();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Instantiate specified class, either alias or fully-qualified class name.
     */
    @SuppressWarnings("unchecked")
    public static Parser newInstance(String name)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {

        Class<? extends Parser> clazz = lookup(name);
        if (clazz == null) {
            clazz = (Class<? extends Parser>) Class.forName(name);
        }
        return clazz.newInstance();
    }
}
