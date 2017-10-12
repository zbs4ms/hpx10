<script>
/*
 * Created by zhengji
 * Date: 2017/9/11
 */
import EditDialog from './_thumbs/EditDialog.vue'

let adding = false
export default {
  name: 'Auth',
  components: {
    EditDialog
  },
  data () {
    return {
      tableLoading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      tableData: [{
        no: 1,
        userName: 'paul1',
        auth: ['预约管理', '运营管理']
      }, {
        no: 1,
        userName: 'paul1',
        auth: ['预约管理', '运营管理']
      }],
      editDialogVisible: false,
      editData: null
    }
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
    getList (params) {
      this.tableLoading = true
    },
    // 编辑或新增
    openEditDialog (rowData, isAdd) {
      this.editDialogVisible = true
      this.editData = rowData
      adding = !!isAdd
    },
    // 提交编辑或新增
    handleEditSubmit (data, respondCb) {
      console.log(data, respondCb, 'respondCb')
    },
    handleEditCancel () {
      console.log('adding', adding)
    },
    delRow (rowData) {
    }
  }
}
</script>

<template>
  <div class="auth-manage">
    <div class="flex--vcenter page-top">
      <div class="page-title">
        权限管理
      </div>
    </div>
    <div class="table-tools flex--vcenter">
      <div class="btn-wrap">
        <el-button
          class="btn--add"
          type="primary"
          @click="openEditDialog(null, true)">
          新增 <i class="el-icon-plus"></i>
        </el-button>
      </div>
    </div>
    <el-table
      v-loading="tableLoading"
      ref="multipleTable"
      :data="tableData"
      tooltip-effect="dark"
      style="width: 100%">
      <el-table-column
        prop="no"
        label="序号"
        min-width="120">
      </el-table-column>
      <el-table-column
        prop="userName"
        label="用户名"
        min-width="140"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column
        prop="auth"
        label="权限"
        min-width="140"
        :formatter="(row, column, cellValue) => {
          return row.auth.join(' / ')
        }"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column
        min-width="140"
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

  .auth-manage {
    .table-tools {
      margin-top: 20px;
      justify-content: flex-end;

      .el-button {
        border-radius: 18px;
      }
    }

    .el-table {
      margin-top: 20px;
      th, td {
        text-align: center;
      }

      .operate-item {
        color: $color4;
        font-size: 18px;
        cursor: pointer;
        & + .operate-item {
          margin-left: 20px;
        }
      }
    }

    .pagination-wrap {
      margin-top: 30px;
      text-align: right;
    }
  }
</style>
