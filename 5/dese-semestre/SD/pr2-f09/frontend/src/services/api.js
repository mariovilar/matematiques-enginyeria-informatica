import AuthService from "@/services/auth.js";

export default {
  getAvailableShips() {
    return [
      {
        type: 1,
        isVertical: true,
        size: 1,
      },
      {
        type: 2,
        isVertical: true,
        size: 2,
      },
      {
        type: 3,
        isVertical: true,
        size: 3,
      },
      {
        type: 4,
        isVertical: true,
        size: 4,
      },
      {
        type: 5,
        isVertical: true,
        size: 5,
      },
    ];
  },

  // USERS
  getUser(userId) {
    return AuthService.getAxiosInstance().get(`/api/v1/user/${userId}`);
  },

  // GAMES
  getAllGames() {
    return AuthService.getAxiosInstance().get("/api/v1/games/");
  },

  createGame(data) {
    return AuthService.getAxiosInstance().post("/api/v1/games/", data);
  },

  getGame(gameId) {
    return AuthService.getAxiosInstance().get(`/api/v1/games/${gameId}`);
  },

  updateGame(gameId, data) {
    return AuthService.getAxiosInstance().put(`/api/v1/games/${gameId}`, data);
  },

  modifyGame(gameId, data) {
    return AuthService.getAxiosInstance().patch(`/api/v1/games/${gameId}`, data);
  },

  deleteGame(gameId) {
    return AuthService.getAxiosInstance().delete(`/api/v1/games/${gameId}`);
  },

  // PLAYERS and GAMES
  getPlayerInGame(gameId, playerId) {
    return AuthService.getAxiosInstance().get(`/api/v1/games/${gameId}/players/${playerId}`);
  },

  getPlayersInGame(gameId) {
    return AuthService.getAxiosInstance().get(`/api/v1/games/${gameId}/players/`);
  },

  addPlayerToGame(gameId, data) {
    return AuthService.getAxiosInstance().post(`/api/v1/games/${gameId}/players/`, data);
  },

  updatePlayerInGame(gameId, playerId, data) {
    return AuthService.getAxiosInstance().put(`/api/v1/games/${gameId}/players/${playerId}`, data);
  },

  removePlayerFromGame(gameId, playerId) {
    return AuthService.getAxiosInstance().delete(`/api/v1/games/${gameId}/players/${playerId}`);
  },

  // SHIP and PLAYERS and GAMES
  getShipsByPlayerInGame(gameId, playerId) {
    return AuthService.getAxiosInstance().get(
      `/api/v1/games/${gameId}/players/${playerId}/vessels/`
    );
  },

  addShipToPlayerInGame(gameId, playerId, data) {
    return AuthService.getAxiosInstance().post(
      `/api/v1/games/${gameId}/players/${playerId}/vessels/`,
      data
    );
  },

  getShipByPlayerInGame(gameId, playerId, vesselId) {
    return AuthService.getAxiosInstance().get(
      `/api/v1/games/${gameId}/players/${playerId}/vessels/${vesselId}`
    );
  },

  updateShipInPlayerInGame(gameId, playerId, vesselId, data) {
    return AuthService.getAxiosInstance().put(
      `/api/v1/games/${gameId}/players/${playerId}/vessels/${vesselId}`,
      data
    );
  },

  modifyShipInPlayerInGame(gameId, playerId, vesselId, data) {
    return AuthService.getAxiosInstance().patch(
      `/api/v1/games/${gameId}/players/${playerId}/vessels/${vesselId}`,
      data
    );
  },

  removeShipFromPlayerInGame(gameId, playerId, vesselId) {
    return AuthService.getAxiosInstance().delete(
      `/api/v1/games/${gameId}/players/${playerId}/vessels/${vesselId}`
    );
  },

  // SHOTS nad PLAYERS and GAMES
  getShotsByPlayerInGame(gameId, playerId) {
    return AuthService.getAxiosInstance().get(
      `/api/v1/games/${gameId}/players/${playerId}/shots/`
    );
  },

  addShotToPlayerInGame(gameId, playerId, data) {
    return AuthService.getAxiosInstance().post(
      `/api/v1/games/${gameId}/players/${playerId}/shots/`,
      data
    );
  },

  getShotByPlayerInGame(gameId, playerId, shotId) {
    return AuthService.getAxiosInstance().get(
      `/api/v1/games/${gameId}/players/${playerId}/shots/${shotId}`
    );
  },

  // BOARDS and PLAYERS and GAMES
  getBoardsByPlayerInGame(gameId, playerId) {
    return AuthService.getAxiosInstance().get(
      `/api/v1/games/${gameId}/players/${playerId}/board/`
    );
  },

  getBoardInPlayerInGame(gameId, playerId, boardId) {
    return AuthService.getAxiosInstance().get(
      `/api/v1/games/${gameId}/players/${playerId}/board/${boardId}`
    );
  },

  // LEADERBOARD
  getLeaderboard() {
    return AuthService.getAxiosInstance().get(`/api/v1/leaderboard/`)
  }
};