from rest_framework import viewsets, filters, permissions, status
from rest_framework.response import Response
from rest_framework.exceptions import ValidationError
from django.shortcuts import get_object_or_404
from django.contrib.auth.models import User
from .serializers import ( PlayerSerializer, GameSerializer, BoardSerializer,
                          BoardVesselSerializer, ShotSerializer, VesselSerializer,
                          GameStateResponseSerializer, UserSerializer)
from . import models
from .models import Player, Game, Vessel, Board, BoardVessel, Shot
import random

# User view set
class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer

    def get_permissions(self):
        if self.action == 'create':
            return [permissions.AllowAny()]
        return [permissions.IsAuthenticated()]
    
# Player view set
class PlayerViewSet(viewsets.ModelViewSet):
    queryset = Player.objects.all()
    serializer_class = PlayerSerializer
    filter_backends = [filters.SearchFilter]
    search_fields = ['nickname']
    http_method_names = ['get', 'post', 'put', 'patch', 'delete']
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        game_id = self.kwargs.get('game_pk')
        if game_id:
            game = get_object_or_404(Game, id=game_id)
            return game.players.all()
        return super().get_queryset()

    def create(self, request, *args, **kwargs):
        game_id = kwargs.get('game_pk')
        if not game_id:
            return Response({"detail": "Game ID not provided."}, status=status.HTTP_400_BAD_REQUEST)

        game = get_object_or_404(Game, id=game_id)

        # Get the first player in the game
        existing_players = list(game.players.all())
        first_player = existing_players[0] if existing_players else None

        # Ensure the user has a Player object
        player, _ = Player.objects.get_or_create(user=request.user, defaults={"nickname": request.user.username})
        
        # Check if the game is full
        if game.players.count() >= 2:
            return Response({'detail': 'Game already full.'}, status=status.HTTP_400_BAD_REQUEST)
        elif game.players.count() == 0:
            return Response({'detail': 'No initial player in the game.'}, status=status.HTTP_400_BAD_REQUEST)
                
        # Check if the player already exists
        if game.players.filter(id=player.id).exists():
            return Response({'detail': 'Player already in this game.'}, status=400)
        
        # Add player to game
        game.players.add(player)
        game.phase = Game.PHASE_PLACEMENT
        game.turn = first_player

        game.save()

        # Create their board
        Board.objects.create(game=game, player=player)

        # Respond with player data
        serializer = self.get_serializer(player)
        return Response(serializer.data, status=status.HTTP_201_CREATED)

# Game view set
class GameViewSet(viewsets.ModelViewSet):
    queryset = Game.objects.all()
    serializer_class = GameSerializer    
    filter_backends = [filters.SearchFilter, filters.OrderingFilter]
    search_fields = ['phase']
    ordering_fields = ['id']
    http_method_names = ['get', 'post', 'put', 'patch', 'delete']
    permission_classes = [permissions.IsAuthenticated]

    def retrieve(self, request, *args, **kwargs):
        instance = self.get_object()
        serializer = GameStateResponseSerializer(instance, context={"request": request})
        return Response(serializer.data, status=status.HTTP_200_OK)
    
    def get_or_create_bot_player(self):
        bot_user, _ = User.objects.get_or_create(username='bot_user')
        bot_player, created = models.Player.objects.get_or_create(user=bot_user, defaults={'nickname': 'Bot'})
        # Ensure the bot player has the correct nickname (signal may have set it to username)
        if bot_player.nickname != 'Bot':
            bot_player.nickname = 'Bot'
            bot_player.save()
        return bot_player

    def get_cells(self, ri, ci, rf, cf):
        cells = []
        if ri == rf:
            for c in range(min(ci, cf), max(ci, cf) + 1):
                cells.append((ri, c))
        elif ci == cf:
            for r in range(min(ri, rf), max(ri, rf) + 1):
                cells.append((r, ci))
        return cells
    
    def auto_place_vessels(self, board):
        vessels = models.Vessel.objects.all()
        game = board.game

        def is_valid_placement(existing, new_cells):
            for vessel in existing:
                existing_cells = self.get_cells(vessel.ri, vessel.ci, vessel.rf, vessel.cf)
                if set(existing_cells) & set(new_cells):
                    return False
            return True

        placed_vessels = []

        for vessel in vessels:
            placed = False
            while not placed:
                is_vertical = random.choice([True, False])
                if is_vertical:
                    ri = random.randint(0, game.height - vessel.size)
                    rf = ri + vessel.size - 1
                    ci = cf = random.randint(0, game.width - 1)
                else:
                    ci = random.randint(0, game.width - vessel.size)
                    cf = ci + vessel.size - 1
                    ri = rf = random.randint(0, game.height - 1)

                new_cells = self.get_cells(ri, ci, rf, cf)
                if is_valid_placement(placed_vessels, new_cells):
                    board_vessel = models.BoardVessel.objects.create(
                        board=board,
                        vessel=vessel,
                        ri=ri,
                        ci=ci,
                        rf=rf,
                        cf=cf,
                        alive=True
                    )
                    placed_vessels.append(board_vessel)
                    placed = True

        board.prepared = True
        board.save()

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        # Get the requesting user’s Player, this way we avoid sending the user in the body
        user_player = get_object_or_404(models.Player, user=request.user)
        game = serializer.save(owner=user_player)
        game.players.add(user_player)

        # Create a board for the user player
        models.Board.objects.create(game=game, player=user_player)
        
        # If we are not in multiplayer mode, we create the bot
        multiplayer_value = request.data.get("multiplayer")
        # Handle boolean conversion properly - DRF sometimes sends booleans as strings
        if isinstance(multiplayer_value, str):
            is_multiplayer = multiplayer_value.lower() in ('true', '1', 'yes')
        else:
            is_multiplayer = bool(multiplayer_value)
            
        if not is_multiplayer:
            bot_player = self.get_or_create_bot_player()
            game.players.add(bot_player)

            # Create a board for the bot player
            bot_board = models.Board.objects.create(game=game, player=bot_player)

            # Auto-place vessels for the bot
            self.auto_place_vessels(bot_board)
        
            # Set phase to 'placement' so the human can now place ships
            game.phase = Game.PHASE_PLACEMENT

        game.save()

        return Response(self.get_serializer(game).data, status=status.HTTP_201_CREATED)

# Vessel view set
class VesselViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Vessel.objects.all()
    serializer_class = VesselSerializer
    permission_classes = [permissions.IsAuthenticated]

# Board view set
class BoardViewSet(viewsets.ModelViewSet):
    queryset = Board.objects.all()
    serializer_class = BoardSerializer
    http_method_names = ['get']
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        game_id = self.kwargs.get('game_pk')
        player_id = self.kwargs.get('player_pk')
        if game_id and player_id:
            return Board.objects.filter(game__id=game_id, player__id=player_id)
        return super().get_queryset() 

# BoardVessel view set
class BoardVesselViewSet(viewsets.ModelViewSet):
    queryset = BoardVessel.objects.all()
    serializer_class = BoardVesselSerializer
    http_method_names = ['get', 'post', 'put', 'patch', 'delete']
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        game_id = self.kwargs.get('game_pk')
        player_id = self.kwargs.get('player_pk')
        if game_id and player_id:
            return BoardVessel.objects.filter(board__game__id=game_id, board__player__id=player_id)
        return super().get_queryset()
    
    def create(self, request, *args, **kwargs):
        game_id = kwargs.get('game_pk')
        player_id = kwargs.get('player_pk')

        # Check if game_id and player_id are provided
        if not (game_id and player_id):
            return Response({"detail": "Game ID or Player ID not provided."}, status=status.HTTP_400_BAD_REQUEST)

        game = get_object_or_404(Game, id=game_id)
        player = get_object_or_404(Player, id=player_id)

        # The player must belong to the game
        if not game.players.filter(id=player.id).exists():
            return Response({"detail": "This player is not part of the specified game."}, status=status.HTTP_400_BAD_REQUEST)

        # The game must be in placement phase
        if game.phase != Game.PHASE_PLACEMENT:
            return Response({"detail": f"Cannot place a vessel during the '{game.phase}' phase."}, status=status.HTTP_400_BAD_REQUEST)

        board = get_object_or_404(Board, game=game, player=player)
        
        # Validate the request data
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        vessel = serializer.validated_data['vessel']
        ri = serializer.validated_data['ri']
        ci = serializer.validated_data['ci']
        rf = serializer.validated_data['rf']
        cf = serializer.validated_data['cf']

        self.validate_board_vessel_placement(game, board, vessel, min(ri, rf), min(ci, cf), max(ri, rf), max(ci, cf))
        
        # Save the new vessel
        serializer.save(board=board)

        # Check if all vessels have been placed
        placed_vessel_ids = BoardVessel.objects.filter(board=board).values_list('vessel_id', flat=True).distinct()
        all_vessels_ids = Vessel.objects.values_list('id', flat=True)

        # If all vessels have been placed, the board is prepared
        if set(placed_vessel_ids) == set(all_vessels_ids):
            board.prepared = True
            board.save()

            # If all boards are prepared, the game can start
            if all(b.prepared for b in Board.objects.filter(game=game)):
                game.phase = Game.PHASE_PLAYING
                game.turn = player
                game.save()

        return Response(serializer.data, status=status.HTTP_201_CREATED)

    def validate_board_vessel_placement(self, game, board, vessel, ri, ci, rf, cf, exclude_id=None):
        # Board limits
        if not (0 <= ri < game.height) or not (0 <= rf < game.height):
            raise ValidationError(f"Row must be within 0 and {game.height - 1}.")
        if not (0 <= ci < game.width) or not (0 <= cf < game.width):
            raise ValidationError(f"Column must be within 0 and {game.width - 1}.")

        # Non diagonal placement
        if ri != rf and ci != cf:
            raise ValidationError("Vessel must be placed horizontally or vertically (no diagonals).")

        # Verify the coordinates are in order
        if ci == cf and ri > rf:
            raise ValidationError("In vertical placement, ri must be less than rf.")
        if ri == rf and ci > cf:
            raise ValidationError("In horizontal placement, ci must be less than cf.")

        # Verify the vessel size
        expected_size = vessel.size
        actual_size = (rf - ri + 1) if ci == cf else (cf - ci + 1)
        if expected_size != actual_size:
            raise ValidationError(f"Incorrect vessel size: expected {expected_size}, got {actual_size}.")

        # Verify that the vessel has not been placed already
        vessels_qs = BoardVessel.objects.filter(board=board, vessel=vessel)
        if exclude_id:
            vessels_qs = vessels_qs.exclude(id=exclude_id)
        if vessels_qs.exists():
            raise ValidationError("This vessel has already been placed.")

        # Verify that it does not overlap with another vessel
        new_cells = self.get_cells(ri, ci, rf, cf)
        existing_vessels = BoardVessel.objects.filter(board=board)
        if exclude_id:
            existing_vessels = existing_vessels.exclude(id=exclude_id)
        for other in existing_vessels:
            other_cells = self.get_cells(other.ri, other.ci, other.rf, other.cf)
            if set(new_cells) & set(other_cells):
                raise ValidationError("This placement overlaps with another vessel.")

    def get_cells(self, ri, ci, rf, cf):
        cells = []
        if ri == rf:
            for c in range(min(ci, cf), max(ci, cf) + 1):
                cells.append((ri, c))
        elif ci == cf:
            for r in range(min(ri, rf), max(ri, rf) + 1):
                cells.append((r, ci))
        return cells

# Shot view set
class ShotViewSet(viewsets.ModelViewSet):
    queryset = Shot.objects.all()
    serializer_class = ShotSerializer
    http_method_names = ['get', 'post']
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        game_id = self.kwargs.get('game_pk')
        player_id = self.kwargs.get('player_pk')
        if game_id and player_id:
                return Shot.objects.filter(game__id=game_id, player__id=player_id)
        return super().get_queryset()
    
    def create(self, request, *args, **kwargs):
        game_id = kwargs.get('game_pk')
        player_id = kwargs.get('player_pk')
        
        # Check if game_id and player_id are provided
        if not (game_id and player_id):
            return Response({"detail": "Game ID or Player ID not provided."}, status=status.HTTP_400_BAD_REQUEST)

        game = get_object_or_404(Game, id=game_id)
        player = get_object_or_404(Player, id=player_id)

        # The player must belong to the game
        if not game.players.filter(id=player.id).exists():
            return Response({"detail": "This player is not part of the specified game."}, status=status.HTTP_400_BAD_REQUEST)

        # The game must be in placement phase
        if game.phase != Game.PHASE_PLAYING:
            return Response({"detail": f"Cannot shoot during the '{game.phase}' phase."}, status=status.HTTP_400_BAD_REQUEST)

        # Validate the request data
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        row = serializer.validated_data['row']
        col = serializer.validated_data['col']

        # Coordinates must be within the board limits
        if not (0 <= row < game.height) or not (0 <= col < game.width):
            return Response({"detail": f"Coordinates out of bounds. Max row: {game.height - 1}, col: {game.width - 1}."}, status=status.HTTP_400_BAD_REQUEST)

        # Verify that the player has not already shot at this cell
        board = get_object_or_404(Board, game=game, player=player)
        if Shot.objects.filter(board=board, row=row, col=col).exists():
            return Response({"detail": "This coordinate has already been shot at by this player."}, status=status.HTTP_400_BAD_REQUEST)

        # Get the opponent's board
        opponent = game.players.exclude(id=player.id).first()
        if not opponent:
            return Response({"detail": "No opponent found in this game."}, status=status.HTTP_400_BAD_REQUEST)

        opponent_board = get_object_or_404(Board, game=game, player=opponent)
        
        # Verify if the shot hits a vessel
        hit_vessel = None
        result = 0
        
        for vessel in BoardVessel.objects.filter(board=opponent_board, alive=True):
            covered_cells = self.get_cells(vessel.ri, vessel.ci, vessel.rf, vessel.cf)
            if (row, col) in covered_cells:
                hit_vessel = vessel
                break
            
        # Compute the result of the shot
        if hit_vessel:
            # Check if it is hit or sunk
            hit_cells = {(s.row, s.col) for s in Shot.objects.filter(vessel_hit=hit_vessel)}
            covered_cells = set(self.get_cells(hit_vessel.ri, hit_vessel.ci, hit_vessel.rf, hit_vessel.cf))

            # Sunk
            if (covered_cells - hit_cells - {(row, col)}) == set():
                result = 2 
                hit_vessel.alive = False
                hit_vessel.save()
                
                # After a vessel is sunk
                if not BoardVessel.objects.filter(board=opponent_board, alive=True).exists():
                    game.phase = Game.PHASE_GAMEOVER
                    game.winner = player
                    game.save()

            # Hit
            else:
                result = 1

        # Store the shot
        shot = serializer.save(
            player=player,
            game=game,
            board=board,
            vessel_hit=hit_vessel,
            result=result
        )

        # Change turn only if shot was a miss
        if result == 0:
            game.turn = opponent
            game.save()

        return Response(self.get_serializer(shot).data, status=status.HTTP_201_CREATED)

    def get_cells(self, ri, ci, rf, cf):
        cells = []
        if ri == rf:
            for c in range(min(ci, cf), max(ci, cf) + 1):
                cells.append((ri, c))
        elif ci == cf:
            for r in range(min(ri, rf), max(ri, rf) + 1):
                cells.append((r, ci))
        return cells
    
# Leaderboard view
class LeaderboardViewSet(viewsets.ReadOnlyModelViewSet):
    permission_classes = [permissions.AllowAny]
    pagination_class = None 

    def get_queryset(self):
        return Player.objects.none()
    
    def list(self, request):
        player_stats = []

        # We get all players
        for player in Player.objects.all():
            # We count how many games have they finished
            total = Game.objects.filter(
                players=player,
                phase='gameOver'
            ).distinct().count()

            # We count how many wins they have
            wins = Game.objects.filter(
                winner=player,
                phase='gameOver'
            ).count()

            # We do all the computing which is straigh forward
            score = (wins / total) * 100 if total > 0 else 0.0

            player_stats.append({
                'nickname': player.nickname,
                'total_games': total,
                'wins': wins,
                'score': round(score, 2),
            })

        # Sort and return top 5
        sorted_stats = sorted(player_stats, key=lambda x: x['score'], reverse=True)[:5]
        return Response(sorted_stats)
    
    def list(self, request):
        # Auxiliar function to compute the stats using filters
        def compute_stats(filter_kwargs):
            stats = []

            # We get all players
            for player in Player.objects.all():
                # We count how many games have they finished
                total = Game.objects.filter(players=player, phase='gameOver', **filter_kwargs).distinct().count()

                # We count how many wins they have
                wins = Game.objects.filter(winner=player, phase='gameOver', **filter_kwargs).count()

                # We do all the computing which is straigh forward
                score = (wins / total) * 100 if total > 0 else 0.0
                stats.append({
                    'nickname': player.nickname,
                    'total_games': total,
                    'wins': wins,
                    'score': round(score, 2),
                })
            return sorted(stats, key=lambda x: (x['score'], x['total_games']), reverse=True)[:5]

        # We retreive all data
        return Response({
            'general': compute_stats({}),
            'multiplayer': compute_stats({'multiplayer': True}),
            'bot': compute_stats({'multiplayer': False}),
        })