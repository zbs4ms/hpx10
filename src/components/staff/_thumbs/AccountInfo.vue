<script>

  /* 常量 */
  // 民族数据
  import { NATIONS } from '@/_consts/index'

  import {
    getCompanyApi
  } from '../api'

  export default {
    name: 'AccountInfo',
    props: {
      type: String, // 'create' / 'edit'
      data: Object
    },
    created () {
    },
    data () {
      return {
        formData: {
          name: '', // 姓名
          account: '', // 账号
          companyName: '', // 企业
          companyId: '',
          email: '', // 邮箱
          department: '', // 部门
          enable: '1', // 状态
          gender: '1', // 性别
          nation: '汉族', // 民族
          origin: '', // 籍贯
          birth: '', // 出生年月
          education: '', // 学历
          school: '', // 毕业院校
          resign: '', // 职务
          professor: '', // 职称
          workHistory: '', // 工作履历
          socialAppointments: '', // 社会兼职
          scientificAchievements: '' // 重大成果
        },
        // 限制日期控件选择范围
        pickerOptions: {
          disabledDate (time) {
            return time.getTime() > Date.now()
          }
        },
        companyJson: '',
        // 企业搜索结果
        enterprises: [],
        // 企业搜索loading
        enterprisesLoading: false,
        submitLoading: false // 提交loading
      }
    },
    watch: {
      data: {
        handler (newData) {
          if (newData) {
            Object.keys(this.formData).forEach(item => {
              this.formData[item] = newData[item] || ''
            })
          }
        }
      }
    },
    computed: {
      // 民族数据
      NATIONS () {
        return NATIONS
      },
      // 转换expire格式（element保存的是date对象，表单提交的是时间戳）
      birthProxy: {
        get () {
          let birth = +this.formData.birth || ''
          return birth && new Date(birth)
        },
        set (val) {
          this.formData.birth = new Date(val).getTime()
        }
      }
    },
    methods: {
      // 企业搜索
      searchEnterprises (query) {
        this.enterprisesLoading = true
        getCompanyApi(query).then(data => {
          this.enterprises = data.content
        }).finally(() => {
          this.enterprisesLoading = false
        })
      },
      // 取消
      cancel () {
        this.$emit('cancel')
      },
      // 表单提交
      toNext () {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            let formData = Object.assign({}, this.formData)
            if (this.type === 'edit') {
              delete formData.companyId
            }
            this.$emit('to-next', formData)
          }
        })
      },
      // 从搜索列表中选择企业
      handlePickEnterprise (item) {
        let data = JSON.parse(item)
        this.formData.companyId = data.id
        this.formData.companyName = data.companyName
      },
      // 校验账号
      validAccount (rule, val, cb) {
        if (!(!(/^\d+$/.test(val)) && /^[a-zA-Z0-9]+$/.test(val))) {
          cb(new Error('管理员账号只能是字母、字母+数字'))
        } else {
          cb()
        }
      }
    },
    filters: {
      json (val) {
        return JSON.stringify(val)
      }
    }
  }
</script>

<template>
  <div class="account-info">
    <el-form
      ref="ruleForm"
      :model="formData"
      label-width="120px">
      <div class="panel">
        <h4 class="panel__title">账号信息：</h4>
        <div class="panel__body clr">
          <el-form-item
            label="姓名："
            prop="name"
            :rules="[{
              required: true, message: '请输入姓名', trigger: 'blur'
            }]">
            <el-input
              v-model.trim="formData.name"
              auto-complete="off"
              placeholder="请输入姓名"></el-input>
          </el-form-item>
          <el-form-item
            label="账号："
            prop="account"
            required
            :rules="[{
              required: true, message: '请输入账号', trigger: 'blur'
            },
            {
              min: 6, max: 20, message: '账号长度6-20个字符', trigger: 'blur',
            },
            {
              validator: validAccount, tigger: 'blur'
            }]">
            <el-input
              v-model.trim="formData.account"
              auto-complete="off"
              placeholder="请输入账号"></el-input>
          </el-form-item>
          <el-form-item
            v-if="type === 'edit'"
            label="企业："
            style="height: 37px">
            <span>{{ formData.companyName }}</span>
          </el-form-item>
          <!--<el-form-item-->
            <!--v-else-->
            <!--label="企业："-->
            <!--prop="companyName"-->
            <!--:rules="[{-->
              <!--required: true, message: '请输入企业', trigger: 'blur'-->
            <!--}]">-->
            <!--<el-input-->
              <!--v-model="formData.companyName"-->
              <!--auto-complete="off"-->
              <!--placeholder="请输入企业"></el-input>-->
          <!--</el-form-item>-->
          <el-form-item
            v-else
            label="企业："
            prop="companyName"
            :rules="[{
              required: true, message: '请选择企业', trigger: 'change'
            }]">
            <el-select
              v-model.trim="companyJson"
              filterable
              remote
              placeholder="搜索企业"
              :remote-method="searchEnterprises"
              :loading="enterprisesLoading"
              @change="handlePickEnterprise">
              <el-option
                v-for="item in enterprises"
                :key="item.id"
                :label="item.companyName"
                :value="item | json">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            label="邮箱："
            prop="email"
            :rules="[{
              required: true, message: '请输入邮箱', trigger: 'blur'
            }, {
              pattern: /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/, message: '邮件地址需由字母、数字或下划线组成', trigger: 'blur'
            }]">
            <el-input
              v-model.trim="formData.email"
              auto-complete="off"
              placeholder="请输入邮箱"></el-input>
          </el-form-item>
          <el-form-item
            label="部门："
            prop="department"
            :rules="[{
              required: true, message: '请输入部门', trigger: 'blur'
            }]">
            <el-input
              v-model.trim="formData.department"
              auto-complete="off"
              placeholder="请输入部门"></el-input>
          </el-form-item>
          <el-form-item
            label="账号状态：">
            <el-select v-model.trim="formData.enable">
              <el-option
                v-for="item in [{label: '启用', value: '1'}, {label: '禁用', value: '0'}]"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </div>
      </div>
      <div class="panel">
        <h4 class="panel__title">个人信息：</h4>
        <div class="panel__body clr">
          <el-form-item
            label="性别：">
            <el-radio class="radio" v-model="formData.gender" label="1">男</el-radio>
            <el-radio class="radio" v-model="formData.gender" label="0">女</el-radio>
          </el-form-item>
          <el-form-item
            label="民族：">
            <el-select
              v-model="formData.nation"
              filterable>
              <el-option
                v-for="item in NATIONS"
                :key="item"
                :label="item"
                :value="item">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            label="籍贯："
            prop="origin">
            <el-input
              v-model="formData.origin"
              auto-complete="off"
              placeholder="请输入籍贯"></el-input>
          </el-form-item>
          <el-form-item
            label="出生日期："
            prop="birth">
            <el-date-picker
              style="width: 100%"
              type="date"
              placeholder="选择日期"
              :picker-options="pickerOptions"
              v-model="birthProxy"
              :editable="false">
            </el-date-picker>
          </el-form-item>
          <el-form-item
            label="学历：">
            <el-select
              v-model="formData.education">
              <el-option
                v-for="item in ['大专', '本科', '研究生', '博士']"
                :key="item"
                :label="item"
                :value="item">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            label="毕业院校："
            prop="school">
            <el-input
              v-model="formData.school"
              auto-complete="off"
              placeholder="请输入毕业院校"></el-input>
          </el-form-item>
          <el-form-item
            label="职务："
            prop="resign">
            <el-input
              v-model="formData.resign"
              auto-complete="off"
              placeholder="请输入职务"></el-input>
          </el-form-item>
          <el-form-item
            label="职称："
            prop="professor">
            <el-input
              v-model="formData.professor"
              auto-complete="off"
              placeholder="请输入职称"></el-input>
          </el-form-item>
          <el-form-item
            style="max-width: 100%; width: 100%;"
            label="工作履历："
            prop="workHistory">
            <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入工作履历"
              v-model="formData.workHistory">
            </el-input>
          </el-form-item>
          <el-form-item
            style="max-width: 100%; width: 100%;"
            label="社会兼职："
            prop="socialAppointments">
            <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入社会兼职"
              v-model="formData.socialAppointments">
            </el-input>
          </el-form-item>
          <el-form-item
            style="max-width: 100%; width: 100%;"
            label="重大成果："
            prop="scientificAchievements">
            <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入重大成果"
              v-model="formData.scientificAchievements">
            </el-input>
          </el-form-item>
        </div>
      </div>
    </el-form>
    <div class="btn-wrap flex--hcenter">
      <el-button
        @click="cancel">
        取 消
      </el-button>
      <el-button
        type="primary"
        v-loading="submitLoading"
        @click="toNext">
        下一步
      </el-button>
    </div>
  </div>
</template>

<style lang="scss">
  .account-info {
    .panel {
      &__title {
        margin-top: 0;
        font-size: 18px;
        font-weight: 500;
      }
    }

    .panel__body {
      flex-wrap: wrap;
    }

    .el-form-item {
      float: left;
      width: 50%;

      @media (min-width: 1600px) {
        & {
          width: 33.33333%;
        }
      }
    }

    .btn-wrap {
      margin-top: 20px;
    }
  }
</style>
