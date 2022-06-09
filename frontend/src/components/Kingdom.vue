<template>
<div class="ui grid container">
    <div class="one column row">
      <div class="column">
        <div class="ui secondary pointing menu">
          <div class="item active">
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
          <div class="item">
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

            <!-- <div v-if="kingdom">
                <div>kingdom name: {{kingdom.kingdom.kingdomName}}</div>            
                <div>gold: {{kingdom.resources.gold.amount}}</div>
                <div>gold production: {{kingdom.resources.gold.goldProduction}}</div>
                <div>food: {{kingdom.resources.food.amount}}</div>
                <div>food production: {{kingdom.resources.food.foodProduction}}</div>
                <div>troops: {{army}}</div>
                <ul>
                <li v-for = "building in kingdom.buildings" :key="building.type">
                    {{building.type}}
                </li>
                </ul>
                <div>queue </div>
            </div> -->


    <div v-if="kingdom" class="one column row">
        <div class="column"> 
            <table class="ui table">
                <thead>
                    <th class="center aligned">Kingdom name</th>
                    <th class="center aligned">Gold</th>
                    <th class="center aligned">Gold production</th>
                    <th class="center aligned">Food</th>
                    <th class="center aligned">Food production</th>
                </thead>
                <tbody>
                    <tr>
                        <td class="center aligned">{{kingdom.kingdom.kingdomName}}</td>
                        <td class="center aligned">{{kingdom.resources.gold.amount}}</td>
                        <td class="center aligned">{{kingdom.resources.gold.goldProduction}}</td>
                        <td class="center aligned">{{kingdom.resources.food.amount}}</td>
                        <td class="center aligned">{{kingdom.resources.food.foodProduction}}</td>
                    </tr>

                </tbody>
            </table>
        </div>
    </div>

    <div class="two column row">
        <div class="column">
            <h3>Rename Kingdom</h3>
            <RenameKingdom />
        </div>

        <div class="column">
            <h3>Change Kingdom</h3>
            <KingdomList />
        </div>
    </div>

  </div>
</template>

<script>
import axios from "axios"
import RenameKingdom from '@/views/RenameKingdom'
import KingdomList from '@/components/KingdomList'

export default {
    name: "Kingdom",

    components: {
        RenameKingdom,
        KingdomList,
    },

    created() {
        console.log(this.$route)
        this.kingdomId = this.$route.params.kingdomId
    },
    data() {
        return{
            kingdomId: null,
            kingdom: null,
            /* todo get token from login */
            token: '',
            kingdomKey: localStorage.getItem('kingdomId'),
        }
    },
    methods:{
        tokenCreation(){
            this.token= 'Bearer '+ localStorage.getItem('token')
            return this.token
        }
    },
    computed: {
        army() {
            var army ={
                "knight": 0,
                "footman": 0,
                "phalanx": 0,
                "scout": 0,
                "trebuchet": 0,
                "diplomat": 0,
            }
            const knight = this.kingdom.troops.filter(x => x.type == 'KNIGHT')
            army.knight = knight.length
            const footman = this.kingdom.troops.filter(x => x.type == 'FOOTMAN')
            army.footman = footman.length
            const phalanx = this.kingdom.troops.filter(x => x.type == 'PHALANX')
            army.phalanx = phalanx.length
            const scout = this.kingdom.troops.filter(x => x.type == 'SCOUT')
            army.scout = scout.length
            const trebuchet = this.kingdom.troops.filter(x => x.type == 'TREBUCHET')
            army.trebuchet = trebuchet.length
            const diplomat = this.kingdom.troops.filter(x => x.type == 'DIPLOMAT')
            army.diplomat = diplomat.length
            return army
        }
    },

    mounted() {
        axios.get("/kingdoms/"+this.kingdomId,{'headers':{'Authorization': this.tokenCreation()}})
        .then((response) => this.kingdom = response.data)
        .catch((error) => console.log(error.message))
    },
}
</script>
