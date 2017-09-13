// 当前模块名（路由名前缀）
const MODULE_PREFIX = 'setting_account'

const AccountSetting = resolve => require(['./Index'], resolve)
export const INDEX = {
  path: '/setting/account',
  name: `${MODULE_PREFIX}_index`
}

export default [{
  path: INDEX.path,
  name: INDEX.name,
  component: AccountSetting
}]
