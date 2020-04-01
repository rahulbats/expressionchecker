package com.confluent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class ExpressionChecker {
    public static void main(final String[] args) throws Exception {
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ritchie-offset-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS"));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""+System.getenv("API_KEY")+"\" password=\""+System.getenv("SECRET")+"\";");
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream(System.getenv("topic"));
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(System.getenv("expression"));
        textLines
                .filter((key, value) -> {
                    // Now here's where the story begins...
                    ExpressionEvaluator ee = new ExpressionEvaluator();

                    // The expression will have two "int" parameters: "a" and "b".
                    ee.setParameters(new String[] { "$" }, new Class[] { JsonNode.class });

                    // And the expression (i.e. "result") type is also "int".
                    ee.setExpressionType(Boolean.class);

                    // And now we "cook" (scan, parse, compile and load) the fabulous expression.
                    try {

                        ee.cook(System.getenv("expression"));
                        // Eventually we evaluate the expression - and that goes super-fast.
                         boolean result =  (Boolean) ee.evaluate(new Object[] { mapper.readTree(value) });
                        //System.out.println("expression result "+result);
                         return result;
                    } catch (CompileException e) {
                        e.printStackTrace();
                    } catch (Parser.ParseException e) {
                        e.printStackTrace();
                    } catch (Scanner.ScanException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return false;

                })
                .mapValues(value -> {
                    System.out.println(value);
                    return value;
                });


        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                streams.close();
            }
        }));
    }
}
