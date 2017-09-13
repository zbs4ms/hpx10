import { fetchApi } from '@/utils/index'

/**
  * 显示帐号参数请求
*/
export const GETLIST_URL = '/sysconstant/search/companyQuantity'

export const getListApi = (companyQuantity) => {
  return fetchApi({
    url: GETLIST_URL,
    type: 'get'
  })
}

/**
  * 修改帐号参数请求
*/
export const UPDATEACCOUNT_URL = '/sysconstant/saveOrUpdate'

export const upAutApi = (data) => {
  return fetchApi({
    url: UPDATEACCOUNT_URL,
    type: 'post',
    data: data
  })
}
