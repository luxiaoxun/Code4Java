# 环境变量
| 变量名 | 描述 | 是否必须 | 示例 |
| ---- | ---- | ---- | ---- |
| JAVA_OPTS | java 启动参数 | 否 | "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8899" |
| KAFKA_URL | 审计日志发送到 kafka 服务的地址。不配置时，审计日志输出到标准输出流 | 否 | 10.10.10.10:9092 |
| KAFKA_TOPIC | 审计日志发送到 kafka 服务的 topic。如果 KAFKA_URL 没有配置，该项不生效，也可以不配置 | 否 | topic_test |
