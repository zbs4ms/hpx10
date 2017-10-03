<script>
//  import { Cookie } from '@/utils/index'

  // 一级导航
  import NAVS from './NAVS'

  export default {
    name: 'LeftNav',
    data () {
      return {
      }
    },
    created () {
      console.log(this.$route.path.match(/\/\w+((?=\/)|$)/)[0])
      this.NAVS = NAVS
//      let accountInfo = JSON.parse(decodeURIComponent(Cookie.get('accountInfo')))
//      this.userName = accountInfo.userName
    },
    methods: {
      handleSelect (index) {
        this.$refs.elMenu.openedMenus = [] // 关闭打开的子菜单
      }
    }
  }
</script>

<template>
  <div id="left-nav">
    <el-menu
      theme="dark"
      ref="elMenu"
      :unique-opened="true"
      :default-active="$route.path.match(/\/\w+((?=\/)|$)/)[0]"
      @select="handleSelect"
      router>
      <el-menu-item
        class="menu-index"
        index="user">
        <span style="font-size: 18px;">后台管理系统</span>
      </el-menu-item>
      <template v-for="nav in NAVS">
        <el-submenu
          v-if="nav.children"
          :key="nav.label"
          index="nav">
          <template slot="title"><i v-if="nav.icon" class="nav-icon" :class="nav.icon"></i>{{ nav.label }}</template>
          <el-menu-item
            v-for="subNav in nav.children"
            :key="subNav.label"
            :index="subNav.path">
            {{ subNav.label }}
          </el-menu-item>
        </el-submenu>
        <el-menu-item
          v-else
          :index="nav.path">
          <i v-if="nav.icon" class="nav-icon" :class="nav.icon"></i>{{ nav.label }}
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<style lang="scss">
  @import '~@/assets/style/variables/index.scss';

  #left-nav {
    overflow-y: auto;
    border-radius: 2px;
    width: 200px;
    height: 100%;

    background: $bg1;

    .menu-index {
      height: 72px;
      line-height: 72px;
    }
    .el-menu--dark {
      background: transparent;

      .el-submenu .el-menu {
        background: $bg2;
      }
      .el-menu-item, .el-submenu__title {
        color: $color1;

        &.is-active {
          background: $bg4;
          color: #fff;
        }
      }
    }

    .nav-icon {
      display: inline-block;
      transform: scale(1.4);
      margin-right: 6px;
    }
  }
</style>
