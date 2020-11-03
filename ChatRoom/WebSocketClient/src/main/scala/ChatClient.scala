package chatroom.websocket.client

import java.net.URI
import java.util.Scanner

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake


object ChatClient extends App {
  try {
    val input = new Scanner(System.in)
    println("What is your name?")
    val name = input.nextLine

    val uriStr = "ws://localhost:8080/chatroom"
    val URI = new URI(uriStr)
    var sessionID: String = null
    var isFinished = false
    val wsClient = new WebSocketClient(URI) {
      override def onOpen(serverHandshake: ServerHandshake): Unit = {
        println("onOpen: "+Thread.currentThread().getName+", "+Thread.currentThread().getPriority)
      }

      override def onMessage(s: String): Unit = {
        if(s.startsWith("ID:")) {
          sessionID = s.substring(3)
          this.send(sessionID+":Welcome "+name+"!")
        } else if (s != "Succeed") {
         println(s)
        }
      }

      override def onClose(i: Int, s: String, b: Boolean): Unit = {
        println("Chatroom is closed!")
        this.close()
        isFinished = true
      }

      override def onError(e: Exception): Unit = {}
    }
    wsClient.connect()

    while (sessionID == null) {
      Thread.sleep(100L) // must sleep!!!
    }

    while(!isFinished) {
      val message = input.nextLine()
      if (message == "EXIT") {
        isFinished = true
        wsClient.send(sessionID+":"+name+" has left!")
        wsClient.close()
      } else {
        wsClient.send(sessionID+":"+name+":"+message)
      }
    }
  } catch {
    case ex: Exception => println(ex)
  }
}
