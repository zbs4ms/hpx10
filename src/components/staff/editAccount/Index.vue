<script>
  import AccountInfo from '../_thumbs/AccountInfo'
  // 权限选择
  import Auth from '@/components/_common/auth/Auth'

  import {
    getStaffInfoApi,
    updateStaffApi
  } from '../api'

  import {
    INDEX
  } from '../router'

  export default {
    name: 'EditAccount',
    components: {
      AccountInfo,
      Auth
    },
    created () {
      this.init()
    },
    data () {
      return {
        pageLoading: false,
        info: {},
        submitLoading: false,
        toNext: false
      }
    },
    methods: {
      init () {
        this.pageLoading = true
        let memberInfoId = this.$route.params.id
        getStaffInfoApi(memberInfoId).then((data) => {
          let content = data.content || {}
          Object.keys(content).forEach(item => {
            if (typeof content[item] === 'number') {
              content[item] += ''
            }
          })
          this.info = content
        }).finally(() => {
          this.pageLoading = false
        })
      },
      handleCancel () {
        this.$router.push({
          name: INDEX.name
        })
      },
      handleToNext (formData) {
        this.toNext = true
        this.info = Object.assign({}, this.info, formData)
      },
      handleSubmit (formData) {
        this.submitLoading = true
        updateStaffApi(Object.assign({}, this.info, formData))
        .then(() => {
          this.$message({
            type: 'success',
            message: '更新成功'
          })
          this.init()
        })
        .finally(() => {
          this.submitLoading = false
        })
      },
      submit () {
        this.submitLoading = true
        let chosen = this.$refs.auth.getCheckedKeys()
        updateStaffApi(Object.assign({}, this.info, {
          permissionsAndPermissionTrees: JSON.stringify(chosen)
        })).then(() => {
          this.$message({
            type: 'success',
            message: '编辑账号成功'
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
  <div id="account__edit" v-loading="pageLoading">
    <transition-group
      :name="toNext ? 'slide-left' : 'slide-right'"
      mode="out-in">
      <account-info
        v-show="!toNext"
        key="info"
        :data="info"
        type="edit"
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
            :account-id="info.accountId">
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
            提交
          </el-button>
        </div>
      </div>
    </transition-group>
  </div>
</template>

<style lang="scss">
  #account__edit {
    .btn-wrap {
      margin-top: 20px;
    }
  }
</style>
