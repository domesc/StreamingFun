package com.domesc


import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{KafkaProducer, KafkaProducerRecord}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.StringSerializer

import scala.util.Random

object Main extends App {

  val conf = ConfigFactory.load

  // Create a org.apache.kafka.clients.producer.KafkaProducer from properties defined in a Typesafe Config file
  val producer = KafkaProducer(
    Conf(new StringSerializer(), new StringSerializer(), acks = "all").withConf(conf)
  )

  for(i <- 0 to 1000){
    val record = KafkaProducerRecord("streaming_topic", Some("key"), Random.nextString(4))
    producer.send(record)
  }

}
