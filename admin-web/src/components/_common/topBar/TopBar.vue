<script>
  import { Cookie } from '@/utils/index'
  import { Message } from 'element-ui'

  import {
    logoutApi
  } from './api'

  export default {
    name: 'TopBar',
    data () {
      return {
        userName: '' // 姓名
      }
    },
    created () {
//      let accountInfo = JSON.parse(decodeURIComponent(Cookie.get('accountInfo')))
//      this.userName = accountInfo.account
    },
    methods: {
      // 清除cookie
      logout () {
        logoutApi().then(() => {
          Cookie.remove('login')
          this.$router.push('/login')
          Message({
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
      <el-dropdown>
        <span class="el-dropdown-link">
          {{ userName }}<i class="el-icon-caret-bottom el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <!--<el-dropdown-item @click.native="$router.push({name: 'user_index'})">我的帐号</el-dropdown-item>-->
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
    .user {
      padding-left: 20px;
      height: 100%;
      background: url('./images/icon-user.png') no-repeat left center;
      background-size: 18px 18px;
      cursor: pointer;
    }
  }
</style>
