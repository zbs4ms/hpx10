<script>
  /*
   * Created by zhengji
   * Date: 2017/10/21
   */
  import SearchTable from '@/components/_common/searchTable/SearchTable'

  export default {
    name: 'Relation',
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
          'prop': 'name',
          'label': '姓名',
          'min-width': '140'
        }
      }, {
        attrs: {
          'prop': 'tel',
          'label': '手机号',
          'min-width': '140'
        }
      }, {
        attrs: {
          'prop': 'visitNo',
          'label': '就诊号',
          'min-width': '140'
        }
      }, {
        attrs: {
          'prop': 'ID_no',
          'label': '身份证号',
          'min-width': '180'
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
            name: item.orderNumber,
            tel: item.id,
            visitNo: item.name,
            'ID_no': item.name
          }))
          this.total = content.total || 0
        }
      }
      return {
        apiKeysMap: {
          currentPage: 'pageNum'
        }
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
