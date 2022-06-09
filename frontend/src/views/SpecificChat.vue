<template>

    <div clas="root" v-if="data">
        
        <p>{{ data.subject }}</p>

    </div>

</template>

<script>
export default {
    name: 'SpecificChat',
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
  mounted(chatId) {
    axios
    .get("/chats/"+chatId,{'headers':{'Authorization': this.tokenCreation()}})
    .then((response) => (this.data = response.data))
    .catch((error) => console.log(error.message))
  }
}
</script>

<style>

</style>