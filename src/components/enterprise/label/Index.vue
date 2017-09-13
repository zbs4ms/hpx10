<script>
  import EnterpriseLabel from '@/components/_common/label/Label'

  import {
    getLabelsApi // 获取所有标签接口
  } from '@/rootApi'

  import {
    getChosenApi,
    updateChosenApi
  } from '../api'

  import {
    INDEX
  } from '../router'

  export default {
    name: 'EnterpriseLabelInfo',
    components: {
      EnterpriseLabel
    },
    created () {
      this.init()
    },
    data () {
      return {
        // 页面数据加载loading
        pageLoading: false,
        sorts: [],
        chosen: {
        },
        submitLoading: false
      }
    },
    methods: {
      // 初始化页面
      init () {
        this.pageLoading = true
        getLabelsApi().then(data => {
          let results = data.content || []
          this.sorts = results
          let chosen = {}
          results.forEach(item => {
            if (item.attribute === 1) {
              chosen[item.title.id] = ''
            } else {
              chosen[item.title.id] = []
            }
          })
          getChosenApi(this.$route.params.id).then(data => {
            let content = data.content || {}

            Object.keys(content.labels || {}).forEach(item => {
              let itemVal = content.labels[item]
              if (Array.isArray(itemVal)) {
                itemVal = itemVal.map(item => item + '')
              } else {
                itemVal += ''
              }
              chosen[item] = itemVal
            })
          }).finally(() => {
            this.pageLoading = false
          })
          this.chosen = chosen
        })
      },
      // 保存
      submit () {
        this.submitLoading = true

        let labelInfoIdList = []
        let chosen = this.chosen
        Object.keys(chosen).forEach((key) => {
          let item = chosen[key]
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

        updateChosenApi(JSON.stringify({
          companyId: +this.$route.params.id,
          labelInfoIdList: labelInfoIdList
        })).then(() => {
          this.$message({
            type: 'success',
            message: '企业标签更新成功'
          })
          this.$router.push(INDEX.path)
          this.$eventBus.$emit(`${INDEX.name}.init`)
        }).catch(() => {
          this.$message({
            type: 'error',
            message: '企业标签更新失败'
          })
        }).finally(() => {
          this.submitLoading = false
        })
      }
    }
  }
</script>

<template>
  <div class="enterprise-label-info" v-loading="pageLoading">
    <h3 class="page-title">企业标签</h3>
    <enterprise-label
      type="use"
      :sorts.sync="sorts"
      :chosen.sync="chosen"></enterprise-label>
    <div
      class="btn-wrap"
      style="text-align: center">
      <el-button @click="$router.push({ name: 'enterprise_index' })">返回</el-button>
      <el-button
        type="primary"
        :disabled="submitLoading"
        v-loading="submitLoading"
        @click="submit">
        保存
      </el-button>
    </div>
  </div>
</template>

<style lang="scss">
</style>
