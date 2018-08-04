# launch zookeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# start kafka
bin/kafka-server-start.sh config/server.properties

# create the topics
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic favourite-colour
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic user-colour --config cleanup.policy=compact
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic favourite-colour-count --config cleanup.policy=compact

# Start consumer on the output topic
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic favourite-colour-count \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.serializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.serializer=org.apache.kafka.common.serialization.LongDeserializer

# Start kafka streams application

# Start a kafka producer
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic favourite-colour
## Insert text
domenico,blue
carlo,green
domenico,red
paolo,red