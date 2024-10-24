package expo.modules.socket

import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SocketModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("Socket")

    // Sets constant properties on the module. Can take a dictionary or a closure that returns a dictionary.
    Constants(
      "PI" to Math.PI
    )

    // Defines event names that the module can send to JavaScript.
    Events("onChange")

    // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
    Function("hello") {
      "Hello world! 👋"
    }

    // Defines a JavaScript function that always returns a Promise and whose native code
    // is by default dispatched on the different thread than the JavaScript runtime runs on.
    AsyncFunction("setValueAsync") { value: String ->
      // Send an event to JavaScript.
      sendEvent("onChange", mapOf(
        "value" to value
      ))
    }

    AsyncFunction("startClient") { promise: Promise ->
      try {
        println("SOCKET - Connecting to server")

        val socket = Socket("10.0.2.2", 3000)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        println("SOCKET - Listening at ${socket.localSocketAddress}")

        output.println("Hello")
        val response = input.readLine()

        println("SOCKET - Received $response")

        input.close()
        output.close()
        socket.close()
      } catch (e: Exception) {
        promise.reject("SOCKET_EXCEPTION", "Could not create socket", e)
      }
    }
  }
}
