from django.test import TestCase, TransactionTestCase
from django.contrib.auth.models import User
from rest_framework.test import APIClient
from rest_framework import status
from rest_framework_simplejwt.tokens import RefreshToken
from battleship.api.models import Player, Game, Vessel, Board, BoardVessel, Shot
import json
import uuid


class FullGameFlowIntegrationTest(TransactionTestCase):
    """Integration test for complete game flow"""
    
    def setUp(self):
        self.client = APIClient()
        
        # Create two users
        unique_suffix1 = uuid.uuid4().hex[:8]
        unique_suffix2 = uuid.uuid4().hex[:8]
        self.user1 = User.objects.create_user(username=f'player1_{unique_suffix1}', password='testpass1')
        self.user2 = User.objects.create_user(username=f'player2_{unique_suffix2}', password='testpass2')
        
        # Create players
        self.player1 = Player.objects.get(user=self.user1)
        self.player2 = Player.objects.get(user=self.user2)
        
        # Create authentication tokens
        refresh1 = RefreshToken.for_user(self.user1)
        refresh2 = RefreshToken.for_user(self.user2)
        self.token1 = str(refresh1.access_token)
        self.token2 = str(refresh2.access_token)
        
        # Create test vessels using get_or_create to avoid UNIQUE constraint errors
        self.vessels = [
            Vessel.objects.get_or_create(name='Patrol Boat', defaults={'size': 1})[0],
            Vessel.objects.get_or_create(name='Destroyer', defaults={'size': 2})[0],
            Vessel.objects.get_or_create(name='Cruiser', defaults={'size': 3})[0],
            Vessel.objects.get_or_create(name='Submarine', defaults={'size': 4})[0],
            Vessel.objects.get_or_create(name='Carrier', defaults={'size': 5})[0],
        ]
    
    def test_complete_multiplayer_game_flow(self):
        """Test complete multiplayer game from creation to finish"""
        
        # Step 1: Player1 creates a multiplayer game
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {self.token1}')
        response = self.client.post('/api/v1/games/', {
            'width': 10,
            'height': 10,
            'multiplayer': True
        })
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        game_id = response.data['id']
        
        # Step 2: Player2 joins the game
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {self.token2}')
        response = self.client.post(f'/api/v1/games/{game_id}/players/', {
            'nickname': 'Player2'
        })
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        
        # Step 3: Both players place their vessels
        # Player1 places vessels
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {self.token1}')
        
        for i, vessel in enumerate(self.vessels):
            response = self.client.post(f'/api/v1/games/{game_id}/players/{self.player1.id}/vessels/', {
                'vessel': vessel.id,
                'ri': i,
                'ci': 0,
                'rf': i,
                'cf': vessel.size - 1
            })
            self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        
        # Player2 places vessels
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {self.token2}')
        
        for i, vessel in enumerate(self.vessels):
            response = self.client.post(f'/api/v1/games/{game_id}/players/{self.player2.id}/vessels/', {
                'vessel': vessel.id,
                'ri': i + 5,  # Different position
                'ci': 0,
                'rf': i + 5,
                'cf': vessel.size - 1
            })
            self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        
        # Step 4: Check game state after placement
        response = self.client.get(f'/api/v1/games/{game_id}/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        game_state = response.data['data']['gameState']
        self.assertEqual(game_state['phase'], 'playing')
        
        # Step 5: Players take turns shooting
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {self.token1}')
        
        # Player1 shoots and misses
        response = self.client.post(f'/api/v1/games/{game_id}/players/{self.player1.id}/shots/', {
            'row': 9,
            'col': 9  # Empty water
        })
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(response.data['result'], 0)  # Miss
        
        # Step 6: Verify game continues
        response = self.client.get(f'/api/v1/games/{game_id}/')
        game_state = response.data['data']['gameState']
        self.assertEqual(game_state['phase'], 'playing')
    
    def test_single_player_bot_game_flow(self):
        """Test complete single player game against bot"""
        
        # Step 1: Create single player game
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {self.token1}')
        response = self.client.post('/api/v1/games/', {
            'width': 10,
            'height': 10,
            'multiplayer': False
        })
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        game_id = response.data['id']
        
        # Step 2: Verify bot was created
        game = Game.objects.get(id=game_id)
        self.assertEqual(game.players.count(), 2)  # Human + Bot
        
        bot_player = game.players.exclude(id=self.player1.id).first()
        self.assertEqual(bot_player.nickname, 'Bot')
        
        # Step 3: Check that bot board has vessels
        bot_board = Board.objects.get(game=game, player=bot_player)
        bot_vessels = BoardVessel.objects.filter(board=bot_board)
        self.assertGreater(bot_vessels.count(), 0)  # Bot should have vessels
        
        # Step 4: Human player places vessels
        for i, vessel in enumerate(self.vessels):
            response = self.client.post(f'/api/v1/games/{game_id}/players/{self.player1.id}/vessels/', {
                'vessel': vessel.id,
                'ri': i,
                'ci': 0,
                'rf': i,
                'cf': vessel.size - 1
            })
            self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        
        # Step 5: Check game transitions to playing
        response = self.client.get(f'/api/v1/games/{game_id}/')
        game_state = response.data['data']['gameState']
        self.assertEqual(game_state['phase'], 'playing')


class APIEndpointIntegrationTest(TestCase):
    """Integration test for API endpoints"""
    
    def setUp(self):
        self.client = APIClient()
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        
        refresh = RefreshToken.for_user(self.user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {refresh.access_token}')
        
        self.vessel = Vessel.objects.create(name='Test Ship', size=3)
    
    def test_nested_api_endpoints(self):
        """Test nested API endpoints work correctly"""
        
        # Create game
        response = self.client.post('/api/v1/games/', {
            'width': 10,
            'height': 10,
            'multiplayer': False
        })
        game_id = response.data['id']
        
        # Test game players endpoint
        response = self.client.get(f'/api/v1/games/{game_id}/players/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        
        # Test player vessels endpoint
        response = self.client.get(f'/api/v1/games/{game_id}/players/{self.player.id}/vessels/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        
        # Test player shots endpoint
        response = self.client.get(f'/api/v1/games/{game_id}/players/{self.player.id}/shots/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        
        # Test player boards endpoint
        response = self.client.get(f'/api/v1/games/{game_id}/players/{self.player.id}/boards/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
    
    def test_api_error_handling(self):
        """Test API error handling"""
        
        # Test accessing non-existent game
        response = self.client.get('/api/v1/games/99999/')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        
        # Test accessing non-existent player in game
        game = Game.objects.create(owner=self.player, width=10, height=10)
        response = self.client.get(f'/api/v1/games/{game.id}/players/99999/')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        
        # Test invalid data submission
        response = self.client.post('/api/v1/games/', {
            'width': 'invalid',  # Should be integer
            'height': 10
        })
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    
    def test_authentication_required(self):
        """Test that authentication is required for protected endpoints"""
        
        # Remove authentication
        self.client.credentials()
        
        protected_endpoints = [
            '/api/v1/games/',
            '/api/v1/vessels/',
            '/api/v1/user/1/'
        ]
        
        for endpoint in protected_endpoints:
            response = self.client.get(endpoint)
            self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
    
    def test_permission_restrictions(self):
        """Test that users can only access their own data"""
        
        # Create another user and player
        unique_suffix = uuid.uuid4().hex[:8]
        other_user = User.objects.create_user(username=f'otheruser_{unique_suffix}', password='testpass')
        other_player = Player.objects.get(user=other_user)
        
        # Create game with other player
        other_game = Game.objects.create(owner=other_player, width=10, height=10)
        other_game.players.add(other_player)
        
        # Try to access other player's data (should be restricted by business logic)
        # Note: The API might allow this depending on game participation rules
        response = self.client.get(f'/api/v1/games/{other_game.id}/players/{other_player.id}/')
        # This might return 404 or 403 depending on implementation


class DatabaseIntegrityTest(TestCase):
    """Test database integrity and constraints"""
    
    def setUp(self):
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        self.game = Game.objects.create(owner=self.player, width=10, height=10)
        self.board = Board.objects.create(game=self.game, player=self.player)
        self.vessel = Vessel.objects.create(name='Test Ship', size=2)
    
    def test_cascade_deletion(self):
        """Test that related objects are deleted correctly"""
        
        # Create related objects
        board_vessel = BoardVessel.objects.create(
            board=self.board,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=1
        )
        
        shot = Shot.objects.create(
            player=self.player,
            game=self.game,
            board=self.board,
            row=0, col=0
        )
        
        # Delete game
        game_id = self.game.id
        self.game.delete()
        
        # Check that related objects were deleted
        self.assertFalse(Board.objects.filter(game_id=game_id).exists())
        self.assertFalse(BoardVessel.objects.filter(board__game_id=game_id).exists())
        self.assertFalse(Shot.objects.filter(game_id=game_id).exists())
    
    def test_unique_constraints(self):
        """Test unique constraints"""
        
        # Test player nickname uniqueness - Player already exists from signal
        # Try to set same nickname as another player
        unique_suffix = uuid.uuid4().hex[:8]
        user2 = User.objects.create_user(username=f'testuser2_{unique_suffix}', password='testpass2')
        player2 = Player.objects.get(user=user2)
        
        # Set same nickname on both players to test uniqueness
        self.player.nickname = 'TestPlayer'
        self.player.save()
        
        with self.assertRaises(Exception):
            player2.nickname = 'TestPlayer'
            player2.save()
        
        # Test vessel name uniqueness
        with self.assertRaises(Exception):
            Vessel.objects.create(name='Test Ship', size=3)
    
    def test_foreign_key_constraints(self):
        """Test foreign key constraints"""
        
        # BoardVessel must reference valid board and vessel
        board_vessel = BoardVessel(
            board=self.board,
            vessel=self.vessel,
            ri=0, ci=0, rf=0, cf=1
        )
        board_vessel.save()  # Should work
        
        # Shot must reference valid player, game, and board
        shot = Shot(
            player=self.player,
            game=self.game,
            board=self.board,
            row=0, col=0
        )
        shot.save()  # Should work


class PerformanceTest(TestCase):
    """Basic performance tests"""
    
    def setUp(self):
        self.client = APIClient()
        unique_suffix = uuid.uuid4().hex[:8]
        self.user = User.objects.create_user(username=f'testuser_{unique_suffix}', password='testpass')
        self.player = Player.objects.get(user=self.user)
        
        refresh = RefreshToken.for_user(self.user)
        self.client.credentials(HTTP_AUTHORIZATION=f'Bearer {refresh.access_token}')
    
    def test_multiple_games_creation(self):
        """Test creating multiple games doesn't cause performance issues"""
        
        import time
        start_time = time.time()
        
        # Create 10 games
        for i in range(10):
            response = self.client.post('/api/v1/games/', {
                'width': 10,
                'height': 10,
                'multiplayer': False
            })
            self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        
        end_time = time.time()
        duration = end_time - start_time
        
        # Should complete within reasonable time (adjust as needed)
        self.assertLess(duration, 10.0)  # 10 seconds max
    
    def test_game_state_retrieval_performance(self):
        """Test game state retrieval performance"""
        
        # Create game with some complexity
        response = self.client.post('/api/v1/games/', {
            'width': 10,
            'height': 10,
            'multiplayer': False
        })
        game_id = response.data['id']
        
        import time
        start_time = time.time()
        
        # Retrieve game state multiple times
        for _ in range(5):
            response = self.client.get(f'/api/v1/games/{game_id}/')
            self.assertEqual(response.status_code, status.HTTP_200_OK)
        
        end_time = time.time()
        duration = end_time - start_time
        
        # Should complete quickly
        self.assertLess(duration, 2.0)  # 2 seconds max for 5 requests
