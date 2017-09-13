import { fetchApi } from '@/utils/index'

// 查询列表
export const GET_LIST_URL = '/account/findAccountList'
export const getListApi = (params) => {
  return fetchApi({
    url: GET_LIST_URL,
    type: 'get',
    params,
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}

/* 人员信息编辑 */
// 获取人员信息
export const GET_STAFF_INFO_URL = '/account/findMemberInfoById/'
export const getStaffInfoApi = (memberInfoId) => {
  return fetchApi({
    url: GET_STAFF_INFO_URL + memberInfoId,
    type: 'get',
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}
// 更新人员信息
export const UPDATE_STAFF_URL = '/account/updateAccount'
export const updateStaffApi = (data) => {
  return fetchApi({
    url: UPDATE_STAFF_URL,
    type: 'post',
    data,
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}

/* 新建人员账号 */
// 新建普通人员的时候，选取企业下拉列表接口
export const GET_COMPANY_URL = '/company/searchCompany'
export const getCompanyApi = (query) => {
  return fetchApi({
    url: GET_COMPANY_URL,
    type: 'get',
    params: {
      query
    }
  })
}

// 获取权限树
export const GET_TREE_URL = '/permission/getPermissionTreeOneself/'
export const getTreeApi = (companyId) => {
  return fetchApi({
    url: GET_TREE_URL + companyId,
    type: 'get'
  })
}

// 新建人员
export const CREATE_STAFF_URL = '/account/createAccount'
export const createStaffApi = (data) => {
  return fetchApi({
    url: CREATE_STAFF_URL,
    type: 'post',
    data
  })
}

// 更新普通人员权限
export const UPDATE_AUTH_URL = '/account/updatePermission'
export const updateAuthApi = (data) => {
  return fetchApi({
    url: UPDATE_AUTH_URL,
    type: 'post',
    data
  })
}
