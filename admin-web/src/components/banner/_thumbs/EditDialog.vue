<script>
/*
 * Created by zhengji
 * Date: 2017/8/30
 */
let fileObj = ''
let initData = {
  submitLoading: false,
  fileInputValid: true
}

import ImgUploader from '@/components/_common/imgUploader/ImgUploader.vue'

export default {
  name: 'EditDialog',
  props: {
    value: {
      type: Boolean
    },
    data: {
      type: Object
    }
  },
  components: {
    ImgUploader
  },
  data () {
    return Object.assign({}, {
      form: {
        name: '',
        no: '',
        link: ''
      }
    }, initData)
  },
  computed: {
    visible: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('input', val)
      }
    }
  },
  watch: {
    data (val) {
      if (val) {
        Object.assign(this.form, this.data)
      }
    }
  },
  methods: {
    handleCancel () {
      this.visible = false
      this.$emit('cancel')
    },
    handleSubmit () {
      this.$refs.ruleForm.validate((valid) => {
        let checkFileExist = this.$refs.imgUploader.checkFileExist(fileObj)
        if (valid) {
          checkFileExist.then(() => {
            this.submitLoading = true
            this.$emit('submit', Object.assign({}, this.form, {
              file: fileObj
            }), (success) => {
              this.submitLoading = false
              if (success) {
                this.visible = false
              }
            })
          })
        }
      })
    },
    handleClose () { // 清空数据
      fileObj = ''
      Object.keys(initData).forEach(key => {
        this[key] = initData[key]
      })
      this.$refs.ruleForm.resetFields()
      this.form = {
        name: '',
        no: '',
        link: ''
      }
      this.$refs.imgUploader.clearFileInput()
    },
    handleFileChange (newFile) {
      fileObj = newFile
    }
  }
}
</script>

<template>
  <div class="edit-dialog">
    <el-dialog
      class="dialog--center"
      :title="`${data ? '修改' : '新增'}BANNER`"
      :visible.sync="visible"
      @close="handleClose">
      <el-form
        ref="ruleForm"
        :model="form"
        label-position="top">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item
              label="名称"
              prop="name"
              class="banner-name"
              required
              :rules="[
                { required: true, message: '名称不能为空'},
                { pattern: /^\s*\S{0,30}$/, message: '字体长度不能大于30', trigger: 'change, blur'}
              ]">
              <el-input v-model="form.name" auto-complete="off"></el-input>
              <span class="text-length">{{ form.name ? form.name.length : 0 }}/30</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              label="排序"
              prop="no"
              required
              :rules="[
                { required: true, message: '排序不能为空'},
                { pattern: /^(?!0)(?:[0-9]{1,3}|1000)$/, message: '请输入1-1000的数字', trigger: 'change, blur'}
              ]">
              <el-input
                v-model="form.no"
                auto-complete="off"
                placeholder="请输入1-1000之间的数字">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item
          label="跳转链接"
          prop="link"
          required
          :rules="[
            { required: true, message: '跳转链接不能为空'},
            { pattern: /^(https?):\/\/[\w\-]+(\.[\w\-]+)+([\w\-\.,@?^=%&:\/~\+#]*[\w\-\@?^=%&\/~\+#])?$/, message: '请输入正确的链接地址', trigger: 'blur'}
          ]">
          <el-input
            v-model.trim="form.link"
            auto-complete="off">
          </el-input>
        </el-form-item>
        <el-form-item
          label="封面图"
          required>
          <img-uploader
            ref="imgUploader"
            :img-src="form.cover"
            @file-change="handleFileChange"></img-uploader>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button
          @click="handleCancel"
          :disabled="submitLoading">
          取 消
        </el-button>
        <el-button
          type="primary"
          :disabled="submitLoading"
          v-loading="submitLoading"
          @click="handleSubmit">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style lang="scss">
  @import "~@/assets/style/variables/index";
  .edit-dialog {
    .el-dialog {
      min-width: 720px;
    }

    .upload-box {
      position: relative;
      border: 1px dashed #c0ccda;
      width: 118px;
      height: 118px;
      overflow: hidden;
      background-size: contain;
      background-position: center;
      background-repeat: no-repeat;

      &.miss-file {
        border-color: #ff4949;
      }

      .el-icon-plus {
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        font-size: 18px;
        color: $color1;
      }

      .el-icon-close {
        position: absolute;
        top: 5px;
        right: 5px;
        color: #b2b2b2;
        cursor: pointer;
      }
    }
    .upload-input {
      position: absolute;
      opacity: 0;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      cursor: pointer;
    }
    .upload-notice {
      line-height: 1.4;
      color: $color5;
      & + .upload-notice {
        margin-top: 5px;
      }
    }

    .banner-name {
      .el-input__inner {
        padding-right: 50px;
      }
    }
  }
</style>
