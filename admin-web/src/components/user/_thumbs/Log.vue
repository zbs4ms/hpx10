<script>
  /*
   * Created by zhengji
   * Date: 2017/10/21
   */
  import SearchTable from '@/components/_common/searchTable/SearchTable'
  import { Loading } from 'element-ui'
  import {
    convertDate
  } from '@/utils/index'

  import {
    shelveApi,
    topApi,
    checkApi
  } from '../../log/api'

  export default {
    name: 'Log',
    components: {
      SearchTable
    },
    data () {
      this.tableAttrs = {
        'props': {
          'tooltip-effect': 'dark',
          'style': 'width: 100%',
          'align': 'center'
        }
      }
      this.columnData = [{
        attrs: {
          'prop': 'createTime',
          'label': '创建时间',
          'min-width': '80',
          'formatter' (row, col) {
            return row.createTime ? convertDate(row.createTime) : '--'
          }
        }
      }, {
        attrs: {
          'prop': 'title',
          'label': '日记标题',
          'min-width': '140'
        }
      }, {
        attrs: {
          'prop': 'status',
          'label': '审批状态',
          'min-width': '140'
        }
      }, {
        attrs: {
          'label': '操作',
          'min-width': '180'
        },
        scopedSlots: {
          default: (scope) => {
            return (
              <div class="flex--vcenter operations">
                <span class="operate-item flex--vcenter">
                  <el-switch
                    style="margin-right: 10px;"
                    value={scope.row.isTop}
                    onChange={() => this.switchTop(scope.row)}
                    {...{props: { 'on-text': '', 'off-text': '' }}}>
                  </el-switch>
                  { scope.row.top === 1 ? '置顶' : '取消置顶' }
                </span>
                <span
                  class="operate-item"
                  style="color: #20a0ff;"
                  onClick={() => this.handleCheck(scope.row)}>
                  审核
                </span>
                <span
                  class="operate-item"
                  style="color: #20a0ff;"
                  onClick={() => this.handleUnShelve(scope.row)}>
                  { scope.row.enable + '' === '0' ? '下架' : '上架' }
                </span>
              </div>
            )
          }
        }
      }]
      this.listApi = {
        requestFn: () => {
          return Promise.resolve({
            content: {
              list: [{}]
            }
          })
        },
        responseFn (data) {
          let content = data.content || {}
          this.tableData = (content.list || []).map((item) => ({
            createTime: item.orderNumber,
            title: item.id,
            status: item.name
          }))
          this.total = content.total || 0
        }
      }
      return {
        apiKeysMap: {
          createTime: {
            value: undefined
          },
          title: {
            value: undefined
          },
          status: {
            value: undefined
          },
          currentPage: 'pageNum'
        }
      }
    },
    methods: {
      // （取消）置顶
      switchTop (rowData) {
        topApi(rowData.id).then(res => {
          this.$message({
            type: 'success',
            message: '操作成功'
          })
        }).finally(() => {
          this.$refs.searchTable.init()
        })
      },
      // 审核（打开审核弹窗）
      handleCheck (rowData) {
        this.onRowData = rowData // 暂存当前行的数据
        this.checkDialogVisible = true
      },
      // 审核（提交）
      handleCheckSubmit () {
        checkApi(this.onRowData.id, this.checkStatus).then(res => {
          this.$message({
            type: 'success',
            message: '审核成功'
          })
          this.$refs.searchTable.getList()
        }).finally(() => {
          this.checkDialogVisible = false
        })
      },
      // 上下架
      handleUnShelve (rowData) {
        const enable = rowData.enable
        const operateText = enable + '' === '0' ? '下架' : '上架'
        this.$confirm(`是否${operateText}该日志？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              const loading = Loading.service({ fullscreen: true })
              shelveApi(rowData.id).then(res => {
                this.$message({
                  type: 'success',
                  message: `${operateText}成功`
                })
                this.$refs.searchTable.getList()
              }).finally(() => {
                loading.close()
                done()
              })
            } else {
              done()
            }
          }
        })
      }
    }
  }
</script>

<template>
  <div>
    <search-table
      ref="searchTable"
      :table-attrs="tableAttrs"
      :column-data="columnData"
      :list-api="listApi"
      :api-keys-map="apiKeysMap">
    </search-table>
  </div>
</template>

<style lang="scss" scoped>
</style>
