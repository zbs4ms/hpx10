// 当前模块名（路由名前缀）
const MOUDLE_PREDIX = 'enterprise'

// 当前模块首页：企业列表
const EnterpriseManage = resolve => require(['./Index'], resolve)
export const INDEX = {
  path: '/enterprise',
  name: `${MOUDLE_PREDIX}_index`
}

// 新建监管机构
const NewRegulator = resolve => require(['./newRegulator/Index'], resolve)
export const NEW_REGULATOR = {
  path: '/enterprise/newRegulator',
  name: `${MOUDLE_PREDIX}_newRegulator`
}

// 新建下级企业
const NewSubEnterprise = resolve => require(['./newSubEnterprise/Index'], resolve)
export const NEW_SUB_ENTERPRISE = {
  path: '/enterprise/newSubEnterprise',
  name: `${MOUDLE_PREDIX}_newSubEnterprise`
}

// 编辑企业
const EditEnterprise = resolve => require(['./editEnterprise/Index'], resolve)
export const EDIT = {
  path: '/enterprise/:id(\\d+)',
  name: `${MOUDLE_PREDIX}_edit`
}

// 企业标签
const EnterpriseLabel = resolve => require(['./label/Index'], resolve)
export const LABEL = {
  path: '/enterprise/:id(\\d+)/label',
  name: `${MOUDLE_PREDIX}_label`
}

// 企业分类
const EnterpriseKind = resolve => require(['./kind/Index'], resolve)
export const KIND = {
  path: '/enterprise/:id(\\d+)/kind',
  name: `${MOUDLE_PREDIX}_kind`
}

export default [{
  path: INDEX.path,
  name: INDEX.name,
  component: EnterpriseManage
}, {
  path: NEW_REGULATOR.path,
  name: NEW_REGULATOR.name,
  component: NewRegulator
}, {
  path: NEW_SUB_ENTERPRISE.path,
  name: NEW_SUB_ENTERPRISE.name,
  component: NewSubEnterprise
}, {
  path: EDIT.path,
  name: EDIT.name,
  component: EditEnterprise
}, {
  path: LABEL.path,
  name: LABEL.name,
  component: EnterpriseLabel
}, {
  path: KIND.path,
  name: KIND.name,
  component: EnterpriseKind
}]
