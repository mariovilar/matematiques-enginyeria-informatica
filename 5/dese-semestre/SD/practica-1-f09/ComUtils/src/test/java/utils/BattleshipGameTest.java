package utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.message.*;
import utils.game.BattleshipGame;
import utils.game.GameBoard;
import utils.enums.GameState;
import utils.exceptions.BattleshipException;

/**
 * Test class for the BattleshipGame class.
 * This class is used to test the different functionalities of the BattleshipGame class.
 * The methods that we will test are the most relevant methods from the interface IBattleshipGame:
 * - getRemainingVessels(int playerId, int type)
 * - getNumPlayers()
 * - getPlayerId(int index)
 * - isPlayerReady(int playerId)
 * - addVessel(int playerId, int ri, int ci, int rf, int cf, int type)
 * - getRemainingVessels(int playerId, int type)
 * - notifyStatus(int playerId)
 */

public class BattleshipGameTest {

    private BattleshipComUtils comUtils;
    private BattleshipGame game;

    // El gameId ha de ser un nombre de 5 dígits
    private final int validGameId = 12345;

    @Before
    public void setUp() throws Exception {
        File file = new File("test");
        file.createNewFile();
        FileInputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(file);
        comUtils = new BattleshipComUtils(inputStream, outputStream);
        game = new BattleshipGame(validGameId, comUtils);
    }

    @After
    public void tearDown() throws Exception {
        // No cal netejar res específic en aquest test
    }

    @Test
    public void testAddPlayerSuccess() throws BattleshipException {
        // Afegeix el primer jugador i comprova que és actiu.
        boolean added = game.addPlayer(1, (byte)10, (byte)10, false);
        assertTrue("El primer jugador s'hauria d'afegir correctament.", added);
        assertEquals("El jugador actiu ha de ser el 1", 1, game.getActivePlayer());

        // Afegeix el segon jugador
        boolean added2 = game.addPlayer(2, (byte)10, (byte)10, false);
        assertTrue("El segon jugador s'hauria d'afegir correctament.", added2);
        assertEquals("El jugador actiu ha de ser el 1 encara", 1, game.getActivePlayer());
        
        // Verifica que no es pot afegir un tercer jugador
        boolean added3 = game.addPlayer(3, (byte)10, (byte)10, false);
        assertFalse("Només es poden afegir 2 jugadors.", added3);

        // Comprova que el jugador actiu és el primer
        assertEquals("El jugador actiu ha de ser el 1", 1, game.getActivePlayer());
    }

    @Test
    public void testGetNumPlayersAndGetPlayerId() throws BattleshipException {
        game.addPlayer(1, (byte)10, (byte)10, false);
        game.addPlayer(2, (byte)10, (byte)10, false);
        
        int player1 = game.getPlayerId(0);
        int player2 = game.getPlayerId(1);
        // Com que les claus es guarden en un HashMap, permetem qualsevol ordre.
        assertTrue("Els jugadors han de ser 1 i 2", 
                   (player1 == 1 && player2 == 2));
    }

    @Test
    public void testAddVesselAndRemainingVessels() throws BattleshipException {
        // Afegim el jugador 1 amb un tauler de 10x10
        game.addPlayer(1, (byte)10, (byte)10, false);
        
        // Configurem els vessels totals
        byte[] vessels = {1,2,1,1,1};
        game.setVessels(vessels);

        // Inicialment, per tipus 1, totalVessels és {1,2,1,1,1} → remaining per tipus 1 = 1.
        int remainingBefore = game.getRemainingVessels(1, 1);
        assertEquals("Hauria de quedar 1 vaixell de tipus 1", 1, remainingBefore);

        // Afegeix un vaixell de tipus 1
        boolean addedVessel = game.addVessel(1, 1, 1, 1, 5, 1);
        assertTrue("El vaixell hauria d'afegir-se correctament.", addedVessel);
        
        int remainingAfter = game.getRemainingVessels(1, 1);
        assertEquals("No hauria de quedar cap vaixell de tipus 1", 0, remainingAfter);
        
        // Comprova que intentar afegir un altre vaixell del mateix tipus falla.
        boolean addedAgain = game.addVessel(1, 2, 2, 2, 6, 1);
        assertFalse("No es poden afegir més vaixells del tipus 1 del que està permès.", addedAgain);

        // Afegir un altre tipus de vaixell (per exemple, tipus 2) en cas que la configuració per tipus 2 sigui diferent.
        // Suposem que per al tipus 2 la quantitat inicial permet 2 vaixells.
        int remainingType2Before = game.getRemainingVessels(1, 2);
        assertEquals("Hauria de quedar 2 vaixells de tipus 2", 2, remainingType2Before);
        
        // Afegim el primer vaixell de tipus 2. La mida és diferent a l'esperada (T2=4)
        boolean addedType2First = game.addVessel(1, 3, 3, 3, 7, 2);
        assertFalse("Vaixell de mida inadequada.", addedType2First);

        // Afegim el primer vaixell de tipus 2. Ara la mida és correcta
        addedType2First = game.addVessel(1, 3, 3, 3, 6, 2);
        assertTrue("El primer vaixell de tipus 2 hauria d'afegir-se correctament.", addedType2First);
        
        int remainingType2AfterFirst = game.getRemainingVessels(1, 2);
        assertEquals("Després d'afegir-ne 1, haurien de quedar 1 vaixell de tipus 2", 1, remainingType2AfterFirst);
        
        // Afegim el segon vaixell de tipus 2
        boolean addedType2Second = game.addVessel(1, 4, 4, 4, 7, 2);
        assertTrue("El segon vaixell de tipus 2 hauria d'afegir-se també.", addedType2Second);
        
        int remainingType2AfterSecond = game.getRemainingVessels(1, 2);
        assertEquals("No hauria de quedar cap vaixell de tipus 2", 0, remainingType2AfterSecond);
        
        // Prova final: intentar afegir més d'un vaixell de tipus 2 ha de fallar.
        boolean addedType2Again = game.addVessel(1, 5, 5, 5, 7, 2);
        assertFalse("No es poden afegir més vaixells de tipus 2 del que està permès.", addedType2Again);
    }

    @Test
    public void testAddVesselEdgeCases() throws BattleshipException {
        // Afegim el jugador amb un tauler de 10x10
        game.addPlayer(1, (byte)10, (byte)10, false);
        
        // 1. Prova: intent de col·locar un vaixell fora dels límits (coordenades negatives)
        boolean outsideNegative = game.addVessel(1, -1, 0, -1, 4, 1);
        assertFalse("No es pot posar un vaixell amb coordenades negatives.", outsideNegative);
        
        // 2. Prova: intent de col·locar un vaixell fora dels límits (columna superior al màxim)
        boolean outsideWidth = game.addVessel(1, 0, 8, 0, 12, 1);
        assertFalse("No es pot posar un vaixell fora dels límits del tauler.", outsideWidth);
        
        // 3. Prova: intent de col·locar un vaixell amb mida inadequada per al tipus 1 (espera 5 caselles però en posa 4)
        // Per tipus 1, se suposa que la mida esperada és 5 (segons el vector shipSizes)
        boolean inadequateSize = game.addVessel(1, 1, 1, 1, 3, 1); // longitud = 4
        assertFalse("Un vaixell amb mida inadequada ha de fallar.", inadequateSize);
        
        // 4. Col·locar correctament un vaixell de tipus 1 a la fila 1, que ocupa les columnes 1 a 5
        boolean validPlacement = game.addVessel(1, 1, 1, 1, 5, 1); // longitud = 5
        assertTrue("El vaixell hauria d'afegir-se correctament.", validPlacement);
        
        // 5. Prova: intentar col·locar un vaixell que solapi (col·lisioni) amb el vaixell anterior
        // Intentem posar un vaixell que ocupa, per exemple, les columnes 1 a 6, on ja estan ocupades les columnes 0-4.
        boolean overlapping = game.addVessel(1, 1, 1, 5, 1, 1);
        assertFalse("No es pot col·locar un vaixell que solapi amb un altre.", overlapping);
        
        // 6. Prova: Posar un vaixell vertical fora dels límits
        boolean verticalOutside = game.addVessel(1, 8, 1, 12, 1, 2);
        assertFalse("Un vaixell vertical fora dels límits ha de fallar.", verticalOutside);
        
        // 7. Prova: Col·locar un vaixell vertical amb mida inadequada.
        // Si per al tipus 2 s'espera mida 4 però es prova col·locar-ne un de mida 3, ha de fallar.
        boolean verticalInadequate = game.addVessel(1, 2, 2, 6, 2, 2); // mida = 3
        assertFalse("Un vaixell vertical amb mida inadequada ha de fallar.", verticalInadequate);
        
        // 8. Prova: Col·locar correctament un vaixell vertical de tipus 3.
        // Per al tipus 3, esperem una mida de 3 (segons shipSizes: tipus 2 → 3)
        boolean validVertical = game.addVessel(1, 8, 8, 5, 8, 2); // mida = 4
        assertTrue("El vaixell vertical hauria d'afegir-se correctament.", validVertical);
    }

    @Test
    public void testIsPlayerReady() throws BattleshipException {
        // Un jugador sense haver posat vaixells no està llest.
        game.addPlayer(1, (byte)10, (byte)10, false);
        game.addPlayer(2, (byte)10, (byte)10, false);

        assertFalse("El jugador no hauria d'estar llest si no ha col·locat els vaixells.",
                    game.isPlayerReady(1));
        // Vegem si el jugador està ready
        byte[] vessels = {1,0,0,0,0};
        game.setVessels(vessels);
        game.addVessel(1, 1, 1, 1, 5, 1);
        assertTrue("El jugador hauria d'estar llest si ha col·locat els vaixells.", game.isPlayerReady(1));
    }

    @Test
    public void testShotInvalidCoordinates() throws BattleshipException {
        // Afegim dos jugadors amb taulers de 10x10
        game.addPlayer(1, (byte)10, (byte)10, false);
        game.addPlayer(2, (byte)10, (byte)10, false);
        
        // Configurem la configuració global de vaixells.
        // Exemple: per el tipus 1 es permet 1 vaixell (vesselsConfig[0] = 1)
        byte[] vesselsConfig = {1, 0, 0, 0, 0};
        game.setVessels(vesselsConfig);
        
        // Jugador 1: afegim un vaixell vàlid (horitzontal) amb mida 5 (tipus 1).
        boolean addedP1 = game.addVessel(1, 1, 1, 1, 5, 1);  // Longitud = 5
        assertTrue("El vaixell del jugador 1 hauria d'afegir-se correctament.", addedP1);
        
        // Jugador 2: afegim un vaixell vàlid (també tipus 1) amb mida 5.
        boolean addedP2 = game.addVessel(2, 2, 2, 2, 6, 1);  // Longitud = 5
        assertTrue("El vaixell del jugador 2 hauria d'afegir-se correctament.", addedP2);
        
        // Comprovem que ambdós jugadors estan "ready" (ja que han col·locat els vaixells)
        assertTrue("El jugador 1 ha d'estar ready.", game.isPlayerReady(1));
        assertTrue("El jugador 2 ha d'estar ready.", game.isPlayerReady(2));
        
        // Executem un shot: el jugador 1 fa un shot a la posició 3,3 del tauler (assumim que és vàlida).
        int shotResult = game.shot(1, 12, 12);
        // Validem que el shot no és valod, ja que ens estem passant dels límits del tauler.
        assertEquals("El shot ha de retornar -1", -1, shotResult);
        shotResult = game.shot(1, -1, -1);
        assertEquals("El shot ha de retornar -1 quan les coordenades són no positives", -1, shotResult);
    }

    @Test
    public void testShotHit() throws BattleshipException {
        game.addPlayer(1, (byte)10, (byte)10, false);
        game.addPlayer(2, (byte)10, (byte)10, false);
        
        // Configurem la configuració global de vaixells.
        // Exemple: per el tipus 1 es permet 1 vaixell (vesselsConfig[0] = 1)
        byte[] vesselsConfig = {1, 0, 0, 0, 0};
        game.setVessels(vesselsConfig);
        
        // Jugador 1: afegim un vaixell vàlid (horitzontal) amb mida 5 (tipus 1).
        boolean addedP1 = game.addVessel(1, 1, 1, 1, 5, 1);  // Longitud = 5
        assertTrue("El vaixell del jugador 1 hauria d'afegir-se correctament.", addedP1);
        
        // Jugador 2: afegim un vaixell vàlid (també tipus 1) amb mida 5.
        boolean addedP2 = game.addVessel(2, 2, 2, 2, 6, 1);  // Longitud = 5
        assertTrue("El vaixell del jugador 2 hauria d'afegir-se correctament.", addedP2);
        
        // Comprovem que ambdós jugadors estan "ready" (ja que han col·locat els vaixells)
        assertTrue("El jugador 1 ha d'estar ready.", game.isPlayerReady(1));
        assertTrue("El jugador 2 ha d'estar ready.", game.isPlayerReady(2));
        
        // Executem un shot: el jugador 1 shota a la posició 3,3 del tauler (assumim que és vàlida).
        int shotResult = game.shot(1, 3, 3);
        
        // Validem que el shot és vàlid (no retorna -1, que indicaria error).
        assertNotEquals("El shot no hauria de retornar -1 perquè és correcte.", -1, shotResult);
        // Indiquem que el shot no és aigua
        assertEquals("El shot hauria de ser aigua.", 0, shotResult);

        // El jugador 1 fa un shot a una cel·la del vaixell enemic (per exemple, (2,2))
        shotResult = game.shot(1, 2, 2);
    
        // Comprovem que aquest shot resulta en "tocat" (expected: 1)
        assertEquals("El shot hauria de donar tocat (hit) però no enfonsat.", 1, shotResult);

        shotResult = game.shot(2, 2, 2);
        assertEquals("El jugador 2 no hauria de poder fer un shot ja que no és el seu torn.", -1, shotResult);
    }

    @Test
    public void testShotSink() throws BattleshipException {
        // Afegir dos jugadors (jugador 1 dispara, jugador 2 té el vaixell)
        game.addPlayer(1, (byte)10, (byte)10, false);
        game.addPlayer(2, (byte)10, (byte)10, false);
        
        // Configurar els vaixells: per al tipus 1 es permet 1 vaixell
        byte[] vesselsConfig = {1, 0, 0, 0, 0};
        game.setVessels(vesselsConfig);
        
        // Afegir un vaixell al jugador 2: horitzontal de (2,2) a (2,6) (mida 5)
        boolean addedP1 = game.addVessel(1, 1, 2, 1, 6, 1);
        assertTrue("El jugador 1 ha d'estar ready.", game.isPlayerReady(1));
        assertTrue("El vaixell del jugador 2 hauria d'afegir-se correctament.", addedP1);
        
        boolean addedP2 = game.addVessel(2, 2, 2, 2, 6, 1);
        // Comprovem que el jugador 2 està "ready"
        assertTrue("El jugador 2 ha d'estar ready.", game.isPlayerReady(2));
        assertTrue("El vaixell del jugador 2 hauria d'afegir-se correctament.", addedP2);
        
        // El jugador 1 realitza shots sobre totes les cel·les del vaixell:
        // Primeres quatre cel·les han de retornar "tocat" (1)
        int shot1 = game.shot(1, 2, 2);
        assertEquals("El primer shot hauria de donar tocat.", 1, shot1);
        int shot2 = game.shot(1, 2, 3);
        assertEquals("El segon shot hauria de donar tocat.", 1, shot2);
        int shot3 = game.shot(1, 2, 4);
        assertEquals("El tercer shot hauria de donar tocat.", 1, shot3);
        int shot4 = game.shot(1, 2, 5);
        assertEquals("El quart shot hauria de donar tocat.", 1, shot4);
        
        // El darrer shot al vaixell ha de resultar en "enfonsat" (2)
        int shot5 = game.shot(1, 2, 6);
        assertEquals("El cinquè shot hauria d'enfonsar el vaixell.", 2, shot5);
    }

    @Test
    public void testNotifyStatus() throws BattleshipException, IOException {
        // Afegim dos jugadors amb taulers de 10x10
        byte[] vessels = {1,0,0,0,0};
        game.setVessels(vessels);
        game.addPlayer(1, (byte)10, (byte)10, false);
        game.addVessel(1, 1, 1, 1, 5, 1);
        game.addPlayer(2, (byte)10, (byte)10, false);
        game.addVessel(2, 2, 2, 2, 6, 1);
        assertTrue("Els dos jugadors estan preparats", game.allPlayersReady());

        // Posem l'estat a PLAYING, això ho gestiona el GameHandler
        game.setGameState(GameState.PLAYING);

        // Fem un impacte a la posició 2,2 del jugador 2
        int isShot = game.shot(1, 2, 2);
        assertEquals("El shot hauria de retornar shot", 1, isShot);

        // Fem un impacte a la posició 1,1 del jugador 1
        isShot = game.shot(1, 3, 2);
        assertEquals("El shot hauria de retornar aigua", 0, isShot);

        // Canviem l'active player manualment, es gestiona al GameHandler
        game.setActivePlayer(2);
        isShot = game.shot(2, 1, 1);
        assertEquals("El shot hauria de retornar shot", 1, isShot);
        
        
        // L'estat hauria de ser playing i els arrays haurien de ser els següents
        byte expectedState = GameState.PLAYING.getCode();
        byte[] expectedBoard1 = new byte[100];
        byte[] expectedBoard2 = new byte[100];
        expectedBoard1[(1-1)*10 + (1-1)] = (byte) 20; // tocat
        expectedBoard1[1] = (byte) 11;               // posició vaixell propi
        expectedBoard1[2] = (byte) 11;               // posició vaixell propi
        expectedBoard1[3] = (byte) 11;               // posició vaixell propi
        expectedBoard1[4] = (byte) 11;               // posició vaixell propi
        expectedBoard2[(2-1)*10 + (2-1)] = (byte) 2;  // tocat
        expectedBoard2[(3-1)*10 + (2-1)] = (byte) 1;  // aigua
        
        byte[] info = new byte[2];
        info[0] = 0; // Jugador 1 espera
        info[1] = 1; // Jugador actiu és el jugador 2

        // Construïm el missatge esperat
        GameStatusMessage expected = new GameStatusMessage(expectedState, 100, expectedBoard1, expectedBoard2, info);

        // Com que el Dummy no llença excepcions, notifyStatus ha de retornar true.
        boolean notified = game.notifyStatus(1);
        assertTrue("El notifyStatus ha de retornar true per jugador no bot amb tauler inicialitzat.", notified);
        GameStatusMessage msg = (GameStatusMessage) this.comUtils.readMessage();
        assertEquals(msg, expected);
    }

    @Test
    public void testLeaveGameAndEndGame() throws BattleshipException {
        game.addPlayer(1, (byte)10, (byte)10, false);
        game.addPlayer(2, (byte)10, (byte)10, false);
        // El jugador 2 abandona la partida.
        game.leaveGame(2);
        assertEquals("Després d'abandonar, el nombre de jugadors ha de ser 1.", 1, game.getNumPlayers());
        // El jugador 1 abandona la partida.
        game.leaveGame(1);
        assertEquals("Després d'abandonar, el nombre de jugadors ha de ser 0.", 0, game.getNumPlayers());
       
        game.addPlayer(1, (byte)10, (byte)10, false);
        game.addPlayer(2, (byte)10, (byte)10, false);
        game.endGame();
        assertEquals("Després de finalitzar la partida, el nombre de jugadors ha de ser 0.", 0, game.getNumPlayers());
    }
}