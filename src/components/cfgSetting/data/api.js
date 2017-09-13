import { fetchApi } from '@/utils/index'

/**
  * 显示数据参数请求
*/
export const GETLIST_URL = '/sysconstant/search/companyVisible'

export const getListApi = (companyVisible) => {
  return fetchApi({
    url: GETLIST_URL,
    type: 'get'
  })
}

/**
  * 修改数据参数请求
*/
export const UPDATEDATA_URL = '/sysconstant/saveOrUpdate'

export const upDataApi = (data) => {
  return fetchApi({
    url: UPDATEDATA_URL,
    type: 'post',
    data: data
  })
}
