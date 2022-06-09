import { createRouter, createWebHistory } from 'vue-router'
import RenameKingdom from '../views/RenameKingdom.vue'
import Troops from '../views/Troops.vue'
import Kingdom from '../components/Kingdom.vue'
import KingdomList from '../components/KingdomList.vue'
import KingdomInfo from '../components/KingdomInfo.vue'
import Leaderboard from '../components/Leaderboard.vue'
import Login from '../views/Login.vue'
import SignUp from '../views/SignUp.vue'
import RegisterKingdom from '../views/RegisterKingdom.vue'
import BuildingsInfo from '../components/BuildingsInfo.vue'
import Battle from '../components/Battle.vue'
import Chats from '../views/Chats.vue'
import CreateChat from '../views/CreateChat.vue'
import SpecificChat from '../views/SpecificChat.vue'

const routes = [
  {
    path: '/renamekingdom',
    name: 'RenameKingdom',
    component: RenameKingdom
  },
  {
    path: '/kingdoms/1/troops',
    name: 'Troops',
    component: Troops
  },
  {
    path: '/kingdom/:kingdomId',
    name: 'Kingdom',
    component: Kingdom
  },
  {
    path: '/kingdomList',
    name: 'KingdomList',
    component: KingdomList
  },
  {
    path: '/kingdomInfo',
    name: 'KingdomInfo',
    component: KingdomInfo
  },
  {
    path: '/buildings',
    name: 'BuildingsInfo',
    component: BuildingsInfo
  },
  {
    path: '/leaderboard',
    name: 'Leaderboard',
    component: Leaderboard
  },
  {
    path: '/',
    name: 'Login',
    component: Login,
  },
  {
    path: '/signup',
    name: 'SignUp',
    component: SignUp,
  },
  {
    path: '/registerkingdom',
    name: 'RegisterKingdom',
    component: RegisterKingdom,
  },
  {
    path: '/battle',
    name: 'Battle',
    component: Battle,
  },
  {
    path: '/chats',
    name: 'Chats',
    component: Chats
  },
  {
    path: '/createChat',
    name: 'CreateChat',
    component: CreateChat
  },
  {
    path: '/specificChat/:chatId',
    name: 'SpecificChat',
    component: SpecificChat
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router