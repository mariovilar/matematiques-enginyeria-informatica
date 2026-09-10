<script setup>
import { ref, onMounted } from "vue";
import { useAuthStore } from "../store/authStore";
import { useGameStore } from "../store/index.js";
import { useRouter } from "vue-router";

const store = useGameStore();
const authStore = useAuthStore();
const router = useRouter();

const username = ref("");
const password = ref("");

// New variables to store the register
const registerUsername = ref("");
const registerEmail = ref("");
const registerPassword = ref("")
const registerPassword2 = ref("");
const isRegistering = ref(false);

const availableGames = ref([]);
const previousGames = ref([]);
const showGameList = ref(false);

onMounted(() => {
	authStore.initializeAuthStore();
	authStore.getAllPlayers();
});

const leaderboard = () => {
	router.push("/leaderboard");
};

const startGame = () => {
	router.push("/game");
};

const authenticateUser = () => {
	if (!username.value || !password.value) {
		alert("Please enter both username and password.");
		return;
	}
	authStore.login({ username: username.value, password: password.value });
};

const logOut = () => {
	authStore.logout();
};

const startMultiplayerGame = async () => {
	await store.startNewGame(true);
	startGame();
};

const startGameWithBot = async () => {
	await store.startNewGame(false);
	startGame();
};

const joinAvailableGame = async () => {
	availableGames.value = [];

	const games = await store.getGamesWithAvailableSlot();
	if (!games.length) {
		alert("No available games to join.");
		showGameList.value = false;
		return;
	}

	// Attach usernames asynchronously
	const gamesWithUsernames = await Promise.all(
		games.map(async (game) => {
			try {
				const response = await store.getPlayerInGame(game.id, game.owner);
				game.username = response.data.nickname;
			} catch (error) {
				game.username = "Unknown";
			}
			return game;
		})
	);

	availableGames.value = gamesWithUsernames;
	showGameList.value = true;
};

const joinPreviousGame = async () => {
	previousGames.value = [];

	const games = await store.getMyPreviousGames();
	if (!games.length) {
		alert("No available games to join.");
		showGameList.value = false;
		return;
	}

	// Attach usernames asynchronously
	const gamesWithUsernames = await Promise.all(
		games.map(async (game) => {
			try {
				const response = await store.getPlayerInGame(game.id, game.players.find(id => id != authStore.getMyId()))
				game.username = response.data.nickname;
			} catch (error) {
				game.username = "Unknown";
			}
			return game;
		})
	);

	previousGames.value = gamesWithUsernames;
	showGameList.value = true;
};

const joinSelectedGame = async (gameId, old = false) => {
	await store.joinGame(gameId, old);
	startGame();
};

// Function to register a new user
const registerUser = async () => {
	if (!registerUsername.value || !registerEmail.value || !registerPassword.value || !registerPassword2.value) {
		alert("All fields are required.");
		return;
	}
	if (registerPassword.value !== registerPassword2.value) {
		alert("Passwords do not match.");
		return;
	}

	try {
		await authStore.register({
			username: registerUsername.value,
			email: registerEmail.value,
			password: registerPassword.value,
			password2: registerPassword2.value,
		});
		alert("Account created. You can now log in.");
		username.value = registerUsername.value;
		password.value = registerPassword.value;
		isRegistering.value = false;
	} catch (e) {
		alert("Failed to register: " + e.message);
	}
};
</script>

<template>
	<div class="home text-center mt-4">
		<h1>Welcome to Battleship Game</h1>

		<div v-if="authStore.isAuthenticated" class="mt-5 mb-5">
			<h3>Hi {{ authStore.username }}! You're logged in!</h3>
			<div class="mb-3">
				Access Token: {{ authStore.accessToken.slice(0, 20) }}...
			</div>
			<button class="btn btn-primary btn-block" @click="startMultiplayerGame">
				Start New Multiplayer Game
			</button><br>
			<button class="btn btn-info btn-block" @click="startGameWithBot">
				Start New Game With Bot
			</button><br>
			<button class="btn btn-success btn-block" @click="joinAvailableGame">
				Join Available Game
			</button><br>
			<div v-if="showGameList && availableGames.length" class="mt-3">
				<h4>Select a game to join:</h4>
				<ul class="list-group mx-auto mb-3" style="max-width: 300px;">
					<li v-for="game in availableGames" :key="game.id"
						class="list-group-item d-flex justify-content-between align-items-center">
						Game #{{ game.id }} vs {{ game.username }}
						<button class="btn btn-sm btn-success" @click="joinSelectedGame(game.id)">
							Join
						</button>
					</li>
				</ul>
			</div>
			<button class="btn btn-warning btn-block" @click="joinPreviousGame">
				Join Previous Game
			</button><br>
			<div v-if="showGameList && previousGames.length" class="mt-3">
				<h4>Select a game to join:</h4>
				<ul class="list-group mx-auto mb-3" style="max-width: 300px;">
					<li v-for="game in previousGames" :key="game.id"
						class="list-group-item d-flex justify-content-between align-items-center">
						Game #{{ game.id }} vs {{ game.username }}
						<button class="btn btn-sm btn-success" @click="joinSelectedGame(game.id, true)">
							Join
						</button>
					</li>
				</ul>
			</div>
			<button class="btn btn-secondary btn-block" @click="leaderboard">Leaderboard</button>
			<br>
			<button class="btn btn-danger btn-block" @click="logOut">Log Out</button>
		</div>
		<div v-else>
			<div>
				<button class="btn btn-link" @click="isRegistering = !isRegistering">
					{{ isRegistering ? "Already have an account?" : "Don't have an account?" }}
				</button>
			</div>

			<div v-if="isRegistering">
				<h3>Create an Account</h3>
				<form @submit.prevent="registerUser" class="mx-auto" style="max-width: 300px">
					<input v-model="registerUsername" type="text" placeholder="Username" class="form-control mb-2"
						required />
					<input v-model="registerEmail" type="email" placeholder="Email" class="form-control mb-2"
						required />
					<input v-model="registerPassword" type="password" placeholder="Password" class="form-control mb-2"
						required />
					<input v-model="registerPassword2" type="password" placeholder="Confirm Password"
						class="form-control mb-2" required />
					<button class="btn btn-primary w-100">Register</button>
					<div v-if="authStore.error" class="text-danger mt-2">{{ authStore.error }}</div>
				</form>
			</div>

			<div v-else>
				<h3>Login</h3>
				<form @submit.prevent="authenticateUser" class="mx-auto" style="max-width: 300px">
					<input v-model="username" type="text" placeholder="Username" class="form-control mb-2" />
					<input v-model="password" type="password" placeholder="Password" class="form-control mb-2" />
					<button class="btn btn-primary w-100" :disabled="authStore.loading">
						{{ authStore.loading ? "Logging in..." : "Log In" }}
					</button>
					<div v-if="authStore.error" class="text-danger mt-2">{{ authStore.error }}</div>
				</form>
			</div>
		</div>
	</div>
</template>

<style scoped>
.home {
	max-width: 600px;
	margin: 0 auto;
}

.btn-block {
	width: 100%;
	max-width: 250px;
	margin: 0 auto;
	display: block;
}
</style>
