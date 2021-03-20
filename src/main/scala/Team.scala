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
    loop()

    @tailrec
    def loop(): Unit = {
      val br = new BufferedReader(new InputStreamReader(System.in))
      print("Enter order: ")
      br.readLine match {
        case null | "exit" =>
          ()
        case other if Utils.availableItems.contains(other) =>
          publishOrder(other)
          loop()
        case other =>
          println(s"Invalid item name. Type one of following: ${Utils.availableItems.mkString(", ")}")
          loop()
      }
    }

    def publishOrder(itemToOrder: String): Unit = {
      val message = s"${itemToOrder}_$teamName"
      channel.basicPublish(exchangeName, itemToOrder, null, message.getBytes("UTF-8"))
      println("Sent: " + message)
    }
  }
}
