<script>
  /*
   * Created by meiyu
   * Date: 2017/7/31
   */
  import {
    DASHBOARD
  } from '../router'
  import {
    getAuditAPi,
    updateAuditAPi
  } from '../api'

  export default {
    name: 'Audit',
    data () {
      return {
        auditForm: {
          tableData: []
        }
      }
    },
    created () {
      this.init()
    },
    computed: {
      DASHBOARD () {
        return DASHBOARD
      }
    },
    methods: {
      // 初始
      init () {
        getAuditAPi({
          year: this.$route.params.year,
          companyId: 79
        }).then((data) => {
          let content = data.content
          this.auditForm.tableData = (content || []).map((item, index) => {
            return item
          })
        })
      },
      // 提交
      submit () {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            let auditDTOList = this.auditForm.tableData

            updateAuditAPi(encodeURIComponent(JSON.stringify({
              auditDTOList: auditDTOList,
              companyId: this.$route.params.companyId,
              year: this.$route.params.year
            }))).then(() => {
              this.$message({
                type: 'success',
                message: '更新成功'
              })
              this.$router.push({ name: DASHBOARD.name, params: { companyId: this.$route.params.companyId, year: this.$route.params.year } })
            })
          } else {
            console.log('error submit!!')
            return false
          }
        })
      },
      // 验证备注信息
      validRemarks (rul, val, cb) {
        if (val === '') {
          cb(new Error('不能是空'))
          this.isErrorRemark = true
        } else if (/^[0-9]*$/g.test(val)) {
          cb(new Error('只能是文字'))
          this.isErrorRemark = true
        } else {
          cb()
          this.isErrorRemark = false
        }
      }
    }
  }
</script>

<template>
  <div class="audit-modify">
    <h3 class="page-title">审计/修正（{{ this.$route.params.year }}年）</h3>
    <el-form
      ref="ruleForm"
      :model="auditForm">
      <el-table
        :data="auditForm.tableData"
        border
        style="width: 100%; margin-bottom: 22px;">
        <el-table-column
          prop="name"
          label="指标名称"
          min-width="180">
        </el-table-column>
        <el-table-column
          prop="reportValue"
          label="报告值"
          min-width="100">
        </el-table-column>
        <el-table-column
          label="审计值"
          min-width="180">
            <template scope="scope">
              <el-form-item
                :prop="'tableData.' + scope.$index + '.audit'"
                :rules="[
                  { type: 'number', message: '必须为数字值', trigger: 'blur,change'}
                ]">
                <el-input
                  placeholder="请输入数字"
                  v-model.number = "scope.row.audit"
                  size="small"
                  type="age"
                  auto-complete="off">
                </el-input>
              </el-form-item>
            </template>
        </el-table-column>
        <el-table-column
          label="特殊因素修正值"
          min-width="180">
            <template scope="scope">
              <el-form-item
                :prop="'tableData.' + scope.$index + '.specialRevise'"
                :rules="[
                  { type: 'number', message: '必须为数字值', trigger: 'blur,change'}
                ]">
                <el-input
                  placeholder="请输入数字"
                  v-model.number = "scope.row.specialRevise"
                  size="small"
                  type="age"
                  auto-complete="off">
                </el-input>
              </el-form-item>
            </template>
        </el-table-column>
        <el-table-column
          label="备注"
          min-width="180">
            <template scope="scope">
              <el-form-item
                :prop="'tableData.' + scope.$index + '.remark'"
                :rules="[
                  { validator: validRemarks, trigger: 'blur,change' }
                ]">
                <el-input
                  placeholder="请输入500个以内的汉字"
                  v-model.number = "scope.row.remark"
                  size="small"
                  auto-complete="off">
                </el-input>
              </el-form-item>
            </template>
        </el-table-column>
      </el-table>
    </el-form>
    <div class="btn-wrap flex--hcenter">
      <el-button @click="$router.push({ name: DASHBOARD.name, params: { companyId: $route.params.companyId, year: $route.params.year } })">取消</el-button>
      <el-button @click="submit()" type="primary">提交</el-button>
    </div>
  </div>
</template>

<style lang="scss">
  .audit-modify {
    padding-bottom: 20px;
    background: #fff;
    height: 815px;
  }
</style>
