package utils.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import utils.BattleshipComUtils;
import utils.enums.ErrorType;
import utils.enums.GameState;
import utils.exceptions.BattleshipException;
import utils.message.GameStatusMessage;

/**
 * Represents a Battleship game where players can add vessels, shoot, and
 * manage game state.
 * <p>
 * This class implements IBattleshipGame and uses a communication utility (
 * BattleshipComUtils) to send status messages.
 * </p>
 */
public class BattleshipGame implements IBattleshipGame {
    private BattleshipComUtils comutils;
    private int gameId;
    private GameState gameState = GameState.WAITING_PLAYERS;
    private Map<Integer, Boolean> players = new HashMap<>(); // true if is a bot, false if not
    private Map<Integer, GameBoard> boards = new HashMap<>(); // List of boards
    private Map<Integer, byte[]> vessels = new HashMap<>(); // List of bytes containing the vessels
    private int activePlayer = -1;
    private int winPlayer = -1;
    private byte[] totalVessels = { 1, 1, 1, 1, 1 }; // [T1, T2, T3, T4, T5]

    /**
     * Initializes a new Battleship game with the specified ID.
     *
     * @param gameId   the unique game identifier; its length must be 5
     * @param comutils the communication utility for sending messages
     * @throws BattleshipException if the game ID is invalid
     */
    public BattleshipGame(int gameId, BattleshipComUtils comutils) throws BattleshipException {
        int len = String.valueOf(gameId).length();
        if (len != 5) {
            throw new BattleshipException(ErrorType.INVALID_GAME_ID);
        }

        this.comutils = comutils;
        this.gameId = gameId;
    }

    /*
     *
     * BOARDS
     * Functions to manage the boards
     *
     */

    /**
     * Creates a board for a player if one does not already exist.
     *
     * @param playerId the player's identifier
     * @param W        the board width
     * @param H        the board height
     */
    private void createBoards(int playerId, byte W, byte H) {
        if (!boards.containsKey(playerId)) {
            boards.put(playerId, new GameBoard(W, H));
        }
    }

    /**
     * Returns the game board associated with the specified player.
     *
     * @param playerId the player's identifier
     * @return the player's game board
     */
    public GameBoard getOwnBoard(int playerId) {
        return boards.get(playerId);
    }

    /**
     * Returns the opponent's board for a given player.
     *
     * @param playerId the player's identifier
     * @return the opponent's game board
     */
    private GameBoard getOpponentBoard(int playerId) {
        return boards.get(getOpponentId(playerId));
    }

    /**
     * Returns a modified version of the opponent's board for display.
     * <p>
     * It converts the opponent's board values as follows: if the cell value is
     * non-zero and divisible by 10, then it is divided by 10; otherwise, the cell is
     * set to 0.
     * </p>
     *
     * @param playerId the player's identifier
     * @return a byte array representing the opponent's board for display
     */
    private byte[] getOppBoard(int playerId) {
        byte[] board = getOpponentBoard(playerId).getBoard();
        byte[] opponentsBoard = new byte[board.length];

        for (int i = 0; i < board.length; i++) {
            byte value = board[i];
            if (value % 10 == 0) {
                opponentsBoard[i] = (byte) (value == 0 ? 0 : value / 10);
            } else {
                opponentsBoard[i] = (byte) 0;
            }
        }

        return opponentsBoard;
    }

    /**
     * Returns the dimensions of one of the game boards.
     *
     * @return a byte array representing the board dimensions, or null if no boards
     *         exist
     */
    public byte[] getBoardDimensions() {
        if (!boards.isEmpty()) {
            return boards.values().iterator().next().getBoardDimensions();
        }
        return null;
    }

    /**
     * Processes a shot at a specific cell.
     *
     * @param playerId the player's identifier
     * @param r        the row (1-indexed)
     * @param c        the column (1-indexed)
     * @return the result of the shot, or -1 if invalid
     */
    @Override
    public int shot(int playerId, int r, int c) {
        byte[] boardSize = getBoardDimensions();
        int H = (int) boardSize[0];
        int W = (int) boardSize[1];
        // Check the board dimensions
        if (r < 1 || c < 1 || r > H || c > W) {
            return -1;
        }
        return processShot(playerId, r - 1, c - 1);
    }

    /**
     * Processes a shot at a random cell.
     *
     * @param playerId the player's identifier
     * @return the result of the shot, or -1 if invalid
     */
    @Override
    public int shot(int playerId) {
        // Get the board dimensions
        byte[] boardSize = getBoardDimensions();
        byte W = boardSize[0];
        byte H = boardSize[1];
        // Pick a random cell to shot
        Random rand = new Random();
        int r = rand.nextInt(H);
        int c = rand.nextInt(W);
        // Process the shot
        return processShot(playerId, r, c);
    }

    /**
     * Processes the shot on the opponent's board.
     *
     * @param playerId the player's identifier
     * @param r        the row (0-indexed)
     * @param c        the column (0-indexed)
     * @return the result of the shot, -1 if invalid, or other status codes defined by
     *         GameBoard
     */
    private int processShot(int playerId, int r, int c) {
        if (!boards.containsKey(playerId)) {
            return -1;
        }

        if (playerId != getActivePlayer()) {
            return -1;
        }

        GameBoard board = getOpponentBoard(playerId);
        int[] data = board.cellValue(r, c);
        int instance = data[0];
        int type = data[1];
        if (type == 0 && instance != 0) {
            return -1;
        }

        int shotResult = board.shot(r, c);
        if (shotResult == -1) {
            return -1;
        }

        if (shotResult == 2) {
            this.removeVessel(playerId, type);
        }

        return shotResult;
    }

    /*
     *
     * PLAYERS
     * Functions to manage the players
     *
     */

    /**
     * Adds a player to the game along with its board and vessels.
     *
     * @param playerId the player's identifier
     * @param W        the board width
     * @param H        the board height
     * @param ai       whether the player is controlled by AI
     * @return true if the player was added successfully; false otherwise
     */
    public boolean addPlayer(int playerId, byte W, byte H, boolean ai) {
        if (players.containsKey(playerId) || players.size() >= 2) {
            return false;
        }

        players.put(playerId, ai);
        vessels.put(playerId, new byte[5]);
        createBoards(playerId, W, H);

        if (players.size() == 1) {
            setActivePlayer(playerId);
        }

        return true;
    }

    /**
     * Checks if a player exists in the game.
     *
     * @param playerId the player's identifier
     * @return true if the player exists; false otherwise
     */
    public boolean playerExists(int playerId) {
        return this.players.containsKey(playerId);
    }

    /**
     * Removes a player and related resources from the game.
     *
     * @param playerId the player's identifier
     * @return true if the player was removed; false otherwise
     */
    private boolean removePlayer(int playerId) {
        if (this.players.containsKey(playerId)) {
            this.players.remove(playerId);
            this.boards.remove(playerId);
            this.vessels.remove(playerId);
            return true;
        }
        return false;
    }

    /**
     * Sets the active player for the game.
     *
     * @param playerId the identifier of the active player
     */
    public void setActivePlayer(int playerId) {
        this.activePlayer = playerId;
    }

    /**
     * Returns the currently active player.
     *
     * @return the active player's identifier, or -1 if the game is finished
     */
    @Override
    public int getActivePlayer() {
        if (this.gameState.getCode() == 4) {
            return -1;
        }
        return this.activePlayer;
    }

    /**
     * Sets the winning player.
     *
     * @param playerId the winner's identifier
     */
    public void setWinPlayer(int playerId) {
        this.winPlayer = playerId;
    }

    /**
     * Returns the winning player.
     *
     * @return the winning player's identifier, or -1 if the game is not finished
     */
    @Override
    public int getWinPlayer() {
        if (this.gameState.getCode() != 4) {
            return -1;
        }
        return this.winPlayer;
    }

    /**
     * Returns the number of players in the game.
     *
     * @return the count of players
     */
    @Override
    public int getNumPlayers() {
        return this.players.size();
    }

    /**
     * Returns the player ID at the specified index.
     *
     * @param idx the index of the player
     * @return the player ID, or -1 if the index is out of bounds
     */
    @Override
    public int getPlayerId(int idx) {
        if (idx < 0 || idx >= this.players.size() || this.players.isEmpty()) {
            return -1;
        }
        return new ArrayList<>(players.keySet()).get(idx);
    }

    /**
     * Retrieves the opponent's player ID.
     *
     * @param playerId the current player's identifier
     * @return the opponent's identifier, or -1 if not available
     */
    public int getOpponentId(int playerId) {
        if (players.size() < 2) {
            return -1;
        }

        for (int opponentId : players.keySet()) {
            if (opponentId != playerId) {
                return opponentId;
            }
        }

        return -1;
    }

    /**
     * Checks whether the player has placed all vessels.
     *
     * @param playerId the player's identifier
     * @return true if the player is ready; false otherwise
     */
    @Override
    public boolean isPlayerReady(int playerId) {
        if (!playerExists(playerId)) {
            return false;
        }
        for (int i = 0; i < 5; i++) {
            if (this.getRemainingVessels(playerId, i + 1) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether all players in the game are ready.
     *
     * @return true if all players are ready; false otherwise
     */
    public boolean allPlayersReady() {
        for (int playerId : players.keySet()) {
            if (!isPlayerReady(playerId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the specified player is controlled by AI.
     *
     * @param playerId the player's identifier
     * @return true if the player is a bot; false otherwise
     */
    @Override
    public boolean isBot(int playerId) {
        return players.get(playerId);
    }

    /*
     *
     * VESSELS
     * Functions to manage the vessels
     *
     */

    /**
     * Sets the total number of vessels available for each type.
     *
     * @param vessels an array representing the total vessels per type
     */
    public void setVessels(byte[] vessels) {
        if (vessels.length == 5) {
            this.totalVessels = vessels;
        }
    }

    /**
     * Returns the total vessels configuration.
     *
     * @return a byte array of total vessels per type
     */
    public byte[] getVessels() {
        return this.totalVessels;
    }

    /**
     * Adds random vessels for a bot player.
     *
     * @param playerId the bot player's identifier
     */
    public void addRandomVessels(int playerId) {
        if (!boards.containsKey(playerId)) {
            return;
        }

        byte[] boardSize = getBoardDimensions();
        byte W = boardSize[0];
        byte H = boardSize[1];
        Random rand = new Random();

        int[] shipSizes = { 5, 4, 3, 3, 2 };

        for (int type = 1; type <= totalVessels.length; type++) {
            int shipsToPlace = totalVessels[type - 1];

            for (int i = 0; i < shipsToPlace; i++) {
                boolean placed = false;
                while (!placed) {
                    int row = rand.nextInt(H);
                    int col = rand.nextInt(W);
                    boolean isVertical = rand.nextBoolean();
                    int endRow = isVertical ? row + shipSizes[type - 1] - 1 : row;
                    int endCol = !isVertical ? col + shipSizes[type - 1] - 1 : col;

                    if (endRow < H && endCol < W) {
                        placed = addVessel(playerId, row, col, endRow, endCol, type);
                    }
                }
            }
        }
    }

    /**
     * Adds a vessel to the player's board.
     *
     * @param playerId the player's identifier
     * @param ri       starting row (1-indexed)
     * @param ci       starting column (1-indexed)
     * @param rf       ending row (1-indexed)
     * @param cf       ending column (1-indexed)
     * @param type     the vessel type (1 to 5)
     * @return true if the vessel was added successfully; false otherwise
     */
    @Override
    public boolean addVessel(int playerId, int ri, int ci, int rf, int cf, int type) {
        if (!boards.containsKey(playerId)) {
            return false;
        }

        GameBoard board = getOwnBoard(playerId);
        byte[] playerVessels = vessels.get(playerId);

        if (playerVessels[type - 1] >= totalVessels[type - 1]) {
            return false;
        }

        int instance = playerVessels[type - 1] + 1;
        if (board.addVessel(ri - 1, ci - 1, rf - 1, cf - 1, type, instance)) {
            playerVessels[type - 1]++;
            vessels.put(playerId, playerVessels);
            return true;
        }

        return false;
    }

    /**
     * Returns the number of remaining vessels of a given type for a player.
     *
     * @param playerId the player's identifier
     * @param type     the vessel type (1 to 5)
     * @return the remaining number of vessels of the specified type
     */
    @Override
    public int getRemainingVessels(int playerId, int type) {
        byte[] playerVessels = vessels.get(playerId);
        return this.totalVessels[type - 1] - playerVessels[type - 1];
    }

    /**
     * Checks if all vessels for a given player have been sunk.
     *
     * @param playerId the player's identifier
     * @return true if all vessels are sunk; false otherwise
     */
    public boolean allVesselsSunk(int playerId) {
        if (!vessels.containsKey(playerId)) {
            return false;
        }

        for (int i = 0; i < totalVessels.length; i++) {
            if (getRemainingVessels(playerId, i + 1) > 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Removes one instance of a vessel for a given player.
     *
     * @param playerId the player's identifier
     * @param type     the vessel type (1 to 5)
     */
    private void removeVessel(int playerId, int type) {
        if (!vessels.containsKey(playerId)) {
            return;
        }

        byte[] playerVessels = vessels.get(playerId);
        if (type < 1 || type > totalVessels.length || playerVessels[type - 1] <= 0) {
            return;
        }

        playerVessels[type - 1]--;
        vessels.put(playerId, playerVessels);
    }

    /*
     *
     * STATE
     * Functions to manage the state
     *
     */

    /**
     * Sets the game state.
     *
     * @param state the new game state
     */
    public void setGameState(GameState state) {
        this.gameState = state;
    }

    /**
     * Notifies all players of the current game status.
     *
     * @return true if notifications to all players succeed; false otherwise
     */
    public boolean notifyStatusToEveryone() {
        boolean result = true;
        for (int playerId : players.keySet()) {
            result &= notifyStatus(playerId);
        }
        return result;
    }

    /**
     * Notifies the specified player of the current game status.
     *
     * @param playerId the player's identifier
     * @return true if the notification is sent successfully; false otherwise
     */
    @Override
    public boolean notifyStatus(int playerId) {
        if (!boards.containsKey(playerId)) {
            return false;
        }

        if (isBot(playerId)) {
            return true;
        }

        byte gameState = this.gameState.getCode();
        GameBoard ownBoard = getOwnBoard(playerId);
        byte[] opponentBoard = getOppBoard(playerId);
        byte[] info = computeInfo(gameState, playerId);

        try {
            GameStatusMessage status = new GameStatusMessage(gameState,
                    ownBoard.getBoardDimensions()[2], ownBoard.getBoard(), opponentBoard, info);
            this.comutils.writeMessage(status);
            return true;
        } catch (BattleshipException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Computes additional game status information for a player.
     *
     * @param gameState the current game state code
     * @param player    the player's identifier
     * @return a byte array containing status information
     */
    private byte[] computeInfo(byte gameState, int player) {
        byte[] info;
        int opponent = getOpponentId(player);

        switch (gameState) {
            case 2:
                info = new byte[7];
                info[0] = (byte) (isPlayerReady(player) ? 1 : 0);
                info[1] = (byte) (isPlayerReady(opponent) ? 1 : 0);
                info[2] = (byte) (getRemainingVessels(player, 1) + getRemainingVessels(opponent, 1));
                info[3] = (byte) (getRemainingVessels(player, 2) + getRemainingVessels(opponent, 2));
                info[4] = (byte) (getRemainingVessels(player, 3) + getRemainingVessels(opponent, 3));
                info[5] = (byte) (getRemainingVessels(player, 4) + getRemainingVessels(opponent, 4));
                info[6] = (byte) (getRemainingVessels(player, 5) + getRemainingVessels(opponent, 5));
                break;
            case 3:
                info = new byte[2];
                info[0] = (byte) (getActivePlayer() == player ? 1 : 0);
                info[1] = (byte) (getActivePlayer() == opponent ? 1 : 0);
                break;
            case 4:
                info = new byte[2];
                info[0] = (byte) (getWinPlayer() == player ? 1 : 0);
                info[1] = (byte) (getWinPlayer() == opponent ? 1 : 0);
                break;
            default:
                info = new byte[0];
                break;
        }
        return info;
    }

    /*
     *
     * GAME
     * Functions to manage the game
     *
     */

    /**
     * Checks whether the game has any players.
     *
     * @return true if at least one player exists; false otherwise
     */
    public boolean gameAlreadyCreated() {
        return this.getNumPlayers() > 0;
    }

    /**
     * Returns the unique game identifier.
     *
     * @return the game ID
     */
    public int getGameId() {
        return this.gameId;
    }

    /**
     * Returns the current game state code.
     *
     * @return the game state code
     */
    @Override
    public byte getGameState() {
        return this.gameState.getCode();
    }

    /**
     * Removes a player from the game.
     *
     * @param playerId the player's identifier
     */
    @Override
    public void leaveGame(int playerId) {
        if (this.players.containsKey(playerId)) {
            this.players.remove(playerId);
        }
    }

    /**
     * Ends the game by setting its state to finished, notifying all players, and
     * then removing them.
     */
    @Override
    public void endGame() {
        this.setGameState(GameState.FINISHED);
        List<Integer> playerIds = new ArrayList<>(players.keySet());

        for (int playerId : playerIds) {
            notifyStatus(playerId);
        }
        for (int playerId : playerIds) {
            removePlayer(playerId);
        }
    }
}