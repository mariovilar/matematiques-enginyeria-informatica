package p1.client;

import utils.enums.GameState;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;
import utils.message.*;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * GameClient class that encapsulates the game's logic.
 * It follows a sequence of states as per the established protocol.
 */
public class GameClient {

    private Client client;
    private static final String HELP = "Type your desired command (type it in UPPERCASE) and its arguments";
    private GameState gameState;
    private int gameId = 0;
    private int playerId = 0;

    /**
     * Initializes the game client with a client instance.
     *
     * @param client The client instance to interact with the server.
     */
    public GameClient(Client client) {
        this.client = client;
        this.gameState = GameState.WAITING_PLAYERS;
        init();
    }

    /**
     * Implement the game logic, ensuring that the game transitions
     * through the correct states following the established protocol.
     * This method is called when the game client is initialized.
     * It runs the game client and handles the game loop.
     */
    public void init() {
        System.out.println("GameClient started");
        run();
    }

    /**
     * Starts the game client.
     * This method will be responsible for initializing and managing the game session.
     * <p>
     * The loop listens for messages then dispatches them according to their type.
     * </p>
     */
    public void run() {
        while (true) {
            try {
                // We retrieve the message
                Message message = null;
                String lastCommand = "";
                try {
                    message = this.client.comutils.readMessage();
                } catch (SocketTimeoutException e) {
                    // Client timeout reached
                    switch (this.gameState) {
                        case WAITING_PLAYERS:
                            System.out.println("Waiting for players...");
                            lastCommand = readTerminal();
                            break;
                        case SETUP:
                            System.out.println("Setting up the game...");
                            lastCommand = readTerminal();
                            break;
                        case PLAYING:
                            System.out.println("Playing the game...");
                            lastCommand = readTerminal();
                            break;
                        case FINISHED:
                            this.client.closeConnection();
                            break;
                        default:
                            break;
                    }
                    // Wait for the next iteration
                    continue;
                } catch (IOException e) {
                    return;
                } catch (NullPointerException e) {
                    this.client.closeConnection();
                    return;
                }

                System.out.println("Received message from server");
                // We get the message type
                MessageType mt = message.getType();

                // We check the message type and process it accordingly
                switch (mt) {
                    // In case we receive an OK message, we set the credentials
                    // and print the playerId and gameId
                    case OK:
                        OkMessage OkMessage = (OkMessage) message;
                        int playerId = OkMessage.getPlayerId();
                        int gameId = OkMessage.getGameId();
                        setCredentials(playerId, gameId);
                        System.out.println("OK Message: playerId " + playerId + " and gameId " + gameId);
                        break;
                    // In case we receive an error, we print the error message
                    case ERROR:
                        ErrorMessage ErrorMessage = (ErrorMessage) message;
                        System.out.println("ERROR " + ErrorMessage.getByteCode() + ": " + ErrorMessage.getMessage());
                        break;
                    // In case we receive a gameconfig, we display the actual boards
                    case GAMECONFIG:
                        // Cast the message
                        GameConfigMessage GameConfigMessage = (GameConfigMessage) message;

                        // Get the proper data
                        byte W = GameConfigMessage.getWidth();
                        byte H = GameConfigMessage.getHeight();
                        byte[] vessels = GameConfigMessage.getVessels();
                        System.out.println("GAMECONFIG message received");
                        System.out.println("The height of the board is " + H);
                        System.out.println("The width of the board is " + W);

                        // Print the vessels on the board
                        System.out.println("The vessels on the board are: " + arrayMessages(vessels));
                        break;
                    // In case we receive a game status, we display the actual state as is
                    // We also set the game state
                    case GAMESTATUS:
                        System.out.println("GAMESTATUS message received");
                        gameStatusMessage((GameStatusMessage) message);
                        break;
                    // In case we hit, we display the hit message
                    case HIT:
                        HitMessage HitMessage = (HitMessage) message;
                        System.out.println("HIT message received");
                        byte hit = HitMessage.getSink();
                        switch (hit) {
                            case 0:
                                System.out.println("You have hit a vessel");
                                break;
                            case 1:
                                System.out.println("You have hit and SUNK a vessel.");
                                break;
                            default:
                                break;
                        }
                        break;
                    case FAIL:
                        System.out.println("FAIL message received");
                        break;
                    default:
                        break;
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                this.client.closeConnection(); // Close client properly
            } catch (BattleshipException e) {
                System.out.println("ERROR " + e.getErrorType() + ": " + e.getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads terminal input and processes the typed command.
     * <p>
     * The command is split into words with the first word representing
     * the command type. Based on the command type, different game actions
     * are triggered.
     * </p>
     *
     * @return The command type as a string (finally we decided not to use it).
     */
    private String readTerminal() {
        System.out.println("Type your desired command (type it in UPPERCASE) and its arguments");
        String commandLine = System.console().readLine();
        // OP writes several word command, first word is the type of command
        String[] args = commandLine.split(" ");
        String command = args[0];
        switch (command) {
            case "help":
                help();
                break;
            case "CREATE":
                if (args.length != 10) {
                    System.out.println("CREATE <playerName> <w> <h> <v1> <v2> <v3> <v4> <v5> <ai (0 or 1)>");
                    break;
                }
                // We create the vessels
                byte[] vessels = new byte[5];
                for (int i = 0; i < 5; i++) {
                    vessels[i] = Byte.parseByte(args[i + 4]);
                }
                createCommand(args[1], Byte.parseByte(args[2]), Byte.parseByte(args[3]), vessels,
                        Byte.parseByte(args[9]));
                break;
            case "JOIN":
                if (args.length != 2) {
                    System.out.println("JOIN <playerName>");
                    break;
                }
                joinCommand(args[1]);
                break;
            case "GETCONFIG":
                if (args.length != 3) {
                    System.out.println("GETCONFIG <playerId> <gameId>");
                    break;
                }
                getConfigCommand(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                break;
            case "ADDVESSEL":
                if (args.length != 8) {
                    System.out.println("ADDVESSEL <playerId> <gameId> <type> <ri> <ci> <rf> <cf>");
                    break;
                }
                byte[] initialPosition = { Byte.parseByte(args[4]), Byte.parseByte(args[5]) };
                byte[] finalPosition = { Byte.parseByte(args[6]), Byte.parseByte(args[7]) };
                addVesselCommand(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                        Byte.parseByte(args[3]), initialPosition, finalPosition);
                break;
            case "SHOT":
                if (args.length != 5) {
                    System.out.println("SHOT <playerId> <gameId> <r> <c>");
                    break;
                }
                byte[] position = { Byte.parseByte(args[3]), Byte.parseByte(args[4]) };
                shotCommand(Integer.parseInt(args[1]), Integer.parseInt(args[2]), position);
                break;
            case "GETSTATUS":
                if (args.length != 3) {
                    System.out.println("GETSTATUS <playerId> <gameId>");
                    break;
                }
                getStatusCommand(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                break;
            case "LEAVE":
                if (args.length != 3) {
                    System.out.println("LEAVE <playerId> <gameId>");
                    break;
                }
                leaveCommand(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                break;
            default:
                System.out.println("Invalid command. Please try again.");
                help();
                break;
        }
        return command;
    }

    /**
     * Creates a string representation of a byte array.
     *
     * @param array A byte array.
     * @return A string with each byte separated by a space.
     */
    private String arrayMessages(byte[] array) {
        String message = "";

        for (byte element : array) {
            message += element + " ";
        }

        return message;
    }

    /**
     * Sets the credentials (playerId and gameId) if they have not been set yet.
     *
     * @param playerId The player's identifier.
     * @param GameId   The game's identifier.
     */
    private void setCredentials(int playerId, int GameId) {
        if (this.playerId == 0) {
            this.playerId = playerId;
        }
        if (this.gameId == 0) {
            this.gameId = GameId;
        }
    }

    /**
     * Processes a GAMESTATUS message received from the server.
     * <p>
     * It updates the client’s game state and displays current board
     * information.
     * </p>
     *
     * @param message The GameStatusMessage received from the server.
     * @throws SocketTimeoutException if a socket timeout occurs.
     * @throws BattleshipException    if a battleship-related error occurs.
     * @throws IOException            if an I/O error occurs.
     */
    private void gameStatusMessage(GameStatusMessage message)
            throws SocketTimeoutException, BattleshipException, IOException {
        if (message == null) {
            return;
        }

        // Get the proper data
        byte gameState = message.getGameState();
        // Set the game state
        this.gameState = GameState.valueOf(gameState);

        // Get the proper data
        int boardSize = message.getBoardSize();
        byte[] board1 = message.getBoard1();
        byte[] board2 = message.getBoard2();
        byte[] info = message.getInfo();

        System.out.println("The state of the game is " + this.gameState);
        System.out.println("The board size is " + boardSize);
        System.out.println("Player 1's board is " + arrayMessages(board1));
        System.out.println("Player 2's board is " + arrayMessages(board2));
        System.out.println("Info is " + arrayMessages(info));
    }

    /**
     * Displays available commands to the terminal.
     */
    private void help() {
        System.out.println("CREATE <playerName> <w> <h> <v1> <v2> <v3> <v4> <v5> <ai (0 or 1)>");
        System.out.println("JOIN <playerName>");
        System.out.println("GETCONFIG <playerId> <gameId>");
        System.out.println("ADDVESSEL <playerId> <gameId> <type> <ri> <ci> <rf> <cf>");
        System.out.println("SHOT <playerId> <gameId> <r> <c>");
        System.out.println("GETSTATUS <playerId> <gameId>");
        System.out.println("LEAVE <playerId> <gameId>");
    }

    /**
     * Sends a create message to the server for creating a new game session.
     *
     * @param playerName The name of the player.
     * @param W          The width of the game board.
     * @param H          The height of the game board.
     * @param vessels    An array representing the vessels.
     * @param ai         Indicates if the game is against AI (0 or 1).
     */
    private void createCommand(String playerName, byte W, byte H, byte[] vessels, byte ai) {
        try {
            CreateMessage createMessage = new CreateMessage(playerName, W, H, vessels, ai);
            System.out.println("Sending create message to the server");
            this.client.comutils.writeMessage(createMessage);
        } catch (SocketTimeoutException e) {
            this.client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BattleshipException e) {
            System.out.println("ERROR " + e.getErrorType() + ": " + e.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a join message to the server for a player to join an existing game.
     *
     * @param playerName The name of the joining player.
     */
    private void joinCommand(String playerName) {
        try {
            JoinMessage joinMessage = new JoinMessage(playerName);
            System.out.println("Sending join message to the server");
            this.client.comutils.writeMessage(joinMessage);
        } catch (SocketTimeoutException e) {
            this.client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BattleshipException e) {
            System.out.println("ERROR " + e.getErrorType() + ": " + e.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a get configuration message to the server.
     *
     * @param playerId The player's identifier.
     * @param gameId   The game's identifier.
     */
    private void getConfigCommand(int playerId, int gameId) {
        try {
            GetConfigMessage getConfigMessage = new GetConfigMessage(playerId, gameId);
            System.out.println("Sending getConfig message to the server");
            this.client.comutils.writeMessage(getConfigMessage);
        } catch (SocketTimeoutException e) {
            this.client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BattleshipException e) {
            System.out.println("ERROR " + e.getErrorType() + ": " + e.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an add vessel message to the server to place a new vessel.
     *
     * @param playerId The player's identifier.
     * @param gameId   The game's identifier.
     * @param type     The type of the vessel.
     * @param xi       The initial position (row and column).
     * @param xf       The final position (row and column).
     */
    private void addVesselCommand(int playerId, int gameId, byte type, byte[] xi, byte[] xf) {
        try {
            AddVesselMessage addVesselMessage = new AddVesselMessage(playerId, gameId, type, xi, xf);
            System.out.println("Sending addVessel message to the server");
            this.client.comutils.writeMessage(addVesselMessage);
        } catch (SocketTimeoutException e) {
            this.client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BattleshipException e) {
            System.out.println("ERROR " + e.getErrorType() + ": " + e.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a shot message to the server indicating an attack move.
     *
     * @param playerId The player's identifier.
     * @param gameId   The game's identifier.
     * @param position A two-element byte array representing the target position (row and column).
     */
    private void shotCommand(int playerId, int gameId, byte[] position) {
        try {
            ShotMessage addVesselMessage = new ShotMessage(playerId, gameId, position);
            System.out.println("Sending shot message to the server");
            this.client.comutils.writeMessage(addVesselMessage);
        } catch (SocketTimeoutException e) {
            this.client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BattleshipException e) {
            System.out.println("ERROR " + e.getErrorType() + ": " + e.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a get status message to the server to retrieve the current game status.
     *
     * @param playerId The player's identifier.
     * @param gameId   The game's identifier.
     */
    private void getStatusCommand(int playerId, int gameId) {
        try {
            GetStatusMessage getStatusMessage = new GetStatusMessage(playerId, gameId);
            System.out.println("Sending getStatus message to the server");
            this.client.comutils.writeMessage(getStatusMessage);
        } catch (SocketTimeoutException e) {
            this.client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a leave message to the server to exit the game session.
     *
     * @param playerId The player's identifier.
     * @param gameId   The game's identifier.
     */
    private void leaveCommand(int playerId, int gameId) {
        try {
            LeaveMessage leaveMessage = new LeaveMessage(playerId, gameId);
            System.out.println("Sending leave message to the server");
            this.client.comutils.writeMessage(leaveMessage);
        } catch (SocketTimeoutException e) {
            this.client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}