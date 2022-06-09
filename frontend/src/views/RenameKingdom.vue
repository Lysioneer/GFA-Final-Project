<template>
<section class="signup-view">
  <form class="ui form" @submit="checkForm">
    <h3 v-if="successMsg.length">Your new kingdom name is {{ successMsg }}!</h3>
    <div class="field">
        <div v-if="errors.length" class="ui danger message">
            <i class="close icon" @click="closeErrorMessage()"></i>
            <p>
                
                <b>Please correct the following error(s):</b>
                <ul>
                    <li v-for="error in errors" :key="error">{{ error }}</li>
                </ul>
            </p>
        </div>
        
        <label for="kingdomName">Enter new kindom name:</label>
        <div class="ui left icon input">
            <i class="chess king icon"></i>
            <input type="text" v-model="kingdomName" id="kingdomName" name="kingdomName" placeholder="Kingdom name" pattern="[^\s]*" title="Spaces are not allowed!">
        </div>
    </div>
    <button class="ui inverted secondary button" type="submit" @click="renameKingdom">RENAME KINGDOM</button>
  </form>
  </section>
</template>

<script>
import axios from 'axios'

export default {
    name: 'RenameKingdom',
    data() {
        return {
            kingdomName: '',
            result: '',
            errors: [],
            successMsg: '',
            token: "",
        }
    },
    methods: {
        renameKingdom() {
            axios({
                method: "put",
                url: '/kingdoms/1',
                headers: {'Authorization': this.tokenCreation()},
                data: {
                    kingdomName: this.kingdomName
                },
                
            })
            .then(this.successMsg = this.kingdomName)
            .catch((error) => {
                console.log(error)
            })
        },
        closeErrorMessage() {
        this.errors = [];
        },
        tokenCreation(){
        this.token= 'Bearer '+ localStorage.getItem('token')
        return this.token
        },
        checkForm: function(e) {
            this.errors = []

            if(!this.kingdomName) {
                this.errors.push('Name required')
            }

            if(this.kingdomName.length < 3) {
                this.errors.push('The name must be at least 3 characters long')
            }

            //TODO: Add whitespace check

            e.preventDefault();

        }
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
</style>