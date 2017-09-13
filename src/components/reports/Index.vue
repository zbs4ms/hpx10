<script>
  /*
   * Created by zhengji
   * Date: 2017/7/26
   */
  // 搜索输入框组件
  import SearchInput from '@/components/_common/searchInput/SearchInput'
  import {
    DASHBOARD
  } from './router'

  import {
    getListApi
  } from '@/components/enterprise/api'

  /* mixin */
  import {
    keepAlive
  } from '@/mixin/index'

  export default {
    name: 'Reports',
    components: {
      SearchInput
    },
    mixins: [
      keepAlive([DASHBOARD.name])
    ],
    data () {
      return {
        // 页面数据加载loading
        pageLoading: false,
        searchKeyword: '',
        // 列表数据
        tableData: [{
          companyName: 'fff'
        }],
        // 当前页码
        currentPage: 1,
        // 每页显示条数
        pageSize: 2,
        // 列表查询数据条数
        listTotal: 1
      }
    },
    created () {
    },
    computed: {
      DASHBOARD () {
        return DASHBOARD
      }
    },
    methods: {
      // 初始化页面
      init () {
        this.$nextTick(() => {
          this.$refs.searchInput.clearInput(false)
        })
        this.currentPage = 1
        this.searchKeyword = ''
        this.getList({
          pageSize: this.pageSize
        })
      },
      handleSearch (keyword) {
        this.searchKeyword = keyword
        this.getList({
          query: keyword || undefined
        }).then(() => {
          this.currentPage = 1
        })
      },
      handlePageChange (current) {
        this.getList({
          query: this.searchKeyword,
          startPage: current
        })
      },
      // 查询列表
      getList ({ query, startPage = 1, pageSize }) {
        this.pageLoading = true
        return getListApi({
          query,
          startPage,
          pageSize: this.pageSize
        }).then((data) => {
          let content = data.content || {}
          this.tableData = content.list || []
          this.listTotal = content.total
        }).finally(() => {
          this.pageLoading = false
        })
      },
      // 模块内跳转页面（会缓存当前路由组件的数据和状态）
      pushState (routeName, opts = {}) {
        // 缓存当前组件数据和状态
        this.keepAlive(this.$options.name)

        this.$nextTick(() => {
          this.$router.push({
            name: routeName,
            ...opts
          })
        })
      }
    }
  }
</script>

<template>
  <div class="report-search">
    <h3 class="page-title">企业管理</h3>
    <div class="table-toolbar flex">
      <search-input
        ref="searchInput"
        placeholder="企业名称"
        @search="handleSearch">
      </search-input>
    </div>
    <el-table
      v-loading="pageLoading"
      class="search-table"
      :data="tableData"
      :stripe="true"
      border
      tooltip-effect="dark"
      style="margin-top: 20px">
      <el-table-column
        prop="companyName"
        label="企业名称"
        min-width="250"
        :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column
        label="操作"
        min-width="100">
        <template scope="scope">
          <el-button type="text" @click="pushState(DASHBOARD.name, { params: {companyId: scope.row.companyId} })">查看上传财务报表</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div
      class="pagination-wrap"
      v-if="listTotal > pageSize">
      <el-pagination
        :current-page.sync="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next, jumper"
        :total="listTotal"
        @current-change="handlePageChange">
      </el-pagination>
    </div>
  </div>
</template>

<style lang="scss">
  .report-search {
    .router-link {
      color: #20a0ff;
    }

    .pagination-wrap {
      text-align: center;
      margin-top: 20px;
    }
  }
</style>
