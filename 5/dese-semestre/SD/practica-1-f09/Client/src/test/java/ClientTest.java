import org.junit.Test;
import static org.junit.Assert.*;
import utils.ComUtils;

import p1.client.Client;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;

/**
 * Unit test for the {@link p1.client.Client} class.
 * This test verifies its ability to connect to a server and send/receive data.
 */
public class ClientTest {

    /**
     * Inner class representing a simple echo server that listens on a given port.
     */
    class ServerEcho extends Thread {

        private int port;
        private ServerSocket ss;
        private Socket socket;
        private ComUtils comutils;

        /**
         * Initializes the echo server with a specified port and sets up the connection.
         *
         * @param port The port number the server listens on.
         */
        public ServerEcho(int port) {
            this.port = port;
            setConnection();

        }

        /**
         * Starts the server's execution loop.
         */
        public void run() {
            this.init();
        }

        /**
         * Gets the ComUtils instance for communication with the given socket.
         *
         * @param socket The socket associated with the client.
         * @return A ComUtils instance for data transmission.
         */
        public ComUtils getComutils(Socket socket) {
            if (comutils == null) {
                try {
                    comutils = new ComUtils(socket.getInputStream(), socket.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException("I/O Error when creating the ComUtils:\n" + e.getMessage());
                }
            }
            return comutils;
        }

        /**
         * Sets up the server socket to listen for incoming connections.
         */
        public void setConnection() {
            if (this.ss == null) {
                try {
                    ss = new ServerSocket(port);
                    System.out.println("Server up & listening on port " + port + "...\nPress Cntrl + C to stop.");
                } catch (IOException e) {
                    throw new RuntimeException("I/O error when opening the Server Socket:\n" + e.getMessage());
                }
            }
        }

        /**
         * Returns the client socket.
         *
         * @return The connected client socket.
         */
        public Socket getSocket() {
            return this.socket;
        }

        /**
         * Main server loop: accepts a client connection, reads an integer, echoes it
         * back, and then closes the connection.
         */
        private void init() {
            while (true) {
                try {
                    socket = ss.accept(); // Accepts an incoming client connection.
                    comutils = getComutils(socket);
                    System.out.println("Client accepted");
                    int readedInt = comutils.read_int32(); // Reads an integer from the client.
                    comutils.write_int32(readedInt); // Echoes the integer back to the client.
                    System.out.println("Closing server...");
                    socket.close(); // Closes the client connection.
                } catch (IOException e) {
                    throw new RuntimeException("I/O error when accepting a client:\n" + e.getMessage());
                } catch (SecurityException e) {
                    throw new RuntimeException("Operation not accepted:\n" + e.getMessage());
                } catch (IllegalBlockingModeException e) {
                    throw new RuntimeException("There is no connection ready to be accepted:\n" + e.getMessage());
                }

            }
        }
    }

    /**
     * Example test to verify client-server communication.
     * Starts a server, creates a client, sends an integer, and verifies the
     * response.
     */
    @Test
    public void example_test() {

        try {
            (new ServerEcho(8080)).start(); // Start the echo server.

            Client client = new Client("localhost", 8080); // Create a client connecting to localhost.
            System.out.println("Connection started...");
            ComUtils comUtils = client.getComutils();
            comUtils.write_int32(2); // Send an integer to the server.
            int readedInt = comUtils.read_int32(); // Receive the echoed integer from the server.
            assertEquals(2, readedInt); // Verify the received integer matches the sent value.
            client.getSocket().close(); // Close the client connection.
            System.out.println("Connexion closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}