<script setup>
import { onMounted } from "vue";
import { useGameStore } from "../store";
import { useAuthStore } from "../store/authStore";
import { useRouter } from "vue-router";

import GameBoard from "../components/GameBoard.vue";
import DockingArea from "../components/DockingArea.vue";

const store = useGameStore();
const authStore = useAuthStore();
const router = useRouter();

const leaderboard = () => {
	window.open("/leaderboard", "_blank");
};

onMounted(() => {
	const gameId = localStorage.getItem("activeGameId");
	if (gameId) {
		store.getGameState(gameId);
	} else {
		store.startNewGame(store.game.multiplayer);
	}
});

const onLogout = () => {
	if (confirm("Are you sure you want to log out?")) {
		authStore.logout();
		router.push("/");
	}
};

const backToHome = () => {
	router.push("/");
};
</script>

<template>
	<div class="container-fluid">
		<h1 class="text-center my-2">
			Battleship (Hello: {{ authStore.username }})
			<button class="btn btn-sm btn-outline-danger ms-2" @click="onLogout">
				Logout
			</button>
		</h1>
		<div class="row">
			<!-- Player's Board -->
			<div class="col-lg-5 d-flex flex-column align-items-center">
				<h3 class="text-center">Your Fleet</h3>
				<GameBoard :board="store.playerBoard" :ships="store.playerPlacedShips"
					@cell-click="store.handlePlayerBoardClick" />
			</div>

			<!-- Docking Area -->
			<div v-if="store.gamePhase === 'placement'" class="col-lg-2">
				<h3 class="text-center">Dock</h3>
				<DockingArea :ships="store.availableShips" @ship-selected="store.selectShip"
					@rotate-ship="store.rotateSelectedShip" />
			</div>

			<!-- Controls -->
			<div v-else-if="store.gamePhase === 'gameOver'" class="col-lg-2 d-flex flex-column justify-content-center">
				<div class="game-controls text-center">
					<div class="game-status mb-3">{{ store.gameStatus }}</div>
					<button v-if="store?.game?.multiplayer" class="btn btn-primary mb-2"
						@click="store.startNewGame(true)">
						New Multiplayer Game
					</button>
					<button class="btn btn-info mb-2" @click="store.startNewGame(false)">
						New Game With Bot
					</button>
					<button v-if="store.inviteGame" class="btn btn-success btn-block"
						@click="store.joinGame(store.inviteGame.id)">
						Join new game with {{ store.inviteGame.username }}
					</button>
				</div>
			</div>

			<div v-else-if="store.gamePhase === 'playing'" class="col-lg-2 d-flex flex-column justify-content-center">
				<div class="game-controls text-center">
					<div class="game-status mb-3">{{ store.gameStatus }}</div>
					Lets play!
				</div>
			</div>

			<div v-else-if="store.gamePhase === 'waiting'" class="col-lg-2 d-flex flex-column justify-content-center">
				<div class="game-controls text-center">
					<div class="game-status mb-3">{{ store.gameStatus }}</div>
					Waiting for an opponent
				</div>
			</div>

			<!-- Opponent Board -->
			<div class="col-lg-5 d-flex flex-column align-items-center">
				<h3 class="text-center">Enemy Fleet</h3>
				<GameBoard :board="store.opponentBoard" :ships="store.opponentShips" :hidden="true"
					@cell-click="store.handleOpponentBoardClick" />
			</div>
		</div>

		<button class="btn btn-secondary btn-block mt-4" @click="leaderboard">
			See leaderboard
		</button>

		<button class="btn btn-primary btn-block mt-4" @click="backToHome">
			Back to main menu
		</button>
	</div>
</template>

<style>
.game-controls {
	background-color: #f8f9fa;
	border-radius: 8px;
	padding: 20px;
	margin: 20px 0;
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.game-status {
	font-size: 1.2rem;
	font-weight: bold;
}

.btn-block {
	width: 100%;
	max-width: 250px;
	display: block;
}
</style>
