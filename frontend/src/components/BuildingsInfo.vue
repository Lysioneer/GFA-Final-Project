<template>
    <div class="ui grid container">
        <div class="one column row">
            <div class="column">

                <div class="ui secondary pointing menu">
                <div class="item">
                    <router-link :to="{name: 'Kingdom', params:{kingdomId: this.kingdomId}}">Kingdom</router-link>
                </div>
                <div class="item active">
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
                <div class="item">
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

                <div>
                    <div>
                    <img class="image" :src="require(`@/assets/buildinglist.png`)"><br>
                    <button class="ui inverted secondary button" id="refreshButton" @click="reloadData()"> Refresh </button>
                    <button class="ui inverted secondary button" @click="addBuildingFormData()"> Construct </button>
                    </div>
                    <BuildingList 
                    :buildings="buildings" 
                    @buildingselect="onBuildingSelect">
                    </BuildingList>
                </div>

                <div v-if="buildingTypes.length > 0">
                    <span style="color:Red;" v-if="addBuildingError">
                    <img style="error" :src="require(`@/assets/errormsg.png`)"><br>    
                    Your wishes exploded.<br>
                    {{ addBuildingError }} </span><br>

                    <span style="color:Green;" v-if="addBuilding">
                    Construction of a new {{ addBuilding.type.replace(/_/, " ") }} allowed and started.<br>
                    End of construction: {{ timeConvert(addBuilding.constructTime) }}</span><br>


                    <form class="ui large form" @submit.stop.prevent="handleSubmit" @click="post">

                        <div class="two fields">
                            <div class="field">
                            <label>Building Type</label>
                            <select v-model="addType" id="selectBuilding">
                                <option value="none" selected disabled hidden>Select type</option>
                                <option v-for="(type, value) in buildingTypes" :value="type.typeName" :key="value" >
                                    {{ type.typeName.replace(/_/, " ")}}
                                </option>
                            </select>
                            </div>
                            <div class="field">
                                <label>Building Position</label>
                                <select v-model="addPosition" id="selectPosition">
                                    <option value="none" selected disabled hidden>Select position</option>
                                    <option v-for="(position, value) in freePositions" :value="position.position" :key="value" >
                                        {{ position.position }} 
                                    </option>
                                </select>
                            </div>
                        </div>
                        <div>
                            <button class="ui inverted secondary button">Submit</button>
                        </div>

                        <!-- <label>Building Type</label>
                        <select v-model="addType" id="selectBuilding">
                            <option value="none" selected disabled hidden>Select type</option>
                            <option v-for="(type, value) in buildingTypes" :value="type.typeName" :key="value" >
                                {{ type.typeName.replace(/_/, " ")}}
                            </option>
                        </select><br>
                        <label>Building Position</label>
                        <select v-model="addPosition" id="selectPosition">
                            <option value="none" selected disabled hidden>Select position</option>
                            <option v-for="(position, value) in freePositions" :value="position.position" :key="value" >
                                {{ position.position }} 
                            </option>
                        </select><br>
                        <button class="ui inverted secondary button">Submit</button> -->

                    </form>
                    <br>
                </div>

                <div v-if="selectedBuilding">
                    <span style="color:Red;" v-if="upgradeBuildingError">
                    <img style="error" :src="require(`@/assets/errormsg.png`)"><br>    
                    Your wishes exploded.<br>
                    {{ upgradeBuildingError }} </span><br>

                    <span style="color:Green;" v-if="selectedBuilding.constructTime">Upgrade in progress.<br>
                    End of construction: {{ timeConvert(selectedBuilding.constructTime) }}</span><br>

                    <span style="color:Green;" v-if="selectedBuilding.destroyTime">Downgrade in progress.<br>
                    End of work: {{ timeConvert(selectedBuilding.destroyTime) }}</span><br>
                    
                    <img :src="require(`@/assets/${selectedBuilding.type}.png`)" :alt="selectedBuilding.type"><br>
                    Type: {{ selectedBuilding.type.replace(/_/, " ") }} <br>
                    Level: {{ selectedBuilding.level }}<br>
                    Position: {{ selectedBuilding.position }}<br><p></p>
                    <button class="ui inverted secondary button" @click="buildingConstruction('upgrade',false)">Upgrade</button> 
                    <button class="ui inverted secondary button" @click="buildingDowngrade('tear-down',false)">Downgrade</button>
                    <button class="ui inverted secondary button" @click="buildingDemolition('tear-down',true)">Demolish</button><br>  
                </div>
            </div>
        </div>
    </div>

    <div>
        <ConstructionResult
            v-show="isModalVisible"
            @close="closeModal"
            :result="result"
            :endTime="endTime"
            :demolition="demolition"
        />
    </div>

    
</template>

<script>
import axios from "axios";
import BuildingList from "./BuildingList.vue";
import ConstructionResult from "./ConstructionResult.vue"

export default {
    name: 'BuildingsInfo',

    components: {
        BuildingList,
        ConstructionResult
    },

    data() {
        return {
            buildings: [],
            buildingTypes: [],
            freePositions: [],
            kingdomId: localStorage.getItem('kingdomId'),
            selectedBuilding: null,
            addBuilding: null,
            addType: null,
            addPosition: null,
            result: "",
            isModalVisible: false,
            endTime: null,
            demolition: false,
            token: ""
        };
    },

    methods: {
        tokenCreation(){
            this.token= 'Bearer '+ localStorage.getItem('token')
            return this.token
        },

        closeModal() {
            this.isModalVisible = false;
            this.result = "";
            this.demolition = false;
            this.endTime = null;
            },

        onBuildingSelect(building) {
            if (this.selectedBuilding == building) {
                document.getElementById("refreshButton").click()
            }
            else {
                this.selectedBuilding = building;
            }
            this.freePositions = [];
            this.buildingTypes = [];
        },

        timeConvert(value) {
            const milliseconds = value * 1000;
            const dateObject = new Date(milliseconds);
            return dateObject.toLocaleString();
        },

        buildingConstruction(action, instant) {
            axios
                .put(`/kingdoms/${this.kingdomId}/buildings/${this.selectedBuilding.buildingId}`, {
                action: action,
                instant: instant, }, 
                {'headers':{'Authorization': this.tokenCreation()}})
                .then((response) => (this.selectedBuilding = response.data, this.endTime = this.timeConvert(response.data.constructTime)))
                .then(() => {document.getElementById("refreshButton").click()})
                .catch((error) => (this.result = error.response.data.error))
                ;
                this.isModalVisible = true;
                this.buildingTypes = [];
                this.freePositions = [];
                return this.result
        },

        buildingDowngrade(action, instant) {
            axios
                .put(`/kingdoms/${this.kingdomId}/buildings/${this.selectedBuilding.buildingId}`, {
                action: action,
                instant: instant, }, 
                {'headers':{'Authorization': this.tokenCreation()}})
                .then((response) => (this.selectedBuilding = response.data, this.endTime = this.timeConvert(response.data.destroyTime)))
                .then(() => {document.getElementById("refreshButton").click()})
                .catch((error) => (this.result = error.response.data.error))
                ;
                this.isModalVisible = true;
                this.buildingTypes = [];
                this.freePositions = [];
                return this.result
        },

        buildingDemolition(action, instant) {
            axios
                .put(`/kingdoms/${this.kingdomId}/buildings/${this.selectedBuilding.buildingId}`, {
                action: action,
                instant: instant, }, 
                {'headers':{'Authorization': this.tokenCreation()}})
                .then((response) => (this.selectedBuilding = response.data))
                .then(() => {document.getElementById("refreshButton").click()})
                .catch((error) => (this.result = error.response.data.error))
                ;
                this.demolition = true;
                this.isModalVisible = true;
                return this.result
        },

        reloadData() {
            axios
                .get(`/kingdom/${this.kingdomId}/buildings`, {
                    headers: {
                    Authorization:
                        this.tokenCreation(),
                    },
                })
                .then((response) => (this.buildings = response.data.kingdomBuildings))
                .then(this.freePositions = [])
                .then(this.buildingTypes = [])
                .then(this.result = "")
                .then(this.selectedBuilding = null)
                .catch((error) => console.log(error.message));            
        },

        addBuildingFormData() {
            axios
                .get(`/buildingTypes/${this.kingdomId}`, {
                    headers: {
                    Authorization:
                        this.tokenCreation(),
                    },
                })
                .then((response) => (this.buildingTypes = response.data.buildingTypes, this.freePositions = response.data.freePositions))                
                .then(this.selectedBuilding = null)
                .catch((error) => console.log(error.message))
                .finally(() => {document.getElementById("selectBuilding").selectedIndex=-1;
                document.getElementById("selectPosition").selectedIndex=-1;}); 
        },

        handleSubmit() {
            axios 
                .post(`/kingdoms/${this.kingdomId}/buildings`, {
                buildingType: this.addType,
                position: this.addPosition}, 
                {'headers':{'Authorization': this.tokenCreation()}})
                .then((response) => {this.endTime = this.timeConvert(response.data.constructTime)})
                .then((response) => {document.getElementById("refreshButton").click()})
                .catch((error) => (this.result = error.response.data.error));
                this.isModalVisible = true;
        }
    },

    mounted() {
    axios
      .get(`/kingdom/${this.kingdomId}/buildings`, {
        headers: {
          Authorization:
            this.tokenCreation(),
        },
      })
      .then((response) => (this.buildings = response.data.kingdomBuildings))
      .catch((error) => console.log(error.message));
    },
};

</script>

<style scoped>
.image {
    width: 60px;
    height: 60px;
    padding: 10px;
}

.error {
    width: 35px;
    height: 35px;
}
/* 
form {
    max-width: 420px;
    margin: 30px auto;
    background: white;
    text-align: left;
    padding: 40px;
    border-radius: 10px;
}

label {
    color: #aaa;
    display: inline-block;
    margin: 25px 0 15px;
    font-size: 0.6em;
    text-transform: uppercase;
    letter-spacing: 1px;
    font-weight: bold;
} 

input {
    display: block;
    padding: 10px 6px;
    width: 100%;
    box-sizing: border-box;
    border: none;
    border-bottom: 1px solid #ddd;
    color: #555;
} */


</style>