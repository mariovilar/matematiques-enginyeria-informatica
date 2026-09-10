package p1.server;

import java.io.IOException;
import java.util.Random;

import utils.BattleshipComUtils;
import utils.enums.ErrorType;
import utils.enums.GameState;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;
import utils.message.*;
import utils.game.BattleshipGame;

/**
 * Handles the game logic for a connected client.
 * This class is responsible for managing the communication and
 * executing the game protocol from the server's side.
 */
public class GameHandler extends Thread {
    /** Communication utility for handling input and output streams. */
    private BattleshipComUtils comutils;
    private BattleshipGame game;
    private boolean isPlayer1Active = false;
    private boolean multiplayerMode = false;

    /**
     * Initializes the game handler with a communication utility.
     *
     * @param comutils The communication utility for interacting with the client.
     */
    public GameHandler(BattleshipComUtils comutils, boolean multiplayerMode) {
        this.comutils = comutils;
        this.multiplayerMode = multiplayerMode;
        init();
    }

    /**
     * Starts the game handler.
     * This method will be responsible for initializing and managing the game
     * session.
     */
    @Override
    public void start() {
        super.run();
    }

    /**
     * Initializes the game handler.
     * This method sets up the initial state of the game and prepares it for
     * execution.
     */
    public void init() {
        System.out.println("Setting up game...");
        this.isPlayer1Active = true;
    }

    /**
     * Main execution loop for the game handler.
     * This method will continuously listen for messages from the client and
     * execute the corresponding game commands.
     */
    @Override
    public void run() {
        System.out.println("GameHandler running");
        try {
            while (this.isPlayer1Active) {
                Message message = null;
                try {
                    message = this.comutils.readMessage();
                    if (message == null) {
                        this.game.endGame();
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Client disconnected");
                    break;
                }

                System.out.println("Received message from the client: " + message.getType());
                MessageType messageType = message.getType();

                // Current status of the game
                GameState currentStatus = GameState.WAITING_PLAYERS;
                if (this.game != null) {
                    currentStatus = GameState.valueOf(this.game.getGameState());
                }
                GameState status = null;

                // If the game hasn't been finished we can execute the commands
                if (currentStatus != GameState.FINISHED) {
                    switch (messageType) {
                        case CREATE:
                            CreateMessage createMessage = (CreateMessage) message;
                            if (currentStatus == GameState.WAITING_PLAYERS) {
                                status = createCommand(createMessage);
                            } else {
                                invalidStateMessage(currentStatus, MessageType.CREATE);
                            }
                            break;
                        case JOIN:
                            JoinMessage joinMessage = (JoinMessage) message;
                            if (currentStatus == GameState.WAITING_PLAYERS) {
                                String playerName = joinMessage.getPlayerName();
                                status = joinCommand(playerName);
                            } else {
                                invalidStateMessage(currentStatus, MessageType.JOIN);
                            }
                            break;
                        case GETCONFIG:
                            GetConfigMessage getConfigMessage = (GetConfigMessage) message;
                            if (currentStatus != GameState.WAITING_PLAYERS) {
                                status = getConfigCommand();
                            } else {
                                int playerId = getConfigMessage.getPlayerId();
                                int gameId = getConfigMessage.getGameId();
                                invalidStateMessage(playerId, gameId, currentStatus, MessageType.GETCONFIG);
                            }
                            break;
                        case ADDVESSEL:
                            AddVesselMessage addVesselMessage = (AddVesselMessage) message;
                            if (currentStatus == GameState.SETUP) {
                                status = addVesselCommand(addVesselMessage);
                            } else {
                                int playerId = addVesselMessage.getPlayerId();
                                int gameId = addVesselMessage.getGameId();
                                invalidStateMessage(playerId, gameId, currentStatus, MessageType.ADDVESSEL);
                            }
                            break;
                        case SHOT:
                            ShotMessage shotMessage = (ShotMessage) message;
                            int gameId = shotMessage.getGameId();
                            int playerId = shotMessage.getPlayerId();
                            // When shoting we must check that it's player's turn
                            if (currentStatus == GameState.PLAYING) {
                                if (this.game.getActivePlayer() == shotMessage.getPlayerId()) {
                                    status = shotCommand(shotMessage);
                                } else {
                                    ErrorMessage error = new ErrorMessage(ErrorType.INVALID_PLAYER_NAME,
                                            "Player " + playerId + " from game " + gameId + ": It's not your turn.");
                                    this.comutils.writeMessage(error);
                                }
                            } else {
                                invalidStateMessage(playerId, gameId, currentStatus, MessageType.SHOT);
                            }
                            break;
                        case GETSTATUS:
                            status = getStatusCommand((GetStatusMessage) message);
                            break;
                        case LEAVE:
                            status = leaveCommand((LeaveMessage) message);
                            break;
                        default:
                            System.out.println("Invalid command received");
                            break;
                    }
                }

                // If the status has changed, notify all players
                if (status != null && status != currentStatus) {
                    // If someone leaves, we end the game
                    if (status == GameState.FINISHED) {
                        this.game.endGame();
                    } else {
                        this.game.setGameState(status);
                        this.game.notifyStatus(this.game.getActivePlayer());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BattleshipException e) {
            ErrorMessage error = new ErrorMessage(e.getErrorType());
            try {
                this.comutils.writeMessage(error);
            } catch (BattleshipException | IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.isPlayer1Active = false;
            this.comutils.closeStreams();
        }
    }

    /**
     * Creates a message indicating an invalid state and writes it to the output stream.
     * @param currentStatus
     * @param messageType
     */
    private void invalidStateMessage(GameState currentStatus, MessageType messageType) {

        ErrorMessage error = new ErrorMessage(ErrorType.INVALID_STATE, "ERROR: " + messageType
                + " is not valid in the current state of the game: " + currentStatus);
        try {
            this.comutils.writeMessage(error);
        } catch (BattleshipException | IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Creates a message indicating an invalid state and writes it to the output stream.
     * @param playerId
     * @param gameId
     * @param currentStatus
     * @param messageType
     */
    private void invalidStateMessage(int playerId, int gameId, GameState currentStatus, MessageType messageType) {
        String errorString = "ERROR: Player " + playerId + " at " + gameId +
                ": " + messageType + " is not valid in the state: " + currentStatus;

        ErrorMessage error = new ErrorMessage(ErrorType.INVALID_STATE, errorString);
        try {
            this.comutils.writeMessage(error);
        } catch (BattleshipException | IOException e1) {
            e1.printStackTrace();
        }
    }
    

    /**
     * Creates a new game based on the provided CreateMessage and sets up the game state.
     *
     * @param message The CreateMessage containing the game configuration details such as
     *                board dimensions and vessels.
     * @return The resulting GameState after the game creation process. Returns GameState.SETUP
     *         if playing against an AI, or GameState.WAITING_PLAYERS if waiting for other players.
     * @throws BattleshipException If the game cannot be created due to invalid data or if a game
     *                             already exists.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private GameState createCommand(CreateMessage message) throws BattleshipException, IOException {
        // We generate the game and player id (they may not be used in certain)
        int gameId = 20000 + new Random().nextInt(9999);
        int playerId = 10000 + new Random().nextInt(9999);

        try {
            // We do the whole setup
            boolean ai = (!this.multiplayerMode && message.isAi() == 1);

            // If the game already existed, we return an error
            if (this.game != null) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            this.game = new BattleshipGame(gameId, this.comutils);

            // We check if the game was already created
            boolean gameAlreadyCreated = this.game.gameAlreadyCreated();
            if (gameAlreadyCreated) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            // We try to add the player
            boolean addPlayer = this.game.addPlayer(playerId, message.getWidth(), message.getHeight(), false);
            if (!addPlayer) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            this.game.setVessels(message.getVessels());
            this.game.setActivePlayer(playerId);

            // If we are playing agains a bot we create it and add the vessels
            if (ai) {
                int botPlayer = 10000 + new Random().nextInt(9999);
                this.game.addPlayer(botPlayer, message.getWidth(), message.getHeight(), true);
                this.game.addRandomVessels(botPlayer);
            }

            // Write the response and send it to the client
            OkMessage response = new OkMessage(playerId, this.game.getGameId());
            this.comutils.writeMessage(response);

            return ai ? GameState.SETUP : GameState.WAITING_PLAYERS;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (BattleshipException e) {
            ErrorMessage error = new ErrorMessage(e.getErrorType(),
                    "ERROR: Game may have already been created, or some data is invalid.");
            this.comutils.writeMessage(error);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles the join command from a player.
     * This method adds a player to an existing game and sets up the game state.
     *
     * @param playerName The name of the player joining the game.
     * @return The resulting GameState after the join process. Returns GameState.SETUP if the
     *         game is in setup mode.
     * @throws BattleshipException If the game cannot be joined due to invalid data or if a game
     *                             already exists.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private GameState joinCommand(String playerName) throws BattleshipException, IOException {
        // Generate a random player ID
        Random random = new Random();

        // Els generem entre 10000 i 19999, per exemple
        int playerId = 10000 + random.nextInt(9999);

        try {
            // If the game doesn't exist, we throw an error
            if (this.game == null) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            // If there are no players we first have to CREATE
            if (!this.game.gameAlreadyCreated()) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            // Get the board dimensions
            byte[] dimensions = this.game.getBoardDimensions();

            // Add the player to the game
            boolean addPlayer = this.game.addPlayer(playerId, dimensions[0], dimensions[1], false);

            // If something failed we return the WAITING_PLAYERS state
            if (!addPlayer) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            // Write the response and send it to the client
            OkMessage response = new OkMessage(playerId, this.game.getGameId());
            this.comutils.writeMessage(response);
            // Return the GameState setup
            return GameState.SETUP;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (BattleshipException e) {
            ErrorMessage error = new ErrorMessage(e.getErrorType(),
                    "ERROR: Game may already be full or has not been created yet.");
            this.comutils.writeMessage(error);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles the get configuration command from a player.
     * This method retrieves the game configuration details and sends them to the client.
     *
     * @return The resulting GameState after the get configuration process.
     * @throws BattleshipException If the game cannot be accessed or if an error occurs during
     *                             communication.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private GameState getConfigCommand() throws BattleshipException, IOException {
        try {
            // If the game doesn't exist, we throw an error
            if (this.game == null) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            // Get the board dimensions
            byte[] dimensions = this.game.getBoardDimensions();
            byte width = dimensions[0];
            byte height = dimensions[1];

            // Get the vessels configuration
            byte[] vessels = this.game.getVessels();

            GameConfigMessage response = new GameConfigMessage(width, height, vessels);
            this.comutils.writeMessage(response);
            return GameState.SETUP;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (BattleshipException e) {
            ErrorMessage error = new ErrorMessage(e.getErrorType(), e.getDescription());
            this.comutils.writeMessage(error);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles the add vessel command from a player.
     * This method adds a vessel to the player's board and updates the game state accordingly.
     *
     * @param message The AddVesselMessage containing the vessel details.
     * @return The resulting GameState after the add vessel process.
     * @throws BattleshipException If the game cannot be accessed or if an error occurs during
     *                             communication.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private GameState addVesselCommand(AddVesselMessage message) throws BattleshipException, IOException {
        try {
            // If the game doesn't exist, we throw an error
            if (this.game == null) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            int playerId = message.getPlayerId();

            // If the player doesn't exist, we throw an error
            if (!this.game.playerExists(playerId)) {
                throw new BattleshipException(ErrorType.INVALID_PLAYER_ID);
            }

            boolean addVessel = this.game.addVessel(playerId, (int) message.getInitialPosition()[0],
                    (int) message.getInitialPosition()[1], (int) message.getFinalPosition()[0],
                    (int) message.getFinalPosition()[1], (int) message.getVesselType());

            // Throw an exception if the vessel position is not fine
            if (!addVessel) {
                throw new BattleshipException(ErrorType.INVALID_COORDINATE);
            }

            // Write the response and send it to the client
            OkMessage response = new OkMessage(playerId, this.game.getGameId());
            this.comutils.writeMessage(response);

            // If the player is ready we start playing
            return this.game.allPlayersReady() ? GameState.PLAYING : GameState.SETUP;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (BattleshipException e) {
            ErrorMessage error = new ErrorMessage(e.getErrorType());
            this.comutils.writeMessage(error);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles the shot command from a player.
     * This method processes the shot action and updates the game state accordingly.
     *
     * @param message The ShotMessage containing the shot details.
     * @return The resulting GameState after the shot process.
     * @throws BattleshipException If the game cannot be accessed or if an error occurs during
     *                             communication.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private GameState shotCommand(ShotMessage message) throws BattleshipException, IOException {
        int playerId = message.getPlayerId();

        // We get the coordinates
        byte[] coordinates = message.getPosition();
        byte r = coordinates[0];
        byte c = coordinates[1];

        try {
            // If the game doesn't exist, we throw an error
            if (this.game == null) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            // If the player doesn't exist, we throw an error
            if (!this.game.playerExists(playerId)) {
                throw new BattleshipException(ErrorType.INVALID_PLAYER_ID);
            }

            // Perform the shot
            int shotResult = this.game.shot(playerId, r, c);
            GameState result = handleShot(shotResult, playerId);

            // If the player has won, we don't let the bot play
            if (result == GameState.FINISHED)
                return GameState.FINISHED;

            // Shot for the oponent in case of being a bot
            int opponentId = this.game.getOpponentId(playerId);
            boolean isOpponentAi = this.game.isBot(opponentId);

            if (isOpponentAi) {
                int aiShotResult = this.game.shot(opponentId);
                result = handleShot(aiShotResult, opponentId);
            }
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (BattleshipException e) {
            ErrorMessage error = new ErrorMessage(e.getErrorType(), "ERROR: Coordinates were wrong");
            this.comutils.writeMessage(error);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles the shot result and updates the game state accordingly.
     *
     * @param shotResult The result of the shot action.
     * @param playerId   The ID of the player who made the shot.
     * @return The resulting GameState after processing the shot result.
     * @throws BattleshipException If an error occurs during processing.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private GameState handleShot(int shotResult, int playerId) throws BattleshipException, IOException {
        boolean isBot = this.game.isBot(playerId);
        int opponent;
        // Handle shot results
        switch (shotResult) {
            case -1: // Invalid shot
                throw new BattleshipException(ErrorType.INVALID_COORDINATE);

            // Missed shot
            case 0:
                this.comutils.writeMessage(new FailMessage());
                opponent = this.game.getOpponentId(playerId);
                this.game.setActivePlayer(opponent);

                // Fem un canvi "virtual" d'estat, ja que passem de player1 a player2 o viceversa
                if (isBot) {
                    // The opponent is the "real player", so notify the opponent
                    this.game.notifyStatus(opponent);
                } else {
                    // És multijugador, així que notifiquem a tots
                    this.game.notifyStatusToEveryone();
                }
                break;

            // Hit
            case 1:
                this.comutils.writeMessage(new HitMessage((byte) 0));
                opponent = this.game.getOpponentId(playerId);
                
                // If there's a hit, the player can repeat
                this.game.setActivePlayer(playerId);

                if (isBot) {
                    // The opponent is the "real player", so notify the opponent
                    this.game.notifyStatus(opponent);
                } else {
                    // És multijugador, així que notifiquem a tots
                    this.game.notifyStatusToEveryone();
                }
                break;

            // Ship sunk
            case 2:

                this.comutils.writeMessage(new HitMessage((byte) 1));
                opponent = this.game.getOpponentId(playerId);

                // If there's a hit, the player can repeat
                this.game.setActivePlayer(playerId);

                if (isBot) {
                    // The opponent is the "real player", so notify the opponent
                    this.game.notifyStatus(opponent);
                } else {
                    // És multijugador, així que notifiquem a tots
                    this.game.notifyStatusToEveryone();
                }                

                // Check if the opponent has lost all ships
                if (this.game.allVesselsSunk(this.game.getOpponentId(playerId))) {
                    this.game.setWinPlayer(playerId);
                    return GameState.FINISHED;
                }

                break;
        }

        return GameState.PLAYING;
    }

    /**
     * Handles the get status command from a player.
     * This method retrieves the current game status and sends it to the client.
     *
     * @param message The GetStatusMessage containing the player ID.
     * @return The resulting GameState after the get status process.
     * @throws BattleshipException If the game cannot be accessed or if an error occurs during
     *                             communication.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private GameState getStatusCommand(GetStatusMessage message) throws BattleshipException, IOException {
        try {
            // Get the player
            int playerId = message.getPlayerId();

            // If the game doesn't exist, we throw an error
            if (this.game == null) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            // If the player doesn't exist, we throw an error
            if (!this.game.playerExists(playerId)) {
                throw new BattleshipException(ErrorType.INVALID_PLAYER_ID);
            }

            this.game.notifyStatus(message.getPlayerId());
            return GameState.valueOf(this.game.getGameState());
            
        } catch (BattleshipException e) {
            ErrorMessage error = new ErrorMessage(e.getErrorType());
            this.comutils.writeMessage(error);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles the leave command from a player.
     * This method processes the leave action and updates the game state accordingly.
     *
     * @param message The LeaveMessage containing the player ID and game ID.
     * @return The resulting GameState after the leave process.
     * @throws BattleshipException If the game cannot be accessed or if an error occurs during
     *                             communication.
     * @throws IOException If an I/O error occurs during communication with the client.
     */
    private GameState leaveCommand(LeaveMessage message) throws BattleshipException, IOException {
        try {
            // Add the player to the game
            int playerId = message.getPlayerId();

            // If the game doesn't exist, we throw an error
            if (this.game == null) {
                throw new BattleshipException(ErrorType.GAME_NOT_AVAILABLE);
            }

            // If the player doesn't exist, we throw an error
            if (!this.game.playerExists(playerId)) {
                throw new BattleshipException(ErrorType.INVALID_PLAYER_ID);
            }

            OkMessage response = new OkMessage(playerId, message.getGameId());
            this.comutils.writeMessage(response);
            return GameState.FINISHED;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (BattleshipException e) {
            ErrorMessage error = new ErrorMessage(e.getErrorType());
            this.comutils.writeMessage(error);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}