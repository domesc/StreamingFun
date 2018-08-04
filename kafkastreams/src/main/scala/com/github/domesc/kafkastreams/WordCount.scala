package com.github.domesc.kafkastreams

import java.util.Properties

import com.lightbend.kafka.scala.streams.DefaultSerdes._
import com.lightbend.kafka.scala.streams.ImplicitConversions._
import com.lightbend.kafka.scala.streams.{KStreamS, KTableS, StreamsBuilderS}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object StreamingApp extends App {
  val config = new Properties()
  config.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-application")
  config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
  config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)

  val builder = new StreamsBuilderS()
  val wordCountStream: KStreamS[String, String] = builder.stream[String, String]("word-count-input")

  val wordCounts: KTableS[String, Long] = wordCountStream
    .mapValues(_.toLowerCase())
    .flatMapValues(_.split(" "))
    .selectKey{ case (key, value) => value}
    .groupByKey
    .count("Counts")

  wordCounts.toStream.to("word-count-output")
  val kafkaSApp = new KafkaStreams(builder.build(), config)

  kafkaSApp.start()

  //print the topology
  println(kafkaSApp.localThreadsMetadata())

  //shutdown the application gracefully
  Runtime.getRuntime.addShutdownHook(new Thread(() => kafkaSApp.close()))

}
