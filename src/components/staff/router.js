// 当前模块名（路由名前缀）
const MODULE_PREFIX = 'staff'

const StaffManage = resolve => require(['./Index'], resolve)
export const INDEX = {
  path: '/staff',
  name: `${MODULE_PREFIX}_index`
}

// 创建账号
const CreateAccount = resolve => require(['./createAccount/Index'], resolve)
export const CREATE = {
  path: '/staff/create',
  name: `${MODULE_PREFIX}_create`
}

// 账号编辑
const EditAccount = resolve => require(['./editAccount/Index'], resolve)
export const EDIT = {
  path: '/staff/:id(\\d+)',
  name: `${MODULE_PREFIX}_edit`
}

// 权限
const AuthEdit = resolve => require(['./auth/Index'], resolve)
export const AUTH = {
  path: '/staff/:id(\\d+)/auth',
  name: `${MODULE_PREFIX}_auth`
}

export default [{
  path: INDEX.path,
  name: INDEX.name,
  component: StaffManage
}, {
  path: CREATE.path,
  name: CREATE.name,
  component: CreateAccount
}, {
  path: EDIT.path,
  name: EDIT.name,
  component: EditAccount
}, {
  path: AUTH.path,
  name: AUTH.name,
  component: AuthEdit
}]
