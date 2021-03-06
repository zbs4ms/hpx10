import { fetchApi } from '@/utils/index'

// 获取banner列表
export const GET_LIST_URL = '/diray/query'
export const getListApi = (params) => {
  return fetchApi({
    url: GET_LIST_URL,
    type: 'get',
    params
  })
}

// 上下架
export const SHELVE_URL = '/diray/show'
export const shelveApi = (id) => {
  return fetchApi({
    url: SHELVE_URL,
    type: 'post',
    params: {
      id
    }
  })
}

// 置顶
export const TOP_URL = '/diray/show'
export const topApi = (id) => {
  return fetchApi({
    url: TOP_URL,
    type: 'post',
    params: {
      id
    }
  })
}

// 审核
export const CHECK_URL = '/diray/verify'
export const checkApi = (id, status) => {
  return fetchApi({
    url: TOP_URL,
    type: 'post',
    params: {
      id
    }
  })
}
