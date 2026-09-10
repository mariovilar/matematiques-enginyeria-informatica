package utils.game;

/**
 * Represents a game board for battleship style games.
 * <p>
 * The board supports adding vessels, recording shots, and checking vessel states.
 * It maintains an original board state to allow checking if vessels are sunk.
 * </p>
 */
public class GameBoard {
    private byte W;
    private byte H;
    private byte[] board;
    private byte[] originalBoard;

    /**
     * Constructs a GameBoard with the specified width and height.
     *
     * @param W the width of the board
     * @param H the height of the board
     */
    public GameBoard(byte W, byte H) {
        this.W = W;
        this.H = H;
        this.board = new byte[W * H];

        // We create a copy of board so we can check for the sunk vessels
        this.originalBoard = this.board.clone();
    }

    /**
     * Retrieves the current board state.
     *
     * @return the board as a byte array
     */
    public byte[] getBoard() {
        return this.board;
    }

    /**
     * Retrieves the board dimensions and total cells.
     *
     * @return a byte array containing width, height, and total cells (W * H)
     */
    public byte[] getBoardDimensions() {
        return new byte[] { this.W, this.H, (byte) ((this.W * this.H) & 0xFF) };
    }

    /**
     * Computes the index in the board array for the given row and column.
     *
     * @param row the row number
     * @param col the column number
     * @return the computed index
     */
    private int getIndex(int row, int col) {
        return row * this.W + col;
    }

    /**
     * Checks if the given cell coordinates are valid.
     *
     * @param row the row number
     * @param col the column number
     * @return true if the cell is within the board, false otherwise
     */
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < this.H && col >= 0 && col < this.W;
    }

    /**
     * Checks if the given type is valid.
     *
     * @param t the type to check
     * @return true if the type is between 1 and 5, false otherwise
     */
    private boolean isValidType(int t) {
        return t >= 1 && t <= 5;
    }

    /**
     * Checks if the given instance is valid.
     *
     * @param n the instance to check
     * @return true if the instance is between 0 and 25, false otherwise
     */
    private boolean isValidInstance(int n) {
        return n >= 0 && n <= 25;
    }

    /**
     * Decodes the stored byte value into its instance number and type.
     *
     * @param value the encoded cell value
     * @return an array where index 0 is the instance number and index 1 is the type
     */
    private int[] decodeCell(byte value) {
        int n = (value & 0xFF) / 10;
        int t = (value & 0xFF) % 10;
        return new int[] { n, t };
    }

    /**
     * Gets the decoded cell value at the specified coordinates.
     *
     * @param row the row number
     * @param col the column number
     * @return an int array where index 0 is the instance and index 1 is the type
     */
    public int[] cellValue(int row, int col) {
        return decodeCell(board[getIndex(row, col)]);
    }

    /**
     * Sets a cell with the encoded instance and type if the coordinates, instance, and type are valid.
     *
     * @param row the row number
     * @param col the column number
     * @param n   the instance number
     * @param t   the type of vessel
     */
    private void setCell(int row, int col, int n, int t) {
        if (isValidCell(row, col) && isValidInstance(n) && (isValidType(t) || t == 0)) {
            int index = this.getIndex(row, col);
            byte value = encodeCell(n, t);

            // We store the info in the original and the board
            board[index] = value;
            originalBoard[index] = value;
        }
    }

    /**
     * Encodes the instance and type into a single byte.
     *
     * @param n the instance number
     * @param t the type of vessel
     * @return the encoded byte
     */
    private byte encodeCell(int n, int t) {
        return (byte) (n * 10 + t);
    }

    /**
     * Sets a special state for the cell.
     * <p>
     * The valid states are:
     * 0 - Default
     * 1 - Miss
     * 2 - Hit
     * 3 - Sunk
     * </p>
     *
     * @param row   the row number
     * @param col   the column number
     * @param state the special state to be set (0-3)
     */
    public void setSpecialCell(int row, int col, int state) {
        if (isValidCell(row, col) && state >= 0 && state <= 3) {
            board[getIndex(row, col)] = (byte) (10 * state);
        }
    }

    /**
     * Encodes a special state into a byte.
     *
     * @param state the special state (0-3)
     * @return the encoded state as a byte
     */
    private byte encodeSpecialState(int state) {
        return (byte) (state * 10);
    }

    /**
     * Attempts to add a vessel to the board.
     * <p>
     * The vessel must be placed either horizontally or vertically, without overlapping existing vessels,
     * and its size must match the expected size for its type.
     * </p>
     *
     * @param ri       starting row index
     * @param ci       starting column index
     * @param rf       ending row index
     * @param cf       ending column index
     * @param type     the type of vessel (1-5)
     * @param instance the instance number of the vessel
     * @return true if the vessel was successfully added, false otherwise
     */
    public boolean addVessel(int ri, int ci, int rf, int cf, int type, int instance) {
        // Validate the type and instance
        if (!isValidType(type) || !isValidInstance(instance)) {
            return false;
        }

        // Check the board dimensions
        if (ri < 0 || ci < 0 || rf < 0 || cf < 0 || rf >= H || cf >= W) {
            return false;
        }

        // Expected size of the vessels
        int[] shipSizes = { 5, 4, 3, 3, 2 };
        int expectedSize = shipSizes[type - 1];

        // Determine the orientation
        if (ri == rf) {
            int start = Math.min(ci, cf);
            int end = Math.max(ci, cf);
            int actualSize = end - start + 1;

            // Invalid size
            if (actualSize != expectedSize) {
                return false;
            }

            // We check if there's space
            for (int col = start; col <= end; col++) {
                if (board[getIndex(ri, col)] != 0) {
                    return false;
                }
            }

            // We add the vessel
            for (int col = start; col <= end; col++) {
                setCell(ri, col, instance, type);
            }
        } else if (ci == cf) {
            int start = Math.min(ri, rf);
            int end = Math.max(ri, rf);
            int actualSize = end - start + 1;

            // Invalid size
            if (actualSize != expectedSize) {
                return false;
            }

            // We check if there's space
            for (int row = start; row <= end; row++) {
                if (board[getIndex(row, ci)] != 0) {
                    return false;
                }
            }

            // We add the vessel
            for (int row = start; row <= end; row++) {
                setCell(row, ci, instance, type);
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Processes a shot at the specified cell.
     * <p>
     * If the shot misses, the cell is marked accordingly.
     * If the shot hits a vessel, it checks if the vessel has been sunk.
     * </p>
     *
     * @param r the row number
     * @param c the column number
     * @return 0 for a miss, 1 for a hit, 2 if the vessel is sunk, or -1 for an invalid shot
     */
    public int shot(int r, int c) {
        if (!isValidCell(r, c)) {
            return -1;
        }

        // We get the shot cell
        byte cellValue = getBoard()[getIndex(r, c)];

        // Miss
        if (cellValue == 0) {
            setSpecialCell(r, c, 1);
            return 0;
        }

        int[] decoded = decodeCell(cellValue);
        int n = decoded[0];
        int t = decoded[1];

        if (isValidType(t)) {
            // Hit
            setSpecialCell(r, c, 2);

            // Check if the vessel is sunk
            if (isVesselSunk(n, t)) {
                markVesselAsSunk(n, t);
                return 2;
            }
            return 1;
        }

        return -1;
    }

    /**
     * Sets all cells of the vessel with the specified instance and type as sunk.
     *
     * @param instance the instance number of the vessel
     * @param type     the type of the vessel
     */
    private void markVesselAsSunk(int instance, int type) {
        // If the vessel is sunk, we set all values to indicate sunk state
        for (int i = 0; i < board.length; i++) {
            int[] decoded = decodeCell(board[i]);
            int n = decoded[0];
            int t = decoded[1];

            if (n == instance && t == type) {
                board[i] = encodeSpecialState(3);
            }
        }
    }

    /**
     * Checks if a vessel with the specified instance and type is sunk.
     * <p>
     * The vessel is considered sunk if every cell belonging to it has been hit.
     * </p>
     *
     * @param instance the instance number of the vessel
     * @param type     the type of the vessel
     * @return true if the vessel is sunk, false otherwise
     */
    private boolean isVesselSunk(int instance, int type) {
        // We loop through the board looking for the vessels
        // of same instance and type, in case of finding one not hit
        // it means the vessel is not sunk
        boolean found = false;

        for (int i = 0; i < originalBoard.length; i++) {
            int[] decoded = decodeCell(originalBoard[i]);
            int n = decoded[0];
            int t = decoded[1];

            if (n == instance && t == type) {
                found = true;
                if (board[i] != encodeSpecialState(2)) {
                    return false;
                }
            }
        }
        return found;
    }
}