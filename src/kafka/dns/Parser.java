package kafka.dns;

import java.util.Map;
import com.fasterxml.jackson.databind.node.ObjectNode;
/**
 * Created by WeiChen on 2015/7/28.
 */
public interface Parser {
    public void initialize(ObjectNode root);
    public Map<String, String> parse(String body);
}
