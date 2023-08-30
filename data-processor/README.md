
## data-process
flink 处理kafka数据

消费kafka数据经过计算将数据sink到socket 然后前端进行展示

window窗口10s触发一次

为了调试方便 并行度设置为1 sink的并行度为1否则socket服务端口被占用，服务无法启动

/resources/test_data.json 为测试数据 orderTime自行修改来推进watermark