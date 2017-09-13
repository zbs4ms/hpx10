<script>
/* 自定义组件 */
// 重置密码弹窗
import ResetPsdDialog from '@/components/_common/resetPsdDialog/ResetPsdDialog'
// 搜索输入框组件
import SearchInput from '@/components/_common/searchInput/SearchInput'

/* mixin */
import {
  keepAlive
} from '@/mixin/index'

/* 路由常量 */
import {
  INDEX,
  NEW_REGULATOR, // 新建监管机构
  EDIT, // 企业编辑
  LABEL, // 企业标签
  KIND, // 企业分类
  NEW_SUB_ENTERPRISE // 新建下级企业
} from './router'

import {
  enterpriseTypeMap
} from './const'

import {
  getListApi
} from './api'
import {
  resetPsdApi,
  changeAtStateApi
} from '@/rootApi'

import {
  convertDate
} from '@/utils/index'

export default {
  name: 'EnterpriseManage',
  components: {
    ResetPsdDialog,
    SearchInput
  },
  mixins: [
    keepAlive([NEW_REGULATOR.name, NEW_SUB_ENTERPRISE.name, EDIT.name, LABEL.name])
  ],
  created () {
    /* @@{ CONFIRM_ME：箭头函数的this怎么指向$eventBus } */
    let that = this
    that.$eventBus.$on(`${INDEX.name}.init`, (payload) => {
      that.init()
    })
  },
  data () {
    return {
      // 页面数据加载loading
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
      this.$nextTick(() => {
        this.$refs.searchInput.clearInput(false)
      })
      this.currentPage = 1
      this.searchKeyword = ''
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
    // 获取企业类型
    getTypeByCode (code) {
      return enterpriseTypeMap[code] || ''
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
    // 列表修改密码
    toResetPsd (accountId) {
      this.currAccountId = accountId
      this.modifyPsdVisible = true
    },
    // 切换到编辑页
    toEdit (companyId) {
      this.pushState(EDIT.name, {
        params: {
          id: companyId
        }
      })
    },
    // 切换到企业标签页
    toLabel (id) {
      this.pushState(LABEL.name, {
        params: { id }
      })
    },
    // 切换分类
    toKind (id) {
      this.pushState(KIND.name, {
        params: { id }
      })
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
    // 禁用或启用公司
    switchStatus (row, index) {
      let enable = row.enable
      this.$confirm(`是否确定${+enable === 1 ? '禁用' : '启用'}该企业账号?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: +enable === 1 ? 'warning' : 'info'
      }).then(() => {
        changeAtStateApi({
          companyId: row.companyId
        }).then(() => {
          this.getList({
            query: this.searchKeyword,
            pageSize: this.currentPage
          })
          this.$message({
            type: 'success',
            message: `${+enable === 1 ? '禁用' : '启用'}企业账号成功`
          })
        })
      })
    },
    // 转换时间
    convertDate (timeStamp) {
      return convertDate(timeStamp)
    }
  }
}
</script>

<template>
  <div id="enterprise-manange">
    <h3 class="page-title">企业管理</h3>
    <div class="table-toolbar flex">
      <search-input
        ref="searchInput"
        placeholder="名称/code"
        @search="handleSearch">
      </search-input>
      <div class="table-toolbar--right">
        <el-button
          type="primary"
          @click="pushState('enterprise_newRegulator')">
          新建监管机构
        </el-button>
        <el-button
          type="primary"
          @click="pushState('enterprise_newSubEnterprise')">
          新建下级企业
        </el-button>
      </div>
    </div>
    <el-table
      v-loading="pageLoading"
      class="search-table"
      :data="tableData"
      :stripe="true"
      border
      tooltip-effect="dark"
      style="width: 100%">
      <el-table-column
        prop="companyName"
        label="企业名称"
        min-width="250"
        :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column
        prop="type"
        label="企业类型"
        min-width="120"
        :show-overflow-tooltip="true"
        :formatter="(row, column) => this.getTypeByCode(row.type)">
      </el-table-column>
      <el-table-column
        prop="accountName"
        label="管理员账号"
        min-width="170"
        :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column
        prop="expireTime"
        label="有效期"
        min-width="120"
        :show-overflow-tooltip="true"
        :formatter="(row, column) => {
          return this.convertDate(row.expireTime)
        }">
      </el-table-column>
      <el-table-column
        prop="enable"
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
            @click="toResetPsd(scope.row.accountId)">
            修改密码
          </el-button>
          <el-button
            type="text"
            size="small"
            @click="toEdit(scope.row.companyId)">
            编辑
          </el-button>
          <el-button
            type="text"
            size="small"
            @click="toLabel(scope.row.companyId)">
            标签
          </el-button>
          <el-button
            type="text"
            size="small"
            @click="toKind(scope.row.companyId)">
            分类
          </el-button>
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
  #enterprise-manange {
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
