from django.contrib.auth.models import User
from rest_framework import serializers
from .models import Player, Game, Vessel, Board, BoardVessel, Shot

# User serializer
class UserSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)
    password2 = serializers.CharField(write_only=True)

    class Meta:
        model = User
        fields = ('username', 'email', 'password', 'password2')

    def validate(self, data):
        if data['password'] != data['password2']:
            raise serializers.ValidationError("Passwords do not match.")
        return data

    # We use create_user() for password management
    def create(self, validated_data):
        validated_data.pop('password2')
        return User.objects.create_user(**validated_data)

# Player serializer
class PlayerSerializer(serializers.ModelSerializer):
    class Meta:
        model = Player
        fields = '__all__'

# Game serializer
class GameSerializer(serializers.ModelSerializer):
    owner = serializers.PrimaryKeyRelatedField(read_only=True)

    class Meta:
        model = Game
        fields = ['id', 'players', 'width', 'height', 'multiplayer', 'turn', 'phase', 'winner', 'owner']
        read_only_fields = ['players', 'turn', 'phase', 'winner', 'owner']

# Vessel serializer
class VesselSerializer(serializers.ModelSerializer):
    class Meta:
        model = Vessel
        fields = '__all__'

# Board serializer
class BoardSerializer(serializers.ModelSerializer):
    class Meta:
        model = Board
        fields = '__all__'

# Board vessel serializer
class BoardVesselSerializer(serializers.ModelSerializer):
    class Meta:
        model = BoardVessel
        fields = '__all__'
        read_only_fields = ['board']

# Shot serializer
class ShotSerializer(serializers.ModelSerializer):
    class Meta:
        model = Shot
        fields = '__all__'
        read_only_fields = ['board', 'player', 'game', 'vessel_hit', 'result']

# Game state
class GameStateResponseSerializer(serializers.Serializer):
    status = serializers.IntegerField(default=200)
    message = serializers.CharField(default="OK")
    data = serializers.SerializerMethodField()

    def get_data(self, obj):
        return {
            "gameState": GameStateSerializer(obj, context=self.context).data
        }

class GameStateSerializer(serializers.Serializer):
    gameId = serializers.IntegerField(source="id")
    phase = serializers.CharField()
    turn = serializers.SerializerMethodField()
    winner = serializers.SerializerMethodField()
    owner = serializers.IntegerField(source='owner.id')
    multiplayer = serializers.BooleanField()
    player1 = serializers.SerializerMethodField()
    player2 = serializers.SerializerMethodField()

    def get_turn(self, obj):
        return obj.turn.nickname if obj.turn else None

    def get_winner(self, obj):
        return obj.winner.nickname if obj.winner else None

    def get_player1(self, obj):
        request = self.context.get("request")
        current_player = getattr(request.user, "player", None)

        if not current_player:
            return None

        try:
            board = Board.objects.get(game=obj, player=current_player)
            return PlayerStateSerializer(board).data
        except Board.DoesNotExist:
            return None

    def get_player2(self, obj):
        request = self.context.get("request")
        current_player = request.user.player
        board = Board.objects.filter(game=obj).exclude(player=current_player).first()
        return PlayerStateSerializer(board).data if board else None

# Player serializer
class PlayerStateSerializer(serializers.Serializer):
    id = serializers.CharField(source='player.id')
    username = serializers.CharField(source='player.nickname')
    placedShips = serializers.SerializerMethodField()
    availableShips = serializers.SerializerMethodField()
    board = serializers.SerializerMethodField()

    def get_placedShips(self, board):
        ships = []
        vessels = BoardVessel.objects.filter(board=board)
        for vessel in vessels:
            ships.append({
                "type": vessel.vessel.id,
                "position": {"row": vessel.ri, "col": vessel.ci},
                "isVertical": vessel.ri != vessel.rf,
                "size": vessel.vessel.size
            })
        return ships

    def get_availableShips(self, board):
        placed_vessels_ids = BoardVessel.objects.filter(board=board).values_list("vessel_id", flat=True)
        all_vessels = Vessel.objects.exclude(id__in=placed_vessels_ids)
        return [{
            "type": v.id,
            "isVertical": True,
            "size": v.size
        } for v in all_vessels]

    def get_board(self, board):
        width = board.game.width
        height = board.game.height
        grid = [[0 for _ in range(width)] for _ in range(height)]

        # Place the ships
        vessel_map = {}
        for vessel in BoardVessel.objects.filter(board=board):
            cells = self.get_cells(vessel.ri, vessel.ci, vessel.rf, vessel.cf)
            for r, c in cells:
                grid[r][c] = vessel.vessel.id
                vessel_map[(r, c)] = vessel

        # Place the shots
        for shot in Shot.objects.filter(game=board.game).exclude(player=board.player):
            pos = (shot.row, shot.col)
            if pos in vessel_map:
                grid[shot.row][shot.col] = -vessel_map[pos].vessel.id  # hit
            elif grid[shot.row][shot.col] == 0:
                grid[shot.row][shot.col] = 11  # miss

        return grid

    def get_cells(self, ri, ci, rf, cf):
        cells = []
        if ri == rf:
            for c in range(min(ci, cf), max(ci, cf) + 1):
                cells.append((ri, c))
        elif ci == cf:
            for r in range(min(ri, rf), max(ri, rf) + 1):
                cells.append((r, ci))
        return cells