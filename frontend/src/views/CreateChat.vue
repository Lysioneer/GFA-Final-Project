<template>

    <div class="ui grid container">
        <div class="one column row">
            <div class="column">

                <div class="ui secondary pointing menu">
                <div class="item">
                    <router-link to="/home">Kingdom</router-link>
                </div>
                <div class="item">
                    <router-link to="/buildings">Buildings</router-link>
                </div>
                <div class="item">
                    <router-link to="/kingdoms/1/troops">Troops</router-link>
                </div>
                <div class="item">
                    <router-link to="/battle">Battle</router-link>
                </div>
                <div class="item">
                    <router-link to="/leaderboard">Leaderboard</router-link>
                </div>
                <div class="item active">
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


        <div class="one column row">
          <div class="column">

            <div id="root">

                <router-link to="/chats"> Cancel </router-link>

                <div id="errorMessage">
                  <p>{{this.error}}</p>
                </div>

                <div class="chatForm">
                  <form v-on:submit.prevent="chatCreateForm">
                    
                    <div v-for="(input, index) in allMembers" :key="`usernameInput-${index}`">

                      <input v-model="input.username" type="text" placeholder="New member's username" />

                      <svg

                        @click="addField(input, allMembers), addMember(input, form.members)"
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 24 24"
                        width="24"
                        height="24"
                        class="ml-2 cursor-pointer"
                      >
                        <path fill="none" d="M0 0h24v24H0z" />
                        <path
                          fill="green"
                          d="M11 11V7h2v4h4v2h-4v4h-2v-4H7v-2h4zm1 11C6.477 22 2 17.523 2 12S6.477 2 12 2s10 4.477 10 10-4.477 10-10 10zm0-2a8 8 0 1 0 0-16 8 8 0 0 0 0 16z"
                        />
                      </svg>

                      <svg
                        v-show="allMembers.length > 1"
                        @click="removeField(index, allMembers), removeMember(index, form.members)"
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 24 24"
                        width="24"
                        height="24"
                        class="ml-2 cursor-pointer"
                      >
                        <path fill="none" d="M0 0h24v24H0z" />
                        <path
                          fill="#EC4899"
                          d="M12 22C6.477 22 2 17.523 2 12S6.477 2 12 2s10 4.477 10 10-4.477 10-10 10zm0-2a8 8 0 1 0 0-16 8 8 0 0 0 0 16zm0-9.414l2.828-2.829 1.415 1.415L13.414 12l2.829 2.828-1.415 1.415L12 13.414l-2.828 2.829-1.415-1.415L10.586 12 7.757 9.172l1.415-1.415L12 10.586z"
                        />
                      </svg>

                    </div>


                    <input type="text" v-model="form.subject" placeholder="Subject" /> <br>
                    <input type="text" v-model="form.text" placeholder="First message" /> <br>

                    <button type="submit"> Create! </button>

                  </form>
                </div>

                <div class="overview">

                  <!-- {{this.form}} -->

                  <h3>Members of the new chat are: </h3>
                  <div v-for="member in allMembers" :key="member.username">
                    <p>{{ member.username }}</p>
                  </div>

                  <h3>Subject of the new chat is: </h3>
                  <p>{{ form.subject }}</p>

                  <h3>First message of the new chat is: </h3>
                  <p>{{ form.text }}</p>

                </div>

              </div>

                </div>

          </div>
        </div>







  
</template>

<script>
import axios from 'axios'

export default {
    name: 'CreateChat',
    data() {
      return {
        allMembers: [{username: ""}],
        form: {
          members: [],
          subject: '',
          text: ''
        },
        error: ""
      }
    },
    methods: {
      addField(value, fieldType) {
        fieldType.push({ value: "" })
      },
      addMember(input, members) {
        members.push(input.username)
      },
      removeMember(index, members) {
        members.splice(index, 1)
      },
      removeField(index, fieldType) {
        fieldType.splice(index, 1)
      },
      tokenCreation() {
      this.token = "Bearer " + localStorage.getItem("token");
      return this.token;
      },
      chatCreateForm() {
        this.error = ""
        axios.post('/chats', this.form,{'headers':{'Authorization': this.tokenCreation()}})
        .then((res) => {})
        .catch((error) => (this.error = error.response.data.error))
        .finally(() => {})
        if (this.error.equals("")) {

          this.$router.push('/chats')
        }
      }
    }
}
</script>

<style>

  .overview {
    margin: 20px;
    border: solid 1px grey;
    border-radius: 10px;
    text-align: center;
    width: 400px;
  }

  input{
    margin: 5px;
  }

</style>