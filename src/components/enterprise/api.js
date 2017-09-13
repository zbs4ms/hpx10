import { fetchApi } from '@/utils/index'

// 查询列表
export const GET_LIST_URL = '/company/search'
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

/* 编辑企业 */
// 获取企业详情
export const GET_ENTERPRISE_INFO_URL = '/company/findCompanyById/'
export const getEnterpriseInfoApi = (companyId) => {
  return fetchApi({
    url: GET_ENTERPRISE_INFO_URL + companyId,
    type: 'get',
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}
// 提交修改
export const UPDATE_ENTERPRISE_URL = '/company/update'
export const updateEnterpriseApi = (data) => {
  return fetchApi({
    url: UPDATE_ENTERPRISE_URL,
    type: 'post',
    data,
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}

// 新建监管机构
export const NEW_ACCOUNT_URL = '/company/create'
export const newAccountApi = (data) => {
  return fetchApi({
    url: NEW_ACCOUNT_URL,
    type: 'post',
    data,
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}

/* 新建下级企业 */
// 搜索企业
export const GET_ENTERPRISE_LIST_URL = '/company/searchlist'
export const getEnterpriseListApi = (query) => {
  return fetchApi({
    url: GET_ENTERPRISE_LIST_URL,
    type: 'get',
    params: {
      query
    },
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}

// 搜索上级企业
export const GET_PARENT_LIST_URL = '/company/searchParent'
export const getParentListApi = () => {
  return fetchApi({
    url: GET_PARENT_LIST_URL,
    type: 'get',
    success (data) {
    },
    error (err) {
      console.log(err)
    }
  })
}

// 企业标签
export const GET_CHOSEN_URL = '/label/findCompanyLabel/'
export const getChosenApi = (companyId) => {
  return fetchApi({
    url: GET_CHOSEN_URL + companyId,
    type: 'get'
  })
}

// 添加企业标签
export const UPDATE_CHOSEN_URL = '/label/addLabel'
export const updateChosenApi = (json) => {
  return fetchApi({
    url: UPDATE_CHOSEN_URL,
    type: 'post',
    data: {
      labels: json
    }
  })
}

// 企业规模和性质
export const GET_KIND_URL = '/label/findAllGzwLabel'
export const getKindApi = () => {
  return fetchApi({
    url: GET_KIND_URL,
    type: 'get'
  })
}

// 企业名称和行业分类
export const GET_KINDS_URL = '/label/findCompanyGzwLabel/'
export const getKindsApi = (companyId) => {
  return fetchApi({
    url: GET_KINDS_URL + companyId,
    type: 'get'
  })
}

// 添加企业分类
export const UPDATE_KIND_URL = '/label/addGzwLabel'
export const updateKindApi = (json) => {
  return fetchApi({
    url: UPDATE_KIND_URL,
    type: 'post',
    data: {
      labels: json
    }
  })
}
