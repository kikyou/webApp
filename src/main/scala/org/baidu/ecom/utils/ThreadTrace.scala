package org.baidu.ecom.utils

import scala.collection.mutable.ArrayBuffer

/**
  * Created by baidu on 2017/1/18.
  */


class Test {
  import Test._
  println(num)
}

object Test {
  val num = 10
}

object ThreadTrace {
  def main(args: Array[String]): Unit = {
    var callStack = new ArrayBuffer[String]()

    Thread.currentThread().getStackTrace.foreach { ste =>
      println(ste.getClassName)
      println(ste.getMethodName)
      println(ste.getLineNumber)
    }

  }
}
