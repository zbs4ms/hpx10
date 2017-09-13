<script>
  /* 自定义组件 */
  import CommonInputs from '../_thumbs/CommonInputs'
  // 权限选择
  import Auth from '@/components/_common/auth/Auth'

  import {
    newAccountApi
  } from '../api'

  import {
    INDEX
  } from '../router'

  export default {
    name: 'NewRegulator',
    components: {
      CommonInputs,
      Auth
    },
    data () {
      return {
        extraInputs: {
          name: ''
        },
        info: {},
        toNext: false,
        submitLoading: false
      }
    },
    computed: {},
    methods: {
      // 下一步
      handleToNext (formData) {
        this.toNext = true
        if (+formData.isCreateSubordinate === 1) {
          formData.subordinateNumber = '0'
        }
        this.info = Object.assign({}, this.info, formData)
      },
      // 提交修改
      submit () {
        this.submitLoading = true
        let chosen = this.$refs.auth.getCheckedKeys()
        newAccountApi(Object.assign({}, this.info, {
          companyType: 0,
          type: 0,
          parentId: -1,
          companyEnable: 1,
          permissionsAndPermissionTrees: JSON.stringify(chosen)
        }))
        .then(() => {
          this.$router.push(INDEX.path)
          this.$eventBus.$emit(`${INDEX.name}.init`)
          this.$message({
            type: 'success',
            message: '新建成功'
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
  <div id="new-regulator">
    <transition-group
      :name="toNext ? 'slide-left' : 'slide-right'"
      mode="out-in">
      <common-inputs
        v-show="!toNext"
        key="input"
        page-title="新建监管机构"
        :key-map="{
          name: 'companyName',
          adminAccount: 'account',
          adminMail: 'email',
          expire: 'expireDate',
          subAccountNum: 'accountNumber',
          allowNewChild: 'isCreateSubordinate',
          subEnterpriseNum: 'subordinateNumber'
        }"
        :init-data="info"
        :extra-inputs="extraInputs"
        @to-next="handleToNext">
        <template slot="extra-inputs" scope="scope">
          <el-form-item
            label="监管机构名称："
            prop="name"
            :rules="[{
              required: true, message: '请输入监管机构名称', trigger: 'blur'
            }]">
            <el-input
              v-model.trim="scope.formData.name"
              auto-complete="off"
              placeholder="名称">
            </el-input>
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
            account-id="-1">
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
#new-regulator {
  .btn-wrap {
    margin-top: 20px;
  }
}
</style>
