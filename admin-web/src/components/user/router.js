import {
  INDEX,
  PERSONAL_PAGE
} from './_consts/routers'

export default [{
  path: INDEX.path,
  name: INDEX.name,
  component: resolve => require(['./Index'], resolve)
}, {
  path: PERSONAL_PAGE.path,
  name: PERSONAL_PAGE.name,
  component: resolve => require(['./HomePage'], resolve)
}]
