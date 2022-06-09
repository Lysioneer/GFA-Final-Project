<template>

    <div class="ui grid container">
        <div class="one column row">
            <div class="column">

                <div class="ui secondary pointing menu">
                <div class="item">
                    <router-link :to="{name: 'Kingdom', params:{kingdomId: this.kingdomId}}">Kingdom</router-link>
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
                <div class="root">
                    <div class="link">
                    <router-link to="/createChat"><h3>Create new chat</h3></router-link>
                    </div>
                    <ListOfChats />
                    
                </div>
            </div>
        </div>

    </div>
    
</template>

<script>

    import ListOfChats from '../components/ListOfChats.vue'

    export default {
        name: 'Chats',
        data() {
            return {
                kingdomId: localStorage.getItem('kingdomId'),
            }
        },
        components: {
            ListOfChats
        }
    }


</script>

<style>

    /* .link {
        text-align: left;
    } */

</style>