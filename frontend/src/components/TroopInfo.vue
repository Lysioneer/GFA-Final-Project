<template>
  <div class="troopInfo">
    <h3>Troop info</h3>
    <!-- <label>Troop Info</label>     -->
    <div v-if="unitLevelList.length > 0">
      <div class="troopInfoTable">
        <table class="ui small table">
          <thead>
            <tr>
              <th></th>
              <th class="center aligned">Type</th>
              <th class="center aligned">Level</th>
              <th class="center aligned">Battle abilities</th>
              <th class="center aligned">Attack</th>
              <th class="center aligned">Defence</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="unit in unitLevelList" :key="unit">
              <td class="center aligned">
                <img :src="require(`@/assets/${unit.type}-icon.png`)" />
              </td>
              <td class="center aligned">{{ unit.type }}</td>
              <td class="center aligned">{{ unit.level }}</td>
              <td class="center aligned">{{ unit.ability }}</td>
              <td class="center aligned">{{ unit.attack }}</td>
              <td class="center aligned">{{ unit.defence }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <button v-else class="ui inverted secondary button" @click="refreshUnitLevelList">Refresh</button>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "TroopInfo",
  data() {
    return {
      unitLevelList: [],
      timer: '200000',
      token: ""
    };
  },

  methods: {
    tokenCreation() {
      this.token = "Bearer " + localStorage.getItem("token");
      return this.token;
    },
    async refreshUnitLevelList() {
      await axios
        .get("/kingdoms/1/unitLevel",{'headers':{'Authorization': this.tokenCreation()}})
        .then((response) => {
          this.unitLevelList = response.data;
        })
        .catch((error) => console.log(error.message));
    },
  },

  created() {
    axios
      .get("/kingdoms/1/unitLevel",{'headers':{'Authorization': this.tokenCreation()}})
      .then((response) => {
        this.unitLevelList = response.data;
      })
      .catch((error) => console.log(error.message));

      this.timer = setInterval(this.refreshUnitLevelList, 30000)
  },
};
</script>

<style scoped>
/* .infoHead,
.infoBody,
.troopInfo {
  font-size: 10px;
} */
</style>