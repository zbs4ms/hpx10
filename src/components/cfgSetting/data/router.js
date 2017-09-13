// 当前模块名（路由名前缀）
const MODULE_PREFIX = 'setting_data'

const DataSetting = resolve => require(['./Index'], resolve)
export const INDEX = {
  path: '/setting/data',
  name: `${MODULE_PREFIX}_index`
}

export default [{
  path: INDEX.path,
  name: INDEX.name,
  component: DataSetting
}]
