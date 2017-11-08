import { fetchApi } from '@/utils/index'

// 获取用户列表
export const GET_LIST_URL = '/account/queryUser'
export const getListApi = (params) => {
  return fetchApi({
    url: GET_LIST_URL,
    type: 'get',
    params
  })
}

// 获取个人信息
export const USER_INFO_URL = '/account/queryUserDetail'
export const userInfoApi = (accountId) => {
  return fetchApi({
    url: USER_INFO_URL,
    type: 'get',
    params: {
      accountId
    }
  })
}
