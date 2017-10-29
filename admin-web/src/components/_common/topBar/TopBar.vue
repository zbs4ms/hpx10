<script>
  import { Cookie } from '@/utils/index'
  import { mapState, mapMutations } from 'vuex'
  import {
    UPDATE_ACCOUNTINFO
  } from '@/store/global'

  import {
    logoutApi
  } from './api'

  export default {
    name: 'TopBar',
    data () {
      return {
      }
    },
    computed: {
      ...mapState({
        // 用户名
        userName: state => state.accountInfo.account
      })
    },
    methods: {
      ...mapMutations({
        updateAccountInfo: UPDATE_ACCOUNTINFO // 更新vuex中的accountInfo和auth
      }),
      // 清除cookie
      logout () {
        logoutApi().then(() => {
          Cookie.remove('login')
          localStorage.removeItem('accountInfo')
          this.$router.push('/login')
          this.updateAccountInfo({})
          this.$message({
            type: 'success',
            message: '退出成功'
          })
        })
      }
    }
  }
</script>

<template>
  <div id="top-bar" class="clr">
    <div class="user flex--vcenter rt">
      <i class="icon-user2"></i>
      <el-dropdown>
        <span class="el-dropdown-link">
          {{ userName }}
          <i class="el-icon-caret-bottom el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<style lang="scss">
  @import "../../../assets/style/variables/index";

  #top-bar {
    width: 100%;
    height: $topBar_h;

    .icon-user2 {
      font-size: 18px;
      margin-right: 6px;
      color: $color6;
    }
    .el-dropdown-link {
      cursor: pointer;
    }
  }
</style>
