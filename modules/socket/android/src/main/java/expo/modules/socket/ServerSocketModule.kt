package expo.modules.socket

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.ServerSocket

class ServerSocketModule : Module() {
    override fun definition() = ModuleDefinition {
        Name("ServerSocket")

        AsyncFunction("startServer") { promise: Promise ->
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val socket = DatagramSocket(Constants.SERVER_BROADCAST_PORT)
                    println("SERVERSOCKET - Listening to broadcast")

                    while (true) {
                        val buffer = ByteArray(1024)
                        val packet = DatagramPacket(buffer, buffer.size)

                        socket.receive(packet)
                        println("SERVERSOCKET - Received broadcast packet")

                        val receivedMessage = String(packet.data, 0, packet.length)
                        if (receivedMessage == Constants.SERVER_DISCOVERY_MESSAGE) {
                            val manager = appContext.reactContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                            val link = manager.getLinkProperties(manager.activeNetwork) as LinkProperties
                            val hostname = link.linkAddresses.firstOrNull() { linkAddress -> linkAddress.address.hostAddress?.contains('.') ?: false }?.address?.hostAddress ?: "NOT_FOUND"

                            val responsePacket = DatagramPacket(
                                hostname.toByteArray(),
                                hostname.length,
                                packet.address,
                                packet.port
                            )

                            println("SERVERSOCKET - Sending broadcast response")
                            socket.send(responsePacket)
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val serverSocket = ServerSocket(Constants.SERVER_SOCKET_PORT)

                    while (true) {
                        println("SERVERSOCKET - Listening to clients")

                        val clientSocket = serverSocket.accept()
                        val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                        val output = PrintWriter(clientSocket.getOutputStream(), true)

                        var message: String?
                        while (input.readLine().also { message = it } != null) {
                            println("SERVERSOCKET - Received $message")
                        }

                        println("SERVERSOCKET - Sending message")
                        output.println("Thank you")

                        input.close()
                        output.close()
                        clientSocket.close()
                    }
                }

                promise.resolve()
            } catch (e: Exception) {
                promise.reject("SERVER_SOCKET_EXCEPTION", "Could not create server socket", e)
            }
        }
    }
}
