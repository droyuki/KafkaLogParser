package kafka.dns;

/**
 * Created by WeiChen on 2015/7/28.
 */

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DNSRawParser implements Parser {
    private static final Logger logger = Logger.getLogger(DNSRawParser.class);
    //private SimpleDateFormat timeFormatter;
    private Parser JSONParser;
    //public static final String TIME_FORMAT = "MM/dd/yyyy hh:mm:ss a";
    public static final String LF = "\n";

    @Override
    public void initialize(ObjectNode root) {
        //this.timeFormatter = new SimpleDateFormat(TIME_FORMAT);

        try {
            this.JSONParser = ParserFactory.newInstance("JSON");
        } catch (ClassNotFoundException e) {
            logger.error("Parser class not found. Exception follows.", e);
        } catch (InstantiationException e) {
            logger.error("Could not instantiate Parser. Exception follows.", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to access Parser. Exception follows.", e);
        }
    }

    @Override
    public Map<String, String> parse(String bodyJSON) {
        Map<String, String> bodyMapSplunkJSON = JSONParser.parse(bodyJSON);
        String raw = bodyMapSplunkJSON.get("_raw");
        String splunkTime = bodyMapSplunkJSON.get("_time");
        Map<String, String> bodyMap = new HashMap<String, String>();
        String[] data = raw.split(LF);
        String header = data[0];
        String body = data[1];
        bodyMap.put("rt", splunkTime);
        Map<String, String> tmpMap = new HashMap<String, String>();
        tmpMap = parseHeader(header);
        bodyMap.putAll(tmpMap);
        body = raw.replace(header, "");
        tmpMap.clear();
        //System.out.println("Body Map size=" + bodyMap.size());
        tmpMap = parseBody(bodyMap, body);
        bodyMap.putAll(tmpMap);
        return bodyMap;
    }

    private Map<String, String> parseBody(Map<String, String> bodyMap, String body) {
        String[] lines = body.split(LF);
        //System.out.println("LINE: " + lines.length);
        boolean msgSectionFlag = false, qSectionFlag = false, ansSectionFlag = false, autSectionFlag = false, addSectionFlag = false;
        ArrayList<String> msgSetcion = new ArrayList<>();
        ArrayList<String> qSection = new ArrayList<>();
        ArrayList<String> ansSection = new ArrayList<>();
        ArrayList<String> autSection = new ArrayList<>();
        ArrayList<String> addSection = new ArrayList<>();
        for (String line : lines) {
            //System.out.println("line:: " + line);
            if (line.contains("Message:")) {
                msgSectionFlag = true;
                continue;
            } else if (line.contains("QUESTION SECTION:")) {
                msgSectionFlag = false;
                qSectionFlag = true;
                continue;
            } else if (line.contains("ANSWER SECTION:")) {
                qSectionFlag = false;
                ansSectionFlag = true;
                continue;
            } else if (line.contains("AUTHORITY SECTION:")) {
                ansSectionFlag = false;
                autSectionFlag = true;
                continue;
            } else if (line.contains("ADDITIONAL SECTION:")) {
                autSectionFlag = false;
                addSectionFlag = true;
                continue;
            }
            if (msgSectionFlag)
                msgSetcion.add(line);
            if (qSectionFlag)
                qSection.add(line);
            if (ansSectionFlag)
                ansSection.add(line);
            if (autSectionFlag)
                autSection.add(line);
            if (addSectionFlag)
                addSection.add(line);

        }

        //parse msg section
        for (String msg : msgSetcion) {
            //replace strange char with ;
            String tmp = msg.replaceAll("([\\W]+)", ";");
            String key = tmp.split(";")[1];
            String value = tmp.split(";")[2];
            bodyMap.put("Msg_" + key, value);
        }

        //parse answer section
        for (String ans : ansSection) {
            int dataCounter = 1;
            if (ans.contains("empty")) {
                bodyMap.put("AnsDATA", "empty");
                break;
            } else if (ans.contains("DATA")) {
                //if answer is domain
                String answerData = ans.split("DATA")[1];
                if (answerData.contains("(0)")) {
                    String dataTmp = "(" + answerData.replaceAll("(\\W+)[(]", "\\|").split("\\|")[1];
                    System.out.println("data tmp=" + dataTmp);
                    String data = domainAnalyzer(dataTmp, "");
                    //System.out.println(data);
                    bodyMap.put("AnsDATA", data);
                    //System.out.println("\"Ans" + "DATA" + dataCounter + "\":\"" + data + "\",");
                    dataCounter++;
                    continue;
                } else { //answer is ip
                    String data = answerData.replaceAll("([\\W]+)", "\\|");
                    bodyMap.put("AnsDATA", data);
                }
            }
        }
        return bodyMap;
    }

    private Map<String, String> parseHeader(String body) {
        //System.out.println(body);

        Map<String, String> bodyMap = new HashMap<String, String>();
        String[] dateTime = body.split("/");
        String month = dateTime[0];
        String date = dateTime[1];
        int dtLength = month.length() + date.length() + 18;

        //get DateTime
        String dt = (String) body.subSequence(0, dtLength);
        bodyMap.put("DateTime", dt);
        body = (String) body.subSequence(dtLength + 1, body.length());


        //get MsgID
        String msgId = (String) body.subSequence(0, 4);
        bodyMap.put("MsgID", msgId);
        body = (String) body.subSequence(5, body.length());

        //get Packet
        String pkg = (String) body.subSequence(8, 24);
        bodyMap.put("PACKET", pkg);
        body = (String) body.subSequence(25, body.length());


        //get protocol
        String protocol = (String) body.subSequence(0, 3);
        bodyMap.put("Protocol", protocol);
        body = (String) body.subSequence(4, body.length());

        //get direction
        String dir = (String) body.subSequence(0, 3);
        bodyMap.put("Direction", dir);
        body = (String) body.subSequence(4, body.length());

        //get request ip
        String[] ipArray = body.split("\\.");

        //if this field start with::
        String ip = "";
        if (ipArray[0].startsWith(":")) {
            ip = ipArray[0].split("([\\s]+)")[0];
            bodyMap.put("RequestIP", ip);
        } else {
            ip = ipArray[0] + "." + ipArray[1] + "." + ipArray[2] + "." + ipArray[3].split(" ")[0];
            bodyMap.put("RequestIP", ip);
            body = (String) body.subSequence(ip.length(), body.length());
        }

        //get I don't know
        body.replace(ip, "");
        String sth1tmp = body.replaceAll("([\\W]+)", "\\|");
        String sth1 = sth1tmp.split("\\|")[1];
        body = body.split(sth1)[1];
        //get sth in []
        String sth2 = body.split("\\[")[1].split("\\]")[0];
        //body = body.split(sth2)[1];
        body = body.split(sth2)[1];

        //get Record type
        String rcType = body.split("\\(")[0].replaceAll("[\\W]", "");
        bodyMap.put("RecordType", rcType.replaceAll("([\\W])", ""));
        body = (String) body.subSequence(rcType.length(), body.length());

        //get Domain
        String domain = domainAnalyzer(body, "").replaceFirst("(^[\\W]+)", "");
        bodyMap.put("Domain", domain);

        return bodyMap;
    }

    //Analyze domain recursively
    private String domainAnalyzer(String domain, String result) {
        String domainLength = domain.split("\\(")[1].split("\\)")[0];
        int dml = Integer.parseInt(domainLength);
        if (dml == 0) {
            return result;
        } else {
            String domainName = (String) domain.subSequence(domainLength.length() + 2, dml + 4);
            domain = (String) domain.substring(dml + domainLength.length() + 2, domain.length());
            return domainAnalyzer(domain, result + domainName.replaceAll("\\(", "") + ".");
        }
    }
}
