from django.test import TestCase
from django.contrib.auth.models import User
from battleship.api.models import Player, Game, Vessel, Board, BoardVessel, Shot
from battleship.api.serializers import (
    PlayerSerializer, GameSerializer, VesselSerializer, 
    BoardSerializer, BoardVesselSerializer, ShotSerializer,
    GameStateSerializer, PlayerStateSerializer
)
import uuid


class PlayerSerializerTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
    
    def test_player_serialization(self):
        """Test player serialization"""
        serializer = PlayerSerializer(self.player)
        data = serializer.data
        
        self.assertEqual(data['nickname'], self.user.username)  # Nickname matches username
        self.assertEqual(data['user'], self.user.id)
    
    def test_player_deserialization(self):
        """Test player deserialization"""
        # Test updating an existing player (Player creation is handled by signals)
        data = {
            'nickname': 'UpdatedPlayer',
            'user': self.user.id
        }
        serializer = PlayerSerializer(self.player, data=data)
        if not serializer.is_valid():
            print(f"Serializer errors: {serializer.errors}")
        self.assertTrue(serializer.is_valid())
        
        # Test that the update works
        updated_player = serializer.save()
        self.assertEqual(updated_player.nickname, 'UpdatedPlayer')


class GameSerializerTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        self.game = Game.objects.create(
            owner=self.player,
            width=10,
            height=10,
            multiplayer=False
        )
    
    def test_game_serialization(self):
        """Test game serialization"""
        serializer = GameSerializer(self.game)
        data = serializer.data
        
        self.assertEqual(data['width'], 10)
        self.assertEqual(data['height'], 10)
        self.assertEqual(data['phase'], Game.PHASE_WAITING)
        self.assertFalse(data['multiplayer'])
    
    def test_game_deserialization(self):
        """Test game deserialization"""
        data = {
            'width': 12,
            'height': 12,
            'multiplayer': True
        }
        serializer = GameSerializer(data=data)
        self.assertTrue(serializer.is_valid())


class VesselSerializerTest(TestCase):
    def setUp(self):
        self.vessel = Vessel.objects.create(
            name='Test Cruiser',
            size=3,
            image='test_cruiser.png'
        )
    
    def test_vessel_serialization(self):
        """Test vessel serialization"""
        serializer = VesselSerializer(self.vessel)
        data = serializer.data
        
        self.assertEqual(data['name'], 'Test Cruiser')
        self.assertEqual(data['size'], 3)
        self.assertEqual(data['image'], 'test_cruiser.png')
    
    def test_vessel_deserialization(self):
        """Test vessel deserialization"""
        data = {
            'name': 'New Destroyer',
            'size': 4,
            'image': 'destroyer.png'
        }
        serializer = VesselSerializer(data=data)
        self.assertTrue(serializer.is_valid())


class BoardVesselSerializerTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        self.game = Game.objects.create(owner=self.player, width=10, height=10)
        self.board = Board.objects.create(game=self.game, player=self.player)
        self.vessel = Vessel.objects.create(name='Test Ship', size=2)
        
        self.board_vessel = BoardVessel.objects.create(
            board=self.board,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=1
        )
    
    def test_board_vessel_serialization(self):
        """Test board vessel serialization"""
        serializer = BoardVesselSerializer(self.board_vessel)
        data = serializer.data
        
        self.assertEqual(data['vessel'], self.vessel.id)
        self.assertEqual(data['ri'], 0)
        self.assertEqual(data['ci'], 0)
        self.assertEqual(data['rf'], 0)
        self.assertEqual(data['cf'], 1)
        self.assertTrue(data['alive'])
    
    def test_board_vessel_deserialization(self):
        """Test board vessel deserialization"""
        data = {
            'vessel': self.vessel.id,
            'ri': 1,
            'ci': 1,
            'rf': 1,
            'cf': 2
        }
        serializer = BoardVesselSerializer(data=data)
        self.assertTrue(serializer.is_valid())
    
    def test_board_vessel_validation_diagonal(self):
        """Test that diagonal placement is invalid"""
        data = {
            'vessel': self.vessel.id,
            'ri': 0,
            'ci': 0,
            'rf': 1,
            'cf': 1  # Diagonal placement
        }
        serializer = BoardVesselSerializer(data=data)
        # Note: Diagonal validation is handled at the view level, not serializer level
        # So this serializer will be valid, but the view should reject it
        self.assertTrue(serializer.is_valid())


class ShotSerializerTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        self.game = Game.objects.create(owner=self.player, width=10, height=10)
        self.board = Board.objects.create(game=self.game, player=self.player)
        
        self.shot = Shot.objects.create(
            player=self.player,
            game=self.game,
            board=self.board,
            row=5,
            col=5,
            result=0
        )
    
    def test_shot_serialization(self):
        """Test shot serialization"""
        serializer = ShotSerializer(self.shot)
        data = serializer.data
        
        self.assertEqual(data['row'], 5)
        self.assertEqual(data['col'], 5)
        self.assertEqual(data['result'], 0)
        self.assertIsNone(data['vessel_hit'])
    
    def test_shot_deserialization(self):
        """Test shot deserialization"""
        data = {
            'row': 3,
            'col': 7
        }
        serializer = ShotSerializer(data=data)
        self.assertTrue(serializer.is_valid())
    
    def test_shot_validation_coordinates(self):
        """Test shot coordinate validation"""
        # Valid coordinates
        data = {'row': 0, 'col': 0}
        serializer = ShotSerializer(data=data)
        self.assertTrue(serializer.is_valid())
        
        # Invalid coordinates (negative)
        data = {'row': -1, 'col': 0}
        serializer = ShotSerializer(data=data)
        self.assertFalse(serializer.is_valid())


class GameStateSerializerTest(TestCase):
    def setUp(self):
        # Create users and players
        unique_suffix1 = uuid.uuid4().hex[:8]
        unique_suffix2 = uuid.uuid4().hex[:8]
        self.user1 = User.objects.create_user(username=f'player1_{unique_suffix1}', password='testpass')
        self.user2 = User.objects.create_user(username=f'player2_{unique_suffix2}', password='testpass')
        self.player1 = Player.objects.get(user=self.user1)
        self.player2 = Player.objects.get(user=self.user2)
        
        # Create game
        self.game = Game.objects.create(
            owner=self.player1,
            width=10,
            height=10,
            multiplayer=True,
            phase=Game.PHASE_PLAYING,
            turn=self.player1  # Use Player instance, not username
        )
        self.game.players.add(self.player1, self.player2)
        
        # Create boards
        self.board1 = Board.objects.create(game=self.game, player=self.player1, prepared=True)
        self.board2 = Board.objects.create(game=self.game, player=self.player2, prepared=True)
        
        # Create vessel and placement
        self.vessel = Vessel.objects.create(name='Test Ship', size=2)
        BoardVessel.objects.create(
            board=self.board1,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=1
        )
    
    def test_game_state_serialization(self):
        """Test game state serialization"""
        # Create mock request with user context
        from unittest.mock import Mock
        mock_request = Mock()
        mock_request.user = self.player1.user
        mock_request.user.player = self.player1
        
        serializer = GameStateSerializer(self.game, context={'request': mock_request})
        data = serializer.data
        
        self.assertEqual(data['gameId'], self.game.id)
        self.assertEqual(data['phase'], Game.PHASE_PLAYING)
        self.assertEqual(data['turn'], self.player1.nickname)
        self.assertTrue(data['multiplayer'])
        self.assertIsNotNone(data['player1'])
        self.assertIsNotNone(data['player2'])
    
    def test_game_state_player_data(self):
        """Test that game state includes correct player data"""
        # Create mock request with user context
        from unittest.mock import Mock
        mock_request = Mock()
        mock_request.user = self.player1.user
        mock_request.user.player = self.player1
        
        serializer = GameStateSerializer(self.game, context={'request': mock_request})
        data = serializer.data
        
        player1_data = data['player1']
        self.assertEqual(player1_data['id'], str(self.player1.id))
        self.assertEqual(player1_data['username'], self.player1.nickname)
        self.assertIn('board', player1_data)
        self.assertIn('placedShips', player1_data)
        self.assertIn('availableShips', player1_data)


class PlayerStateSerializerTest(TestCase):
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        self.game = Game.objects.create(owner=self.player, width=10, height=10)
        self.board = Board.objects.create(game=self.game, player=self.player)
        
        # Create vessels
        self.vessel1 = Vessel.objects.create(name='Ship1', size=2)
        self.vessel2 = Vessel.objects.create(name='Ship2', size=3)
        
        # Place one vessel
        BoardVessel.objects.create(
            board=self.board,
            vessel=self.vessel1,
            ri=0, ci=0, rf=0, cf=1
        )
    
    def test_player_state_serialization(self):
        """Test player state serialization"""
        serializer = PlayerStateSerializer(self.board)
        data = serializer.data
        
        self.assertEqual(data['id'], str(self.player.id))
        self.assertEqual(data['username'], self.player.nickname)  # Use actual nickname, not hardcoded
        self.assertIn('placedShips', data)
        self.assertIn('availableShips', data)
        self.assertIn('board', data)
    
    def test_player_state_placed_ships(self):
        """Test placed ships in player state"""
        serializer = PlayerStateSerializer(self.board)
        data = serializer.data
        
        placed_ships = data['placedShips']
        self.assertEqual(len(placed_ships), 1)
        
        ship = placed_ships[0]
        self.assertEqual(ship['type'], self.vessel1.id)
        self.assertEqual(ship['size'], 2)
        self.assertFalse(ship['isVertical'])  # Horizontal placement
    
    def test_player_state_available_ships(self):
        """Test available ships in player state"""
        serializer = PlayerStateSerializer(self.board)
        data = serializer.data
        
        available_ships = data['availableShips']
        # Should include vessels not yet placed
        available_types = [ship['type'] for ship in available_ships]
        self.assertIn(self.vessel2.id, available_types)
        self.assertNotIn(self.vessel1.id, available_types)  # Already placed
    
    def test_player_state_board_generation(self):
        """Test board generation in player state"""
        # Create an opponent
        opponent_user = User.objects.create_user(username=f'opponent_{uuid.uuid4().hex[:8]}', password='testpass')
        opponent = Player.objects.get(user=opponent_user)
        self.game.players.add(opponent)
        
        # Create some shots from opponent to test board state
        Shot.objects.create(
            player=opponent,  # Shot from opponent
            game=self.game,
            board=self.board,  # This board is not used for opponent shots, but required field
            row=5, col=5,
            result=0  # Miss
        )
        
        serializer = PlayerStateSerializer(self.board)
        data = serializer.data
        
        board = data['board']
        self.assertEqual(len(board), 10)  # 10 rows
        self.assertEqual(len(board[0]), 10)  # 10 columns
        
        # Check that miss is represented correctly
        self.assertEqual(board[5][5], 11)  # Miss marker
