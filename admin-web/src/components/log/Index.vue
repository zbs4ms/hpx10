<script>
  /*
   * Created by zhengji
   * Date: 2017/10/6
   */
  import SearchTable from '@/components/_common/searchTable/SearchTable'
  export default {
    name: 'Diary',
    components: {
      SearchTable
    },
    data () {
      this.tableAttrs = this.tableAttrs = {
        'props': {
          'tooltip-effect': 'dark',
          'style': 'width: 100%'
        }
      }
      this.columnData = [{
        attrs: {
          'prop': 'estTime',
          'label': '创建时间',
          'min-width': '100'
        }
      }, {
        attrs: {
          'prop': 'title',
          'label': '日记标题',
          'min-width': '120'
        }
      }, {
        attrs: {
          'prop': 'userNickname',
          'label': '用户昵称',
          'min-width': '80'
        }
      }, {
        attrs: {
          'prop': 'userId',
          'label': '用户ID',
          'min-width': '80'
        }
      }, {
        attrs: {
          'prop': 'status',
          'label': '审批状态',
          'render-header' (h, { column, $index }) {
            return (
              <el-dropdown>
                <span class="el-dropdown-link">
                审批状态<i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item>全部</el-dropdown-item>
                  <el-dropdown-item>待审核</el-dropdown-item>
                  <el-dropdown-item>审核通过</el-dropdown-item>
                  <el-dropdown-item>审核拒绝</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            )
          }
        }
      }, {
        attrs: {
          'label': '操作'
        }
      }]
      this.listApi = {
        requestFn: () => {
          return Promise.reject()
        },
        responseFn (data) {
//          let content = data.content || {}
//          this.tableData = (content.list || []).map((item) => ({
//            no: item.orderNumber,
//            id: item.id,
//            name: item.name,
//            cover: item.bannerUrl,
//            link: item.jumpUrl,
//            visible: !!item.display
//          }))
//          this.total = content.total || 0
        }
      }
      return {
        apiKeysMap: {
          pageSize: {
            value: 10,
            innerKey: 'pageSize' // searchTable组件内部映射的key
          },
          name: {
            value: undefined
          },
          currentPage: 'pageNum'
        },
        createTimeRange: '',
        searchKeyword: ''
      }
    }
  }
</script>

<template>
  <div id="diary-manage">
    <div class="flex--vcenter page-top">
      <div class="page-title">
        日记管理
      </div>
    </div>
    <search-table
      ref="searchTable"
      :table-attrs="tableAttrs"
      :column-data="columnData"
      :list-api="listApi"
      :api-keys-map="apiKeysMap">
      <div class="table-tools flex--vcenter" slot="table-tools">
        <div class="tool-item">
          创建时间：
          <el-date-picker
            v-model="createTimeRange"
            type="daterange"
            style="width: 230px;"
            placeholder="选择日期范围">
          </el-date-picker>
        </div>
        <div class="tool-item">
          搜索关键字：
          <el-input
            v-model="searchKeyword"
            placeholder="请输入ID号码 / 作者昵称 / 日记标题"
            style="width: 290px;">
          </el-input>
        </div>
        <div class="tool-item">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </div>
    </search-table>
  </div>
</template>

<style lang="scss">
</style>
