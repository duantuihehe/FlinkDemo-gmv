package com.gmv.flink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmv.flink.bean.Order;
import com.gmv.flink.sink.SocketSink;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.Objects;

public class CountGMV {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //方便调试
        env.setParallelism(1);
        // build source
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("iZbp1fovdmzckhw8ihqou1Z:9092")
                .setTopics("test")
                .setGroupId("test")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        // build watermark
        WatermarkStrategy<String> orderWatermarkStrategy = WatermarkStrategy
                .<String>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                .withTimestampAssigner(new SerializableTimestampAssigner<String>() {
                    @Override
                    public long extractTimestamp(String s, long l) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Order o = null;
                        try {
                             o = objectMapper.readValue(s, Order.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return o==null?Long.MIN_VALUE:o.getOrderTime().getTime();
                    }
                })
                ;

        SingleOutputStreamOperator<Order> order = env.fromSource(source
                ,orderWatermarkStrategy
                , "kafka-source")
                .process(new ProcessFunction<String, Order>() {
            @Override
            public void processElement(String s, Context context, Collector<Order> collector) throws Exception {
//                System.out.println(context.timerService().currentWatermark());
                Order order = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    order = objectMapper.readValue(s, Order.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                collector.collect(order);
            }
        }).filter(Objects::nonNull);

        order.windowAll( TumblingEventTimeWindows.of(Time.milliseconds(10000)))
                .sum("price")
                .addSink(new SocketSink("localhost", 9000)).setParallelism(1);
        env.execute();
    }
}
