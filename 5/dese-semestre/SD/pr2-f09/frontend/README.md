# frontend

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Project Setup

Make a copy of `env_sample` and name it `.env`

```sh
cp env_sample .env
```

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

# Frontend representation model

Below is the description of the game board representation in the frontend GUI. In particular, vessel characteristics and board representations.

## Vessels

There are only 5 types of vessels and only **one unit is available** for each type.

| **Name**    | **Size** | **Value on Board** | **Image**                                                                                                                                       |
| ----------- | -------- | ------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| Patrol Boat | 1 cell   | 1                  | <img src="src/assets/SeaWarfareSet/PatrolBoat/ShipPatrolHull.png" alt="Patrol Boat" style="transform: rotate(90deg); display: inline-block;" /> |
| Destroyer   | 2 cells  | 2                  | <img src="src/assets/SeaWarfareSet/Destroyer/ShipDestroyerHull.png" alt="Destroyer" style="transform: rotate(90deg); display: inline-block;" /> |
| Cruiser     | 3 cells  | 3                  | <img src="src/assets/SeaWarfareSet/Cruiser/ShipCruiserHull.png" alt="Cruiser" style="transform: rotate(90deg); display: inline-block;" />       |
| Submarine   | 4 cells  | 4                  | <img src="src/assets/SeaWarfareSet/Submarine/ShipSubMarineHull.png" alt="Submarine" style="transform: rotate(90deg); display: inline-block;" /> |
| Carrier     | 5 cells  | 5                  | <img src="src/assets/SeaWarfareSet/Carrier/ShipCarrierHull.png" alt="Carrier" style="transform: rotate(90deg); display: inline-block;" />       |

## Board

The board in the game is represented as an array of $H\times W$, where $H$ is the number of rows and $W$ is the number of columns. For this practical session, we will set the board size constant: $W\times H = 10\times 10$.

Optional activity for extra points: adapt the frontend code to visualize arbitrary board size.

### Own board cell representation (Your fleet)

| Cell value | Meaning         |
| ---------- | --------------- |
| 0          | clear water     |
| 1          | patrol boat     |
| 2          | destroyer       |
| 3          | cruiser         |
| 4          | submarine       |
| 5          | carrier         |
| -1         | hit patrol boat |
| -2         | hit destroyer   |
| -3         | hit cruiser     |
| -4         | hit submarine   |
| -5         | hit carrier     |
| >10        | missed shot     |

### Opponent board cell representation (Enemy fleet)

| Cell value | Meaning     |
| ---------- | ----------- |
| 0          | unknown     |
| <0         | hit         |
| >10        | missed shot |
