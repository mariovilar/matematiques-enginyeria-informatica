# Self Testing

## Group Information

- Your group and team members:
  - Group: F09
  - Team members: David Díez, Mario Vilar

### Implementation checklist

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
  - [x] (**OPT**) multiplayer is implemented
  - [x] multiplayer works correctly
- Stress Testing:
  - [x] can handle multiple concurrent games
  - [x] can handle multiple concurrent players
  - [x] game can be restarted
  - [x] behaviour when cookies are disabled (it should kick you out of the game)
- Post game:
  - [x] (**OPT**) leaderboard is implemented

- Additional features you implemented (please specify):
  - [x] We can't shot too fast to avoid overlapping the bot.

### Encountered issues, how you solved them if you did.

- Write here.

### Post testing improvements
- Disconnected players now have two options: join a previous game they were on, join another player who is looking to play or create another game, with a bot or another player.