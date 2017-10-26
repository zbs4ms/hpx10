/*
 * Created by zhengji
 * Date: 2017/7/31
 */
// 当前模块名（路由名前缀）
export const MOUDLE_PREDIX = 'doctor'
const MOUDLE_ROOT = `/${MOUDLE_PREDIX}`

// 当前模块首页：模块入口
export const INDEX = {
  path: MOUDLE_ROOT,
  name: `${MOUDLE_PREDIX}_root`,
  meta: {
    permissionId: 'm_04',
    label: '医生信息录入'
  }
}
