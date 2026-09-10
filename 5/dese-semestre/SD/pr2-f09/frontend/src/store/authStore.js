import { defineStore } from "pinia";
import { useGameStore } from "./index.js";
import AuthService from "../services/auth";

export const useAuthStore = defineStore("auth", {
	state: () => ({
		username: null,
		accessToken: null,
		refreshToken: null,
		isAuthenticated: false,
		loading: false,
		error: null,
		playersList: [],
	}),
	actions: {
		initializeAuthStore() {
			this.username = localStorage.getItem("username");
			this.accessToken = localStorage.getItem("access");
			this.refreshToken = localStorage.getItem("refresh");
			this.isAuthenticated = !!this.accessToken;
		},
		async login(user) {
			this.loading = true;
			this.error = null;
			try {
				const response = await AuthService.login(user);

				this.username = user.username;
				this.accessToken = response.data.access;
				this.refreshToken = response.data.refresh;
				this.isAuthenticated = true;

				localStorage.setItem("username", this.username);
				localStorage.setItem("access", this.accessToken);
				localStorage.setItem("refresh", this.refreshToken);

				const userId = await this.getUserIdByUsername(this.username);
				localStorage.setItem("user_id", userId);
			}
			catch (error) {
				this.error = error.response?.data?.detail || "Error d'inici de sessió. Torna-ho a intentar.";
				this.isAuthenticated = false;
			} finally {
				this.loading = false;
			};
		},

		logout() {
			this.accessToken = null;
			this.refreshToken = null;
			this.isAuthenticated = false;
			localStorage.removeItem("username");
			localStorage.removeItem("access");
			localStorage.removeItem("refresh");
			localStorage.removeItem("activeGameId");
			localStorage.removeItem("user_id");
			localStorage.removeItem("admin");

			// Clean all variables
			useGameStore().cleanValues();
		},

		async getAllPlayers() {
			try {
				const response = await AuthService.getAllPlayers();
				for (const player of response.data) {
					this.playersList.push({
						id: player.id,
						nickname: player.nickname,
					});
				}
			} catch (error) {
				const message = error.response?.data?.detail || error.message;
				throw new Error(message);
			}
		},

		async getUserIdByUsername(username) {
			return this.getAllPlayers()
				.then(() => {
					const user = this.playersList.find(p => p.nickname === username);
					if (!user) throw new Error("User not found");
					return user.id;
				});
		},

		getMyId() {
			return Number(localStorage.getItem("user_id"));
		},

		getMyUsername() {
			return localStorage.getItem("username");
		},

		setGameId(gameId) {
			localStorage.setItem("activeGameId", gameId);
		},

		register(user) {
			return AuthService.register(user)
				.catch((error) => {
					this.error = error.response?.data?.detail || "Registration failed.";
					throw error;
				});
		}
	},
});