<script>
  import { validAccount } from '@/utils/validator'
  export default {
    name: 'CommonInputs',
    props: {
      pageTitle: String,
      // slot中的input字段
      extraInputs: Object,
      // 字段映射（组件key => 接口key）
      keyMap: Object,
      initData: Object
    },
    created () {
      // 合并formData
      this.formData = Object.assign({}, this.formData, this.extraInputs)
    },
    data () {
      return {
        formData: {
          adminAccount: '',
          adminMail: '',
          expire: '',
          subAccountNum: '',
          allowNewChild: '1',
          subEnterpriseNum: ''
        }
      }
    },
    watch: {
      initData: {
        handler (newData) {
          let formData = this.formData
          let _newData = {}
          Object.keys(formData).forEach(item => {
            _newData[item] = newData[this.keyMap[item] || item] || ''
          })
          this.formData = Object.assign({}, this.formData, _newData)
        }
      }
    },
    computed: {
      // 日期选择器配置项
      pickerOptions () {
        return {
          disabledDate (time) {
            return time.getTime() < Date.now() // - 8.64e7
          }
        }
      },
      // 转换expire格式（element保存的是date对象，表单提交的是时间戳）
      _expire: {
        get () {
          let expire = this.formData.expire ? +this.formData.expire : ''
          return expire && new Date(expire)
        },
        set (val) {
          this.formData.expire = new Date(val).getTime()
        }
      }
    },
    methods: {
      // 校验管理员账号
      validAdminAccount (rule, val, cb) {
        cb(validAccount(val))
      },
      // 校验子企业数量
      validSubEnterprise (rule, val, cb) {
        if (this.formData.allowNewChild === '0' && val === '') {
          cb(new Error('请输入子企业数量'))
        } else {
          cb()
        }
      },
      // 校验账号有效期
      validExpire (rule, val, cb) {
        if (val === '') {
          cb(new Error('请选择账号有效期'))
        } else {
          cb()
        }
      },
      // 下一步
      toNextStep () {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            let formData = {}
            Object.keys(this.formData).forEach((item) => {
              formData[this.keyMap[item] || item] = this.formData[item]
            })
            this.$emit('to-next', formData)
          }
        })
      },
      cancel () {
        this.$router.go(-1)
      }
    }
  }
</script>

<template>
  <div id="enterprise-form-page">
    <h3 class="page-title" v-if="pageTitle">{{ pageTitle }}</h3>
    <el-form
      class="rule-form"
      ref="ruleForm"
      :model="formData"
      label-width="150px">
      <slot name="extra-inputs" :form-data="formData"></slot>
      <el-form-item
        label="管理员账号："
        prop="adminAccount"
        :rules="[{
          validator: validAdminAccount, trigger: 'blur'
        }]">
        <el-input
          v-model="formData.adminAccount"
          auto-complete="off"></el-input>
      </el-form-item>
      <el-form-item
        label="管理员邮箱："
        prop="adminMail"
        :rules="[{
          required: true, message: '请输入管理员邮箱',trigger: 'blur'
        }, {
          pattern: /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/, message: '邮件地址需由字母、数字或下划线组成',trigger: 'blur'
        }]">
        <el-input
          v-model="formData.adminMail"
          auto-complete="off"></el-input>
        <span class="input-tip">提示：邮箱将用于找回密码</span>
      </el-form-item>
      <el-form-item
        label="账号有效期："
        prop="expire"
        :rules="[{
          validator: validExpire, message: '请选择账号有效期',trigger: 'blur'
        }]">
        <el-date-picker
          style="width: 100%"
          type="date"
          placeholder="选择日期"
          :picker-options="pickerOptions"
          v-model="_expire"
          :editable="false">
        </el-date-picker>
      </el-form-item>
      <el-form-item
        label="子账号数量："
        prop="subAccountNum"
        :rules="[{
          required: true, message: '请输入子账号数量',trigger: 'blur'
        }, {
          type: 'string', pattern: /^\d+$/, message: '请正确输入子账号数量：数字',trigger: 'change'
        }]">
        <el-input
          v-model="formData.subAccountNum"
          auto-complete="off"></el-input>
      </el-form-item>
      <el-row>
        <el-col :span="14">
          <el-form-item
            label="是否允许新建子企业："
            label-width="200px">
            <el-select v-model="formData.allowNewChild">
              <el-option
                v-for="item in [{label: '是', value: '0'}, {label: '否', value: '1'}]"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="10" v-if="formData.allowNewChild === '0'">
          <el-form-item
            label="子企业数量："
            label-width="150px"
            prop="subEnterpriseNum"
            :rules="[{
              validator: validSubEnterprise, trigger: 'blur'
            }, {
              type: 'string', pattern: /^\d+$/, message: '请正确输入子企业数量：数字',trigger: 'change'
            }]">
            <el-input
              v-model.trim="formData.subEnterpriseNum"
              auto-complete="off"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="btn-wrap flex--hcenter">
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" @click="toNextStep">下一步</el-button>
    </div>
  </div>
</template>

<style lang="scss">
  #enterprise-form-page {
    width: 600px;

    .rule-form {
      .is-required {
        .el-form-item__label:before {
          display: none;
        }
      }
    }
  }

</style>
