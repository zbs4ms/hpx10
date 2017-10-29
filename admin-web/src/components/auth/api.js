import { fetchApi } from '@/utils/index'

// 创建管理员账号
export const CREATE_URL = '/admin/create'
export const createApi = (data) => {
  return fetchApi({
    url: CREATE_URL,
    type: 'post',
    data
  })
}

// 返回所有权限接口
export const GET_ALL_PERMISSION_URL = '/admin/all_permission'
export const getAllPermissionApi = () => {
  return fetchApi({
    url: GET_ALL_PERMISSION_URL,
    type: 'get'
  })
}
