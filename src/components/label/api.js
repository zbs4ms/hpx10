import { fetchApi } from '@/utils/index'

/**
  * 添加企业标签
  * data 为传给后台的数据(必须是JOSN格式)
 */
export const ADDLABEL_URL = '/label/create'

export const addLabelApi = (data) => {
  return fetchApi({
    url: ADDLABEL_URL,
    type: 'post',
    data: data
  })
}

/**
  * 修改企业标签
  * data 为传给后台的数据(必须是JOSN格式)
 */
export const UPDATELABEL_URL = '/label/update'

export const updateLabelApi = (data) => {
  return fetchApi({
    url: UPDATELABEL_URL,
    type: 'post',
    data: data
  })
}

/**
  * 删除企业标签
  * id 为企业标签的ID
*/
export const DELLABEL_URL = '/label/delete'

export const delLabelApi = (id) => {
  return fetchApi({
    url: DELLABEL_URL,
    type: 'post',
    data: id
  })
}
