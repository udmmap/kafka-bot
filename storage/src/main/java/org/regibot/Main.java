package org.regibot;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.regibot.action.Action;
import org.regibot.action.step.DoctorStep;
import org.regibot.action.step.InitStep;
import org.regibot.action.step.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static HikariDataSource ds;

    final static StreamsBuilder builder = new StreamsBuilder();
    final static CountDownLatch latch = new CountDownLatch(1);
    static KafkaStreams streams;

    static {
        try {
            Properties dsProp = new Properties();
            dsProp.load(ClassLoader.getSystemClassLoader().getResourceAsStream("datasource.properties"));
            HikariConfig configDs = new HikariConfig(dsProp);
            ds = new HikariDataSource(configDs);
        } catch(Exception e) {
            logger.error("Datasource exception", e);
        }
    }

    public static void main(String[] args){
        Step script = new InitStep().setNext(
                new DoctorStep()
        );

        Action act = new Action(script, ds);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "telegramPipe");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KStream inputStream = builder.stream("topicIn");
        inputStream
                .mapValues((ValueMapper<String, String>) act::perform)
                .to("topicOut");

        Topology topology = builder.build();

        logger.info(topology.describe().toString());

        streams = new KafkaStreams(topology, props);

        // catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);


    }
}
