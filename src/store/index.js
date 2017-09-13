import Vue from 'vue'
import Vuex from 'vuex'

import global from './global'
import enterprise from '@/components/enterprise/store'

Vue.use(Vuex)

export default new Vuex.Store({
  ...global,
  modules: {
    enterprise
  },
  strict: process.env.NODE_ENV !== 'production'
})
