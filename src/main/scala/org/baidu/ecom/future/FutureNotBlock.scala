package org.baidu.ecom.future

/**
  * Created by baidu on 2017/1/24.
  */
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

object FutureNotBlock extends App{
  println("starting calculation ...")
  val f = Future {
    val id = Thread.currentThread().getId
    println(s"start computation id=$id")
    Thread.sleep(Random.nextInt(500))
    42
  }

  println(s"before onComplete flag=${f.isCompleted}")
  f.onComplete{
    case Success(value) => {
      val id = Thread.currentThread().getId
      // the id here is equal to id in f
      // which means its the same thread in callback and computation
      println(s"Got the callback, meaning = $value id = $id")
    }
    case Failure(e) => e.printStackTrace
  }

  f.onSuccess({
    case v => {
      val id = Thread.currentThread().getId
      println(s"Got the succeed callback, meaning = $v id = $id")
    }
  })

  f.onSuccess({
    case v => {
      val id = Thread.currentThread().getId
      // the callback si unordered
      println(s"Got the next succeed callback, meaning = $v id = $id")
    }
  })

  // do the rest of your work
  val id = Thread.currentThread().getId
  println(s"start wait id=$id")
  println("A ...")
  Thread.sleep(100)
  println("B ....")
  Thread.sleep(100)
  println("C ....")
  Thread.sleep(100)
  println("D ....")
  Thread.sleep(100)
  println("E ....")
  Thread.sleep(100)

  Thread.sleep(2000)
}
