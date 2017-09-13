<script>
  /*
   * Created by zhengji
   * Date: 2017/7/27
   */
  import {
    DASHBOARD
  } from '../router'

  import {
    getBudget,
    updateBudget
  } from '../api'

  export default {
    name: 'Budget',
    beforeCreate () {
      this.listRender = [{
        label: '营业收入：',
        prop: 'operationRevenue'
      }, {
        label: '营业成本：',
        prop: 'operationCosts'
      }, {
        label: '销售费用：',
        prop: 'sellingExpenses'
      }, {
        label: '管理费用：',
        prop: 'manageExpenses'
      }, {
        label: '财务费用：',
        prop: 'financialExpenses'
      }, {
        label: '营业利润：',
        prop: 'operatingProfit'
      }, {
        label: '利润总额：',
        prop: 'totalProfit'
      }, {
        label: '净利润：',
        prop: 'netProfit'
      }, {
        label: '负债总额：',
        prop: 'totalLiabilities'
      }, {
        label: '劳动生产总值：',
        prop: 'grossLaborProductivity'
      }, {
        label: '工业总产值：',
        prop: 'grossIndustrialProductivity'
      }, {
        label: '新产品产值：',
        prop: 'newProductValue'
      }]
    },
    created () {
      this.listRender.forEach(item => {
        this.$set(this.formData, item.prop, '')
      })
      let routeParams = this.$route.params
      getBudget({
        year: routeParams.year,
        companyId: routeParams.companyId
      }).then(data => {
        let content = data.content
        if (content) {
          this.companyName = content.companyName
          delete content.companyName
          Object.keys(this.formData).forEach(item => {
            this.formData[item] = content[item] ? content[item] + '' : ''
          })
        }
      })
    },
    data () {
      return {
        companyName: '',
        formData: {}
      }
    },
    computed: {
      DASHBOARD () {
        return DASHBOARD
      }
    },
    methods: {
      // 提交
      submit () {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            let routeParams = this.$route.params
            updateBudget(Object.assign({}, this.formData, {
              year: routeParams.year,
              companyId: routeParams.companyId
            }))
            .then(() => {
              this.$message({
                type: 'success',
                message: '修改成功'
              })
            })
            this.$router.push({ name: DASHBOARD.name, params: { companyId: this.$route.params.companyId, year: this.$route.params.year } })
          } else {
            this.$message({
              type: 'error',
              message: '请检查操作'
            })
            return false
          }
        })
      }
    }
  }
</script>

<template>
  <div class="reports-budget">
    <h3 class="page-title">年度预算（{{ this.$route.params.year }}年）</h3>
    <p>编制单位（公章）：{{ this.companyName }}</p>
    <el-form
      class="rule-form"
      ref="ruleForm"
      style="width: 550px"
      label-position="left"
      :model="formData"
      label-width="120px">
      <el-form-item
        v-for="item in listRender"
        :label="item.label"
        :key="item.label"
        :prop="item.prop">
        <el-input
          v-model="formData[item.prop]"
          auto-complete="off">
        </el-input>
        <span class="el-input-tip">（万元）</span>
      </el-form-item>
      <div class="btn-wrap flex--hcenter">
        <el-button @click="$router.push({ name: DASHBOARD.name, params: { companyId: $route.params.companyId, year: $route.params.year } })">取消</el-button>
        <el-button @click="submit()" type="primary">提交</el-button>
      </div>
    </el-form>
  </div>
</template>

<style lang="scss">
  .reports-budget {
    padding-bottom: 20px;
    background: #fff;

    .el-form-item__content {
      > .el-input {
        width: calc(100% - 60px);
      }
      .el-input-tip {
        color: #99a9bf;
      }
    }
  }
</style>
