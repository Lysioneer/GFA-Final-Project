<template>

<section class="signup-view">
  <form class="ui form" @submit.prevent="registerKingdom">
    <h1>Register kingdom</h1>
    <!-- <h3 v-if="kingdomNameMsg.length && coorXMsg.length && coorYMsg.length">Congratulations! Your kingdom {{ kingdomNameMsg }} was created at coordinates {{ coorXMsg }} and {{ coorYMsg }}!</h3> -->
    <!-- <p v-if="errors.length">
          <b>Please correct the following error(s):</b>
          <ul>
              <li v-for="error in errors" :key="error">{{ error }}</li>
          </ul>
    </p> -->


    <div v-if="data.message" class="ui positive message">
      <i class="close icon" @click="closeSuccessMessage()"></i>
      <div class="header">Success!</div>
      <p>{{ data.message }}</p>
      <router-link to="/"><button class="ui inverted secondary button">Back to login page</button></router-link>
    </div>
   

    <div v-if="error" class="ui danger message">
      <i class="close icon" @click="closeErrorMessage()"></i>
        <div class="header">Error!</div>
        <p>{{ error }}</p>
    </div>

    <div class="field">
        <label for="username">Username:</label>
        <div class="ui left icon input">
            <i class="user icon"></i>
            <input type="text" id="username" v-model="username" name="username" placeholder="Username" pattern="[^\s]*" title="Spaces are not allowed!" required>
        </div>
    </div>
    <div class="field">
        <label>Password:</label>
        <div class="ui left icon input">
        <i class="key icon"></i>
        <input type="password" id="password" name="password" v-model="password" placeholder="Password" minlength="8" required>
        </div>
    </div>
    <div class="field">
        <label for="kingdomName">Kingdom Name:</label>
        <div class="ui left icon input">
            <i class="chess king icon"></i>
            <input type="text" id="kingdomName" v-model="kingdomName" name="kingdomName" placeholder="Kingdom name">
        </div>
    </div>
    <div class="two fields">
        <div class="field">
          <select class="ui fluid search dropdown" name="coordinateX" v-model="coordinateX" required>
            <option value="">Coordinate X</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4</option>
            <option value="5">5</option>
            <option value="6">6</option>
            <option value="7">7</option>
            <option value="8">8</option>
            <option value="9">9</option>
            <option value="10">10</option>
            <option value="11">11</option>
            <option value="12">12</option>
            <option value="13">13</option>
            <option value="14">14</option>
            <option value="15">15</option>
            <option value="16">16</option>
            <option value="17">17</option>
            <option value="18">18</option>
            <option value="19">19</option>
            <option value="20">20</option>
          </select>
        </div>
        <div class="field">
          <select class="ui fluid search dropdown" name="coordinateY" v-model="coordinateY" required>
            <option value="">Coordinate Y</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4</option>
            <option value="5">5</option>
            <option value="6">6</option>
            <option value="7">7</option>
            <option value="8">8</option>
            <option value="9">9</option>
            <option value="10">10</option>
            <option value="11">11</option>
            <option value="12">12</option>
            <option value="13">13</option>
            <option value="14">14</option>
            <option value="15">15</option>
            <option value="16">16</option>
            <option value="17">17</option>
            <option value="18">18</option>
            <option value="19">19</option>
            <option value="20">20</option>
          </select>
        </div>
    </div>
    <button class="big ui inverted secondary button" type="submit"
    @click="registerKingdom, checkForm"
    >Register Kingdom</button>
  </form>
  </section>
</template>

<script>
import axios from 'axios'

export default {
    name: 'RegisterKingdom',
    data () {
      return {
          username: "",
          password: "",
          kingdomName: "",
          coordinateX: "",
          coordinateY: "",
          errors: [],
          kingdomNameMsg: "",
          coorXMsg: "",
          coorYMsg: "",
          data: "",
          error: ""
      }
    },  
    methods: {
      registerKingdom() {
        axios({
          method: "post",
          url: "/registration/kingdom",
          data: {
            username: this.username,
            password: this.password,
            kingdomName: this.kingdomName,
            coordinateX: this.coordinateX,
            coordinateY: this.coordinateY
          }
        })
        .then( (response) => (this.data = response.data))
        .catch((error) => (this.error = error.response.data.error));  
      },
      checkForm: function(e) {
          this.errors = []
          if(!this.username) {
              this.errors.push('Name required')
          }
          if(!this.password) {
              this.errors.push('Password required')
          }
          if(!this.kingdomName) {
              this.errors.push('Kingdom name required')
          }
          if(this.kingdomName.length < 3) {
              this.errors.push('The kingdom name must be at least 3 characters long')
          }
          e.preventDefault();
        },
    
        closeSuccessMessage() {
          this.data = "";
        },
        closeErrorMessage() {
          this.error = "";
        },
    }
}
</script>

<style scoped>
.signup-view {
    display: flex;
    justify-content: center;
    align-items: center;

    margin-top: 30px;
  }
  .form {
    width: 300px;
  }
  h1 {
    padding: 20px;
  }
</style>