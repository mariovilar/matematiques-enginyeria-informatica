import axios from "axios";

class AuthService {
	login(user) {
		return this.getAxiosInstance().post("/api/token/", {
			username: user.username,
			password: user.password,
		})
	};

	refresh(refreshToken) {
		return Promise.resolve(
			JSON.stringify({
				access: "mockAccessToken",
			})
		);
	}

	logout() {
		localStorage.removeItem("access");
		localStorage.removeItem("refresh");
		localStorage.removeItem("activeGameId");
		localStorage.removeItem("username")
		localStorage.removeItem("user_id");
		localStorage.removeItem("admin")
	}

	getAccessToken() {
		return localStorage.getItem("access");
	}

	getRefreshToken() {
		return localStorage.getItem("refresh");
	}

	isLoggedIn() {
		return !!localStorage.getItem("access");
	}

	getAllPlayers() {
		return this.getAxiosInstance().get("/api/v1/players/");
	}

	register(user) {
		return axios.post("/api/v1/user/", {
			username: user.username,
			email: user.email,
			password: user.password,
			password2: user.password2,
		});
	}

	getAxiosInstance() {
		const apiUrl = import.meta.env.VITE_API_URL;
		const instance = axios.create({
			baseURL: apiUrl,
			headers: {
				Authorization: `Bearer ${this.getAccessToken()}`,
			},
		});

		instance.interceptors.response.use(
			(response) => response,
			async (error) => {
				if (error.response.status === 401 && this.isLoggedIn()) {
					try {
						const response = await this.refresh(this.getRefreshToken());
						localStorage.setItem("access", response.data.access);
						error.config.headers["Authorization"] =
							"Bearer " + response.data.access;
						return axios.request(error.config);
					} catch (err) {
						this.logout();
					}
				}
				return Promise.reject(error);
			}
		);

		return instance
	}
}

export default new AuthService();