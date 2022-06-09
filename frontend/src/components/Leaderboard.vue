<template>

    <div class="ui grid container">
        <div class="one column row">
            <div class="column">

                <div class="ui secondary pointing menu">
                <div class="item">
                    <router-link :to="{name: 'Kingdom', params:{kingdomId: this.kingdomId}}">Kingdom</router-link>
                </div>
                <div class="item">
                    <router-link to="/buildings">Buildings</router-link>
                </div>
                <div class="item">
                    <router-link to="/kingdoms/1/troops">Troops</router-link>
                </div>
                <div class="item">
                    <router-link to="/battle">Battle</router-link>
                </div>
                <div class="item active">
                    <router-link to="/leaderboard">Leaderboard</router-link>
                </div>
                <div class="item">
                    <router-link to="/chats">Chats</router-link>
                </div>
                <div class="right menu">
                    <div class="ui item">
                    <a href="/" @click="logout">Logout</a>
                    </div>
                </div>
                </div>
            </div>
        </div>
        <div class="one column row">
            <div class="column">
                    <table class="ui large selectable table">
                        <thead>
                            <th>Player</th>
                            <th>Kingdoms</th>
                            <th>Score</th>
                        </thead>
                        <tbody>
                            <tr v-for="score in leaderboard" :key="score.name">
                                <td>{{score.userName}}</td>
                                <td>{{score.kingdomAmount}}</td>
                                <td>{{score.score}}</td>
                            </tr>
                        </tbody>
                    </table>
            </div>
        </div>
    </div>
</template>

<script>
import axios from "axios"

export default {
    name: "leaderboard",
    data(){
        return{
            leaderboard: [],
            kingdomId: localStorage.getItem('kingdomId'),
        }
    },
    mounted() {
        axios.get('/leaderboard')
        .then((response) => this.leaderboard = response.data)
        .catch((error) => console.log(error.message))
    },
}
</script>