<template>
    <div v-if="kingdomList" class="ui grid container">
        <div class="sixteen wide column">
                <div class="ui grid">
                <div class="one column row">
                    <div class="column">
                        <table class="ui selectable table">
                            <thead>
                                <th>
                                    Select Kingdom:
                                </th>
                            </thead>
                            <tbody>
                                <tr v-for = "kingdom in kingdomList.kingdoms" :key="kingdom.id" v-bind:kingdom="kingdom">
                                    <td>
                                        <router-link :to="{name: 'Kingdom', params:{kingdomId: kingdom.id}}"><button @click="saveKingdomId(kingdom.id)" class="ui inverted secondary button">{{kingdom.kingdomName}}</button></router-link>
                                        <!-- <router-link to="/home"><button @click="saveKingdomId(kingdom.id)" class="big ui inverted secondary button">{{kingdom.kingdomName}}</button></router-link> -->
                                    </td>
                                </tr>
                            </tbody>    
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div v-else>
        <RegisterKingdom />
    </div>


<!-- <div>
    <div v-if="kingdomList">
            <ul>
                <KingdomInfo v-for = "kingdom in kingdomList.kingdoms" :key="kingdom.id" v-bind:kingdom="kingdom"></KingdomInfo>
            </ul>
    </div>
</div> -->


</template>

<script>
import axios from "axios"
import KingdomInfo from "./KingdomInfo.vue"


export default {
    name: "KingdomList",
    components: {
        KingdomInfo,
    },
    data() {
        return{
            kingdomList: [],
            token: '',
        } 
    },
    methods:{
        tokenCreation(){
            this.token= 'Bearer '+ localStorage.getItem('token')
            return this.token
        },
        saveKingdomId(id){
            localStorage.setItem('kingdomId', id)
        }
    },

    mounted() {
        axios.get("/identify",{'headers':{'Authorization': this.tokenCreation()}})
        .then((response) => this.kingdomList = response.data)
        .catch((error) => console.log(error.message))
    },
}
</script>

<style scoped>
h1 {
    padding: 20px;
}
.table {
    text-align: center;
}
button {
    min-width: 200px;
}
</style>
