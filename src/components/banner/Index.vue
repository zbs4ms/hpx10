<script>
/*
 * Created by zhengji
 * Date: 2017/8/29
 */
import placeholderImg from '@/assets/images/placeholder.png'

import {
  getListApi,
//  deleteBannerApi,
  deleteBannerBatchApi,
  addBanenrApi,
  modifyBannerApi,
  switchVisibleApi
} from './api'

import { Loading } from 'element-ui'
import EditDialog from './_thumbs/EditDialog.vue'
import ImgZoom from '@/components/_common/imgZoom/ImgZoom.vue'

let adding = false
export default {
  name: 'Banner',
  components: {
    EditDialog,
    ImgZoom
  },
  data () {
    return {
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      tableLoading: false,
      tableData: [],
      multipleSelection: [],
      editDialogVisible: false,
      editData: null
    }
  },
  created () {
    this.init()
    this.placeholderImg = placeholderImg
  },
  watch: {
    editDialogVisible (val) {
      if (!val) {
        this.editData = null
        adding = false
      }
    },
    currentPage (newPageNum) {
      this.getList({
        pageNum: newPageNum
      })
    }
  },
  methods: {
    init () {
      this.getList({
        pageNum: 1
      })
    },
    // 修改展示数量
    changeDisplayNum () {
      this.$prompt('新展示个数', '修改展示数量', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\s*\d+\s*$/,
        inputErrorMessage: '请填入数字',
        confirmButtonClass: 'dialog-confirm-btn',
        beforeClose (action, instance, done) {
          if (action === 'confirm') {
            const loading = Loading.service({ fullscreen: true })
            setTimeout(() => {
              loading.close()
              console.log(loading)
              done()
            }, 2000)
          } else {
            done()
          }
        }
      }).then(({ value }) => {
        this.$message({
          type: 'success',
          message: '你的邮箱是: ' + value
        })
      })
    },
    // 获取banner列表
    getList (params) {
      this.tableLoading = true
      return getListApi(Object.assign({}, {
        name: this.searchKeyword,
        enable: '',
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        orderBy: 'order_number',
        desc: true
      }, params || {})).then((data) => {
        let content = data.content || {}
        this.tableData = (content.list || []).map((item) => ({
          no: item.orderNumber,
          id: item.id,
          name: item.name,
          cover: item.bannerUrl,
          link: item.jumpUrl,
          visible: !!item.display
        }))
        this.total = content.total || 0
      }).finally(() => {
        this.tableLoading = false
      })
    },
    // 搜索banner
    handleSearch (e) {
      this.getList({
        name: this.searchKeyword,
        pageNum: 1
      })
    },
    // 多选
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    // 编辑或新增
    openEditDialog (rowData, isAdd) {
      this.editDialogVisible = true
      this.editData = rowData
      adding = !!isAdd
    },
    // 删除单个banner
    delRow (row) {
      this.$confirm('是否删除该信息？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            deleteBannerBatchApi({
              bannerIdList: row.id
            }).then(res => {
              done()
              this.$message({
                type: 'success',
                message: '删除成功'
              })
              this.getList()
            })
          } else {
            done()
          }
        }
      })
    },
    // 批量删除
    batchRemove () {
      deleteBannerBatchApi({
        bannerIdList: this.multipleSelection.map(item => item.id).join(',')
      }).then(res => {
        this.$message({
          type: 'success',
          message: '删除成功'
        })
        this.getList()
      })
    },
    handleEditCancel () {
    },
    // 提交编辑或新增
    handleEditSubmit (data, respondCb) {
      let formData
      if (data.file) {
        formData = new FormData()
        formData.append('file', data.file)
      }
      let sendData = {
        name: data.name,
        jumpUrl: data.link,
        orderNumber: data.no,
        bannerId: data.id
      }
      let requestFn = adding ? addBanenrApi : modifyBannerApi
      requestFn(sendData, formData).then(res => {
        this.$message({
          type: 'success',
          message: adding ? '添加成功' : '修改成功'
        })
        this.init()
        respondCb(true)
      }).catch(() => {
        respondCb()
      })
    },
    switchVisible (rowData) {
      switchVisibleApi({
        bannerId: rowData.id
      }).then(res => {
        this.$message({
          type: 'success',
          message: rowData.visible ? '显示成功' : '隐藏成功'
        })
      }).finally(() => {
        this.getList()
      })
    }
  }
}
</script>

<template>
  <div id="banner">
    <div class="flex--vcenter page-top">
      <div class="page-title">
        BANNER管理
      </div>
      <div class="display-num-control">
        <span class="label">展示数量 ：</span>
        <span class="display-num">4个</span>
        <span
          class="el-icon-edit"
          @click="changeDisplayNum">
        </span>
      </div>
    </div>
    <div class="table-tools flex--vcenter">
      <div class="search-wrap">
        <span class="search-label">搜索关键字：</span>
        <el-input
          class="inline-block search-input"
          placeholder="请在此输入名称／ID"
          icon="search"
          v-model="searchKeyword"
          @keyup.enter.native="handleSearch"
          :on-icon-click="handleSearch">
        </el-input>
      </div>
      <div class="btn-wrap">
        <el-button
          class="btn--add"
          type="primary"
          @click="openEditDialog(null, true)">
          新增 <i class="el-icon-plus"></i>
        </el-button>
        <el-button
          :disabled="!multipleSelection.length"
          @click="batchRemove">
          批量删除
        </el-button>
      </div>
    </div>
    <el-table
      v-loading="tableLoading"
      ref="multipleTable"
      :data="tableData"
      tooltip-effect="dark"
      style="width: 100%"
      @selection-change="handleSelectionChange">
      <el-table-column
        type="selection"
        width="90">
      </el-table-column>
      <el-table-column
        prop="no"
        label="排序"
        min-width="80">
      </el-table-column>
      <!--<el-table-column-->
        <!--prop="id"-->
        <!--label="ID号"-->
        <!--min-width="120">-->
      <!--</el-table-column>-->
      <el-table-column
        prop="name"
        label="名称"
        min-width="140"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column
        prop="cover"
        min-width="120"
        label="封面图">
        <template scope="scope">
          <img-zoom
            :src="scope.row.cover"
            style="width: 80px;height: 60px;">
          </img-zoom>
        </template>
      </el-table-column>
      <el-table-column
        prop="link"
        label="跳转链接"
        min-width="160"
        show-overflow-tooltip>
        <template scope="scope">
          <a :href="scope.row.link" target="_blank">{{ scope.row.link }}</a>
        </template>
      </el-table-column>
      <el-table-column
        min-width="200"
        label="操作">
        <template scope="scope">
          <div class="flex--center operations">
            <span
              class="operate-item el-icon-edit"
              @click="openEditDialog(scope.row)">
            </span>
            <span
              class="operate-item el-icon-delete"
              @click="delRow(scope.row)">
            </span>
            <span class="operate-item visible-switch flex--vcenter">
              <el-switch
                v-model="scope.row.visible"
                @change="switchVisible(scope.row)"
                on-text=""
                off-text="">
              </el-switch>
              {{ scope.row.visible ? '显示' : '隐藏' }}
            </span>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <edit-dialog
      v-model="editDialogVisible"
      :data="editData"
      @cancel="handleEditCancel"
      @submit="handleEditSubmit">
    </edit-dialog>
    <div class="pagination-wrap">
      <el-pagination
        v-if="total > pageSize"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<style lang="scss">
  @import "~@/assets/style/variables/index";

  #banner {
    .display-num-control {
      margin-left: 60px;
      .label {
        color: $color3;
      }

      .el-icon-edit {
        color: #adb9ca;
        cursor: pointer;
      }
    }

    .table-tools {
      margin-top: 30px;
      justify-content: space-between;
    }
    .search-input {
      width: 300px;
      input {
        border-radius: 18px;
      }
    }
    .search-label {
      color: $color3;
    }
    .btn-wrap {
      .el-button {
        border-radius: 18px;
      }
    }
    .btn--del {
      background: $bg5;
      color: #fff;
      &:hover {
        border-color: transparent;
      }
    }

    .el-table {
      margin-top: 20px;
      th + th, td + td {
        text-align: center;
      }
      td {
        height: 80px;
      }
    }
    .el-table__header {
      th:first-child {
        .cell {
          &:after {
            content: '全选';
            margin-left: 5px;
          }
        }
      }
    }
    .cover-img {
      vertical-align: middle;
      display: inline-block;
      background: $bg6;
    }
    .cover-noimg {
      background: $bg6 url('~@/assets/images/placeholder.png') center no-repeat;
      background-size: 40px 30px;
    }

    .operate-item {
      color: $color4;
      font-size: 18px;
      cursor: pointer;
      & + .operate-item {
        margin-left: 20px;
      }

      .el-switch {
        margin-right: 10px;
      }
    }
    .visible-switch {
      color: $color3;
      font-size: 14px;
    }

    .pagination-wrap {
      margin-top: 30px;
      text-align: right;
    }
  }
</style>
