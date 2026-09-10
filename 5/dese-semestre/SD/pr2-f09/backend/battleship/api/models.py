from django.db import models
from django.contrib.auth.models import User
from django.core.validators import MinValueValidator, MaxValueValidator
from django.core.exceptions import ValidationError

def validate_result(value):
    if value not in (-1, 0, 1, 2):
        raise ValidationError("Result must be -1, 0, 1, or 2.")
         
# Player model
class Player(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    nickname = models.CharField(max_length=50, unique=True)

# Game model
class Game(models.Model):
    PHASE_WAITING = "waiting"
    PHASE_PLACEMENT = "placement"
    PHASE_PLAYING = "playing"
    PHASE_GAMEOVER = "gameOver"
    PHASE_CHOICES = {
        PHASE_WAITING: "Waiting",
        PHASE_PLACEMENT: "Placement",
        PHASE_PLAYING: "Playing",
        PHASE_GAMEOVER: "Game Over",
    }

    players = models.ManyToManyField(Player, related_name="games")
    width = models.IntegerField(validators=[MinValueValidator(5), MaxValueValidator(200)], default=10)
    height = models.IntegerField(validators=[MinValueValidator(5), MaxValueValidator(200)], default=10)
    multiplayer = models.BooleanField(default=False)
    turn = models.ForeignKey(Player, related_name="turn", on_delete=models.SET_NULL, blank=True, null=True)
    phase = models.CharField(max_length=15, choices=PHASE_CHOICES.items(), default=PHASE_WAITING)
    winner = models.ForeignKey(Player, related_name="winner", on_delete=models.SET_NULL, blank=True, null=True)
    owner = models.ForeignKey(Player, related_name="owner", on_delete=models.SET_NULL, null=True)

# Board model
class Board(models.Model):
    game = models.ForeignKey(Game, related_name="boards", on_delete=models.CASCADE)
    player = models.ForeignKey(Player, related_name="boards", on_delete=models.CASCADE)
    prepared = models.BooleanField(default=False)

# Vessel model
class Vessel(models.Model):
    size = models.IntegerField(validators=[MinValueValidator(2), MaxValueValidator(5)])
    name = models.CharField(max_length=20, unique=True) 
    image = models.CharField(max_length=255, blank=True)

# Board vessel model
class BoardVessel(models.Model):
    board = models.ForeignKey(Board, related_name="vessels", on_delete=models.CASCADE)
    vessel = models.ForeignKey(Vessel, related_name="instances", on_delete=models.CASCADE)
    ri = models.IntegerField(validators=[MinValueValidator(0), MaxValueValidator(199)])
    ci = models.IntegerField(validators=[MinValueValidator(0), MaxValueValidator(199)])
    rf = models.IntegerField(validators=[MinValueValidator(0), MaxValueValidator(199)])
    cf = models.IntegerField(validators=[MinValueValidator(0), MaxValueValidator(199)])
    alive = models.BooleanField(default=True)

# Shot model
class Shot(models.Model):       
    player = models.ForeignKey(Player, related_name="shots", on_delete=models.SET_NULL, null=True, blank=True)
    game = models.ForeignKey(Game, related_name="shots", on_delete=models.SET_NULL, null=True, blank=True)
    board = models.ForeignKey(Board, related_name="shots", on_delete=models.CASCADE)
    vessel_hit = models.ForeignKey(BoardVessel, related_name="impacts", on_delete=models.SET_NULL, null=True, blank=True)
    row = models.IntegerField(validators=[MinValueValidator(0), MaxValueValidator(199)])
    col = models.IntegerField(validators=[MinValueValidator(0), MaxValueValidator(199)])
    result = models.IntegerField(default=-1, validators=[validate_result])