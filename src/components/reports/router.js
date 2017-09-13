/*
 * Created by zhengji
 * Date: 2017/7/26
 */
// 当前模块名（路由名前缀）
const MODULE_PREFIX = 'reports'
const MODULE_ROOT = '/' + MODULE_PREFIX

const Annals = resolve => require(['./Index'], resolve)
export const INDEX = {
  path: MODULE_ROOT,
  name: `${MODULE_PREFIX}_index`
}

// dashboard
const Dashboard = resolve => require(['./dashboard/Index'], resolve)
export const DASHBOARD = {
  path: MODULE_ROOT + '/dashboard/:companyId/:year?',
  name: `${MODULE_PREFIX}_dashboard`
}

// 预算
const Budget = resolve => require(['./budget/Index'], resolve)
export const BUDGET = {
  path: MODULE_ROOT + '/dashboard/:companyId/:year/budget',
  name: `${MODULE_PREFIX}_budget`
}

// 审计/修正
const Audit = resolve => require(['./audit/Index'], resolve)
export const AUDIT = {
  path: MODULE_ROOT + '/dashboard/:companyId/:year/audit',
  name: `${MODULE_PREFIX}_audit`
}

export default [{
  path: INDEX.path,
  name: INDEX.name,
  component: Annals
}, {
  path: DASHBOARD.path,
  name: DASHBOARD.name,
  component: Dashboard,
  children: [{
    path: BUDGET.path,
    name: BUDGET.name,
    component: Budget
  }, {
    path: AUDIT.path,
    name: AUDIT.name,
    component: Audit
  }]
}]

