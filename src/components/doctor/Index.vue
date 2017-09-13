<script>
/*
 * Created by zhengji
 * Date: 2017/8/29
 */
import placeholderImg from '@/assets/images/placeholder.png'

import EditDialog from './_thumbs/EditDialog.vue'
import ImgZoom from '@/components/_common/imgZoom/ImgZoom.vue'

import {
  getListApi,
  modifyDoctorApi,
  topDoctorApi,
  queryDepartmentApi
} from './api'

export default {
  name: 'Doctor',
  components: {
    EditDialog,
    ImgZoom
  },
  data () {
    return {
      project: '',
      department: '',
      departmentId: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      tableLoading: false,
      tableData: [],
      multipleSelection: [],
      editDialogVisible: false,
      editData: {}
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
    searchProject () {
    },
    searchDepartment (queryString, cb) {
      console.log(queryString)
      queryDepartmentApi({
        pageNum: 1,
        pageSize: this.pageSize
      }).then(res => {
        let content = res.content || []
        let filters = content.filter(item => {
          return item.name.indexOf(queryString) !== -1
        }).map(item => {
          return {
            value: item.name,
            id: item.id
          }
        })
        cb(filters)
      })
    },
    handleProjectSelect () {},
    handleDepartmentSelect (item) {
      this.departmentId = item.id
    },
    search () {
      this.getList()
    },
    getList (params) {
      this.tableLoading = true
      return getListApi(Object.assign({}, {
        departmentId: this.departmentId,
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        orderBy: 'order_number',
        desc: true
      }, params || {})).then((data) => {
        let content = data.content || {}

        this.tableData = (content.list || []).map((item) => {
          let doctor = item.doctor || {}
          return {
            no: doctor.orderNumber,
            name: doctor.name,
            avatar: doctor.headPortrait,
            describe: doctor.goodDescribe,
            top: !!doctor.isTop,
            id: doctor.id
          }
        })
        this.total = content.total || 0
      }).finally(() => {
        this.tableLoading = false
      })
    },
    openEditDialog (rowData, isAdd) {
      this.editDialogVisible = true
      this.editData = rowData
    },
    handleEditCancel () {
    },
    handleEditSubmit (data, respondCb) {
      let formData
      if (data.file) {
        formData = new FormData()
        formData.append('file', data.file)
      }
      let sendData = {
        goodDescribe: data.describe,
        doctorId: data.id
      }
      modifyDoctorApi(sendData, formData).then(res => {
        this.$message({
          type: 'success',
          message: '修改成功'
        })
        this.init()
        respondCb(true)
      }).catch(() => {
        respondCb()
      })
    },
    // 切换置顶状态
    switchTop (rowData) {
      topDoctorApi({
        doctorId: rowData.id
      }).then(res => {
        this.$message({
          type: 'success',
          message: rowData.top ? '置顶成功' : '操作失败'
        })
      }).finally(() => {
        this.getList()
      })
    }
  }
}
</script>

<template>
  <div id="doctor">
    <div class="flex--vcenter page-top">
      <div class="page-title">
        医生信息录入
      </div>
    </div>
    <div class="table-tools flex--vcenter">
      <div class="tool-item">
        选择项目：
        <el-autocomplete
          v-model="project"
          :fetch-suggestions="searchProject"
          placeholder="输入内容搜索"
          @select="handleProjectSelect"
        ></el-autocomplete>
      </div>
      <div class="tool-item">
        选择科室：
        <el-autocomplete
          v-model="department"
          :fetch-suggestions="searchDepartment"
          placeholder="输入内容搜索"
          @select="handleDepartmentSelect"
        ></el-autocomplete>
      </div>
      <el-button
        class="tool-item"
        type="primary"
        @click="search">搜索</el-button>
    </div>
    <el-table
      ref="multipleTable"
      :data="tableData"
      v-loading="tableLoading"
      tooltip-effect="dark"
      style="width: 100%">
      <el-table-column
        prop="no"
        label="序号"
        min-width="50">
      </el-table-column>
      <el-table-column
        prop="name"
        label="姓名"
        min-width="100"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column
        prop="avatar"
        min-width="120"
        label="照片">
        <template scope="scope">
          <img-zoom
            :src="scope.row.avatar"
            style="width: 80px;height: 60px;">
          </img-zoom>
        </template>
      </el-table-column>
      <el-table-column
        prop="describe"
        label="描述"
        min-width="160"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column
        min-width="180"
        label="操作">
        <template scope="scope">
          <div class="flex--center operations">
            <span class="operate-item top-switch flex--vcenter">
              <el-switch
                v-model="scope.row.top"
                @change="switchTop(scope.row)"
                on-text=""
                off-text="">
              </el-switch>
              {{ scope.row.top === 1 ? '置顶' : '取消置顶' }}
            </span>
            <span
              class="operate-item el-icon-edit"
              @click="openEditDialog(scope.row)">
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

  #doctor {
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

      .tool-item {
        & + .tool-item {
          margin-left: 20px;
        }
      }

      .el-button {
        width: 80px;
        border-radius: 18px;
      }
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
    .top-switch {
      display: inline-block;
      width: 124px;
      text-align: left;
      color: $color3;
      font-size: 14px;
    }

    .pagination-wrap {
      margin-top: 30px;
      text-align: right;
    }
  }
</style>
