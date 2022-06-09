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
          <div class="item active">
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
</div>

  <div class="ui grid container">
    <div class="eight wide column">
      <h1>Battle</h1>
      <div>
        <img src="@\assets\battle.png" alt="" />
      </div><br>

      <div v-if="data.resolutionTime" class="ui success message">
        <i class="close icon" @click="closeSuccessMessage()"></i>
        <div class="header">Army is on the way!</div>
        <p>Battle will happen {{ timeConvert(data.resolutionTime) }}</p>
      </div>

      <div v-if="error" class="ui danger message">
        <i class="close icon" @click="closeErrorMessage()"></i>
        <div class="header">Error!</div>
        <p>{{ error }}</p>
      </div>

      <div class="battle-view">
        <form class="ui form" @submit.stop.prevent="onCreatePost" @click="post">
          <div class="three fields">
            <div class="field">
              <label>Your kingdom</label>
              <select
                class="ui search dropdown"
                required
                v-model="attackerKingdom"
              >
                <option value="" disabled selected>Select your kingdom</option>
                <option value="1">1</option>
              </select>
            </div>
            <div class="field">
              <label>Attack kingdom</label>
              <select
                class="ui search dropdown"
                required
                v-model="defenderKingdom"
              >
                <option value="" disabled selected>
                  Select kingdom to attack
                </option>
                <option value="2">2</option>
              </select>
            </div>
            <div class="field">
              <label>Type of Attack</label>
              <select
                class="ui search dropdown"
                required
                v-model="typeOfAttack"
              >
                <option value="" disabled selected>
                  Select type of attack
                </option>
                <option value="TROOP_ATTACK">Troop attack</option>
                <option value="DESTRUCTION_ATTACK">Destruction attack</option>
                <option value="SPY_ATTACK">Spy attack</option>
                <option value="TAKEOVER_ATTACK">Takeover attack</option>
              </select>
            </div>
          </div>
          <div class="three fields">
            <div class="field">
              <label>Phalanx </label>
              <img src="@\assets\phalanx-icon.png" alt="" />
              <input type="number" min="0" name="PHALANX" v-model="phalanx" />
            </div>
            <div class="field">
              <label>Footman </label>
              <img src="@\assets\footman-icon.png" alt="" />
              <input type="number" min="0" name="FOOTMAN" v-model="footman" />
            </div>
            <div class="field">
              <label>Knight </label>
              <img src="@\assets\knight-icon.png" alt="" />
              <input type="number" min="0" name="KNIGHT" v-model="knight" />
            </div>
          </div>
          <div class="four fields">
            <div class="field">
              <label>Scout </label>
              <img src="@\assets\scout-icon.png" alt="" />
              <input type="number" min="0" name="SCOUT" v-model="scout" />
            </div>
            <div class="field">
              <label>Trebuchet </label>
              <img src="@\assets\trebuchet-icon.png" alt="" />
              <input
                type="number"
                min="0"
                name="TREBUCHET"
                v-model="trebuchet"
              />
            </div>
            <div class="field">
              <label>Diplomat </label>
              <img src="@\assets\diplomat-icon.png" alt="" />
              <input type="number" min="0" name="DIPLOMAT" v-model="diplomat" />
            </div>
            <div class="field">
              <label>Settler </label>
              <img src="@\assets\settler-icon.png" alt="" />
              <input type="number" min="0" name="SETTLER" v-model="settler" />
            </div>
          </div>
          <br>
          <div class="field">
            <button class="big ui inverted secondary button" type="submit">
              Attack
            </button>
          </div>
        </form>
      </div>
    </div>
    <div class="eight wide column">
      <h1>Reports</h1>
      <div>
        <BattleHistory :key="data" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import BattleHistory from "@/components/BattleHistory.vue";

export default {
  name: "Battle",

  components: {
    BattleHistory,
  },

  data() {
    return {
      data: "",
      error: "",
      attackerKingdom: "",
      defenderKingdom: "",
      typeOfAttack: "",
      phalanx: 0,
      footman: 0,
      knight: 0,
      scout: 0,
      trebuchet: 0,
      diplomat: 0,
      settler: 0,
      render: 0,
      token: "",
      kingdomId: localStorage.getItem('kingdomId'),
    };
  },
  methods: {
    onCreatePost() {
      let troops = [
        { type: "PHALANX", quantity: this.phalanx },
        { type: "FOOTMAN", quantity: this.footman },
        { type: "KNIGHT", quantity: this.knight },
        { type: "SCOUT", quantity: this.scout },
        { type: "TREBUCHET", quantity: this.trebuchet },
        { type: "DIPLOMAT", quantity: this.diplomat },
        { type: "SETTLER", quantity: this.settler },
      ];

      axios
        .post(
          "/kingdoms/1/battles",
          {
            defenderKingdomId: this.defenderKingdom,
            battleType: this.typeOfAttack,
            troops: troops,
          },
          {
            headers:{
              Authorization: this.tokenCreation()
            }
          },
        )
        .then((response) => (this.data = response.data))
        .catch((error) => (this.error = error.response.data.error));
      //.catch((error) => console.log(error.response.data.error));

      this.phalanx = 0;
      this.footman = 0;
      this.knight = 0;
      this.scout = 0;
      this.trebuchet = 0;
      this.diplomat = 0;
      this.settler = 0;
    },
    timeConvert(value) {
      const milliseconds = value * 1000;
      const dateObject = new Date(milliseconds);
      return dateObject.toLocaleString();
    },
    closeSuccessMessage() {
      this.data = "";
    },
    closeErrorMessage() {
      this.error = "";
    },
    tokenCreation(){
            this.token= 'Bearer '+ localStorage.getItem('token')
            return this.token
    }
  },
};
</script>

<style scoped>
.battle-view {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>