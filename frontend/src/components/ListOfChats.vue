<template class="ListOfChats">

  <div id="root" v-if="data">
    <ul v-for="chat in data" :key="chat.subject">

      <li>
        <router-link :to="{name: 'SpecificChat', params:{chatId: chat.id}}">{{ chat.subject }}</router-link>
      </li>
      
    </ul>
  </div>

</template>

<script>

import axios from "axios"

export default {
  name: 'ListOfChats',
  data() {
    return {
      data: null,
      token: ""
    }
  },
  methods: {
    tokenCreation() {
      this.token = "Bearer " + localStorage.getItem("token");
      return this.token;
    },
  },
  mounted() {
    axios
    .get("/chats",{'headers':{'Authorization': this.tokenCreation()}})
    .then((response) => (this.data = response.data))
    .catch((error) => console.log(error.message))
  }
}
</script>

<style>

  #root {

    text-align: left;
    
  }

</style>