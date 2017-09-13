<script>
  /* 自定义组件 */
  import AuthTree from '@/components/_common/auth/AuthTree'

  import {
    getTreeApi,
    updateTreeApi
  } from './api'

  export default {
    name: 'AuthCfg',
    components: {
      AuthTree
    },
    created () {
      this.treeLoading = true
      getTreeApi().then(data => {
        let content = data.content || {}
        let treeData = content.permissonAndTreeDtoArrayList || []
        this.treeData = treeData
      }).finally(() => {
        this.treeLoading = false
      })
    },
    data () {
      return {
        treeLoading: false, // 加载树结构loading
        submitLoading: false, // 提交树结构修改loading
        treeData: []
      }
    },
    methods: {
      // 提交修改
      submit () {
        this.submitLoading = true
        updateTreeApi({
          permissionTrees: JSON.stringify(this.treeData)
        }).finally(() => {
          this.submitLoading = false
        }).then(() => {
          this.$message({
            type: 'success',
            message: '权限配置修改成功'
          })
        })
      }
    }
  }
</script>

<template>
  <div id="auth-cfg">
    <h3 class="page-title">权限配置</h3>
    <auth-tree
      type="configure"
      :tree-data.sync="treeData">
    </auth-tree>
    <div class="btn-wrap">
      <el-button
        :disabled="submitLoading || treeLoading"
        v-loading="submitLoading"
        type="primary"
        @click="submit">
        提交
      </el-button>
    </div>
  </div>
</template>

<style lang="scss">
  #auth-cfg {
    .btn-wrap {
      margin-top: 20px;
    }
  }
</style>
