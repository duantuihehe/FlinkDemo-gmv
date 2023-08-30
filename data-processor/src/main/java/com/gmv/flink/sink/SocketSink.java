package com.gmv.flink.sink;

import com.corundumstudio.socketio.SocketIOServer;
import com.gmv.flink.bean.Order;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class SocketSink  extends RichSinkFunction<Order> {
private SocketIOServer socket;
    private String hostName;
    private int port;

    public SocketSink(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        com.corundumstudio.socketio.Configuration conf = new com.corundumstudio.socketio.Configuration();
        conf.setHostname(hostName);
        conf.setPort(port);
         socket = new SocketIOServer(conf);
         socket.start();
    }

    @Override
    public void invoke(Order value, Context context) {
//        System.out.println(value);
        socket.getBroadcastOperations().sendEvent("message",value.getPrice());
    }

    @Override
    public void close() throws Exception {
        super.close();
        socket.stop();
    }
}
