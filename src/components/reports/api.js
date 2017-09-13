/*
 * Created by zhengji
 * Date: 2017/7/26
 */

import { fetchApi } from '@/utils/index'

// 查询年报/月报
export const GET_EXCEL = '/data_center/excel/queryUpload'
export const getExcelApi = (params) => {
  return fetchApi({
    url: GET_EXCEL,
    type: 'get',
    params
  })
}

// 上传快报
export const UPLOAD_MONTH_URL = '/data_center/excel/uploadMonth'
export const uploadMonth = (data) => {
  return fetchApi({
    url: UPLOAD_MONTH_URL,
    type: 'post',
    data
  })
}

// 上传年报
export const UPLOAD_YEAR_URL = '/data_center/excel/uploadYear'
export const uploadYear = (data) => {
  return fetchApi({
    url: UPLOAD_YEAR_URL,
    type: 'post',
    data
  })
}

// 查询年度预算
export const GET_BUDGET_URL = '/financialReport/selectAnnualBudget'
export const getBudget = (params) => fetchApi({
  url: GET_BUDGET_URL,
  type: 'get',
  params
})

// 添加/更新年度预算
export const UPDATE_BUDGET_URL = '/financialReport/saveOrUpdateAnnualBudget'
export const updateBudget = (data) => fetchApi({
  url: UPDATE_BUDGET_URL,
  type: 'post',
  data
})

// 查询年度审计/修正
export const GET_AUDIT_URL = '/financialReport/selectAudit'
export const getAuditAPi = (params) => fetchApi({
  url: GET_AUDIT_URL,
  type: 'get',
  params
})

// 添加
export const UPDATE_AUDIT_URL = '/financialReport/addAudit'
export const updateAuditAPi = (json) => fetchApi({
  url: UPDATE_AUDIT_URL,
  type: 'post',
  data: {
    audit: json
  }
})
