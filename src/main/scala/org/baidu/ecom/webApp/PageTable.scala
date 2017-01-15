package org.baidu.ecom.webApp

/**
  * Created by baidu on 2017/1/15.
  */

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import scala.xml.{Node, Unparsed}

/**
  * Created by baidu on 2017/1/8.
  */
class PageTable extends AbstractHandler {
  //@throws[IOException]
  //@throws[ServletException]
  def handle(target: String, baseRequest: Request, request: HttpServletRequest,
             response: HttpServletResponse) {
    response.setContentType("text/html;charset=utf-8")
    response.setStatus(HttpServletResponse.SC_OK)
    baseRequest.setHandled(true)
    response.getWriter.println("<h1>Hello World</h1>")
    response.getWriter.println(table())
  }

  def table(): Seq[Node] = {
    <li class="disabled"><a href="#">10</a></li>
    <div>
      <table class="23" id="45">
        <tbody>
        </tbody>
      </table>
    </div>
  }

}

object PageTable {

  def main(args: Array[String]): Unit = {
    val server: Server = new Server(8080)
    server.setHandler(new PageTable)
    server.start()
    server.join()
  }
}
