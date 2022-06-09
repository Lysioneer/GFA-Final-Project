<template>
  <div>
    <h3>Upgrade Troop Level</h3>
    <div class="troopUpgrade">
      <div class="ui grid">
        <div class="four wide column">
          <div>
            <div >
              <button class="ui button" @click="type = phalanx">
                <img src="@\assets\phalanx-icon.png" /><br>
                Phalanx
                <!-- <a class="item">Phalanx</a> -->
              </button>
            </div>
            <div >
              <button class="ui button" @click="type = footman">
                <img src="@\assets\footman-icon.png" /><br>
                Footman
                <!-- <a class="item">Footman</a> -->
              </button>
            </div>
            <div >
              <button class="ui button" @click="type = knight">
                <img src="@\assets\knight-icon.png" /><br>
                Knight
                <!-- <a class="item">Knight</a> -->
              </button>
            </div>
            <div >
              <button class="ui button" @click="type = scout">
                <img src="@\assets\scout-icon.png" /><br>
                Scout
                <!-- <a class="item">Scout</a> -->
              </button>
            </div>
            <div >
              <button class="ui button" @click="type = trebuchet">
                <img src="@\assets\trebuchet-icon.png" /><br>
                Trebuchet
                <!-- <a class="item">Trebuchet</a> -->
              </button>
            </div>
            <div >
              <button class="ui button" @click="type = diplomat">
                <img src="@\assets\diplomat-icon.png" /><br>
                Diplomat
                <!-- <a class="item">Diplomat</a> -->
              </button>
            </div>
            <div >
              <button class="ui button" @click="type = settler">
                <img src="@\assets\settler-icon.png" /><br>
                Settler
                <!-- <a class="item">Settler</a> -->
              </button>
            </div>
          </div>
        </div>
        <div class="twelve wide stretched column">
          <div class="ui segment">
            <div>
              <p>Selected type of troops: </p>
              <p>{{ type }} <img :src="require(`@/assets/${type}-icon.png`)" /></p>
              <div>
                <button class="ui inverted secondary button" @click="upgradeTroops">Upgrade</button>
              </div>
              <br>
              <TroopsBuildings />
              <div v-if="error" class="ui danger message">
                <i class="close icon" @click="closeErrorMessage()"></i>
                <div class="header">Error!</div>
                <p>{{ error }}</p>
              </div>
              <div v-if="message" class="ui positive message">
                <i class="close icon" @click="closeSucessMessage()"></i>
                <div class="header">Success!</div>
                <p>{{ message }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import TroopsBuildings from "./TroopsBuildings.vue";

export default {
  name: "TroopUpgrade",
  components: {
    TroopsBuildings,
  },
  data() {
    return {
      type: "phalanx",
      phalanx: "phalanx",
      knight: "knight",
      footman: "footman",
      scout: "scout",
      trebuchet: "trebuchet",
      diplomat: "diplomat",
      settler: "settler",
       error: "",
      message: ""
    };
  },
  methods: {
    tokenCreation() {
      this.token = "Bearer " + localStorage.getItem("token");
      return this.token;
    },
    upgradeTroops() {
      axios({
        method: "put",
        url: `/kingdoms/1/troops`,
        headers: {'Authorization': this.tokenCreation()},
        data: {
          type: this.type,
        },
      })
      .then((response) => (this.message = response.data.status))
      .catch((error) => (this.error = error.response.data.error));
    },
    closeErrorMessage() {
      this.error = "";
    },
    closeSucessMessage() {
      this.message = "";
    },    
  },
};
</script>

<style scoped>
.queue,
.troopTraining {
  font-size: 10px;
}
.button {
  cursor: pointer;
  display: inline;
  height: 55px;
  width: 50px;
  padding: 0;
  margin: 1px;
  vertical-align: middle;
  width: 104px;
}
.item {
  display: inline;
}
</style>