package com.gmv.flink;

import com.gmv.flink.sink.SocketSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CountGMV {
    public static void main(String[] args) throws Exception {
        System.out.println("hello flink !!");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("iZbp1fovdmzckhw8ihqou1Z:9092")
                .setTopics("test")
                .setGroupId("test")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStreamSource<String> ds = env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka-source");
        SingleOutputStreamOperator<Double> map = ds.map(new MapFunction<String, Double>() {
            @Override
            public Double map(String s) {
                Double gmv = 0.0;
                try {
                    gmv = new Double(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return gmv;
            }
        });
         map.addSink(new SocketSink("localhost", 9000)).setParallelism(1);
        env.execute();
    }
}
