package utils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Test;

import utils.message.*;


public class BattleshipComUtilsTest {
    /**
     * Test the CreateMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testCreate() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            String playerName = "Player1";
            byte w = 3;
            byte h = 4;
            byte[] vessels = {1, 1, 1, 1, 1};
            byte ai = 1;
            Message message = new CreateMessage(playerName, w, h, vessels, ai);


            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the OkMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testOK() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            int game_id = 10000;
            int player_id = 20000;
            Message message = new OkMessage(player_id, game_id);


            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the JoinMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testJoin() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            String playerName = "Player1";
            Message message = new JoinMessage(playerName);

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the RejoinMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testRejoin() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            String playerName = "Player1";
            Message message = new RejoinMessage(playerName);

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the GetConfigMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testGetconfig() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            int game_id = 10000;
            int player_id = 20000;
            Message message = new GetConfigMessage(player_id, game_id);

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the GameConfigMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testGameConfig() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            byte w = 3;
            byte h = 4;
            byte[] vessels = {1, 1, 1, 1, 1};
            Message message = new GameConfigMessage(w, h, vessels);

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the AddVesselMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testAddVessel() {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            int player_id = 20000;
            int game_id = 10000;
            byte vessel = 2;
            byte[] initial = {1, 2};
            byte[] fin = {3, 4};

            Message message = new AddVesselMessage(player_id, game_id, vessel, initial, fin);
            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the GameStatusMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testGameStatus() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            byte gameState = 2;
            int boardSize = 5;
            byte[] board1 = {1, 2, 3, 4, 5};
            byte[] board2 = {6, 7, 8, 9, 10};
            byte[] info = {11, 12, 13, 14, 15, 16, 17};

            Message message = new GameStatusMessage(gameState, boardSize, board1, board2, info);

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the ShotMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testShot() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            int player_id = 20000;
            int game_id = 10000;
            byte[] x = {2, 3};

            Message message = new ShotMessage(player_id, game_id, x);

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the HitMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testHit() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            byte sink = 1;
            Message message = new HitMessage(sink);

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the FailMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testFail() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            Message message = new FailMessage();

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the LeaveMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testLeave() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            int player_id = 20000;
            int game_id = 10000;

            Message message = new LeaveMessage(player_id, game_id);

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the GetStatusMessage class
     * @throws Exception
     * @return void
     */
    @Test
    public void testGetStatus() throws Exception {
        // Arrange
        File file = new File("test");
        try {
            file.createNewFile();
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            BattleshipComUtils comUtils = new BattleshipComUtils(inputStream, outputStream);

            int player_id = 20000;
            int game_id = 10000;
            byte[] x = {2, 3};

            Message message = new ShotMessage(player_id, game_id, x);

            // Act (send and read data)
            comUtils.writeMessage(message);
            Message readMessage = comUtils.readMessage();

            // Assert
            assertEquals(message, readMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
