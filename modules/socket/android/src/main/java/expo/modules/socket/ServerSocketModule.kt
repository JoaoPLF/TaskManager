package expo.modules.socket

import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class ServerSocketModule : Module() {
    private val PORT = 3000
    private var serverSocket: ServerSocket? = null

    override fun definition() = ModuleDefinition {
        Name("ServerSocket")

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

        AsyncFunction("startServer") { promise: Promise ->
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    serverSocket = ServerSocket(PORT)
                    println("SERVERSOCKET - Starting server")

                    while (true) {
                        val clientSocket: Socket = serverSocket!!.accept()
                        println("SERVERSOCKET - Client connected")

                        val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                        val output = PrintWriter(clientSocket.getOutputStream(), true)

                        var message: String?
                        while (input.readLine().also { message = it } != null) {
                            println("SERVERSOCKET - Received $message")
                        }

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
