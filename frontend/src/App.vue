<template>
<div>
  <router-view :key="this.$route.path" />
</div>


  <!-- <div id="nav">
    <router-link to="/renamekingdom">Rename Kingdom</router-link>
  </div>
  <div>
    <router-link to="/kingdom">Kingdom</router-link>
  </div>
  <div class="ui menu">
    <div class="header item">
      <router-link to="/">Home </router-link>
    </div>
    <div class="item">
      <router-link to="/kingdomList">kingdomList  </router-link>
    </div>
    <div class="item">
      <router-link to="/chats"> Chats </router-link>
    </div>
    <div class="item">
      <router-link to="/leaderboard">leaderboard  </router-link>
    </div>
    <div class="item">
      <router-link to="/kingdoms/1/troops">Troops</router-link>
    </div>
    <div class="item">
      <router-link to="/buildings">Buildings</router-link>
    </div>
    <div class="item">
      <router-link to="/battle">Battle</router-link>
    </div>
    <div class="item right menu">
      <a href="#" @click="logout">Logout</a>
    </div>
    <div class="item right menu">
      <router-link to="/signup">Sign Up</router-link>
    </div>
    <div class="item right menu">
      <router-link to="/login">Login</router-link>
    </div>
  </div> -->




</template>

<script>
import { mapActions} from 'vuex'
import Login from './views/Login.vue'

export default {
  name: 'app',
  methods: mapActions(["logout"]),
  components: {Login,}
};
</script>


<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 10px;
}
</style>