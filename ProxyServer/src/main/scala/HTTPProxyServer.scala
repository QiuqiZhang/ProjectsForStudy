package proxyserver

import java.io.{BufferedReader, BufferedWriter, File, FileWriter, InputStream, InputStreamReader, OutputStream}
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
      var hasReadHost = false
      while (srcInputReader.ready()) {
        val line = srcInputReader.readLine()
        header.append(line+"\r\n")
        if (!hasReadHost && line.toLowerCase.contains("host")) {
          hostLine = line
          hasReadHost = true
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

      // send response to client
      srcOutput = srcSocket.getOutputStream()
      breakable {
        while (true) {
          val ch = dstInput.read()
          if (ch < 0) break() // InputStream.read(): If no byte is available because the end of the stream has been reached, the value -1 is returned.
          srcOutput.write(ch)
        }
      }
    } catch {
      case ex: Exception => println("ProxyThread: "+ex)
    } finally {
      if (null != dstSocket) dstSocket.close() // will also close the socket's InputStream and OutputStream.
      if (null != srcInput) srcInput.close()
      if (null != srcOutput) srcOutput.close()
    }
  }
}