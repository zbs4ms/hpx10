<script>
  import CommonInputs from '../_thumbs/CommonInputs'

  import {
    INDEX
  } from '../router'

  import {
    newAccountApi,
    getEnterpriseListApi,
    getParentListApi
  } from '../api'

  import {
    getTreeApi
  } from '@/rootApi'

  // 权限选择
  import Auth from '@/components/_common/auth/Auth'

  export default {
    name: 'NewSubEnterprise',
    components: {
      CommonInputs,
      Auth
    },
    created () {
      getParentListApi().then(data => {
        this.parents = data.content || []
      })
    },
    data () {
      return {
        extraInputs: {
          companyName: '', // 公司名
          parent: '' // 上级企业(json)
        },
        info: {
          code: '',
          bbdCompanyId: ''
        },
        code2: '',
        // 上级企业companyId
        parentId: '',
        // 企业搜索结果
        enterprises: [],
        // 企业搜索loading
        enterprisesLoading: false,
        // 上级企业
        parents: [],
        toNext: false,
        submitLoading: false
      }
    },
    watch: {
    },
    methods: {
      // 下一步
      handleToNext (formData) {
        // companyName在提交时需要转换一下（当前为json字符串）
        this.info = Object.assign({}, this.info, formData)
        this.parentId = JSON.parse(this.info.parent).id
        this.toNext = true
      },
      // 企业搜索
      searchEnterprises (query) {
        this.enterprisesLoading = true
        getEnterpriseListApi(query).then(data => {
          this.enterprises = data.content
        }).finally(() => {
          this.enterprisesLoading = false
        })
      },
      // 从搜索列表中选择企业
      handlePickEnterprise (item) {
        let data = JSON.parse(item)
        this.info.code = data.creditCode || ''
        this.code2 = data.creditCode || ''
        this.info.bbdCompanyId = data.bbdCompanyId || ''
      },
      // 提交修改
      submit () {
        this.submitLoading = true
        let info = Object.assign({}, this.info)
        if (+info.isCreateSubordinate === 1) {
          info.subordinateNumber = '0'
        }
        let companyObj = JSON.parse(info.companyName)
        let parentObj = JSON.parse(info.parent)
        delete info.parent
        let chosen = this.$refs.auth.getCheckedKeys()
        newAccountApi(Object.assign({}, info, {
          companyName: companyObj.companyName,
          companyType: (+parentObj.companyType) + 1,
          parentId: parentObj.id,
          type: 0,
          permissionsAndPermissionTrees: JSON.stringify(chosen)
        })).then(() => {
          this.$message({
            type: 'success',
            message: '添加账号成功'
          })

          this.$router.push({
            name: INDEX.name
          })
          this.$eventBus.$emit(`${INDEX.name}.init`)
        }).finally(() => {
          this.submitLoading = false
        })
      },
      getTreeApi (companyId) {
        return getTreeApi(companyId)
      }
    },
    filters: {
      json (val) {
        return JSON.stringify(val)
      }
    }
  }
</script>

<template>
  <div class="new-subenterprise">
    <transition-group
      :name="toNext ? 'slide-left' : 'slide-right'"
      mode="out-in">
      <common-inputs
        ref="commonInputs"
        key="input"
        v-show="!toNext"
        page-title="新建下级企业"
        :key-map="{
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
            label="企业名称："
            prop="companyName"
            :rules="[{
              required: true, message: '请选择企业', trigger: 'blur'
            }]">
            <el-select
              v-model="scope.formData.companyName"
              filterable
              remote
              placeholder="搜索企业"
              :remote-method="searchEnterprises"
              :loading="enterprisesLoading"
              @change="handlePickEnterprise">
              <el-option
                v-for="item in enterprises"
                :key="item.bbdQyxxId"
                :label="item.companyName"
                :value="item | json">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            label="CODE：">
            <span>{{ code2 }}</span>
          </el-form-item>
          <el-form-item
            label="上级机构："
            prop="parent"
            :rules="[{
              required: true, message: '请选择上级机构', trigger: 'change'
            }]">
            <el-select
              v-model="scope.formData.parent"
              placeholder="请选择上级机构"
              :loading="enterprisesLoading">
              <el-option
                v-for="item in parents"
                :key="item.id"
                :label="item.name"
                :value="item | json">
              </el-option>
            </el-select>
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
            :get-tree-api="getTreeApi"
            :account-id="parentId">
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
  .new-subenterprise {
    width: 600px;

    .rule-form {
      .is-required {
        .el-form-item__label:before {
          display: none;
        }
      }
    }

    .btn-wrap {
      margin-top: 20px;
    }
  }
</style>
