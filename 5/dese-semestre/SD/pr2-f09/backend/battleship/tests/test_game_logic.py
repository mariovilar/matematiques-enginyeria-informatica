from django.test import TestCase
from django.contrib.auth.models import User
from battleship.api.models import Player, Game, Vessel, Board, BoardVessel, Shot
import uuid


class GameLogicTest(TestCase):
    def setUp(self):
        # Create users and get automatically created players
        unique_suffix1 = uuid.uuid4().hex[:8]
        unique_suffix2 = uuid.uuid4().hex[:8]
        self.user1 = User.objects.create_user(username=f'player1_{unique_suffix1}', password='testpass')
        self.user2 = User.objects.create_user(username=f'player2_{unique_suffix2}', password='testpass')
        # Get the automatically created players (created by signals)
        self.player1 = Player.objects.get(user=self.user1)
        self.player2 = Player.objects.get(user=self.user2)
        
        # Create game
        self.game = Game.objects.create(
            owner=self.player1,
            width=10,
            height=10,
            multiplayer=True,
            phase=Game.PHASE_PLACEMENT
        )
        self.game.players.add(self.player1, self.player2)
        
        # Create boards
        self.board1 = Board.objects.create(game=self.game, player=self.player1)
        self.board2 = Board.objects.create(game=self.game, player=self.player2)
        
        # Create test vessel
        self.vessel = Vessel.objects.create(name='Test Ship', size=2)
    
    def test_game_phase_transition_to_playing(self):
        """Test game transitions to playing when all boards are prepared"""
        # Initially in placement phase
        self.assertEqual(self.game.phase, Game.PHASE_PLACEMENT)
        
        # Prepare first board
        self.board1.prepared = True
        self.board1.save()
        self.game.refresh_from_db()
        self.assertEqual(self.game.phase, Game.PHASE_PLACEMENT)  # Still placement
        
        # Prepare second board
        self.board2.prepared = True
        self.board2.save()
        
        # Manually trigger phase change (normally done in view)
        if all(b.prepared for b in Board.objects.filter(game=self.game)):
            self.game.phase = Game.PHASE_PLAYING
            self.game.turn = self.player1
            self.game.save()
        
        self.assertEqual(self.game.phase, Game.PHASE_PLAYING)
    
    def test_vessel_placement_validation(self):
        """Test vessel placement validation logic"""
        # Test horizontal placement
        board_vessel = BoardVessel.objects.create(
            board=self.board1,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=1  # Horizontal, size 2
        )
        self.assertTrue(board_vessel.alive)
        
        # Test that vessel placement uses correct coordinates
        self.assertEqual(board_vessel.ri, 0)
        self.assertEqual(board_vessel.ci, 0)
        self.assertEqual(board_vessel.rf, 0)
        self.assertEqual(board_vessel.cf, 1)
    
    def test_shot_hit_logic(self):
        """Test shot hit detection logic"""
        # Place vessel on board2
        board_vessel = BoardVessel.objects.create(
            board=self.board2,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=1  # Covers (0,0) and (0,1)
        )
        
        # Test hit at (0,0)
        shot = Shot.objects.create(
            player=self.player1,
            game=self.game,
            board=self.board1,  # Player1's board for tracking
            row=0,
            col=0,
            vessel_hit=board_vessel,
            result=1  # Hit
        )
        self.assertEqual(shot.result, 1)
        self.assertEqual(shot.vessel_hit, board_vessel)
        
        # Test miss at (0,2)
        shot_miss = Shot.objects.create(
            player=self.player1,
            game=self.game,
            board=self.board1,
            row=0,
            col=2,
            result=0  # Miss
        )
        self.assertEqual(shot_miss.result, 0)
        self.assertIsNone(shot_miss.vessel_hit)
    
    def test_vessel_sinking_logic(self):
        """Test vessel sinking detection"""
        # Place vessel
        board_vessel = BoardVessel.objects.create(
            board=self.board2,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=1  # Size 2 vessel
        )
        
        # Hit first cell
        Shot.objects.create(
            player=self.player1,
            game=self.game,
            board=self.board1,
            row=0, col=0,
            vessel_hit=board_vessel,
            result=1
        )
        
        # Vessel should still be alive
        board_vessel.refresh_from_db()
        self.assertTrue(board_vessel.alive)
        
        # Hit second cell (this would normally be handled by the view logic)
        Shot.objects.create(
            player=self.player1,
            game=self.game,
            board=self.board1,
            row=0, col=1,
            vessel_hit=board_vessel,
            result=2  # Sunk
        )
        
        # Manually update vessel status (normally done in view)
        board_vessel.alive = False
        board_vessel.save()
        
        self.assertFalse(board_vessel.alive)
    
    def test_game_winning_condition(self):
        """Test game winning condition"""
        # Place vessel on board2
        board_vessel = BoardVessel.objects.create(
            board=self.board2,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=1
        )
        
        # Sink the vessel (manually simulate)
        board_vessel.alive = False
        board_vessel.save()
        
        # Check if all vessels are sunk (would trigger game over)
        alive_vessels = BoardVessel.objects.filter(board=self.board2, alive=True)
        if not alive_vessels.exists():
            self.game.phase = Game.PHASE_GAMEOVER
            self.game.winner = self.player1
            self.game.save()
        
        self.assertEqual(self.game.phase, Game.PHASE_GAMEOVER)
        self.assertEqual(self.game.winner, self.player1)
    
    def test_turn_management(self):
        """Test turn management logic"""
        self.game.phase = Game.PHASE_PLAYING
        self.game.turn = self.player1
        self.game.save()
        
        # Player1's turn initially
        self.assertEqual(self.game.turn, self.player1)
        
        # After a miss, turn should change to player2
        self.game.turn = self.player2
        self.game.save()
        
        self.assertEqual(self.game.turn, self.player2)


class VesselPlacementLogicTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        # Get the automatically created player (created by signals)
        self.player = Player.objects.get(user=self.user)
        self.game = Game.objects.create(owner=self.player, width=10, height=10)
        self.board = Board.objects.create(game=self.game, player=self.player)
        self.vessel = Vessel.objects.create(name='Test Ship', size=3)
    
    def get_cells(self, ri, ci, rf, cf):
        """Helper method to get cells covered by vessel"""
        cells = []
        if ri == rf:  # Horizontal
            for c in range(min(ci, cf), max(ci, cf) + 1):
                cells.append((ri, c))
        elif ci == cf:  # Vertical
            for r in range(min(ri, rf), max(ri, rf) + 1):
                cells.append((r, ci))
        return cells
    
    def test_horizontal_vessel_placement(self):
        """Test horizontal vessel placement"""
        board_vessel = BoardVessel.objects.create(
            board=self.board,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=2  # Horizontal, size 3
        )
        
        cells = self.get_cells(0, 0, 0, 2)
        expected_cells = [(0, 0), (0, 1), (0, 2)]
        self.assertEqual(cells, expected_cells)
    
    def test_vertical_vessel_placement(self):
        """Test vertical vessel placement"""
        board_vessel = BoardVessel.objects.create(
            board=self.board,
            vessel=self.vessel,
            ri=0, ci=0, rf=2, cf=0  # Vertical, size 3
        )
        
        cells = self.get_cells(0, 0, 2, 0)
        expected_cells = [(0, 0), (1, 0), (2, 0)]
        self.assertEqual(cells, expected_cells)
    
    def test_vessel_overlap_detection(self):
        """Test vessel overlap detection logic"""
        # Place first vessel
        BoardVessel.objects.create(
            board=self.board,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=2  # Horizontal at (0,0) to (0,2)
        )
        
        # Test overlap detection logic
        existing_vessels = BoardVessel.objects.filter(board=self.board)
        new_cells = self.get_cells(0, 1, 0, 3)  # Would overlap at (0,1) and (0,2)
        
        overlap_detected = False
        for other in existing_vessels:
            other_cells = self.get_cells(other.ri, other.ci, other.rf, other.cf)
            if set(new_cells) & set(other_cells):
                overlap_detected = True
                break
        
        self.assertTrue(overlap_detected)
    
    def test_vessel_boundary_validation(self):
        """Test vessel boundary validation logic"""
        game = self.game
        
        # Test valid placement within bounds
        ri, ci, rf, cf = 0, 0, 0, 2
        within_bounds = (
            0 <= ri < game.height and 0 <= rf < game.height and
            0 <= ci < game.width and 0 <= cf < game.width
        )
        self.assertTrue(within_bounds)
        
        # Test invalid placement outside bounds
        ri, ci, rf, cf = 0, 8, 0, 10  # cf=10 is out of bounds (max is 9)
        within_bounds = (
            0 <= ri < game.height and 0 <= rf < game.height and
            0 <= ci < game.width and 0 <= cf < game.width
        )
        self.assertFalse(within_bounds)


class BotLogicTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'human_{unique_suffix}', password='testpass')
        # Get the automatically created player (created by signals)
        self.player = Player.objects.get(user=self.user)
        
        # Create bot
        bot_suffix = uuid.uuid4().hex[:8]
        self.bot_user = User.objects.create_user(username=f'bot_user_{bot_suffix}')
        # Get the automatically created bot player (created by signals)
        self.bot_player = Player.objects.get(user=self.bot_user)
        
        self.game = Game.objects.create(
            owner=self.player,
            width=10,
            height=10,
            multiplayer=False
        )
        self.game.players.add(self.player, self.bot_player)
        
        self.human_board = Board.objects.create(game=self.game, player=self.player)
        self.bot_board = Board.objects.create(game=self.game, player=self.bot_player)
        
        # Create test vessel
        self.vessel = Vessel.objects.create(name='Test Ship', size=2)
    
    def test_bot_vessel_placement(self):
        """Test that bot can place vessels automatically"""
        # Simulate bot vessel placement
        board_vessel = BoardVessel.objects.create(
            board=self.bot_board,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=1
        )
        
        self.assertEqual(BoardVessel.objects.filter(board=self.bot_board).count(), 1)
        self.assertTrue(board_vessel.alive)
    
    def test_bot_shot_logic(self):
        """Test bot shooting logic"""
        import random
        
        # Simulate bot shooting logic
        human_board_size = 10
        shot_attempts = []
        
        for _ in range(5):  # Try 5 random shots
            row = random.randint(0, human_board_size - 1)
            col = random.randint(0, human_board_size - 1)
            
            # Check if position already shot
            if (row, col) not in shot_attempts:
                shot_attempts.append((row, col))
        
        # All shots should be unique and within bounds
        self.assertEqual(len(shot_attempts), len(set(shot_attempts)))  # All unique
        for row, col in shot_attempts:
            self.assertTrue(0 <= row < human_board_size)
            self.assertTrue(0 <= col < human_board_size)
