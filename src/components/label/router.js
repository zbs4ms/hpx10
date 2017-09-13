// 当前模块名（路由名前缀）
const MODULE_PREFIX = 'label'

const Label = resolve => require(['./Index'], resolve)
export const INDEX = {
  path: '/label',
  name: `${MODULE_PREFIX}_index`
}

export default [{
  path: INDEX.path,
  name: INDEX.name,
  component: Label
}]
