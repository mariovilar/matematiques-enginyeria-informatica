<script setup>
import { ref, onMounted } from "vue";
import { useAuthStore } from "../store/authStore";
import { useGameStore } from "../store/index.js";
import { useRouter } from "vue-router";

const store = useGameStore();
const authStore = useAuthStore();
const router = useRouter();

const username = ref("");
const leaderboard = ref([]);

onMounted(async () => {
    // Get the leaderboard data
    username.value = authStore.username;
    leaderboard.value = await store.getLeaderboard();
});

const backToHome = () => {
    router.push("/");
};
</script>

<template>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Leaderboard</h1>

        <div v-for="(section, name) in leaderboard" :key="name" class="mb-5">
            <h2 class="text-capitalize">{{ name }} Ranking</h2>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Nickname</th>
                        <th>Games played</th>
                        <th>Wins</th>
                        <th>Score (%)</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(player, index) in section" :key="player.nickname"
                        :class="{ 'table-success': player.nickname == username }">
                        <td>{{ index + 1 }}</td>
                        <td>{{ player.nickname }}</td>
                        <td>{{ player.total_games }}</td>
                        <td>{{ player.wins }}</td>
                        <td>{{ player.score }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <button class="btn btn-primary btn-block mt-4" @click="backToHome">
            Back to main menu
        </button>
    </div>
</template>

<style scoped>
.btn-block {
    width: 100%;
    max-width: 250px;
    display: block;
    margin-bottom: 2rem;
}
</style>