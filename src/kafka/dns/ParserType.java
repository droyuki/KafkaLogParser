package kafka.dns;

/**
 * Created by WeiChen on 2015/7/28.
 */
public enum ParserType {
    JSON(JSONParser.class);
    private final Class<? extends Parser> parserClass;

    private ParserType(Class<? extends Parser> parserClass) {
        this.parserClass = parserClass;
    }

    public Class<? extends Parser> getParserClass() {
        return parserClass;
    }
}
