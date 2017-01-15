package org.baidu.ecom.webApp

/**
  * Created by baidu on 2017/1/15.
  */

import java.util.{Timer, TimerTask}

class WebTimer {
  private val timer = new Timer(true)
  timer.schedule(new TimerTask {
    override def run(): Unit = {
      refresh()
    }
  }, 100, 100)

  private def refresh(): Unit = synchronized {
    val total = 60000
    val header = s"[Stage 356}:"
    val tailer = s"(21345 + 12345) / $total]"
    val w = 100 - header.length - tailer.length
    val bar = if (w > 0) {
      val percent = w * 21345 / total
      (0 until w).map { i =>
        if (i < percent) "=" else if (i == percent) ">" else " "
      }.mkString("")
    } else {
      ""
    }
    println(header + bar + tailer)
  }
}

object WebTimer {
  def main(args: Array[String]): Unit = {
    new WebTimer
    Thread.sleep(10000000)
  }
}