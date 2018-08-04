# launch zookeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# start kafka
bin/kafka-server-start.sh config/server.properties

# create the topics
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-input
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-output

# Start consumer on the input topic
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic word-count-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.serializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.serializer=org.apache.kafka.common.serialization.LongDeserializer

# Start kafka streams application

# start a kafka producer
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic word-count-input
# Enter
Hello Domenico
Ciao Domenico
Salut Domenico !
