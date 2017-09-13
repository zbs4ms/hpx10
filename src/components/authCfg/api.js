import { fetchApi } from '@/utils/index'

// 获取权限树
export const GET_TREE_URL = '/permission/getPermissionTree/-1'
export const getTreeApi = () => {
  return fetchApi({
    url: GET_TREE_URL,
    type: 'get',
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}

// 更新权限树
export const UPDATE_TREE_URL = '/permission/updatePermission'
export const updateTreeApi = (data) => {
  return fetchApi({
    url: UPDATE_TREE_URL,
    type: 'post',
    data: data,
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}
