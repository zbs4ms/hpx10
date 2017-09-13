<script>
  /* 自定义组件 */
  // 重置密码弹窗
  import ResetPsdDialog from '@/components/_common/resetPsdDialog/ResetPsdDialog'
  // 搜索输入框组件
  import SearchInput from '@/components/_common/searchInput/SearchInput'

  import {
    getListApi
  } from './api'

  /* mixin */
  import {
    keepAlive
  } from '@/mixin/index'

  import {
    INDEX,
    CREATE,
    EDIT,
    AUTH
  } from './router'
  import {
    resetPsdApi,
    changeStateApi
  } from '@/rootApi'

  export default {
    name: 'StaffManage',
    components: {
      ResetPsdDialog,
      SearchInput
    },
    mixins: [
      keepAlive([CREATE.name, EDIT.name, AUTH.name])
    ],
    created () {
      let that = this
      that.$eventBus.$on(`${INDEX.name}.init`, (payload) => {
        that.init()
      })
    },
    data () {
      return {
        // 页面加载数据loading
        pageLoading: false,
        searchKeyword: '',
        // 列表数据
        tableData: [],
        // 当前页码
        currentPage: 1,
        // 每页显示条数
        pageSize: 10,
        // 列表查询数据条数
        listTotal: 0,
        // 修改密码弹窗显示与否
        modifyPsdVisible: false,
        // 当前操作的accountId（重置密码。。）
        currAccountId: ''
      }
    },
    methods: {
      // 初始化页面
      init () {
        this.currentPage = 1
        this.getList({
          pageSize: this.pageSize
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
      // 企业搜索
      handleSearch (keyword) {
        this.searchKeyword = keyword
        this.getList({
          query: keyword || undefined
        }).then(() => {
          this.currentPage = 1
        })
      },
      // 翻页
      handlePageChange (current) {
        this.getList({
          query: this.searchKeyword,
          startPage: current
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
      },
      // 新建人员账号
      toCreateAccount () {
        this.pushState(CREATE.name)
      },
      // 切换到编辑页
      toEdit (accountId) {
        this.pushState(EDIT.name, {
          params: {
            id: accountId
          }
        })
      },
      // 切换到权限页面
      toAuth (accountId) {
        this.pushState(AUTH.name, {
          params: {
            id: accountId
          }
        })
      },
      // 列表修改密码
      toResetPsd (accountId) {
        this.currAccountId = accountId
        this.modifyPsdVisible = true
      },
      // 列表修改密码提交
      handleResetPsd (formData, resolve) {
        resetPsdApi({
          accountId: this.currAccountId,
          newPassword: formData.psd,
          secNewPassword: formData.rePsd
        }).then(() => {
          resolve()
          this.currAccountId = ''
          this.modifyPsdVisible = false
        })
      },
      // 禁用或启用账号
      switchStatus (row, index) {
        let enable = row.enable
        this.$confirm(`是否确定${+enable === 1 ? '禁用' : '启用'}该账号?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: +enable === 1 ? 'warning' : 'info'
        }).then(() => {
          changeStateApi({
            accountId: row.accountId
          }).then(() => {
            this.tableData[index].enable = +enable === 1 ? 0 : 1
            this.$message({
              type: 'success',
              message: `${+enable === 1 ? '禁用' : '启用'}企业人员账号成功`
            })
          })
        })
      }
    }
  }
</script>

<template>
  <div id="staff-manange">
    <h3 class="page-title">人员管理</h3>
    <div class="table-toolbar flex">
      <search-input
        placeholder="请输入真实姓名/账号/部门信息进行模糊查询"
        @search="handleSearch">
      </search-input>
      <div class="table-toolbar--right">
        <el-button
          type="primary"
          @click="toCreateAccount">
          新建人员账号
        </el-button>
      </div>
    </div>
    <el-table
      class="search-table"
      v-loading="pageLoading"
      :data="tableData"
      :stripe="true"
      border
      tooltip-effect="dark"
      style="width: 100%">
      <el-table-column
        prop="name"
        label="姓名"
        min-width="120"
        :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column
        prop="companyName"
        label="所属企业"
        min-width="200"
        :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column
        prop="department"
        label="部门"
        min-width="120"
        :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column
        prop="account"
        label="账号"
        min-width="170"
        :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column
        prop="status"
        label="账号状态"
        min-width="120"
        :show-overflow-tooltip="true">
        <template scope="scope">
          <el-tag
            :type="+scope.row.enable === 1 ? 'primary' : 'danger'"
            close-transition>{{ +scope.row.enable === 1 ? '已启用' : '已禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        min-width="200">
        <template scope="scope">
          <el-button
            type="text"
            size="small"
            :style="{ color: +scope.row.enable == 1 ? '#ff4949' : '#20a0ff' }"
            @click="switchStatus(scope.row, scope.$index)">
            {{ +scope.row.enable === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button
            type="text"
            size="small"
            @click="toResetPsd(scope.row.accountId)">重置密码</el-button>
          <el-button
            type="text"
            size="small"
            @click="toEdit(scope.row.memberInfoId)">编辑</el-button>
          <el-button
            type="text"
            size="small"
            @click="toAuth(scope.row.accountId)">权限</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div
      class="pagination-wrap"
      v-if="listTotal > 0">
      <el-pagination
        :current-page.sync="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next, jumper"
        :total="listTotal"
        @current-change="handlePageChange">
      </el-pagination>
    </div>
    <reset-psd-dialog
      v-model="modifyPsdVisible"
      @submit="handleResetPsd">
    </reset-psd-dialog>
  </div>
</template>

<style lang="scss">
  #staff-manange {
    .table-toolbar {
      justify-content: space-between;
    }

    .search-table {
      margin-top: 20px;
    }

    .pagination-wrap {
      margin-top: 20px;
      text-align: center;
    }
  }
</style>
