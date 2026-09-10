package p1.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import utils.BattleshipComUtils;

/**
 * Client class to establish a connection to a server and manage communication.
 */
public class Client {

    public static final String INIT_ERROR = "Client should be initialized with -h <host> -p <port>";
    Socket socket;
    String host;
    int port;
    BattleshipComUtils comutils;

    /**
     * Constructs a Client instance and initializes the connection.
     *
     * @param host The hostname or IP address of the server.
     * @param port The port number to connect to.
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.socket = setConnection();
        this.comutils = getComutils();
    }

    /**
     * Returns the ComUtils instance for communication.
     * If not initialized, it initializes it first.
     *
     * @return The ComUtils instance.
     */
    public BattleshipComUtils getComutils() {
        if (comutils == null) {
            try {
                comutils = new BattleshipComUtils(socket.getInputStream(), socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException("I/O Error when creating the ComUtils:\n" + e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Some arguments for ComUtils creation are invalid:\n" + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Error when creating the ComUtils:\n" + e.getMessage());
            }
        }
        return comutils;
    }

    /**
     * Establishes a connection to the specified server.
     *
     * @return The established socket connection.
     */
    public Socket setConnection() {
        Socket connection = null;
        if (this.socket == null) {
            try {
                connection = new Socket(this.host, this.port);
                connection.setSoTimeout(1000); // Timeout of 1 seconds (the server should be faster than this)
                System.out.println("Client connected to server");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Proxy has invalid type or null:\n" + e.getMessage());
            } catch (SecurityException e) {
                throw new SecurityException("Connection to the proxy denied for security reasons:\n" + e.getMessage());
            } catch (UnknownHostException e) {
                throw new RuntimeException("Host is Unknown:\n" + e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(
                        "I/O Error when creating the socket:\n" + e.getMessage() + ". Is the host listening?");
            }
        }
        return connection;
    }

    // Function to close the connection
    public void closeConnection() {
        try {
            // We close the socket
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("The game has ended");
            }
        } catch (IOException e) {
            System.err.println("Error while closing socket: " + e.getMessage());
        }
    }

    /**
     * Returns the current socket connection.
     *
     * @return The socket instance.
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * Main method to initialize the client with command-line arguments.
     *
     * @param args Command-line arguments specifying host and port.
     */
    public static void main(String[] args) {

        if (args.length != 4) {
            throw new IllegalArgumentException("Wrong amount of arguments.\n" + INIT_ERROR);
        }

        if (!args[0].equals("-h") || !args[2].equals("-p")) {
            throw new IllegalArgumentException("Wrong argument keywords.\n" + INIT_ERROR);
        }
        int port;
        try {
            port = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("<port> should be an Integer.");
        }
        String host = args[1];
        Client client = new Client(host, port);

        GameClient gameClient = new GameClient(client);

        // Generem l'excepció per tancar la connexió quan es tanqui el programa
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Interrupció detectada, tancant connexió...");
            client.closeConnection();
        }));

        gameClient.run();
    }
}
