<template>
<!-- <div>
    
</div>

<router-view/> -->

<section class="signup-view">
  <form id="login-form" class="ui form" method="post" @submit="setUserToken">
    <h1>Login</h1>

      <div v-if="error" class="ui danger message">
        <i class="close icon" @click="closeErrorMessage()"></i>
        <div class="header">Error!</div>
        <p>{{ error }}</p>
      </div> 

      <div v-if="info" class="ui danger message">
        <i class="close icon" @click="closeErrorMessage()"></i>
        <div class="header">Error!</div>
        <p>{{ info }}</p>
      </div> 

    <div class="field">
      <label for="username">Username:</label>
      <div class="ui left icon input">
        <i class="user icon"></i>
        <input type="text" id="username" v-model="username" name="username" placeholder="Username" required>
      </div>
    </div>
    <div class="field">
        <label>Password:</label>
        <div class="ui left icon input">
          <i class="key icon"></i>
          <input type="password" id="password" v-model="password" name="password" placeholder="Password" required>
        </div>
    </div>

    <button class="big ui inverted secondary button">Submit</button>

    <p>Don´t have an account? Sign-up now!</p>
    <router-link to="/signup"><button class="ui inverted secondary button">SignUp</button></router-link>

  </form> 
</section>
</template>

<script>
import axios from 'axios';
import RegisterKingdom from "../views/RegisterKingdom.vue"

export default {
    name: 'Login',

    components: {
        RegisterKingdom
    },

    data () {
        return {
            username: "",
            password: "",
            token: "",
            error: "",
            info: ""
        }
    },
    methods: 
    {
        setUserToken (e) {
            e.preventDefault()
            axios.post('/login', {
                username: this.username,
                password: this.password
            })
            .then((response) => {
              if (!response.data.error){
                this.$router.push('/kingdomList');
                localStorage.setItem('token', response.data.token);
              };
            })
            .catch((error) => {
              this.error = error.response.data.error;
              this.info = error.response.data.info;
              if (error.response.data.info){
                this.$router.push('/registerkingdom');
              }
            })
            
        },
        closeErrorMessage() {
          this.error = "";
        },
        
        //...mapActions(['login']),
    //computed: mapGetters(['isLoggedIn'])
    }
    //mapActions(["login"])
}
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