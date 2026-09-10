from django.test import TestCase
from django.contrib.auth.models import User
from django.core.exceptions import ValidationError
from battleship.api.models import Player, Game, Vessel, Board, BoardVessel, Shot
import uuid


class PlayerModelTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
    
    def test_player_creation(self):
        """Test basic player creation (automatically created by signal)"""
        # Player is automatically created by signal when User is created
        self.assertEqual(self.player.user, self.user)
        # Update nickname to test it works
        self.player.nickname = 'TestPlayer'
        self.player.save()
        self.assertEqual(self.player.nickname, 'TestPlayer')
    
    def test_player_nickname_unique(self):
        """Test that nicknames must be unique"""
        self.player.nickname = 'TestPlayer'
        self.player.save()
        
        unique_suffix = uuid.uuid4().hex[:8]
        user2 = User.objects.create_user(username=f'testuser2_{unique_suffix}', password='testpass2')
        player2 = Player.objects.get(user=user2)
        
        with self.assertRaises(Exception):
            player2.nickname = 'TestPlayer'
            player2.save()


class GameModelTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
    
    def test_game_creation(self):
        """Test basic game creation"""
        game = Game.objects.create(
            owner=self.player,
            width=10,
            height=10,
            multiplayer=False
        )
        self.assertEqual(game.width, 10)
        self.assertEqual(game.height, 10)
        self.assertEqual(game.phase, Game.PHASE_WAITING)
        self.assertFalse(game.multiplayer)
    
    def test_game_phases(self):
        """Test game phase constants"""
        self.assertEqual(Game.PHASE_WAITING, 'waiting')
        self.assertEqual(Game.PHASE_PLACEMENT, 'placement')
        self.assertEqual(Game.PHASE_PLAYING, 'playing')
        self.assertEqual(Game.PHASE_GAMEOVER, 'gameOver')


class VesselModelTest(TestCase):
    def test_vessel_creation(self):
        """Test vessel creation with valid data"""
        vessel = Vessel.objects.create(
            name='Test Ship',
            size=3,
            image='test_image.png'
        )
        self.assertEqual(vessel.name, 'Test Ship')
        self.assertEqual(vessel.size, 3)
    
    def test_vessel_size_validation(self):
        """Test vessel size validation (should be between 2-5)"""
        # Test invalid sizes
        with self.assertRaises(ValidationError):
            vessel = Vessel(name='Too Small', size=1)
            vessel.full_clean()
        
        with self.assertRaises(ValidationError):
            vessel = Vessel(name='Too Big', size=6)
            vessel.full_clean()
        
        # Test valid sizes
        for size in [2, 3, 4, 5]:
            vessel = Vessel(name=f'Ship{size}', size=size)
            vessel.full_clean()  # Should not raise


class BoardModelTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        self.game = Game.objects.create(owner=self.player, width=10, height=10)
    
    def test_board_creation(self):
        """Test board creation"""
        board = Board.objects.create(game=self.game, player=self.player)
        self.assertEqual(board.game, self.game)
        self.assertEqual(board.player, self.player)
        self.assertFalse(board.prepared)


class BoardVesselModelTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        self.game = Game.objects.create(owner=self.player, width=10, height=10)
        self.board = Board.objects.create(game=self.game, player=self.player)
        self.vessel = Vessel.objects.create(name='Test Ship', size=3)
    
    def test_board_vessel_creation(self):
        """Test board vessel placement"""
        board_vessel = BoardVessel.objects.create(
            board=self.board,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=2  # Horizontal placement
        )
        self.assertEqual(board_vessel.board, self.board)
        self.assertEqual(board_vessel.vessel, self.vessel)
        self.assertTrue(board_vessel.alive)
    
    def test_board_vessel_coordinates_validation(self):
        """Test coordinate validation (should be 0-199)"""
        # Test valid coordinates
        board_vessel = BoardVessel(
            board=self.board,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=2
        )
        board_vessel.full_clean()  # Should not raise
        
        # Test invalid coordinates
        with self.assertRaises(ValidationError):
            board_vessel = BoardVessel(
                board=self.board,
                vessel=self.vessel,
                ri=-1, ci=0, rf=0, cf=2  # Invalid ri
            )
            board_vessel.full_clean()


class ShotModelTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        self.game = Game.objects.create(owner=self.player, width=10, height=10)
        self.board = Board.objects.create(game=self.game, player=self.player)
    
    def test_shot_creation(self):
        """Test shot creation"""
        shot = Shot.objects.create(
            player=self.player,
            game=self.game,
            board=self.board,
            row=5,
            col=5,
            result=0  # Miss
        )
        self.assertEqual(shot.row, 5)
        self.assertEqual(shot.col, 5)
        self.assertEqual(shot.result, 0)
    
    def test_shot_result_validation(self):
        """Test shot result validation"""
        # Test valid results
        for result in [-1, 0, 1, 2]:
            shot = Shot(
                player=self.player,
                game=self.game,
                board=self.board,
                row=0, col=0,
                result=result
            )
            shot.full_clean()  # Should not raise
        
        # Test invalid result
        with self.assertRaises(ValidationError):
            shot = Shot(
                player=self.player,
                game=self.game,
                board=self.board,
                row=0, col=0,
                result=5  # Invalid result
            )
            shot.full_clean()
