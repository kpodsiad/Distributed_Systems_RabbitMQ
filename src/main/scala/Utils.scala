package com.kpodsiad

import com.rabbitmq.client.{AMQP, BuiltinExchangeType, Channel, ConnectionFactory, DefaultConsumer, Envelope}

object Utils {
  val availableItems = Set("boots", "oxygen", "backpack")
  val teamAdministrationKey = "adminTeam"
  val supplierAdministrationKey = "adminSupplier"

  private val ExchangeName = "supply_chain"
  private val factory = new ConnectionFactory
  factory.setHost("localhost")

  def getChannel: (Channel, String) = {
    val channel = factory.newConnection.createChannel
    channel.exchangeDeclare(ExchangeName, BuiltinExchangeType.TOPIC)
    (channel, ExchangeName)
  }

  def printingConsumer(channel: Channel): DefaultConsumer = new DefaultConsumer(channel) {
    override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
      println(new String(body, "UTF-8"))
    }
  }
}
