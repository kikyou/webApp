package org.baidu.ecom.utils

import java.util.concurrent.Semaphore

/**
  * Created by baidu on 2017/1/23.
  */
object SemaphoreTest {
  def main(args: Array[String]): Unit = {
    val eventLock = new Semaphore(0)

    val threads = (1 to 10).map(index => new Thread() {
      override def run(): Unit = {
        val num = index
        eventLock.acquire()
        println(s"$num")
        Thread.sleep(1000)
        eventLock.release()
      }
    })

    eventLock.release()
    threads.foreach(t => t.start())

    Thread.sleep(100000)
  }
}
