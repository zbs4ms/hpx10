import {
  INDEX
} from './_consts/routers'

export default [{
  path: INDEX.path,
  name: INDEX.name,
  component: resolve => require(['./Index'], resolve)
}]
