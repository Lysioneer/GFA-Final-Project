<template>
  <div class="ui grid">
    <div class="one column row">
      <div class="column">
        <div>
          <BattleResult
            v-show="isModalVisible"
            @close="closeModal"
            :result="result"
          />
        </div>
        
        <table class="ui small single line selectable table">
          <thead>
            <tr>
              <th>Battle type</th>
              <th>Battle time</th>
              <th class="right aligned">Report</th>
            </tr>
          </thead>
          <tbody>
            <tr class="hidden" v-for="battle in data" :key="battle" v-bind:class="battle.attackerKingdomId == this.kingdomId ? null : 'red'">
              <td data-label="Battle typ">
                <i v-if="battle.attackerKingdomId == this.kingdomId" class="bolt icon"></i>
                <i v-else class="shield alternate icon"></i>
                {{battle.battleType}}</td>
              <td data-label="Battle time">{{ timeConvert(battle.battleTime) }}</td>
              <td class="right aligned" data-label="Report"><i @click="getBattleResults(battle.id)" class="file alternate icon"></i></td>
            </tr>
          </tbody>
        </table>

        <button @click="showMore" class="big ui inverted secondary button" id="loadReports">Load more</button>

      <!-- <div>
        {{data}}
      </div> -->

      </div>
    </div>
  </div>
</template> 

<script>
import axios from "axios";
import BattleResult from "@/components/BattleResult.vue";

export default {
  name: "BattleHistory",
  components: {
    BattleResult,
  },

  data() {
    return {
      data: [],
      kingdomId: localStorage.getItem('kingdomId'),
      result: "",
      battleResult: "",
      isModalVisible: false,
    };
  },

  methods: {
    timeConvert(value) {
      const milliseconds = value * 1000;
      const dateObject = new Date(milliseconds);
      return dateObject.toLocaleString();
    },
    // showModal(val) {
    //   this.isModalVisible = true;
    //   this.result = val;
    // },
    closeModal() {
      this.isModalVisible = false;
      this.result = "";
    },
    getBattleResults(battleId) {
      axios
        .get(`/kingdoms/${this.kingdomId}/battles/` + battleId, {
          headers :{
            Authorization: this.tokenCreation()
          }
        })
        .then((response) => (this.result = response.data))
        .catch((error) => console.log(error.message));

      this.isModalVisible = true;

      return this.result;
    },
    showMore() {
      let newRows = document.querySelectorAll("tr.hidden");
        for (let i = 0; i < Math.min(15, newRows.length); i++){
          newRows[i].classList.remove("hidden");
      }
    },
    tokenCreation(){
            this.token= 'Bearer '+ localStorage.getItem('token')
            return this.token
        }
  },

  mounted() {
    axios
      .get(`/battles/${this.kingdomId}`, {
          headers: {
            Authorization: this.tokenCreation()
          }
      })
      .then((response) => (this.data = response.data.pastBattles))
      .finally(() => {
          let rows = document.getElementsByTagName("tr");
          for (let i = 0; i < Math.min(15, rows.length); i++){
            rows[i].classList.remove("hidden");
          } 
      })
      .catch((error) => console.log(error.message));
  },
};
</script>

<style scoped>
div {
  padding: 1px 1px;
}
i {
  cursor: pointer;
}
.blue {
  color: blue;
}
.red {
  color: red;
}
.hidden {
  display: none;
}
</style>