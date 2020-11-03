package chatroom.websocket.server

import java.util.concurrent.ConcurrentHashMap
import javax.websocket.server.ServerEndpoint
import org.springframework.stereotype.Component
import javax.websocket.{OnClose, OnMessage, OnOpen, Session}

@ServerEndpoint("/chatroom")
@Component
class WebSocketServerEndpoint

object WebSocketServerEndpoint {

  val clientMap = new ConcurrentHashMap[String, Session]()

  @OnOpen
  def onOpen(session: Session): Unit = {
    clientMap.put(session.getId, session)
    println("连接成功, "+session.getId)
    session.getBasicRemote.sendText("ID:"+session.getId)
  }

  @OnClose
  def onClose(session: Session): Unit = {
    println("连接关闭, " + session.getId)
    clientMap.remove(session.getId)
  }

  @OnMessage
  def onMsg(text: String): String = {
    val delimiterIdx = text.indexOf(':')
    if (delimiterIdx < 0) "ERROR: the id is missing!"
    else {
      val id = text.substring(0, delimiterIdx)
      val content = text.substring(delimiterIdx+1)
      println("id: "+id+", content: "+content)
      var isSuccessful = false
      val iterator = clientMap.entrySet().iterator()
      while (iterator.hasNext) {
        val client = iterator.next()
        if (client.getKey == id) isSuccessful = true
        else {
          client.getValue.getBasicRemote.sendText(content)
        }
      }
      if (isSuccessful) "Succeed"
      else "ERROR: the id is false!"
    }
  }

}
