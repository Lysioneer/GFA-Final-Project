<template>
    <li v-if="building" @click="onBuildingSelect" class="detailed">        
        <img :src="require(`@/assets/${building.type}.png`)" :alt="building.type">
        {{ building.type.replace(/_/, " ") }} |        
    </li>
    <li v-else>
        Empty place |
    </li>
</template>

<script>
export default {
    name: 'BuildingListItem',

    props: ['building'],

    methods: {
        onBuildingSelect() {
            this.$emit('buildingselect', this.building)
        },
    }
}
</script>

<style scoped>
img {
    height: 25px;
    width: 25px;
}

.detailed:hover {
    cursor: pointer;
}
</style>