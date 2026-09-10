<template>
  <router-view></router-view>
</template>

<script>
import axios from "axios";
const apiUrl = import.meta.env.VITE_API_URL;

export default {
  name: "App",
  beforeCreate() {
    axios.defaults.baseURL = apiUrl;
  },
};
</script>

<style>
/* Global styles can be added here */
</style>
