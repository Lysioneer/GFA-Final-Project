<template>
    <div class="tables">
      <div class="troopList">
        <h3>Your troops</h3>
        <!-- <label class="troopListLabel">Your troops</label>         -->
        <div v-if="troopList.length > 0">
          <table class="ui small table">
            <thead>
              <tr>
                <th></th>
                <th class="center aligned">Type</th>
                <th class="center aligned">Amount</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="troopValue in troopList" :key="troopValue">
                <td class="center aligned"><img v-if="troopValue.type === 'Phalanx'" src="@\assets\phalanx-icon.png"/>
                <img v-if="troopValue.type === 'Footman'" src="@\assets\footman-icon.png"/>
                <img v-if="troopValue.type === 'Knight'" src="@\assets\knight-icon.png"/>
                <img v-if="troopValue.type === 'Scout'" src="@\assets\scout-icon.png"/>
                <img v-if="troopValue.type === 'Trebuchet'" src="@\assets\trebuchet-icon.png"/>
                <img v-if="troopValue.type === 'Diplomat'" src="@\assets\diplomat-icon.png"/>
                <img v-if="troopValue.type === 'Settler'" src="@\assets\settler-icon.png"/></td>
                <td class="center aligned">{{ troopValue.type }}</td>
                <td class="center aligned">{{ troopValue.amount }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <button v-else class="ui inverted secondary button" @click="refreshTroopList">Refresh</button>
      </div>
    </div>
</template>

<script>
import axios from "axios";

export default {
  name: "TroopList",
  data() {
    return {
      troopList: [],
      timer: '200000',
      token: ""
    };
  },

  methods: {
    tokenCreation() {
      this.token = "Bearer " + localStorage.getItem("token");
      return this.token;
    },
    async refreshTroopList() {
      await axios
        .get("/kingdoms/1/troopList",{'headers':{'Authorization': this.tokenCreation()}})
        .then((response) => {
          this.troopList = response.data;
        })
        .catch((error) => console.log(error.message));
    },
  },

  created(){
      axios
      .get("/kingdoms/1/troopList",{'headers':{'Authorization': this.tokenCreation()}})
      .then((response) => {
        this.troopList = response.data;
      })
      .catch((error) => console.log(error.message));

      this.timer = setInterval(this.refreshTroopList, 30000)
  }
};
</script>

<style scoped>
/* .tables{
  font-size: 10px;
} */

</style>