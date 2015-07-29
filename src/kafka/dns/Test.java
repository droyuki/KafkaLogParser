package kafka.dns;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

/**
 * Created by WeiChen on 2015/7/28.
 */
public class Test {
    public static void main(String[] args) {
        String raw1 = "{\"_time\": \"1436975999\", \"_raw\": \"7/15/2015 11:59:59 PM 11A0 PACKET  0000000003AF57F0 UDP Snd 10.28.2.140     eba7 R Q [8085 A DR  NOERROR] PTR    (3)140(1)2(2)28(2)10(7)in-addr(4)arpa(0)\\nUDP response info at 0000000003AF57F0\\n  Socket = 336\\n  Remote addr 10.28.2.140, port 49495\\n  Time Query=321854, Queued=0, Expire=0\\n  Buf length = 0x0200 (512)\\n  Msg length = 0x0050 (80)\\n  Message:\\n    XID       0xeba7\\n    Flags     0x8580\\n      QR        1 (RESPONSE)\\n      OPCODE    0 (QUERY)\\n      AA        1\\n      TC        0\\n      RD        1\\n      RA        1\\n      Z         0\\n      CD        0\\n      AD        0\\n      RCODE     0 (NOERROR)\\n    QCOUNT    1\\n    ACOUNT    1\\n    NSCOUNT   0\\n    ARCOUNT   0\\n    QUESTION SECTION:\\n    Offset = 0x000c, RR count = 0\\n    Name      \\\"(3)140(1)2(2)28(2)10(7)in-addr(4)arpa(0)\\\"\\n      QTYPE   PTR (12)\\n      QCLASS  1\\n    ANSWER SECTION:\\n    Offset = 0x002a, RR count = 0\\n    Name      \\\"[C00C](3)140(1)2(2)28(2)10(7)in-addr(4)arpa(0)\\\"\\n      TYPE   PTR  (12)\\n      CLASS  1\\n      TTL    3600\\n      DLEN   26\\n      DATA   (8)perforce(2)tw(8)trendnet(3)org(0)\\n    AUTHORITY SECTION:\\n      empty\\n    ADDITIONAL SECTION:\\n      empty\"}";
        String raw="{\"_time\": \"1436975999\", \"_raw\": \"7/15/2015 11:59:59 PM 11A0 PACKET  0000000002048020 UDP Snd 10.28.70.13     d951 R Q [8085 A DR  NOERROR] PTR    (3)100(2)42(2)45(2)10(7)in-addr(4)arpa(0)\\nUDP response info at 0000000002048020\\n  Socket = 336\\n  Remote addr 10.28.70.13, port 65070\\n  Time Query=321854, Queued=0, Expire=0\\n  Buf length = 0x0200 (512)\\n  Msg length = 0x0056 (86)\\n  Message:\\n    XID       0xd951\\n    Flags     0x8580\\n      QR        1 (RESPONSE)\\n      OPCODE    0 (QUERY)\\n      AA        1\\n      TC        0\\n      RD        1\\n      RA        1\\n      Z         0\\n      CD        0\\n      AD        0\\n      RCODE     0 (NOERROR)\\n    QCOUNT    1\\n    ACOUNT    1\\n    NSCOUNT   0\\n    ARCOUNT   0\\n    QUESTION SECTION:\\n    Offset = 0x000c, RR count = 0\\n    Name      \\\"(3)100(2)42(2)45(2)10(7)in-addr(4)arpa(0)\\\"\\n      QTYPE   PTR (12)\\n      QCLASS  1\\n    ANSWER SECTION:\\n    Offset = 0x002b, RR count = 0\\n    Name      \\\"[C00C](3)100(2)42(2)45(2)10(7)in-addr(4)arpa(0)\\\"\\n      TYPE   PTR  (12)\\n      CLASS  1\\n      TTL    1200\\n      DLEN   31\\n      DATA   (13)sjdc-mbdiydb1(2)us(8)trendnet(3)org(0)\\n    AUTHORITY SECTION:\\n      empty\\n    ADDITIONAL SECTION:\\n      empty\"}";
        try {
            DNSRawParser dp = new DNSRawParser();
            dp.initialize(new ObjectNode(null));
            Map<String, String> dataMap = dp.parse(raw);
            System.out.println(dataMap.get("Domain"));
        }catch(Exception e){
            System.out.print(e);
        }
    }
}
