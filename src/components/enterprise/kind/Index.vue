<script>
  import {
    getKindApi,
    getKindsApi,
    updateKindApi
  } from '../api'
  import {
    INDEX
  } from '../router'

  export default {
    name: 'EnterpriseKindInfo',
    data () {
      return {
        pageLoading: false,
        submitLoading: false,
        enterpriseForm: {
          name: '',
          kind: ''
        },
        // 数据
        moreBtn: [],
        // 单选
        submitAll: {}
      }
    },
    created () {
      this.init()
    },
    methods: {
      // 初始化
      init () {
        this.pageLoading = true
        getKindApi().then((data) => {
          let results = data.content || []
          this.moreBtn = results
          let submitAll = {}
          results.forEach(item => {
            if (item.attribute === 1) {
              submitAll[item.title.id] = ''
            } else {
              submitAll[item.title.id] = []
            }
          })
          getKindsApi(
            this.$route.params.id
          ).then((data) => {
            this.pageLoading = false
            let content = data.content || {}
            this.enterpriseForm.name = content.companyName
            this.enterpriseForm.kind = content.gzwTypeName
            Object.keys(content.labels || {}).forEach(item => {
              let itemVal = content.labels[item]
              if (Array.isArray(itemVal)) {
                itemVal = itemVal.map(item => item + '')
              } else {
                itemVal += ''
              }
              submitAll[item] = itemVal
            })
            this.submitAll = submitAll
          })
        })
      },
      // 提交
      submit () {
        this.submitLoading = true
        let labelInfoIdList = []
        let submitAll = this.submitAll
        Object.keys(submitAll).forEach((key) => {
          let item = submitAll[key]
          if (Array.isArray(item)) {
            item.forEach((_item) => {
              labelInfoIdList.push(+_item)
            })
          } else {
            if (item) {
              labelInfoIdList.push(+item)
            }
          }
        })
        if (labelInfoIdList.length === 0) {
          labelInfoIdList = [1]
        }

        updateKindApi(JSON.stringify({
          companyId: +this.$route.params.id,
          labelInfoIdList: labelInfoIdList,
          gzwTypeName: this.enterpriseForm.kind
        })).then(() => {
          this.$message({
            type: 'success',
            message: '企业分类更新成功'
          })
          this.$router.push(INDEX.path)
        }).catch(() => {
          this.$message({
            type: 'error',
            message: '企业分类更新失败'
          })
        }).finally(() => {
          this.submitLoading = false
        })
      }
    }
  }
</script>

<template>
  <div class="enterprise-kind-info" v-loading="pageLoading">
    <h3 class="page-title">企业分类</h3>
    <p>（以下分类主要用于绩效评价的标准值选择）</p>
    <el-form
      label-position="left"
      label-width="100px"
      :model="enterpriseForm">
      <el-form-item label="企业名称：">
        <template>
          <span>{{ enterpriseForm.name }}</span>
        </template>
      </el-form-item>
      <el-form-item
        v-for="item in moreBtn"
        :label="item.title.name"
        :key="item.title.id">
        <el-radio-group
          v-model="submitAll[item.title.id + '']">
          <el-radio
            v-for="list in item.labelInfoList"
            :label="list.id + ''"
            :key="list.id">
              {{ list.name }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        label="行业分类："
        prop="kind">
        <el-input
          placeholder="填写样例：工业_电力热力燃气工业_电力生产业_水力发电业"
          style="width: 450px;"
          v-model="enterpriseForm.kind">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="$router.push({ name: 'enterprise_index' })">
          返回
        </el-button>
        <el-button
          type="primary"
          :disabled="submitLoading"
          v-loading="submitLoading"
          @click="submit">
          提交
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss">
  .enterprise-kind-info {
    .enterprise-info-item {
      margin-bottom: 20px;
    }
    .item-label {
      display: inline-block;
      width: 70px;
    }
  }
</style>
