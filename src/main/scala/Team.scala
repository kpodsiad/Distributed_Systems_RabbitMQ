package com.kpodsiad

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
    channel.basicConsume(teamName, true, Utils.printingConsumer(channel))

    val administrationQueue = channel.queueDeclare.getQueue
    channel.queueBind(administrationQueue, exchangeName, Utils.teamAdministrationKey)
    channel.basicConsume(administrationQueue, true, Utils.printingConsumer(channel))

    loop()

    @tailrec
    def loop(): Unit = {
      val br = new BufferedReader(new InputStreamReader(System.in))
      print("Enter order: \n")
      br.readLine match {
        case null | "exit" => ()
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
