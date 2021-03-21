package com.kpodsiad

import com.rabbitmq.client.{AMQP, DefaultConsumer, Envelope}

import java.io.{BufferedReader, InputStreamReader}
import scala.annotation.tailrec

object Admin extends App {
  private val (channel, exchangeName) = Utils.getChannel
  private val queueName = channel.queueDeclare().getQueue
  channel.queueBind(queueName, exchangeName, "#")
  private val consumer: DefaultConsumer =  new DefaultConsumer(channel) {
    val omit = Set(Utils.teamAdministrationKey, Utils.supplierAdministrationKey)
    override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
      if (!omit.contains(envelope.getRoutingKey)) {
        println(new String(body, "UTF-8"))
      }
    }
  }
  channel.basicConsume(queueName, consumer)

  private val modes = Set("S", "T", "A")
  private var mode = "A"
  loop()

  @tailrec
  private def loop(): Unit = {
    val br = new BufferedReader(new InputStreamReader(System.in))
    print("Enter mode or message: \n")
    br.readLine match {
      case null | "exit" => ()
      case maybeMode if modes contains maybeMode => mode = maybeMode; println(s"Changed mode to $mode"); loop()
      case other => sendMessage(other); loop()
    }
  }

  private def sendMessage(str: String): Unit = {
    val publish: String => Unit = channel.basicPublish(exchangeName, _, null, str.getBytes("UTF-8"))
    mode match {
      case "S" => publish(Utils.supplierAdministrationKey)
      case "T" => publish(Utils.teamAdministrationKey)
      case "A" => publish(Utils.supplierAdministrationKey); publish(Utils.teamAdministrationKey)
    }
  }
}
