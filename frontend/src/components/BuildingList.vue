<template>
    <div>         
        <ul class="side-by-side">
           
            <BuildingListItem
            v-for="building in buildings"
            :building="building"
            :key="building"
            @buildingselect="onBuildingSelect">
            </BuildingListItem>
        </ul>
        
    </div>
    
</template>

<script>
import BuildingListItem from './BuildingListItem.vue';

export default {
    name: 'BuildingList',

    components: {
        BuildingListItem,
    },

    props: ['buildings'],

    methods: {
        onBuildingSelect(building) {
            this.$emit('buildingselect', building)
        },
    }
    
}
</script>

<style scoped>
img {
    width: 50px;
    height: 50px;
    padding: 10px;
}

.side-by-side li {
  display: inline;
}
</style>