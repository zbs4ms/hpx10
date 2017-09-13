<script>
  /* 自定义组件 */
  import CommonInputs from '../_thumbs/CommonInputs'
  // 权限选择
  import Auth from '@/components/_common/auth/Auth'

  import {
    enterpriseTypeMap
  } from '../const'

  import {
    INDEX
  } from '../router'

  import {
    getEnterpriseInfoApi,
    updateEnterpriseApi
  } from '../api'

  export default {
    name: 'EditEnterprise',
    components: {
      CommonInputs,
      Auth
    },
    created () {
      this.init()
    },
    data () {
      return {
        pageLoading: false,
        extraInputs: {
        },
        // 企业信息
        info: {
        },
        toNext: false,
        submitLoading: false
      }
    },
    computed: {},
    methods: {
      init () {
        this.pageLoading = true
        let companyId = this.$route.params.id
        getEnterpriseInfoApi(companyId).then((data) => {
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
      // 获取企业类型
      getTypeByCode (code) {
        return enterpriseTypeMap[code] || ''
      },
      // 下一步
      handleToNext (formData) {
        this.toNext = true
        this.info = Object.assign({}, this.info, formData)
      },
      // 提交修改
      submit () {
        this.submitLoading = true
        let chosen = this.$refs.auth.getCheckedKeys()
        updateEnterpriseApi(Object.assign({}, this.info, {
          permissionsAndPermissionTrees: JSON.stringify(chosen)
        }))
        .then(() => {
          this.$router.push(INDEX.path)
          this.$eventBus.$emit(`${INDEX.name}.init`)
          this.$message({
            type: 'success',
            message: '编辑提交成功'
          })
        })
        .finally(() => {
          this.submitLoading = false
        })
      }
    }
  }
</script>

<template>
  <div id="enterprise-edit" v-loading="pageLoading">
    <transition-group
      :name="toNext ? 'slide-left' : 'slide-right'"
      mode="out-in">
      <common-inputs
        v-show="!toNext"
        key="info"
        page-title="编辑企业"
        :key-map="{
          adminAccount: 'account',
          adminMail: 'email',
          expire: 'expireTime',
          subAccountNum: 'accountNumber',
          allowNewChild: 'isCreateSubordinate',
          subEnterpriseNum: 'subordinateNumber'
        }"
        :init-data="info"
        :extra-inputs="extraInputs"
        @to-next="handleToNext">
        <template slot="extra-inputs" scope="scope">
          <el-form-item
            label="机构名称：">
            <span>{{ info.companyName }}</span>
          </el-form-item>
          <el-form-item
            label="类型：">
            <span>{{ getTypeByCode(info.type)  }}</span>
          </el-form-item>
          <el-form-item
            v-if="info.code"
            label="code：">
            <span>{{ info.code }}</span>
          </el-form-item>
          <el-form-item
            v-if="info.parentCompanyName"
            label="上级机构：">
            <span>{{ info.parentCompanyName }}</span>
          </el-form-item>
        </template>
      </common-inputs>
      <div
        v-show="toNext"
        key="auth"
        class="auth">
        <h3 class="page-title">请选择企业的权限</h3>
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
  #enterprise-edit {
    .btn-wrap {
      margin-top: 20px;
    }
  }
</style>
