// 当前模块名（路由名前缀）
export const MOUDLE_PREDIX = 'user'
const MOUDLE_ROOT = `/${MOUDLE_PREDIX}`

// 当前模块首页：模块入口
export const INDEX = {
  path: MOUDLE_ROOT,
  name: `${MOUDLE_PREDIX}_root`
}

// 个人主页
export const PERSONAL_PAGE = {
  path: MOUDLE_ROOT + '/personPage',
  name: `${MOUDLE_PREDIX}_personal_page`
}
