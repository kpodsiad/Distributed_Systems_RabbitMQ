package com.kpodsiad

import com.rabbitmq.client.{BuiltinExchangeType, Channel, ConnectionFactory}

object Utils {
  val availableItems = Set("boots", "oxygen", "backpack")
  private val ExchangeName = "supply_chain"
  private val factory = new ConnectionFactory
  factory.setHost("localhost")

  def getChannel: (Channel, String) = {
    val channel = factory.newConnection.createChannel
    channel.exchangeDeclare(ExchangeName, BuiltinExchangeType.DIRECT)
    (channel, ExchangeName)
  }
}
