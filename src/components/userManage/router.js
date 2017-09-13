// 当前模块名（路由名前缀）
const MODULE_PREFIX = 'user'

const UserManage = resolve => require(['./Index'], resolve)
export const INDEX = {
  path: '/user',
  name: `${MODULE_PREFIX}_index`
}

export default [{
  path: INDEX.path,
  name: INDEX.name,
  component: UserManage
}]
