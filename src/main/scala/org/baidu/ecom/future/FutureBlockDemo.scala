package org.baidu.ecom.future

/**
  * Created by baidu on 2017/1/24.
  */
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object FutureBlockDemo extends App{
  implicit val baseTime = System.currentTimeMillis

  // create a Future
  val f = Future {
    Thread.sleep(500)
    1+1
  }
  // this is blocking(blocking is bad)
  val result = Await.result(f, 1 second)
  // 如果Future没有在Await规定的时间里返回,
  // 将抛出java.util.concurrent.TimeoutException
  println(result)
  Thread.sleep(1000)
}
