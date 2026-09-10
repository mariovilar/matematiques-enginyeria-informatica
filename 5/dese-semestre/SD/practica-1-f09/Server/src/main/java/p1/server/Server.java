package p1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import utils.BattleshipComUtils;

/**
 * The Server class handles client connections and communication.
 * It listens for incoming client requests and starts a new GameHandler thread
 * for each client.
 */
public class Server {
    /** Error message for incorrect initialization arguments. */
    public static final String INIT_ERROR = "Server should be initialized with -p <port>";

    private Socket socket;
    private ServerSocket ss;
    private int port;
    private BattleshipComUtils comutils;

    /**
     * Initializes the server and sets up the connection.
     *
     * @param port The port number on which the server will listen for connections.
     */
    public Server(int port) {
        this.port = port;
        setConnection();
    }

    /**
     * Returns a ComUtils instance for handling communication with the client.
     *
     * @param socket The socket associated with the client connection.
     * @return An instance of ComUtils for communication.
     */
    private BattleshipComUtils getComutils(Socket socket) {
        if (comutils == null) {
            try {
                comutils = new BattleshipComUtils(socket.getInputStream(), socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException("I/O Error when creating the ComUtils:\n" + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Some other exception:\n" + e.getMessage());
            }
        }
        return comutils;
    }

    /**
     * Sets up the server socket to listen for incoming client connections.
     */
    private void setConnection() {
        if (this.ss == null) {
            try {
                ss = new ServerSocket(port);
                ss.setSoTimeout(150000);
                System.out.println("Server up & listening on port " + port + "...\nPress Cntrl + C to stop.");
            } catch (IOException e) {
                throw new RuntimeException("I/O error when opening the Server Socket:\n" + e.getMessage());
            }
        }
    }

    /**
     * Returns the current socket associated with the client connection.
     *
     * @return The client socket.
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * Continuously listens for incoming client connections and starts a new
     * GameHandler thread for each client.
     */
    public void init(boolean multiplayerMode) {
        while (true) {
            try {
                socket = ss.accept();
                comutils = getComutils(socket);
                System.out.println("Client accepted");

                // Create a new GameHandler for every client.
                new GameHandler(comutils, multiplayerMode).run();
            } catch (IOException e) {
                throw new RuntimeException("I/O error when accepting a client:\n" + e.getMessage());
            } catch (SecurityException e) {
                throw new RuntimeException("Operation not accepted:\n" + e.getMessage());
            } catch (IllegalBlockingModeException e) {
                throw new RuntimeException("There is no connection ready to be accepted:\n" + e.getMessage());
            }
        }
    }

    /**
     * Main method to start the server.
     *
     * @param args Command-line arguments specifying the port number.
     */
    public static void main(String[] args) {
        boolean multiplayerMode = args[args.length - 1].equals("--multiplayer");
        if ((multiplayerMode && args.length != 3) || (!multiplayerMode && args.length != 2)) {
            throw new IllegalArgumentException("Wrong amount of arguments.\n" + INIT_ERROR);
        }

        if (!args[0].equals("-p")) {
            throw new IllegalArgumentException("Wrong argument keyword.\n" + INIT_ERROR);
        }

        int port;

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("<port> should be an Integer.");
        }

        Server server = new Server(port);
        server.init(multiplayerMode);
    }
}
