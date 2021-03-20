package com.kpodsiad

import com.rabbitmq.client.{AMQP, DefaultConsumer, Envelope}

import java.io.{BufferedReader, InputStreamReader}
import scala.annotation.tailrec

object Team {
  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      println("Wrong arguments, run program with team's name")
      System.exit(1)
    }

    val teamName = args(0)
    val (channel, exchangeName) = Utils.getChannel
    channel.queueDeclare(teamName, false, false, false, null)
    channel.queueBind(teamName, exchangeName, teamName)
    val consumer: DefaultConsumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        println(new String(body, "UTF-8"))
      }
    }
    channel.basicConsume(teamName, consumer)
    loop()

    @tailrec
    def loop(): Unit = {
      val br = new BufferedReader(new InputStreamReader(System.in))
      print("Enter order: ")
      br.readLine match {
        case null | "exit" =>
          ()
        case item if Utils.availableItems.contains(item) =>
          publishOrder(item)
          loop()
        case _ =>
          println(s"Invalid item name. Type one of following: ${Utils.availableItems.mkString(", ")}")
          loop()
      }
    }

    def publishOrder(itemToOrder: String): Unit = {
      val message = s"$itemToOrder#$teamName"
      channel.basicPublish(exchangeName, itemToOrder, null, message.getBytes("UTF-8"))
      println("Sent: " + message)
    }
  }
}
