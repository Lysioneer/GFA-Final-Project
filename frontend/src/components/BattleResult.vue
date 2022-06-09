<template>
  <div class="modal-backdrop">
    <div class="modal">
      <header class="modal-header">
        <slot name="header"> Battle results! </slot>
        <button type="button" class="btn-close" @click="close">x</button>
      </header>

      <section class="modal-body">
        <slot name="body">
          <div v-if="result.info == 'Nobody returned!'">
            <img src="@\assets\defeat.png" alt="" />
          </div>
          <div v-if="result.info">
            <h3>{{ result.info }}</h3>
          </div>

          <div v-if="result.error">
            <h3>{{ result.error }}</h3>
          </div>

          <div v-if="result.result == 'win'">
            <div v-if="result.battleType == 'TROOP_ATTACK'">
              <h2>Troop attack</h2>
            </div>
            <div v-if="result.battleType == 'DESTRUCTION_ATTACK'">
              <h2>Destruction attack</h2>
            </div>
            <div v-if="result.battleType == 'SPY_ATTACK'">
              <h2>Spy attack</h2>
            </div>
            <div v-if="result.battleType == 'TAKEOVER_ATTACK'">
              <h2>Takeover attack</h2>
            </div>
            <img src="@\assets\victory.png" alt="" />
          </div>

          <div v-if="result.result == 'lost'">
            <div v-if="result.battleType == 'TROOP_ATTACK'">
              <h2>Troop attack</h2>
            </div>
            <div v-if="result.battleType == 'DESTRUCTION_ATTACK'">
              <h2>Destruction attack</h2>
            </div>
            <div v-if="result.battleType == 'SPY_ATTACK'">
              <h2>Spy attack</h2>
            </div>
            <div v-if="result.battleType == 'TAKEOVER_ATTACK'">
              <h2>Takeover attack</h2>
            </div>
            <img src="@\assets\defeat.png" alt="" />
          </div>

          <div v-if="result.result">
            <div class="ui grid container">
              <div class="eight wide column">
                <h3>Attacker</h3>
                <div>
                  <span><b>Kingdom name: </b>{{ result.attacker.details.kingdomName }}</span><br>
                  <span><b>Ruler: </b>{{ result.attacker.details.ruler }}</span>
                </div>
                <div>
                  <span><b>Troops lost:</b><br>
                  <span v-for="(troop,key) in result.attacker.troopsLost" :key="key">
                  {{key}}: {{troop}} <br>
                  </span>
                </span>
                </div>
                <div>
                  <span><b>Stolen resources:</b> <br>
                  <span v-for="(res,key) in result.attacker.resourcesStolen" :key="key">
                  {{key}}: {{res}} <br>
                  </span>
                </span>
                </div>
                <div>
                <span v-if="result.battleType == 'SPY_ATTACK'"><b>Defender intelligence:</b> <br>
                  <span v-for="(intel,key) in result.intelligence" :key="key">
                    {{ key }}: {{intel}} <br>
                  </span>
                </span>
                </div>
              </div>
              <div class="eight wide column">
                <h3>Defender</h3>
                <div>
                  <span><b>Kingdom name: </b>{{ result.defender.details.kingdomName }}</span><br>
                  <span><b>Ruler: </b>{{ result.defender.details.ruler }}</span>
                </div>
                <div>
                  <span><b>Troops lost:</b> <br>
                  <span v-for="(troop,key) in result.defender.troopsLost" :key="key">
                  {{key}}: {{troop}} <br>
                  </span>
                </span>
                </div>
                <div>
                  <span v-if="result.battleType == 'DESTRUCTION_ATTACK'"><b>Buildings lost:</b><br>
                    <span v-for="(battle,key) in result.defender.buildingsLost" :key="key">
                     {{ key }}: {{battle}} <br>
                    </span>
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- <div>{{result}}</div> -->

        </slot>
      </section>

      <footer class="modal-footer">
        <div class="button">
          <button
            class="ui inverted secondary button"
            type="button"
            @click="close"
          >
            Close
          </button>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
export default {
  name: "BattleResult",
  props: ["result"],
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