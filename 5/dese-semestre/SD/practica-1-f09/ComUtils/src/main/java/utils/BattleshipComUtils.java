package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import utils.enums.ErrorType;
import utils.enums.MessageType;
import utils.exceptions.BattleshipException;
import utils.message.*;

/**
 * The BattleshipComUtils class extends the ComUtils class to provide
 * functionality for reading and writing Battleship game messages over streams.
 * It handles various message types, including error messages, game configuration,
 * player actions, and game status updates.
 * 
 * <p>This class provides methods to:
 * <ul>
 *   <li>Read messages from an input stream and parse them into specific message objects.</li>
 *   <li>Write specific message objects to an output stream.</li>
 *   <li>Close the input and output streams safely.</li>
 * </ul>
 * 
 * <p>Each message type has its own read and write methods, which handle the
 * serialization and deserialization of message fields according to the Battleship
 * protocol.
 * 
 * <p>Usage:
 * <pre>
 * InputStream inputStream = ...;
 * OutputStream outputStream = ...;
 * BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);
 * 
 * // Reading a message
 * Message message = comUtils.readMessage();
 * 
 * // Writing a message
 * comUtils.writeMessage(message);
 * 
 * // Closing streams
 * comUtils.closeStreams();
 * </pre>
 * 
 * <p>Note: This class assumes that the input and output streams are properly
 * initialized and that the protocol is followed strictly.
 * 
 * @see ComUtils
 * @see Message
 * @see MessageType
 * @see BattleshipException
 */


public class BattleshipComUtils extends ComUtils {

    /**
     * Constructs a new BattleshipComUtils instance with the specified input and output streams.
     *
     * @param inputStream  the input stream to read data from.
     * @param outputStream the output stream to write data to.
     * @throws BattleshipException if an error specific to Battleship operations occurs.
     * @throws IOException         if an I/O error occurs while initializing the streams.
     */
    public BattleshipComUtils(InputStream inputStream, OutputStream outputStream)
            throws BattleshipException, IOException {
        super(inputStream, outputStream);
    }

    /**
     * Constructor for the BattleshipComUtils class.
     * This initializes an instance of BattleshipComUtils by invoking the constructor
     * of its superclass with the provided ComUtils object.
     *
     * @param obj An instance of ComUtils used to initialize the superclass.
     */
    public BattleshipComUtils(ComUtils obj) {
        super(obj);
    }

    
    /**
     * Closes the input and output streams associated with this instance.
     * Ensures that the output stream is flushed before closing.
     * If an IOException occurs during the process, it is caught and an error
     * message is printed to the console.
     */
    public void closeStreams() {
        try {
            if (super.dataOutputStream != null) {
                super.dataOutputStream.flush();
                super.dataOutputStream.close();
            }
            if (super.dataInputStream != null) {
                super.dataInputStream.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing streams: " + e.getMessage());
        }
    }

    /**
     * Reads a message from the input stream and returns it as a Message object.
     *
     * @return the read Message object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    public Message readMessage() throws BattleshipException, IOException {
        MessageType messageType = MessageType.valueOf(read_bytes(1)[0]);
        Message message = null;
        // Switch case per a cada tipus de missatge, ja que haurem de llegir diferents
        // camps
        switch (messageType) {
            case ERROR:
                message = (ErrorMessage) readErrorMessage();
                break;
            case OK:
                message = (OkMessage) readOkMessage();
                break;
            case CREATE:
                message = (CreateMessage) readCreateMessage();
                break;
            case JOIN:
                message = (JoinMessage) readJoinMessage();
                break;
            case REJOIN:
                message = (RejoinMessage) readRejoinMessage();
                break;
            case GETCONFIG:
                message = (GetConfigMessage) readGetConfigMessage();
                break;
            case GAMECONFIG:
                message = (GameConfigMessage) readGameConfigMessage();
                break;
            case ADDVESSEL:
                message = (AddVesselMessage) readAddVesselMessage();
                break;
            case GETSTATUS:
                message = (GetStatusMessage) readGetStatusMessage();
                break;
            case GAMESTATUS:
                message = (GameStatusMessage) readGamestatusMessage();
                break;
            case SHOT:
                message = (ShotMessage) readShotMessage();
                break;
            case HIT:
                message = (HitMessage) readHitMessage();
                break;
            case FAIL:
                message = (FailMessage) readFailMessage();
                break;
            case LEAVE:
                message = (LeaveMessage) readLeaveMessage();
                break;
            default:
                break;
        }
        return message;
    }

    /**
     * Writes a message to the output stream.
     *
     * @param message the Message object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    public void writeMessage(Message message) throws BattleshipException, IOException {
        MessageType messageType = message.getType();
        // El mateix que en el readMessage, però ara escrivim
        // Haurem de llegir diferents camps en funció del tipus de missatge
        // El nombre de bytes a llegir variarà en funció del missatge
        switch (messageType) {
            case ERROR:
                ErrorMessage errorMessage = (ErrorMessage) message;
                writeErrorMessage(errorMessage);
                break;
            case OK:
                OkMessage okMessage = (OkMessage) message;
                writeOkMessage(okMessage);
                break;
            case CREATE:
                CreateMessage createMessage = (CreateMessage) message;
                writeCreateMessage(createMessage);
                break;
            case JOIN:
                JoinMessage joinMessage = (JoinMessage) message;
                writeJoinMessage(joinMessage);
                break;
            case REJOIN:
                RejoinMessage rejoinMessage = (RejoinMessage) message;
                writeRejoinMessage(rejoinMessage);
                break;
            case GETCONFIG:
                GetConfigMessage getConfigMessage = (GetConfigMessage) message;
                writeGetConfigMessage(getConfigMessage);
                break;
            case GAMECONFIG:
                GameConfigMessage gameConfigMessage = (GameConfigMessage) message;
                writeGameConfigMessage(gameConfigMessage);
                break;
            case ADDVESSEL:
                AddVesselMessage addVesselMessage = (AddVesselMessage) message;
                writeAddVesselMessage(addVesselMessage);
                break;
            case GETSTATUS:
                GetStatusMessage getStatusMessage = (GetStatusMessage) message;
                writeGetStatusMessage(getStatusMessage);
                break;
            case GAMESTATUS:
                GameStatusMessage gameStatusMessage = (GameStatusMessage) message;
                writeGamestatusMessage(gameStatusMessage);
                break;
            case SHOT:
                ShotMessage shotMessage = (ShotMessage) message;
                writeShotMessage(shotMessage);
                break;
            case HIT:
                HitMessage hitMessage = (HitMessage) message;
                writeHitMessage(hitMessage);
                break;
            case FAIL:
                FailMessage failMessage = (FailMessage) message;
                writeFailMessage(failMessage);
                break;
            case LEAVE:
                LeaveMessage leaveMessage = (LeaveMessage) message;
                writeLeaveMessage(leaveMessage);
                break;
            default:
                break;
        }
    }

    /**
     * Reads an error message from the input stream.
     *
     * @return the read ErrorMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    private ErrorMessage readErrorMessage() throws BattleshipException, IOException {
        // Llegim els camps del missatge
        byte type = super.read_bytes(1)[0];
        int messageLength = super.read_int32();
        String description = super.read_string(messageLength);

        return new ErrorMessage(ErrorType.valueOf(type), description);
    }

    /**
     * Reads a create message from the input stream.
     *
     * @return the read CreateMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    private CreateMessage readCreateMessage() throws BattleshipException, IOException {
        // Llegim els camps del missatge
        String playerName = super.read_string(50);

        // Llegim els bytes i els convertim a tipus primitius
        byte w = super.read_bytes(1)[0];
        byte h = super.read_bytes(1)[0];
        byte[] vessels = super.read_bytes(5);
        byte ai = super.read_bytes(1)[0];

        return new CreateMessage(playerName, w, h, vessels, ai);
    }

    /**
     * Reads an OK message from the input stream.
     *
     * @return the read OkMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    private OkMessage readOkMessage() throws BattleshipException, IOException {
        // Llegim els camps del missatge
        // En aquest cas, llegim en enters de 32 bits
        int player_id = super.read_int32();
        int game_id = super.read_int32();
        return new OkMessage(player_id, game_id);
    }

    /**
     * Reads a join message from the input stream.
     *
     * @return the read JoinMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    private JoinMessage readJoinMessage() throws BattleshipException, IOException {
        // Llegim en string el nom del jugador
        String playerName = super.read_string(50);
        return new JoinMessage(playerName);
    }

    /**
     * Reads a rejoin message from the input stream.
     *
     * @return the read RejoinMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    private RejoinMessage readRejoinMessage() throws BattleshipException, IOException {
        String playerName = super.read_string(50);
        return new RejoinMessage(playerName);
    }

    /**
     * Reads a get config message from the input stream.
     *
     * @return the read GetConfigMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    private GetConfigMessage readGetConfigMessage() throws BattleshipException, IOException {
        int player_id = super.read_int32();
        int game_id = super.read_int32();
        return new GetConfigMessage(player_id, game_id);
    }

    /**
     * Reads a game config message from the input stream.
     *
     * @return the read GameConfigMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    private GameConfigMessage readGameConfigMessage() throws BattleshipException, IOException {
        // Llegim els camps del missatge
        // Corresponen a la mida del tauler i les embarcacions
        byte w = super.read_bytes(1)[0];
        byte h = super.read_bytes(1)[0];
        byte[] vessels = super.read_bytes(5);
        return new GameConfigMessage(w, h, vessels);
    }

    /**
     * Reads an add vessel message from the input stream.
     *
     * @return the read AddVesselMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    private AddVesselMessage readAddVesselMessage() throws BattleshipException, IOException {
        // Llegim els camps del missatge
        // Corresponen a la id del jugador, id de la partida, tipus de vaixell i
        // posicions inicials i finals
        int player_id = super.read_int32();
        int game_id = super.read_int32();
        byte vessel_type = super.read_bytes(1)[0];
        byte[] initial_position = super.read_bytes(2);
        byte[] final_position = super.read_bytes(2);
        return new AddVesselMessage(player_id, game_id, vessel_type, initial_position, final_position);
    }

    /**
     * Reads a game status message from the input stream.
     *
     * @return the read GameStatusMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    private GameStatusMessage readGamestatusMessage() throws BattleshipException, IOException {
        byte gameState = super.read_bytes(1)[0];
        int boardSize = super.read_int32();

        // Suppose boards are flattened, there is no other way
        byte[] board1 = super.read_bytes(boardSize);
        byte[] board2 = super.read_bytes(boardSize);

        // Different gameStates will need of different byte lengths
        int bytesToRead = 0;
        switch (gameState) {
            case (2):
                bytesToRead = 7;
                break;
            case (3):
                bytesToRead = 2;
                break;
            case (4):
                bytesToRead = 2;
                break;
            default:
                break;
        }
        byte[] info = super.read_bytes(bytesToRead);
        return new GameStatusMessage(gameState, boardSize, board1, board2, info);
    }

    /**
     * Reads a shot message from the input stream.
     *
     * @return the read ShotMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    public ShotMessage readShotMessage() throws BattleshipException, IOException {
        // Llegim els camps del missatge
        // Corresponen a la id del jugador, id de la partida i la posició del tret
        int player_id = super.read_int32();
        int game_id = super.read_int32();
        byte[] position = super.read_bytes(2);
        return new ShotMessage(player_id, game_id, position);
    }

    /**
     * Reads a hit message from the input stream.
     *
     * @return the read HitMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    public HitMessage readHitMessage() throws BattleshipException, IOException {
        // Llegim els camps del missatge
        // Corresponen a si s'ha enfonsat o no
        byte hit = super.read_bytes(1)[0];
        return new HitMessage(hit);
    }

    /**
     * Reads a fail message from the input stream.
     *
     * @return the read FailMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    public FailMessage readFailMessage() throws BattleshipException, IOException {
        return new FailMessage();
    }

    /**
     * Reads a leave message from the input stream.
     *
     * @return the read LeaveMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    public LeaveMessage readLeaveMessage() throws BattleshipException, IOException {
        // Llegim els camps del missatge
        // Corresponen a la id del jugador i id de la partida
        int player_id = super.read_int32();
        int game_id = super.read_int32();
        return new LeaveMessage(player_id, game_id);
    }

    /**
     * Reads a get status message from the input stream.
     *
     * @return the read GetStatusMessage object.
     * @throws BattleshipException if an error occurs while reading the message.
     * @throws IOException         if an I/O error occurs while reading from the stream.
     */
    public GetStatusMessage readGetStatusMessage() throws BattleshipException, IOException {
        // Llegim els camps del missatge
        // Corresponen a la id del jugador i id de la partida
        int player_id = super.read_int32();
        int game_id = super.read_int32();
        return new GetStatusMessage(player_id, game_id);
    }

    /**
     * Writes an error message to the output stream.
     *
     * @param message the ErrorMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeErrorMessage(ErrorMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim el tipus d'error i la descripció
        super.dataOutputStream.writeByte(MessageType.ERROR.getCode());
        super.dataOutputStream.writeByte(message.getByteCode());

        String text = message.getMessage();
        super.write_int32(text.length());
        super.write_string(text);
    }

    /**
     * Writes an OK message to the output stream.
     *
     * @param message the OkMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeOkMessage(OkMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim en enters de 32 bits
        super.dataOutputStream.writeByte(MessageType.OK.getCode());
        super.write_int32(message.getPlayerId());
        super.write_int32(message.getGameId());
    }

    /**
     * Writes a create message to the output stream.
     *
     * @param message the CreateMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeCreateMessage(CreateMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim el nom del jugador, la mida del tauler, les embarcacions i
        // si és AI
        super.dataOutputStream.writeByte(MessageType.CREATE.getCode());
        super.write_string(message.getPlayerName(), 50);
        super.dataOutputStream.writeByte(message.getWidth());
        super.dataOutputStream.writeByte(message.getHeight());
        for (int i = 0; i < 5; i++) {
            super.dataOutputStream.writeByte(message.getVessels()[i]);
        }
        super.dataOutputStream.writeByte(message.isAi());
    }

    /**
     * Writes a join message to the output stream.
     *
     * @param message the JoinMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeJoinMessage(JoinMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim el nom del jugador
        super.dataOutputStream.writeByte(MessageType.JOIN.getCode());
        super.write_string(message.getPlayerName(), 50);
    }

    /**
     * Writes a rejoin message to the output stream.
     *
     * @param message the RejoinMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeRejoinMessage(RejoinMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim el nom del jugador
        super.dataOutputStream.writeByte(MessageType.REJOIN.getCode());
        super.write_string(message.getPlayerName(), 50);
    }

    /**
     * Writes a get config message to the output stream.
     *
     * @param message the GetConfigMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeGetConfigMessage(GetConfigMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim la id del jugador i la id de la partida
        super.dataOutputStream.writeByte(MessageType.GETCONFIG.getCode());
        super.write_int32(message.getPlayerId());
        super.write_int32(message.getGameId());
    }

    /**
     * Writes a game config message to the output stream.
     *
     * @param message the GameConfigMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeGameConfigMessage(GameConfigMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim la mida del tauler i les embarcacions
        super.dataOutputStream.writeByte(MessageType.GAMECONFIG.getCode());
        super.dataOutputStream.writeByte(message.getWidth());
        super.dataOutputStream.writeByte(message.getHeight());
        for (int i = 0; i < 5; i++) {
            super.dataOutputStream.writeByte(message.getVessels()[i]);
        }

    }

    /**
     * Writes an add vessel message to the output stream.
     *
     * @param message the AddVesselMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeAddVesselMessage(AddVesselMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim la id del jugador, la id de la partida, el tipus de
        // vaixell i les posicions inicials i finals
        super.dataOutputStream.writeByte(MessageType.ADDVESSEL.getCode());
        super.write_int32(message.getPlayerId());
        super.write_int32(message.getGameId());
        super.dataOutputStream.writeByte(message.getVesselType());

        int i;
        // És important conservar l'ordre, pel que ho fem en dos bucles diferents
        for (i = 0; i < 2; i++) {
            super.dataOutputStream.writeByte(message.getInitialPosition()[i]);
        }
        for (i = 0; i < 2; i++) {
            super.dataOutputStream.writeByte(message.getFinalPosition()[i]);
        }
    }

    /**
     * Writes a get status message to the output stream.
     *
     * @param message the GetStatusMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeGetStatusMessage(GetStatusMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim la id del jugador i la id de la partida en format enter de
        // 32 bits (4bytes)
        super.dataOutputStream.writeByte(MessageType.GETSTATUS.getCode());
        super.write_int32(message.getPlayerId());
        super.write_int32(message.getGameId());
    }

    /**
     * Writes a get status message to the output stream.
     *
     * @param message the GetStatusMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeGamestatusMessage(GameStatusMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim l'estat del joc, la mida del tauler i les taules
        super.dataOutputStream.writeByte(MessageType.GAMESTATUS.getCode());
        super.dataOutputStream.writeByte(message.getGameState());
        super.write_int32(message.getBoardSize());
        // Iterem i recuperem les taules
        int i;
        for (i = 0; i < message.getBoardSize(); i++) {
            super.dataOutputStream.writeByte(message.getBoard1()[i]);
        }
        for (i = 0; i < message.getBoardSize(); i++) {
            super.dataOutputStream.writeByte(message.getBoard2()[i]);
        }

        // En funció del gameState, escriurem un número de bytes diferent
        switch (message.getGameState()) {
            case (2):
                for (i = 0; i < 7; i++) {
                    super.dataOutputStream.writeByte(message.getInfo()[i]);
                }
                break;
            case (3):
                for (i = 0; i < 2; i++) {
                    super.dataOutputStream.writeByte(message.getInfo()[i]);
                }
                break;
            case (4):
                for (i = 0; i < 2; i++) {
                    super.dataOutputStream.writeByte(message.getInfo()[i]);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Writes a shot message to the output stream.
     *
     * @param message the ShotMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeShotMessage(ShotMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim la id del jugador, la id de la partida i la posició del
        // tret
        super.dataOutputStream.writeByte(MessageType.SHOT.getCode());
        super.write_int32(message.getPlayerId());
        super.write_int32(message.getGameId());
        for (int i = 0; i < 2; i++) {
            super.dataOutputStream.writeByte(message.getPosition()[i]);
        }
    }

    /**
     * Writes a hit message to the output stream.
     *
     * @param message the HitMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeHitMessage(HitMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Abans de res, escrivim el tipus de missatge
        // I després escrivim si s'ha enfonsat o no
        super.dataOutputStream.writeByte(MessageType.HIT.getCode());
        super.dataOutputStream.writeByte(message.getSink());
    }

    /**
     * Writes a fail message to the output stream.
     *
     * @param message the FailMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeFailMessage(FailMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Escrivim el tipus de missatge
        super.dataOutputStream.writeByte(MessageType.FAIL.getCode());
    }

    /**
     * Writes a leave message to the output stream.
     *
     * @param message the LeaveMessage object to be written.
     * @throws BattleshipException if an error occurs while writing the message.
     * @throws IOException         if an I/O error occurs while writing to the stream.
     */
    private void writeLeaveMessage(LeaveMessage message) throws BattleshipException, IOException {
        // Escrivim els camps del missatge
        // Escrivim el tipus de missatge
        // I després escrivim la id del jugador i la id de la partida
        super.dataOutputStream.writeByte(MessageType.LEAVE.getCode());
        super.write_int32(message.getPlayerId());
        super.write_int32(message.getGameId());
    }
}