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
                <div class="item active">
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


        <div class="three column row" id="troops">
          <div class="three wide column">
            <TroopList />
          </div>
          <div class="seven wide column">
            <TroopInfo />
          </div>
          <div class="six wide column">
            <TroopsQueue />
          </div>
        </div>

        <div class="two column row" id="troops">
          <div class="column">
            <TroopTraining />
          </div>
          <div class="column">
            <TroopUpgrade />
          </div>
        </div>

    </div>

</template>



<script>
import TroopInfo from "@/components/TroopInfo.vue";
import TroopList from "@/components/TroopList.vue";
import TroopTraining from "@/components/TroopTraining.vue";
import TroopUpgrade from "@/components/TroopUpgrade.vue";
import TroopsQueue from "@/components/TroopsQueue.vue"

export default {
  name: "Troops",
  components: {
    TroopInfo,
    TroopList,
    TroopTraining,
    TroopUpgrade,
    TroopsQueue
  },
  data() {
    return {
      kingdomId: localStorage.getItem('kingdomId'),
    };
    
  },
};
</script>

<style scoped>
input {
  size: 50px;
}

.button {
  cursor: pointer;
}

.academyLevel {
  font-size: 18px;
}
</style>
