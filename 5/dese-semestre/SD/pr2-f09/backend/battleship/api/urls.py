from rest_framework_nested.routers import DefaultRouter, NestedSimpleRouter
from django.urls import path, include
from rest_framework.routers import DefaultRouter
from . import views

# Create a router and register our ViewSets with it.
router = DefaultRouter()

# User
router.register(r'user', views.UserViewSet)

# Basic: /players/
# Uses GET, POST, PUT, PATCH, DELETE
router.register(r'players', views.PlayerViewSet)

# Basic: /games/
# Uses GET, POST, PUT, PATCH, DELETE
router.register(r'games', views.GameViewSet)

# Basic: /vessels/
# Uses GET (ReadOnly)
router.register(r'vessels', views.VesselViewSet)

# Nested: /games/{gid}/players/
# Uses GET, POST, DELETE
games_players_router = NestedSimpleRouter(router, r'games', lookup='game')
games_players_router.register(r'players', views.PlayerViewSet, basename='game-players')

# Nested: /games/{gid}/players/{pid}/vessels/
# Uses GET, POST, PUT, PATCH, DELETE
player_vessels_router = NestedSimpleRouter(games_players_router, r'players', lookup='player')
player_vessels_router.register(r'vessels', views.BoardVesselViewSet, basename='player-vessels')

# Nested: /games/{gid}/players/{pid}/shots/
# Uses GET, POST
player_shots_router = NestedSimpleRouter(games_players_router, r'players', lookup='player')
player_shots_router.register(r'shots', views.ShotViewSet, basename='player-shots')

# Nested: /games/{gid}/players/{pid}/boards/
# Uses GET
player_boards_router = NestedSimpleRouter(games_players_router, r'players', lookup='player')
player_boards_router.register(r'boards', views.BoardViewSet, basename='player-boards')

# Basic: /leaderboard/
# Uses: GET
router.register(r'leaderboard', views.LeaderboardViewSet, basename='leaderboard')

urlpatterns = [
    path("", include(router.urls)),
    path("", include(games_players_router.urls)),
    path("", include(player_vessels_router.urls)),
    path("", include(player_shots_router.urls)),
    path("", include(player_boards_router.urls)),
]