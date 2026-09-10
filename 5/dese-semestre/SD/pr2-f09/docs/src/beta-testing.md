# Beta Testing

## Testing scenarios

- Case A: The game is fully functional - i.e., frontend and backend are implemented and communicate correctly. In this case, the testing is performed on the frontend by playing the game.
- Case B: The game is partially functional - i.e., frontend is not fully connected to the backend. In this case, the testing is performed on the backend by sending requests to the API endpoints using the `api/v1/*/` endpoints or `docs/` url.
- Case C: The backend is partially functional - i.e., the backend is not fully implemented. In this case, the testers will interview the developers about what is working and what is not, and about the main issues they encountered and discuss/advise on how to fix them.

## Group Information

- Your group and team members:
  - Group: F09
  - Team members: David Díez, Mario Vilar

## Tested Group Information

### Test group 1

- Test group 1:
  - Group: F10
  - Team members: Victor Sort, Guillem Navarra

### Case A checklist

- Initialization:
  - [x] authentication works correctly
  - [x] (**OPT**) registration is implemented
  - [x] game can be created
- Gameplay:
  - [x] can place ships
  - [x] can fire shots
  - [x] can receive hits and misses
  - [x] can play against a bot
  - [x] game ends correctly (win/loss)
  - [] (**OPT**) multiplayer is implemented
  - [] multiplayer works correctly
- Stress Testing:
  - [x] can handle multiple concurrent games
  - [x] can handle multiple concurrent players
  - [] game can be restarted (disconnected players can rejoin)
  - [] behaviour when cookies are disabled
- Post game:
  - [x] (**OPT**) leaderboard is implemented

- Additional tests (please specify):
  - [] ...
    - [] ...
    - [] ...

### Test group 2

- Test group 2:
  - Group: F11
  - Team members: Adrià, Victor Porras

### Case A checklist

- Initialization:
  - [x] you can get a token pair
  - [x] (**OPT**) registration is implemented
  - [x] authorization is set up correctly for the Users API
  - [x] game can be created
- Gameplay:
  - [x] can place ships
  - [x] can fire shots
  - [x] can receive hits and misses
  - [x] can play against a bot
  - [x] game ends correctly (win/loss)
  - [] (**OPT**) multiplayer is implemented
  - [] multiplayer works correctly
- Post game:
  - [x] (**OPT**) leaderboard is implemented

- Additional tests (please specify):
  - [] ...
    - [] ...
    - [] ...