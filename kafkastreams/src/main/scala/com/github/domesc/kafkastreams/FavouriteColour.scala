package com.github.domesc.kafkastreams

import java.util.Properties

import com.lightbend.kafka.scala.streams.DefaultSerdes._
import com.lightbend.kafka.scala.streams.ImplicitConversions._
import com.lightbend.kafka.scala.streams.{KStreamS, StreamsBuilderS}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

/**
  * Take the comma separated topic of (userId, colour)
  * - filter out bad data
  * - keep only  green, red or blue
  * - get the running count of the favourite colour and output this to a topic
  * (a user count can change)
  */
object FavouriteColour extends App {

  val config = new Properties()
  config.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-colour-app")
  config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
  config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)

  val inputTopic = "favourite-colour"
  val intermediateTopic = "user-colour"
  val outputTopic = "favourite-colour-count"

  val builder = new StreamsBuilderS()
  val favouriteColourStream: KStreamS[String, String] = builder.stream[String, String](inputTopic)

  favouriteColourStream.map { case (key, value) =>
      val Array(userId, colour) = value.split(",")
      (userId, colour) }
    .filter((_, colour) => colour == "green" || colour == "red" || colour == "blue")
    .to(intermediateTopic)

  val userColourTable = builder
    .table[String, String](intermediateTopic)
    .groupBy{ case (_, colour) => (colour, colour)}
    .count()

  userColourTable.toStream.to(outputTopic)

  val kafkaSApp = new KafkaStreams(builder.build(), config)

  kafkaSApp.start()

  //shutdown the application gracefully
  Runtime.getRuntime.addShutdownHook(new Thread(() => kafkaSApp.close()))
}
