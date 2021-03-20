package com.kpodsiad

import com.rabbitmq.client._

import java.io.IOException

object Supplier {
  def main(args: Array[String]): Unit = {
    val (channel, exchangeName) = Utils.getChannel

    val arguments = args.toList.filter(Utils.availableItems.contains) ::: List("admin")
    arguments.foreach { queueName =>
      channel.queueDeclare(queueName, false, false, false, null)
      channel.queueBind(queueName, exchangeName, queueName)
    }

    val consumer: DefaultConsumer = new DefaultConsumer(channel) {
      var orderID = 0

      @throws[IOException]
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit =
        envelope.getRoutingKey match {
          case "admin" =>
            println(s"[Admin]: ${new String(body, "UTF-8")}")
          case _ =>
            val message = new String(body, "UTF-8")
            println(s"Received: $message, processing it as a ${orderID}_$message")
            channel.basicAck(envelope.getDeliveryTag, false)
            //        channel.basicPublish(ExchangeName, "", null, message.getBytes("UTF-8"))
            orderID += 1
        }
    }

    println("Waiting for orders...")
    arguments.foreach(channel.basicConsume(_, false, consumer))
  }
}
