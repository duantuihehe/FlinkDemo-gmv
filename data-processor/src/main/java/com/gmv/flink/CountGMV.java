package com.gmv.flink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmv.flink.bean.Order;
import com.gmv.flink.sink.SocketSink;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.Window;

import java.util.Collection;
import java.util.Objects;

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
        SingleOutputStreamOperator<Order> order = ds.map(new MapFunction<String, Order>() {
            @Override
            public Order map(String s) {
                Order order=null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    order = objectMapper.readValue(s, Order.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return order;
            }
        }).filter(Objects::nonNull);

        order.print();
//         map.addSink(new SocketSink("localhost", 9000)).setParallelism(1);
        env.execute();
    }
}
