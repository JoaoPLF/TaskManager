package expo.modules.socket

import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket

class SocketModule : Module() {
  private var serverAddress: String? = null

  override fun definition() = ModuleDefinition {
    Name("Socket")

    AsyncFunction("findServer") { promise: Promise ->
      val socket = DatagramSocket(Constants.CLIENT_BROADCAST_PORT)
      val broadcastAddress = InetAddress.getByName("255.255.255.255")
      val serverDiscoveryMessage = Constants.SERVER_DISCOVERY_MESSAGE

      val packet = DatagramPacket(
        serverDiscoveryMessage.toByteArray(),
        serverDiscoveryMessage.length,
        broadcastAddress,
        Constants.SERVER_BROADCAST_PORT
      )
      socket.send(packet)
      println("SOCKET - Finding server")

      val buffer = ByteArray(1024)
      val responsePacket = DatagramPacket(buffer, buffer.size)

      socket.receive(responsePacket)
      val response = String(responsePacket.data, 0, responsePacket.length)
      println("SOCKET - Server found, $response")

      if (response != Constants.SERVER_NOT_FOUND_MESSAGE) {
        serverAddress = response
      }

      socket.close()

      promise.resolve()
    }

    AsyncFunction("startClient") { promise: Promise ->
      try {
        if (serverAddress.isNullOrEmpty()) {
          promise.reject("SERVER_UNAVAILABLE", "There is no server to connect to", Throwable())
          return@AsyncFunction
        }

        val socket = Socket(serverAddress, Constants.SERVER_SOCKET_PORT)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        println("SOCKET - Sending message")

        output.println("Hello")
        val response = input.readLine()

        println("SOCKET - Received $response")

        input.close()
        output.close()
        socket.close()

        promise.resolve()
      } catch (e: Exception) {
        promise.reject("SOCKET_EXCEPTION", "Could not create socket", e)
      }
    }
  }
}
