<script>
  // 调用数据参数的接口
  import {
    getListApi,
    upDataApi
  } from './api'
  
  export default {
    name: 'DataCfg',
    created () {
      this.init()
    },
    data () {
      return {
        formData: {
          juniorRadio: {
            name: '',
            value: ''
          }, // 平级企业
          levelRadio: {
            name: '',
            value: ''
          }, // 上级企业
          allRadio: {
            name: '',
            value: ''
          } // 所有的企业
        },
        // 页面加载
        pageLoading: true,
        // 表单提交中
        submitLoading: false
      }
    },
    methods: {
      // 初始化页面
      init () {
        this.pageLoading = true
        getListApi().then((data) => {
          this.pageLoading = false
          let content = data.content || [] // 赋值
          // 定义一个keys
          let keys = ['juniorRadio', 'levelRadio', 'allRadio']
          keys.forEach((item, index) => {
            this.formData[item] = {
              name: content[index].name, // 修改单选key
              value: content[index].value + '' // 修改单选value
            }
          })
        })
      },
      // 表单提交
      submit () {
        this.submitLoading = true
        // 自己定义的空对象
        let reqData = {}
        // 循环自己修改后的表单
        Object.keys(this.formData).forEach(item => {
          // 取到值并赋值
          let val = this.formData[item]
          // 赋值 键值
          reqData[val.name] = val.value
        })
        upDataApi({
          // 后台定死的帐号参数(传的参数必须是字符串)
          groupName: 'companyVisible',
          // 传给后台的值为JSON格式
          constant: JSON.stringify(reqData)
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
    }
  }
</script>

<template>
  <div id="data-setting" v-loading="pageLoading">
    <h3 class="page-title">数据参数设置</h3>
    <div class="data-setting-item">
      <span class="radio-group__title">是否可见平级企业：</span>
      <span class="radio-group__body">
        <el-radio
          :disabled="submitLoading"
          class="radio"
          v-model="formData.juniorRadio.value"
          label="1">
          是
        </el-radio>
        <el-radio
          :disabled="submitLoading"
          class="radio"
          v-model="formData.juniorRadio.value"
          label="0">
          否
        </el-radio>
      </span>
      <span class="radio-group__tip">
        （tips：选择是，则可以看到递归的所有下级；选择否，则只可以看自己的直属下级。）
      </span>
    </div>
    <div class="data-setting-item">
      <span class="radio-group__title">是否可见上级企业：</span>
      <span class="radio-group__body">
        <el-radio
          :disabled="submitLoading"
          class="radio"
          v-model="formData.levelRadio.value"
          label="1">
          是
        </el-radio>
        <el-radio
          :disabled="submitLoading"
          class="radio"
          v-model="formData.levelRadio.value"
          label="0">
          否
        </el-radio>
      </span>
      <span class="radio-group__tip">
        （tips：选择是，则可以看到跟自己同属于一个上级的平级企业；选择否，则不能看到跟自己平级的企业。）
      </span>
    </div>
    <div class="data-setting-item">
      <span class="radio-group__title">是否可见全部企业：</span>
      <span class="radio-group__body">
        <el-radio
          :disabled="submitLoading"
          class="radio"
          v-model="formData.allRadio.value"
          label="1">
          是
        </el-radio>
        <el-radio
          :disabled="submitLoading"
          class="radio"
          v-model="formData.allRadio.value"
          label="0">
          否
        </el-radio>
      </span>
      <span class="radio-group__tip">
        （tips：选择是，则可以看到平台内所有的企业数据）
      </span>
    </div>
    <el-button
      type="primary"
      style="margin-left: 265px"
      v-loading="submitLoading"
      :disabled="submitLoading"
      @click="submit">保存</el-button>
  </div>
</template>

<style lang="scss">
  #data-setting {
    .data-setting-item {
      margin-bottom: 40px;
    }

    .radio-group {
      &__title {
        display: inline-block;
        width: 200px;
      }
      &__tip {
        margin-left: 20px;
        color: #99a9bf;
      }
    }
  }
</style>
