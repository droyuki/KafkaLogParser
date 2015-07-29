package kafka.dns;

/**
 * Created by WeiChen on 2015/7/28.
 */


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONParser implements Parser{
    public static final ObjectMapper mapper = new ObjectMapper(new JsonFactory());

    public JSONParser() {
    }

    @Override
    public void initialize(ObjectNode root) {
    }

    @Override
    public Map<String, String> parse(String body) {
        Map<String, String> bodyMap = null;
        try {
            bodyMap = mapper.readValue(body,
                    new TypeReference<HashMap<String, String>>() {
                    });
        } catch (JsonParseException e) {
            return null;
        } catch (JsonMappingException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return bodyMap;
    }

}
