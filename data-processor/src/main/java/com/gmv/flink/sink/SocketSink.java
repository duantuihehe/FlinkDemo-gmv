package com.gmv.flink.sink;

import com.corundumstudio.socketio.SocketIOServer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class SocketSink  extends RichSinkFunction<Double> {
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
    public void invoke(Double value, Context context) {
socket.getBroadcastOperations().sendEvent("message",value);
    }

    @Override
    public void close() throws Exception {
        super.close();
        socket.stop();
    }
}
