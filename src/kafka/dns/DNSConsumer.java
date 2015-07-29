package kafka.dns;

/**
 * Created by WeiChen on 2015/7/28.
 */

import com.fasterxml.jackson.databind.node.ObjectNode;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DNSConsumer extends  Thread{
    private final ConsumerConnector consumer;
    private final String topic;

    public static void main(String[] args) {
        DNSConsumer consumerThread = new DNSConsumer("dns.raw");
        consumerThread.run();
    }

    public DNSConsumer(String topic) {
        consumer = kafka.consumer.Consumer
                .createJavaConsumerConnector(createConsumerConfig());
        this.topic = topic;
    }

    private static ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", "localhost:2181/kafka");
        props.put("group.id", "GroupDns");
        props.put("zookeeper.session.timeout.ms", "400000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        return new ConsumerConfig(props);

    }

    public void run() {
        DNSRawParser dp = new DNSRawParser();
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        //Define single thread for topic
        topicCountMap.put(topic, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer
                .createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        int noAns = 0, hasAns = 0;
        while (it.hasNext()) {
            System.out.println("INFO Get Message!");
            String raw = new String(it.next().message());
            dp.initialize(new ObjectNode(null));
            try {
                Map<String, String> dataMap = dp.parse(raw);
                String ans = dataMap.get("AnsDATA");
                if(ans.equalsIgnoreCase("empty")){
                    noAns++;
                } else {
                    hasAns++;
                }
                System.out.println("INFO No Ans:" + noAns);
                System.out.println("INFO Has Ans: " + hasAns);
                System.out.println("---------------");
            }catch(Exception e){
                System.out.println(raw);
                e.printStackTrace();
                continue;
            }
        }

    }
}
