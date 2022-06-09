<template>
  <div class="queue">
    <div class="ui five wide column">
      <div v-if="trainingQueue.length == 0" class="five wide column">
        <h3>You are currently not training any troops</h3>
        <button class="ui inverted secondary button" @click="refreshQueue">
          Refresh Queue
        </button>
      </div>

      <div v-else>
        <div class="two wide column">
          <div v-if="trainingQueue.length > 0">
            <h3>Training queue</h3>
            <!-- <label>Training queue</label> -->
            <div class="one column row">
              <div class="troopQueue">
                <table class="ui small table">
                  <thead>
                    <tr>
                      <th class="center aligned">Id</th>
                      <th class="center aligned">Type</th>
                      <th class="center aligned">Time</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="troop in trainingQueue" :key="troop">
                      <td class="center aligned">{{ troop.id }}</td>
                      <td class="center aligned">{{ troop.type }}</td>
                      <td class="center aligned">
                        {{ timeConverter(troop.time) }}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <div>
          <button
            @click="deleteAllInQueue"
            class="ui inverted secondary button"
          >
            Cancel all training
          </button>
        </div>
        <div>
          <button class="ui button" @click="toDelete = phalanx">
            <img src="@\assets\phalanx-icon.png" /><br />
            Phalanx
            <!-- <a class="item">Phalanx</a> -->
          </button>
          <button class="ui button" @click="toDelete = footman">
            <img src="@\assets\footman-icon.png" /><br />
            Footman
            <!-- <a class="item">Footman</a> -->
          </button>
          <button class="ui button" @click="toDelete = knight">
            <img src="@\assets\knight-icon.png" /><br />
            Knight
            <!-- <a class="item">Knight</a> -->
          </button>
          <button class="ui button" @click="toDelete = scout">
            <img src="@\assets\scout-icon.png" /><br />
            Scout
            <!-- <a class="item">Scout</a> -->
          </button>
          <button class="ui button" @click="toDelete = trebuchet">
            <img src="@\assets\trebuchet-icon.png" /><br />
            Trebuchet
            <!-- <a class="item">Trebuchet</a> -->
          </button>
          <button class="ui button" @click="toDelete = diplomat">
            <img src="@\assets\diplomat-icon.png" /><br />
            Diplomat
            <!-- <a class="item">Diplomat</a> -->
          </button>
          <button class="ui button" @click="toDelete = settler">
            <img src="@\assets\settler-icon.png" /><br />
            Settler
            <!-- <a class="item">Settler</a> -->
          </button>
          <button class="ui button" @click="deleteByType">Cancel by type</button>
          <div v-if="toDelete">
            <p>Selected type of troops:</p>
            <p>
              {{ toDelete }}
              <img :src="require(`@/assets/${toDelete}-icon.png`)" />
            </p>
          </div>
        </div>

        <div class="one wide column">
          <div>
            <input
              min="0"
              type="number"
              placeholder="amount"
              :amount="amount"
              @input="inputAmount"
            />
            <!-- {{ amount }} -->
            <br />
            <button @click="deleteAmount" class="ui inverted secondary button">
              Cancel selected amount of troops
            </button>
          </div>
        </div>
      </div>

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
</template>

<script>
import axios from "axios";

export default {
  name: "TroopsQueue",
  data() {
    return {
      amount: 0,
      trainingQueue: [],
      toDelete: null,
      type: "phalanx",
      phalanx: "phalanx",
      knight: "knight",
      footman: "footman",
      scout: "scout",
      trebuchet: "trebuchet",
      diplomat: "diplomat",
      settler: "settler",
      timer: "200000",
      error: "",
      message: "",
      token: "",
    };
  },
  methods: {
    tokenCreation() {
      this.token = "Bearer " + localStorage.getItem("token");
      return this.token;
    },
    inputAmount($event) {
      this.amount = $event.target.value;
    },
    timeConverter(value) {
      const milliseconds = value * 1000;
      const dateObject = new Date(milliseconds);
      return dateObject.toLocaleString();
    },
    async refreshQueue() {
      await axios
        .get("/kingdoms/1/queue", {
          headers: { Authorization: this.tokenCreation() },
        })
        .then((response) => {
          this.trainingQueue = response.data;
        })
        .catch((error) => console.log(error.message));
    },
    deleteAllInQueue() {
      axios
        .delete("/kingdoms/1/troops", {
          headers: { Authorization: this.tokenCreation() },
        })
        .then((response) => (this.message = response.data))
        .catch((error) => (this.error = error.response.data.error));
    },
    deleteByType() {
      axios({
        method: "delete",
        url: `/kingdoms/1/troops`,
        headers: { Authorization: this.tokenCreation() },
        data: {
          type: this.toDelete,
        },
      })
        .then((response) => (this.message = response.data))
        .catch((error) => (this.error = error.response.data.error));
    },
    deleteAmount() {
      axios({
        method: "delete",
        url: `/kingdoms/1/troops`,
        headers: { Authorization: this.tokenCreation() },
        data: {
          amount: parseInt(this.amount),
        },
      })
        .then((response) => (this.message = response.data))
        .catch((error) => (this.error = error.response.data.error));
    },
    closeErrorMessage() {
      this.error = "";
    },
    closeSucessMessage() {
      this.message = "";
    },
  },
  created() {
    axios
      .get("/kingdoms/1/queue", {
        headers: { Authorization: this.tokenCreation() },
      })
      .then((response) => {
        this.trainingQueue = response.data;
      })
      .catch((error) => console.log(error.message));

    this.timer = setInterval(this.refreshQueue, 30000);
  },
};
</script>

<style scoped>
/* .queue {
  font-size: 10px;
} */
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