<template>

<section class="signup-view">
  <form class="ui form" @submit.prevent="registerUser">
    <h1>Sign-Up</h1>

    <!-- <div v-if="successMsg.length" class="ui success message"> 
      {{ successMsg }}
    </div> -->

    <div v-if="successMsg" class="ui positive message">
      <i class="close icon" @click="closeSuccessMessage()"></i>
      <div class="header">Success!</div>
      <p>{{ successMsg }}</p>
    </div>
   

    <div v-if="error" class="ui danger message">
      <i class="close icon" @click="closeErrorMessage()"></i>
        <div class="header">Error!</div>
        <p>{{ error }}</p>
    </div>

    <!-- <p v-if="errors.length">
          <b>Please correct the following error(s):</b>
          <ul>
              <li v-for="error in errors" :key="error">{{ error }}</li>
          </ul>
    </p> -->

    <div class="field">
        <label for="username">Username:</label>
        <div class="ui left icon input">
            <i class="user icon"></i>
            <input type="text" id="username" v-model="username" name="username" placeholder="Username" pattern="[^\s]*" title="Spaces are not allowed!" minlength="3" required>
        </div>
    </div>
    <div class="field">
        <label for="email">Email:</label>
        <div class="ui left icon input">
          <i class="at icon"></i>
          <input type="email" id="email" v-model="email" name="email" placeholder="email@example.com" required>
        </div>
    </div>
    <div class="field">
        <label>Password:</label>
        <div class="ui left icon input">
          <i class="key icon"></i>
          <input type="password" id="password" v-model="password" name="password" placeholder="Password" minlength="8" required>
        </div>
    </div>
    <button 
      class="big ui inverted secondary button" 
      type="submit"
      @click="registerUser, checkForm"
      >SignUp</button>
      <p>Go back to login</p>
      <router-link to="/"><button class="ui inverted secondary button">Login</button></router-link>
  </form>
  </section>
</template>

<script>
import axios from 'axios'

export default {
  name: "SignUp",
  data() {
    return {
      username: "",
      email: "",
      password: "",
      errors: [],
      successMsg: "",
      error: ""
    };
  },
  methods: {
    registerUser() {
      axios({
        method: "post",
        url: "/registration",
        data: {
          username: this.username,
          email: this.email,
          password: this.password,
        },
      })
        .then((this.successMsg = "Please finish your registration by clicking the confirmation link in your email!"))
        .catch((error) => (this.error = error.response.data.error));

      // axios.post('/registration',
      // {
      //   username: this.username,
      //   email: this.email,
      //   password: this.password
      // })
      // //.then($router.push('registerkingdom'))
      // .then((response) => (this.data = response.data))
      // .catch((error) => (this.error = error.response.data.error));
    },
    checkForm: function (e) {
      this.errors = [];
      if (!this.username) {
        this.errors.push("Name required");
      }
      if (!this.email) {
        this.errors.push("Email required");
      }
      if (!this.password) {
        this.errors.push("Password required");
      }
      if (this.username.length < 3) {
        this.errors.push("The username must be at least 3 characters long");
      }
      if (this.password.length < 8) {
        this.errors.push("The password must be at least 8 characters long");
      }
      e.preventDefault();
    },
    closeSuccessMessage() {
      this.data = "";
    },
    closeErrorMessage() {
      this.error = "";
    },
  },
};
</script>

<style scoped>
.signup-view {
  display: flex;
  justify-content: center;
  align-items: center;
}
.form {
  width: 300px;
}
  h1 {
    padding: 20px;
  }
</style>