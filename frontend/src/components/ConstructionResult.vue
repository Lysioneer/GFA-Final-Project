<template>
  <div class="modal-backdrop">
    <div class="modal">
      <header class="modal-header">
        <slot name="header"> Construction results! </slot>
        <button type="button" class="btn-close" @click="close">x</button>
      </header>

      <section class="modal-body">
        <slot name="body">
          
          <div v-if="!result==''">
            <img style="error" :src="require(`@/assets/errormsg.png`)"><br>
            <h3>Your wishes exploded.</h3>
            <h3>{{ result }}</h3>
          </div>

          <div v-else>
            <div v-if="endTime">
            <h3>Construction allowed and started.</h3>
            <h3>End of construction: {{ endTime }}</h3>
            </div>
            <div v-if="demolition">
              <h3>Demolished!</h3>
            </div>
          </div>

        </slot>
      </section>
      
    </div>
  </div>
</template>

<script>
export default {
  name: "ConstructionResult",
  props: ["result", "endTime", "demolition"],
  methods: {
    close() {
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal {
  background: #ffffff;
  box-shadow: 2px 2px 20px 1px;
  overflow-x: auto;
  display: flex;
  flex-direction: column;
  min-width: 50%;
  max-width: 75%;
  max-height: 90%;
}
.modal-header,
.modal-footer {
  padding: 15px;
  display: flex;
}
.modal-header {
  position: relative;
  border-bottom: 1px solid #eeeeee;
  color: #4aae9b;
  justify-content: space-between;
}
.modal-footer {
  border-top: 1px solid #eeeeee;
  flex-direction: column;
  justify-content: flex-end;
  padding: 15px 15px;
}
.modal-body {
  position: relative;
  padding: 20px 10px;
  
}
.btn-close {
  position: absolute;
  top: 0;
  right: 0;
  border: none;
  font-size: 20px;
  padding: 10px;
  cursor: pointer;
  font-weight: bold;
  color: #4aae9b;
  background: transparent;
}
.button {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}
div {
  padding: 10px;
}
</style>