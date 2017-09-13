<script>
  import { Cookie } from '@/utils/index'
  export default {
    name: 'UserManage',
    data () {
      return {
        userName: '', // 姓名
        department: '', // 部门
        account: '', // 账号
        email: '' // 邮箱
      }
    },
    created () {
      let accountInfo = JSON.parse(decodeURIComponent(Cookie.get('accountInfo')))
      Object.assign(this.$data, accountInfo)
    }
  }
</script>

<template>
  <div id="my-account">
    <h3 class="page-title">我的帐号</h3>
    <div class="account-info-item">
      <span class="item-label">姓名：</span>
      <span>{{ userName }}</span>
    </div>
    <div class="account-info-item">
      <span class="item-label">部门：</span>
      <span>{{ department }}</span>
    </div>
    <div class="account-info-item">
      <span class="item-label">帐号：</span>
      <span>{{ account }}</span>
    </div>
    <div class="account-info-item">
      <span class="item-label">邮箱：</span>
      <span>{{ email }}</span>
    </div>
  </div>
</template>

<style lang="scss">
  #my-account {
    .account-info-item {
      margin-bottom: 20px;
    }
    .item-label {
      display: inline-block;
      width: 70px;
    }
  }
</style>
