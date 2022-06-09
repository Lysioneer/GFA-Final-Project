<template>
    <!-- <li> -->
        <!-- <router-link :to="{name: 'Kingdom', params:{kingdomId: kingdom.id}}">{{kingdom.kingdomName}} </router-link> -->
       <!--  <p v-if="kingdom">gold: {{kingdomGold(kingdom.id).resources.gold.amount}} food:{{kingdomGold(kingdom.id)}}</p> -->
    <!-- </li> -->

    <router-link :to="{name: 'Kingdom', params:{kingdomId: kingdom.id}}">{{kingdom.kingdomName}} </router-link>

</template>
<script>

import axios from "axios"

export default {
   name: 'KingdomInfo',
   props:['kingdom'],
    data() {
        return{
            currentKingdom: "",
            token: '',
        }
    },
        methods:{
        tokenCreation(){
            this.token= 'Bearer '+ localStorage.getItem('token')
            return this.token
        },
        kingdomGold(kingdomId){
            axios.get("/kingdoms/"+ localStorage.getItem('kingdomId') ,{'headers':{'Authorization': this.tokenCreation()}})
        .then((response) => this.currentKingdom = response.data)
        .catch((error) => console.log(error.message))
        return this.currentKingdom
        },
    },
}
</script>