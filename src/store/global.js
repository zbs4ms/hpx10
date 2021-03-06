export const STASH = 'global/stash'
export const CLEAR_STASH = 'global/clear_stash'

export const KEEPALIVE = 'global/keepAlive'
export const CLEAR_KEEPALIVE = 'global/clear_keepAlive'

export const GET_AUTH = 'global/get_auth'

const state = {
  // 暂存的数据
  stash: {
    from: '', // 路由
    data: {
    }
  },
  // 保持状态的组件 (多个之间用','分隔)
  keepAlive: 'no-match',
  auth: []
}

const mutations = {
  [STASH] (state, payload) {
    state.stash = payload
  },
  [CLEAR_STASH] (state) {
    state.stash = {}
  },
  [KEEPALIVE] (state, componentName) {
    state.keepAlive = componentName
  },
  [CLEAR_KEEPALIVE] (state, componentName) {
    state.keepAlive = 'no-match'
  },
  [GET_AUTH] (state, auth) {
    state.auth = auth
  }
}
const actions = {
}

export default {
  state,
  mutations,
  actions
}
