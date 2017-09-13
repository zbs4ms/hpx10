<script>
  import Auth from '@/components/_common/auth/Auth'
  import {
    INDEX
  } from '../router'

  import {
    updateAuthApi
  } from '../api'

  export default {
    name: 'StaffAuth',
    components: {
      Auth
    },
    data () {
      return {
        submitLoading: false
      }
    },
    methods: {
      submit () {
        this.submitLoading = true
        let chosen = this.$refs.auth.getCheckedKeys()
        updateAuthApi({
          accountId: this.$route.params.id,
          permissionsAndPermissionTrees: JSON.stringify(chosen)
        })
        .then(() => {
          this.$router.push(INDEX.path)
          this.$eventBus.$emit(`${INDEX.name}.init`)
          this.$message({
            type: 'success',
            message: '权限修改成功'
          })
        })
        .finally(() => {
          this.submitLoading = false
        })
      },
      cancel () {
        this.$router.push({
          name: INDEX.name
        })
      }
    }
  }
</script>

<template>
  <div id="staff-auth">
    <h3 class="page-title" style="margin-bottom: 0;">请选择账号权限</h3>
    <auth
      ref="auth"
      :account-id="$route.params.id">
    </auth>
    <div class="btn-wrap">
      <el-button
        type="primary"
        v-loading="submitLoading"
        :disabled="submitLoading"
        @click="submit">
        确定
      </el-button>
      <el-button
        :disabled="submitLoading"
        @click="cancel">
        取消
      </el-button>
    </div>
  </div>
</template>

<style lang="scss">
  #staff-auth {
    .btn-wrap {
      margin-top: 20px;
    }
  }
</style>
