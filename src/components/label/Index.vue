<script>
  // 导入组件
  import EnterpriseLabel from '@/components/_common/label/Label'
  import {
    addLabelApi, // 添加标签接口
    updateLabelApi, // 更新标签接口
    delLabelApi // 删除标签的接口
  } from './api'

  import {
    getLabelsApi // 获取所有标签接口
  } from '@/rootApi'

  export default {
    name: 'EnterpriseLabelCfg',
    components: {
      EnterpriseLabel
    },
    // 使用周期函数渲染数据
    created () {
      this.init()
    },
    data () {
      return {
        sorts: [],
        chosen: {}, // 分类名：标签选择（单选：字符串，多选：数组）
        // 页面加载 true/false 是否有数据
        pageLoading: true
      }
    },
    methods: {
      // 数据改变重新加载一次页面
      init () {
        this.pageLoading = true
        // 显示所有数据列表的方法
        getLabelsApi({
        })
        .then((data) => {
          this.pageLoading = false
          this.sorts = data.content
        })
        .finally(() => {
        })
      },
      // 更新标签内容
      handleUpdate ({ index, sort }, resolve) {
        sort.type = 1 // 需要传入的后端数据
        updateLabelApi({
          labels: JSON.stringify(sort) // 传入到后台的数据为JOSN格式
        }).then(() => {
          resolve()
          delete sort.type
          this.init()
          this.$message({
            type: 'success',
            message: '编辑成功'
          })
        })
        resolve()
      },
      // 添加新标签
      handleCreate ({ index, sort }, resolve) {
        sort.type = 1 // 需要传入的后端数据
        addLabelApi({
          labels: JSON.stringify(sort) // 传入到后台的数据为JOSN格式
        }).then(() => {
          resolve()
          delete sort.type
          this.init()
          this.$message({
            type: 'success',
            message: '添加成功'
          })
        })
      },
      // 删除标签
      handleDel ({ sort, index }, resolve) {
        // 判断两个状态(新增和编辑)
        if (sort.new) {
          this.sorts.splice(index, 1)
        } else {
          delLabelApi({
            id: sort.title.id  // 以ID来删除当前标签
          }).then(() => {
            resolve()
            delete sort.title.id
            this.init()
            this.$message({
              type: 'success',
              message: '删除成功'
            })
          })
        }
      }
    }
  }
</script>

<template>
  <div id="enterprise-label" v-loading="pageLoading">
    <h3 class="page-title">标签管理</h3>
    <enterprise-label
      type="configure"
      :sorts.sync="sorts"
      :chosen.sync="chosen"
      @update="handleUpdate"
      @create="handleCreate"
      @del="handleDel"></enterprise-label>
  </div>
</template>

<style lang="scss">
</style>
