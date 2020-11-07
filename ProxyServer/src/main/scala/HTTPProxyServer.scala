package proxyserver

import java.io.{BufferedReader, InputStream, InputStreamReader, OutputStream}
import java.net.{ServerSocket, Socket}

import util.control.Breaks._

// Reference: https://www.codejava.net/java-se/networking/java-socket-server-examples-tcp-ip
object HTTPProxyServer extends App {
  var serverSocket: ServerSocket = null
  try {
    serverSocket = new ServerSocket(6868)
    while (true) {
      val clientSocket = serverSocket.accept()
      new ProxyThread(clientSocket).start()
    }
  } finally {
    serverSocket.close()
  }
}

class ProxyThread(srcSocket: Socket) extends Thread {
  override def run {
    var srcInput: InputStream = null //get input from client
    var srcOutput: OutputStream = null  //send response to client
    var dstInput: InputStream = null    //get response from server
    var dstOutput: OutputStream = null  //send request to server
    var dstSocket: Socket = null

    try {
      // Parse out the 'host' and 'port' from the HTTP request (format: https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages)
      srcInput = srcSocket.getInputStream()
      val srcInputReader =  new BufferedReader(new InputStreamReader(srcInput))
      val header = new StringBuilder()
      var hostLine: String = null
      var line = srcInputReader.readLine()
      breakable {
        while (line != null) {
          header.append(line)
          if (line.toLowerCase.contains("host")) {
            hostLine = line
            break
          }
          line = srcInputReader.readLine()
        }
      }
      val dstInfo = hostLine.split(" ")(1).split(":")
      val host = dstInfo(0)
      val port = if (dstInfo.length > 1) dstInfo(1).toInt else 80 //By default, HTTP uses port 80

      // Connect to the destination server
      dstSocket = new Socket(host, port)
      dstOutput = dstSocket.getOutputStream()
      dstInput = dstSocket.getInputStream()

      // send request to the destination server
      dstOutput.write(header.toString().getBytes())
      new RequestSenderThread(srcInput, dstOutput).start() // send the rest of request in a new thread

      // send response to client
      while (true) {
        srcOutput.write(dstInput.read())
      }
    } catch {
      case ex: Exception => println(ex)
    } finally {
      if (null != dstSocket) dstSocket.close() // will also close the socket's InputStream and OutputStream.
      if (null != srcInput) srcInput.close()
      if (null != srcOutput) srcOutput.close()
    }
  }
}

class RequestSenderThread(in: InputStream, out: OutputStream) extends Thread {
  override def run {
    while (true) {
      out.write(in.read())
    }
  }
}