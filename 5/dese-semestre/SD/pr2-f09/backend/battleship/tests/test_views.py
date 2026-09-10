from django.test import TestCase
from django.contrib.auth.models import User
from rest_framework.test import APIClient
from rest_framework import status
from rest_framework_simplejwt.tokens import RefreshToken
from battleship.api.models import Player, Game, Vessel, Board, BoardVessel, Shot
import uuid


class BaseAPITestCase(TestCase):
    def setUp(self):
        self.client = APIClient()
        # Generate unique username to avoid conflicts across test classes
        unique_suffix = str(uuid.uuid4())[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        
        # Create JWT token for authentication
        refresh = RefreshToken.for_user(self.user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {refresh.access_token}')
        
        # Create or get test vessels to avoid UNIQUE constraint errors
        # Use default vessel sizes from signals.py: Patrol Boat=1, Destroyer=2, Cruiser=3
        self.vessels = [
            Vessel.objects.get_or_create(name='Patrol Boat', defaults={'size': 1})[0],
            Vessel.objects.get_or_create(name='Destroyer', defaults={'size': 2})[0], 
            Vessel.objects.get_or_create(name='Cruiser', defaults={'size': 3})[0],
        ]


class PlayerViewSetTest(BaseAPITestCase):
    def test_get_players_in_game(self):
        """Test getting players in a game"""
        game = Game.objects.create(owner=self.player, width=10, height=10)
        game.players.add(self.player)
        
        response = self.client.get(f'/api/v1/games/{game.id}/players/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
    
    def test_add_player_to_game(self):
        """Test adding a player to a game"""
        game = Game.objects.create(owner=self.player, width=10, height=10, multiplayer=True)
        game.players.add(self.player)
        
        # Create another user to add to the game
        user2 = User.objects.create_user(username=f'testuser2_{uuid.uuid4().hex[:8]}', password='testpass2')
        player2 = Player.objects.get(user=user2)
        
        # Authenticate as the second user
        client2 = APIClient()
        refresh2 = RefreshToken.for_user(user2)
        client2.credentials(HTTP_AUTHORIZATION=f'Bearer {refresh2.access_token}')
        
        response = client2.post(f'/api/v1/games/{game.id}/players/', {
            'nickname': 'TestPlayer2'
        })
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
    
    def test_cannot_add_third_player(self):
        """Test that a third player cannot be added to a game"""
        game = Game.objects.create(owner=self.player, width=10, height=10, multiplayer=True)
        game.players.add(self.player)
        
        # Add second player
        user2 = User.objects.create_user(username=f'testuser2_{uuid.uuid4().hex[:8]}', password='testpass2')
        player2 = Player.objects.get(user=user2)
        game.players.add(player2)
        
        # Try to add third player
        response = self.client.post(f'/api/v1/games/{game.id}/players/', {
            'nickname': 'TestPlayer3'
        })
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)


class GameViewSetTest(BaseAPITestCase):
    def test_create_single_player_game(self):
        """Test creating a single player game"""
        response = self.client.post('/api/v1/games/', {
            'width': 10,
            'height': 10,
            'multiplayer': False
        })
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        
        game = Game.objects.get(id=response.data['id'])
        self.assertEqual(game.players.count(), 2)  # Human + Bot
        self.assertFalse(game.multiplayer)
    
    def test_create_multiplayer_game(self):
        """Test creating a multiplayer game"""
        response = self.client.post('/api/v1/games/', {
            'width': 10,
            'height': 10,
            'multiplayer': True
        })
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        
        game = Game.objects.get(id=response.data['id'])
        self.assertEqual(game.players.count(), 1)  # Only creator
        self.assertTrue(game.multiplayer)
    
    def test_get_game_state(self):
        """Test retrieving game state"""
        game = Game.objects.create(owner=self.player, width=10, height=10)
        game.players.add(self.player)
        Board.objects.create(game=game, player=self.player)
        
        response = self.client.get(f'/api/v1/games/{game.id}/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertIn('data', response.data)
        self.assertIn('gameState', response.data['data'])


class VesselViewSetTest(BaseAPITestCase):
    def test_get_available_vessels(self):
        """Test getting available vessels"""
        response = self.client.get('/api/v1/vessels/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertGreaterEqual(len(response.data), 3)  # At least our test vessels


class BoardVesselViewSetTest(BaseAPITestCase):
    def setUp(self):
        super().setUp()
        self.game = Game.objects.create(owner=self.player, width=10, height=10, phase=Game.PHASE_PLACEMENT)
        self.game.players.add(self.player)
        self.board = Board.objects.create(game=self.game, player=self.player)
    
    def test_place_vessel_valid(self):
        """Test placing a vessel with valid coordinates"""
        vessel = self.vessels[0]  # Size 1 (Patrol Boat)
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/vessels/', {
            'vessel': vessel.id,
            'ri': 0,
            'ci': 0,
            'rf': 0,
            'cf': 0  # Single cell placement for size 1 vessel
        })
        if response.status_code != status.HTTP_201_CREATED:
            print(f"Error response: {response.data}")
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
    
    def test_place_vessel_overlap(self):
        """Test that overlapping vessels are rejected"""
        vessel = self.vessels[0]  # Size 1 (Patrol Boat)
        
        # Place first vessel
        BoardVessel.objects.create(
            board=self.board,
            vessel=vessel,
            ri=0, ci=0, rf=0, cf=0  # Single cell
        )
        
        # Try to place overlapping vessel at same position
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/vessels/', {
            'vessel': vessel.id,
            'ri': 0,
            'ci': 0,
            'rf': 0,
            'cf': 0  # Same position - overlaps
        })
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    
    def test_place_vessel_out_of_bounds(self):
        """Test that out of bounds placement is rejected"""
        vessel = self.vessels[0]  # Size 1 (Patrol Boat)
        
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/vessels/', {
            'vessel': vessel.id,
            'ri': 0,
            'ci': 10,  # Out of bounds (max col is 9)
            'rf': 0,
            'cf': 10
        })
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    
    def test_place_vessel_wrong_size(self):
        """Test that wrong vessel size is rejected"""
        vessel = self.vessels[0]  # Size 1 (Patrol Boat)
        
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/vessels/', {
            'vessel': vessel.id,
            'ri': 0,
            'ci': 0,
            'rf': 0,
            'cf': 1  # Size 2, but vessel is size 1
        })
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    
    def test_cannot_place_vessel_wrong_phase(self):
        """Test that vessels cannot be placed outside placement phase"""
        self.game.phase = Game.PHASE_PLAYING
        self.game.save()
        
        vessel = self.vessels[0]  # Size 1 (Patrol Boat)
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/vessels/', {
            'vessel': vessel.id,
            'ri': 0,
            'ci': 0,
            'rf': 0,
            'cf': 0
        })
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)


class ShotViewSetTest(BaseAPITestCase):
    def setUp(self):
        super().setUp()
        self.game = Game.objects.create(
            owner=self.player, 
            width=10, 
            height=10, 
            phase=Game.PHASE_PLAYING,
            turn=self.player
        )
        self.game.players.add(self.player)
        self.board = Board.objects.create(game=self.game, player=self.player)
        
        # Create opponent
        self.opponent_user = User.objects.create_user(username=f'opponent_{uuid.uuid4().hex[:8]}', password='testpass')
        self.opponent = Player.objects.get(user=self.opponent_user)
        self.game.players.add(self.opponent)
        self.opponent_board = Board.objects.create(game=self.game, player=self.opponent)
    
    def test_shoot_miss(self):
        """Test shooting at empty water (miss)"""
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/shots/', {
            'row': 0,
            'col': 0
        })
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(response.data['result'], 0)  # Miss
        self.assertIsNone(response.data['vessel_hit'])
    
    def test_shoot_hit(self):
        """Test shooting at a vessel (hit)"""
        # Place vessel on opponent's board - use size 2 vessel (Destroyer) so it doesn't sink on first hit
        vessel = self.vessels[1]  # Size 2 (Destroyer)
        board_vessel = BoardVessel.objects.create(
            board=self.opponent_board,
            vessel=vessel,
            ri=0, ci=0, rf=0, cf=1  # Two cells horizontally
        )
        
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/shots/', {
            'row': 0,
            'col': 0  # Hit first cell of the 2-cell vessel
        })
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(response.data['result'], 1)  # Hit (not sunk)
        self.assertIsNotNone(response.data['vessel_hit'])
    
    def test_shoot_same_position_twice(self):
        """Test that shooting the same position twice is rejected"""
        # First shot
        self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/shots/', {
            'row': 0,
            'col': 0
        })
        
        # Second shot at same position
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/shots/', {
            'row': 0,
            'col': 0
        })
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    
    def test_shoot_out_of_bounds(self):
        """Test that out of bounds shots are rejected"""
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/shots/', {
            'row': 10,  # Out of bounds
            'col': 0
        })
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    
    def test_cannot_shoot_wrong_phase(self):
        """Test that shots cannot be made outside playing phase"""
        self.game.phase = Game.PHASE_PLACEMENT
        self.game.save()
        
        response = self.client.post(f'/api/v1/games/{self.game.id}/players/{self.player.id}/shots/', {
            'row': 0,
            'col': 0
        })
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)


class LeaderboardViewSetTest(BaseAPITestCase):
    def test_get_leaderboard(self):
        """Test getting leaderboard data"""
        response = self.client.get('/api/v1/leaderboard/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertIn('general', response.data)
        self.assertIn('multiplayer', response.data)
        self.assertIn('bot', response.data)


class AuthenticationTest(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.username = f'testuser_{uuid.uuid4().hex[:8]}'
        self.user = User.objects.create_user(username=self.username, password='testpass')
    
    def test_endpoints_require_authentication(self):
        """Test that protected endpoints require authentication"""
        protected_endpoints = [
            '/api/v1/games/',
            '/api/v1/vessels/',
        ]
        
        for endpoint in protected_endpoints:
            response = self.client.get(endpoint)
            self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
    
    def test_jwt_token_authentication(self):
        """Test JWT token authentication works"""
        # Get token
        response = self.client.post('/api/token/', {
            'username': self.username,
            'password': 'testpass'
        })
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertIn('access', response.data)
        
        # Use token to access protected endpoint
        token = response.data['access']
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {token}')
        
        response = self.client.get('/api/v1/vessels/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
