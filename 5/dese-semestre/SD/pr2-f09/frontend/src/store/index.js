import { defineStore } from "pinia";
import { useAuthStore } from "./authStore";
import api from "../services/api";

export const useGameStore = defineStore("game", {
	state: () => ({
		game: null,
		gamePhase: "placement",
		gameStatus: "Place your ships",
		playerBoard: [],
		opponentBoard: [],
		playerPlacedShips: [],
		opponentShips: [],
		availableShips: [],
		selectedShip: null,
		refreshIntervalId: null,
		inviteGame: null,
		watchIntervalId: null,
	}),

	actions: {
		cleanValues() {
			// Clear the intervals
			clearInterval(this.refreshIntervalId);
			clearInterval(this.watchIntervalId);

			this.game = null;
			this.gamePhase = "placement";
			this.gameStatus = "Place your ships";
			this.playerBoard = [];
			this.opponentBoard = [];
			this.playerPlacedShips = [];
			this.opponentShips = [];
			this.availableShips = [];
			this.selectedShip = null;
			this.refreshIntervalId = null;
			this.inviteGame = null;
			this.watchIntervalId = null;
		},
		getUser(id) {
			return api
				.getUser(id)
				.then((response) => {
					return response.data;
				})
				.catch((error) => {
					const message = error.response?.data?.detail || error.message;
					throw new Error(message);
				});
		},

		async getGameState(gameId) {
			// Get the game status
			this.game = (await api.getGame(gameId)).data.data.gameState;
			this.game.multiplayer = this.game.multiplayer || false;
			this.game.players = [this.game.player1.id];

			if (this.game.player2) {
				this.game.players.push(this.game.player2.id);
			}

			// Store the game id in local storage to avoid duplicates
			useAuthStore().setGameId(gameId);

			// Setup all data
			this.playerBoard = this.game.player1?.board || [];
			this.playerPlacedShips = this.game.player1?.placedShips || [];
			this.availableShips = this.game.player1?.availableShips || [];

			if (this.game.player2) {
				this.opponentBoard = this.game.player2.board || [];
				this.opponentShips = this.game.player2.placedShips || [];
			} else {
				this.opponentBoard = [];
				this.opponentShips = [];
			}

			this.gamePhase = this.game.phase;

			if (this.gamePhase === "playing") {
				const isPlayerTurn = this.game.turn === this.game.player1.username;
				this.gameStatus = isPlayerTurn ? "Your turn" : "Opponent's turn";
			} else if (this.gamePhase === "placement") {
				this.gameStatus = "Place your ships";
			} else if (this.gamePhase === "gameOver") {
				this.gameStatus = "Game Over - Winner " + this.game.winner;
			}

			// Autorefresh the state
			if (this.gamePhase !== "gameOver" && this.game.multiplayer) {
				this.startGamePolling(gameId);
			} else {
				this.stopGamePolling();
			}

			// Check for new invitations
			if (this.gamePhase === "gameOver" && this.game.multiplayer) {
				this.watchForNewGameInvite(gameId, this.game.players.find(id => id != useAuthStore().getMyId()));
			}
		},

		stopGamePolling() {
			if (this.refreshIntervalId) {
				clearInterval(this.refreshIntervalId);
				this.refreshIntervalId = null;
			}
		},

		startGamePolling(gameId) {
			if (this.refreshIntervalId) return;

			this.refreshIntervalId = setInterval(async () => {
				await this.getGameState(gameId);
			}, 2000);
		},

		async watchForNewGameInvite(lastGameId, lastOpponentId) {
			if (this.watchIntervalId) return;

			this.watchIntervalId = setInterval(async () => {
				try {
					// We get all games
					const games = (await api.getAllGames()).data;

					// We look for those which match the criteria
					const matchingGames = games.filter(game =>
						game.multiplayer &&
						game.id > lastGameId &&
						game.phase === "waiting" &&
						game.players.length === 1 &&
						game.players.includes(Number(lastOpponentId))
					);

					if (matchingGames.length > 0) {
						this.inviteGame = matchingGames[0];

						const username = (await api.getPlayerInGame(this.inviteGame.id, this.inviteGame.owner)).data.nickname;
						this.inviteGame.username = username;

						clearInterval(this.watchIntervalId);
						this.watchIntervalId = null
					}
				} catch (error) {
					console.error("Error watching for new game:", error);
				}
			}, 1000);
		},

		async startNewGame(isMultiplayer = false) {
			try {
				this.cleanValues();

				const response = await api.createGame({
					width: 10,
					height: 10,
					multiplayer: isMultiplayer
				});

				const gameId = response.data.id;

				await this.getGameState(gameId);
			} catch (error) {
				console.error("Failed to start new game:", error);
			}
		},

		async getMyPreviousGames() {
			try {
				const response = await api.getAllGames();
				const allGames = response.data;
				const myId = useAuthStore().getMyId();

				// Get available games
				return allGames.filter(game =>
					game.phase !== 'waiting' &&
					game.players.includes(myId)
				);

				// return Object.values(latestByOpponent);
			} catch (error) { return [] }
		},

		async getGamesWithAvailableSlot() {
			try {
				const response = await api.getAllGames();
				const allGames = response.data;
				const myId = useAuthStore().getMyId();

				// Get available games
				const availableGames = allGames.filter(game =>
					game.phase === 'waiting' &&
					game.multiplayer &&
					game.players.length === 1 &&
					!game.players.includes(myId)
				);

				// Get just the last available games
				const latestByOpponent = availableGames.reduce((map, game) => {
					const opponentId = game.players[0];
					if (!map[opponentId] || game.id > map[opponentId].id) {
						map[opponentId] = game;
					}
					return map;
				}, {});

				return Object.values(latestByOpponent);
			} catch (error) { return [] }
		},

		async joinGame(gameId, old) {
			try {
				this.cleanValues();

				const userId = useAuthStore().getMyId();
				const gameState = await api.getGame(gameId);

				const players = [
					gameState.data.data.gameState.player1?.id,
					gameState.data.data.gameState.player2?.id
				];

				if (!players.includes(Number(userId)) && !old) {
					const username = useAuthStore().getMyUsername();
					const player = await useAuthStore().getUserIdByUsername(username);
					await api.addPlayerToGame(gameId, { nickname: player.username });
				}

				await this.getGameState(gameId);
			} catch (error) {
				console.error("Error joining game:", error);
			}
		},

		selectShip(ship) {
			this.selectedShip = { ...ship };
		},

		rotateSelectedShip() {
			if (this.selectedShip) {
				this.selectedShip.isVertical = !this.selectedShip.isVertical;
			}
		},

		setVesselFormatToBackend(row, col, size, isVertical, type) {
			const ri = row;
			const ci = col;
			const rf = isVertical ? row + size - 1 : row;
			const cf = isVertical ? col : col - size + 1;

			return {
				ri: ri,
				ci: ci,
				rf: rf,
				cf: cf,
				vessel: type
			};
		},

		async handlePlayerBoardClick(row, col) {
			if (this.gamePhase !== "placement" || !this.selectedShip) return;

			const ship = this.selectedShip;

			// Add the vessel to the backend
			const vessel = this.setVesselFormatToBackend(row, col, ship.size, ship.isVertical, ship.type)

			try {
				await api.addShipToPlayerInGame(this.game.gameId, useAuthStore().getMyId(), vessel);

				this.selectedShip = null;

				// Reload the state
				await this.getGameState(this.game.gameId)
			} catch (error) {
				// Restore selection
				this.selectedShip = { ...ship };
				const message = error.response?.data?.detail || error.message;
				alert("Could not place vessel: " + JSON.stringify(message));
			}
		},

		isMyTurn() {
			return this.game.turn == useAuthStore().getMyUsername();
		},

		async handleOpponentBoardClick(row, col) {
			// If we are not playing yet, we can't do anything there
			if (this.gamePhase !== "playing") return;

			if (this.game.multiplayer && !this.isMyTurn()) {
				this.gameStatus = "Wait for your turn";
				return;
			}

			// If the opponent board is negative we have already hit there
			if (this.opponentBoard[row][col] < 0) {
				this.gameStatus = "Already hit!";
				return;
			} else if (this.opponentBoard[row][col] === 11) {
				this.gameStatus = "Already missed!";
				return;
			}

			const response = await api.addShotToPlayerInGame(this.game.gameId, useAuthStore().getMyId(), { row, col });
			const isHit = response.data.vessel_hit !== null;

			await this.getGameState(this.game.gameId);
			this.gameStatus = isHit ? "Hit!" : "Miss!";

			if (this.game.winner != null) {
				this.gamePhase = "gameOver";
				this.gameStatus = "Game Over - You won!";
				return;
			}

			// Bot turn if single-player
			if (!this.game.multiplayer && !isHit && this.game.owner == useAuthStore().getMyId()) {
				setTimeout(this.opponentTurn, 500);
			}
		},

		async opponentTurn() {
			if (this.gamePhase !== "playing") return;

			const opponentId = this.game.players.find(id => id != useAuthStore().getMyId());

			let row, col, valid = false;
			while (!valid) {
				row = Math.floor(Math.random() * 10);
				col = Math.floor(Math.random() * 10);
				valid = this.playerBoard[row][col] >= 0 && this.playerBoard[row][col] < 10;
			}

			let response = await api.addShotToPlayerInGame(this.game.gameId, opponentId, { row, col });
			let isHit = response.data.vessel_hit !== null;

			await this.getGameState(this.game.gameId);

			if (this.game.winner != null) {
				this.gamePhase = "gameOver";
				this.gameStatus = "Game Over - You lost!";
				return;
			}

			// Let the bot shoot again if it hits
			if (isHit) {
				setTimeout(this.opponentTurn, 500);
			} else {
				this.gameStatus = "Your turn";
			}
		},

		async getLeaderboard() {
			return (await api.getLeaderboard()).data;
		},

		async getPlayerInGame(gameId, playerId) {
			return await api.getPlayerInGame(gameId, playerId);
		}
	},
});