<script>
  // 导入帐号参数的接口
  import {
    getListApi,
    upAutApi
  } from './api'

  export default {
    name: 'AccountCfg',
    created () {
      this.init()
    },
    data () {
      return {
        formData: {
          subEnterpriseNum: {
            name: '',
            value: ''
          }, // 子企业数
          subAccountNum: {
            name: '',
            value: ''
          } // 子账号数
        },
        // 页面加载
        pageLoading: true,
        // 表单提交中
        submitLoading: false
      }
    },
    methods: {
      // 初始化页面数据
      init () {
        this.pageLoading = true
        getListApi().then((data) => {
          this.pageLoading = false
          let content = data.content || [] // 赋值
          // 定义一个keys
          let keys = ['subEnterpriseNum', 'subAccountNum']
          // 循环赋值
          keys.forEach((item, index) => {
            this.formData[item] = {
              name: content[index].name,
              value: content[index].value + '' // 拿到数据的值并转换成字符串
            }
          })
        })
      },
      // 表单提交
      submit () {
        let upData = {} // 自己定义的空对象
        // 循环自己修改后的表单
        Object.keys(this.formData).forEach(item => {
          // 取到值并赋值
          let val = this.formData[item]
          // 赋值 键值
          upData[val.name] = val.value
        })
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            this.submitLoading = true
            upAutApi({
              // 后台定死的帐号参数(传的参数必须是字符串)
              groupName: 'companyQuantity',
              // 传给后台的值为JSON格式
              constant: JSON.stringify(upData)
            }).then(() => {
              this.$message({
                type: 'success',
                message: '提交成功'
              })
              this.init()
            }).finally(() => {
              this.submitLoading = false
            })
          }
        })
      }
    }
  }
</script>

<template>
  <div id="account-setting" v-loading="pageLoading">
    <h3 class="page-title">账号参数设置</h3>
    <el-form
      ref='ruleForm'
      label-position="left"
      label-width="130px"
      :model="formData">
      <el-form-item
        prop="subEnterpriseNum.value"
        label="默认子企业数："
        :rules="[
          { required: true, message: '请输入默认子企业数', trigger: 'blur' },
          { type: 'string', pattern: /^\d+$/, message: '请输入数字类型',trigger: 'change' }
        ]">
        <el-input
          :disabled="submitLoading"
          v-model.trim="formData.subEnterpriseNum.value"
          placeholder="请输入默认子企业数">
        </el-input>
        <span class="el-input-tip">（tips：监管机构和企业可以新建的下级企业数量。）</span>
      </el-form-item>
      <el-form-item
        prop="subAccountNum.value"
        label="默认子账号数："
        :rules="[
          { required: true, message: '请输入默认子账号数', trigger: 'blur' },
          { type: 'string', pattern: /^\d+$/, message: '请输入数字类型',trigger: 'change' }
        ]">
        <el-input
          :disabled="submitLoading"
          v-model.trim="formData.subAccountNum.value"
          placeholder="请输入默认子账号数">
        </el-input>
        <span class="el-input-tip">（tips：监管机构和企业可以新建的本机构内子账号数量。）</span>
      </el-form-item>
    </el-form>
    <el-button
      type="primary"
      v-loading="submitLoading"
      :disabled="submitLoading"
      @click="submit">保存</el-button>
  </div>
</template>

<style lang="scss">
  #account-setting {
    .is-required {
      .el-form-item__label:before {
        display: none;
      }
    }

    .el-input {
      width: 300px;
    }
    .el-input-tip {
      color:#99a9bf;
    }
  }
</style>
