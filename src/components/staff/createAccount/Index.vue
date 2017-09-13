<script>
  import AccountInfo from '../_thumbs/AccountInfo'
  // 权限选择
  import Auth from '@/components/_common/auth/Auth'

  import {
    createStaffApi
  } from '../api'

  import {
    getTreeApi
  } from '@/rootApi'

  import {
    INDEX
  } from '../router'

  export default {
    name: 'CreateAccount',
    components: {
      AccountInfo,
      Auth
    },
    created () {
    },
    data () {
      return {
        info: {},
        toNext: false,
        submitLoading: false
      }
    },
    methods: {
      handleCancel () {
        this.$router.push({
          name: INDEX.name
        })
      },
      handleToNext (formData) {
        this.toNext = true
        this.info = Object.assign({}, this.info, formData)
      },
      // 获取权限树
      getTree () {
        return getTreeApi(this.info.companyId)
      },
      submit () {
        this.submitLoading = true
        let chosen = this.$refs.auth.getCheckedKeys()
        createStaffApi(Object.assign({}, this.info, {
          permissionsAndPermissionTrees: JSON.stringify(chosen)
        })).then(() => {
          this.$message({
            type: 'success',
            message: '创建账号成功'
          })
          this.$router.push({
            name: INDEX.name
          })
          this.$eventBus.$emit(`${INDEX.name}.init`)
        }).finally(() => {
          this.submitLoading = false
        })
      }
    }
  }
</script>

<template>
  <div id="account__create">
    <transition-group
      :name="toNext ? 'slide-left' : 'slide-right'"
      mode="out-in">
      <account-info
        v-show="!toNext"
        key="info"
        :data="info"
        type="create"
        @cancel="handleCancel"
        @to-next="handleToNext">
      </account-info>
      <div
        v-show="toNext"
        key="auth"
        class="auth">
        <h3 class="page-title" style="margin-bottom: 0;">请选择账号权限</h3>
        <keep-alive>
          <auth
            v-if="toNext"
            ref="auth"
            :get-tree-api="getTree">
          </auth>
        </keep-alive>
        <div class="btn-wrap">
          <el-button
            :disabled="submitLoading"
            @click="toNext = false">
            上一步
          </el-button>
          <el-button
            type="primary"
            :disabled="submitLoading"
            v-loading="submitLoading"
            @click="submit">
            添加账号
          </el-button>
        </div>
      </div>
    </transition-group>
  </div>
</template>

<style lang="scss">
  #account__create {
    .btn-wrap {
      margin-top: 20px;
    }
  }
</style>
