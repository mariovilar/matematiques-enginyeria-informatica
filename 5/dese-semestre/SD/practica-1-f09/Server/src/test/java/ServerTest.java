import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import utils.ComUtils;
import p1.server.Server;

/**
 * Unit test for the {@link p1.server.Server} class.
 * This test ensures that a server instance can be started and that a client
 * can establish a connection to it.
 */
public class ServerTest {

    /**
     * Example test to verify that the server starts correctly and
     * accepts client connections.
     *
     * This test does the following:
     * 1. Starts a server on port 8081 in a separate thread.
     * 2. Attempts to connect to the server using a client socket.
     * 3. Verifies that the connection is established successfully.
     * 4. Initializes {@link utils.ComUtils} to ensure communication setup.
     */
    @Test
    public void example_server_test() {

        boolean connected = false;

        // Start the server in a separate thread to avoid blocking the test execution.
        (new Thread() {
            @Override
            public void run() {
                Server server = new Server(8081);
                server.init(false); // Keep the server running and accepting connections.
            }
        }).start();

        try {

            for (int connectionTry = 0; connectionTry < 3 && !connected; connectionTry++) {

                // Attempt to establish a connection to the running server.
                try (Socket connection = new Socket("localhost", 8081)) {
                    assertNotNull(connection);// Ensure the connection is successfully established.

                    // Initialize ComUtils to check if input and output streams are accessible.
                    new ComUtils(connection.getInputStream(), connection.getOutputStream());

                    connected = true;
                } catch (IOException e) {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace(); // Print stack trace if an I/O error occurs during the connection.
        }
        if (!connected) {
            fail("Failed to establish connection with the server.");
        }
    }
}