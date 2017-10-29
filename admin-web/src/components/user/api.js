import { fetchApi } from '@/utils/index'

// 获取banner列表
export const GET_LIST_URL = '/account/queryUser'
export const getListApi = (params) => {
  return fetchApi({
    url: GET_LIST_URL,
    type: 'get',
    params
  })
}
