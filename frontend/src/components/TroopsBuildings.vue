<template>
  <div>
  </div>
  <div>
    {{ getAcademy }}      
    <div v-if="academy.length > 0" class="academyLevel">
      Academy level : {{ academy[0].level }}
    </div>
    <div v-else>
      <p>Build Academy first or click find academy button!</p>
      <button class="ui inverted secondary button" @click="findAcademy">Find Academy</button>  
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "TroopsBuildings",
  data() {
    return {
      buildings: [],
      academy: [],
      token: ""
    };
  },
  methods: {
    tokenCreation() {
      this.token = "Bearer " + localStorage.getItem("token");
      return this.token;
    },
    findAcademy() {
      var found = this.buildings.filter(function (item) {
        return item.type === "academy";
      });
      this.academy = found;
    },
  },
  created() {
    axios
      .get("/kingdoms/1/buildings",{'headers':{'Authorization': this.tokenCreation()}})
      .then((response) => (this.buildings = response.data.kingdomBuildings))
      .catch((error) => console.log(error.message));
  },
  mounted() {
    axios
      .get("/kingdoms/1/buildings",{'headers':{'Authorization': this.tokenCreation()}})
      .then((response) => (this.buildings = response.data.kingdomBuildings))
      .catch((error) => console.log(error.message));
  },
  computed: {
    getAcademy() {
      var found = this.buildings.filter(function (item) {
        return item.type === "academy";
      });
      this.academy = found;
    },
  },
};
</script>
