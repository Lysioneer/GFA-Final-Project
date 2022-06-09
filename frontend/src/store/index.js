import { createStore } from 'vuex'

export default createStore({
  state: {
    token: window.localStorage.getItem('token')
  },
  mutations: {
    setToken: (state, token) => {
      state.token = token
    }
  },
  actions: {
    login({commit}) {
      commit('setToken', window.localStorage.getItem('token'));
    },
    logout({commit}) {
      commit('setToken', null);
      window.localStorage.removeItem('token');
    }
},
  getters: {
    isLoggedIn: state => !!state.token
  }
})
