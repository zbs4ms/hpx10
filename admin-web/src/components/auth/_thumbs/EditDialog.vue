<script>
/*
 * Created by zhengji
 * Date: 2017/9/11
 */
export default {
  name: 'AuthEditDialog',
  props: {
    value: {
      type: Boolean
    },
    data: {
      type: Object
    }
  },
  data () {
    return {
      form: {
        name: '',
        pickedAuth: [],
        psd: ''
      },
      authOptions: [{
        value: '选项1',
        label: '黄金糕'
      }, {
        value: '选项2',
        label: '双皮奶'
      }, {
        value: '选项3',
        label: '蚵仔煎'
      }, {
        value: '选项4',
        label: '龙须面'
      }, {
        value: '选项5',
        label: '北京烤鸭'
      }],
      pickedAuth: [],
      submitLoading: false
    }
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
  methods: {
    handleSubmit () {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true
          this.$emit('submit', Object.assign({}, this.form), (success) => {
            this.submitLoading = false
            if (success) {
              this.visible = false
            }
          })
        }
      })
    },
    handleClose () {
      this.$refs.ruleForm.resetFields()
      this.form = {
        name: '',
        pickedAuth: [],
        psd: ''
      }
    },
    handleCancel () {
      this.visible = false
      this.$emit('cancel')
    }
  }
}
</script>

<template>
  <div class="auth-edit-dialog">
    <el-dialog
      :title="`${data ? '修改' : '新增'}`"
      :visible.sync="visible"
      @close="handleClose">
      <el-form
        ref="ruleForm"
        :model="form"
        label-position="top">
        <el-row>
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
              <span class="name-length">{{ form.name ? form.name.length : 0 }}/30</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="16">
            <el-form-item
              label="权限"
              prop="auth"
              required
              :rules="[
                { required: true, message: '请选择权限' }
              ]">
              <el-select v-model="form.pickedAuth" multiple placeholder="请选择">
                <el-option
                  v-for="item in authOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="16">
            <el-form-item
              label="密码"
              prop="psd"
              required
              :rules="[
                { required: true, message: '名称不能为空'},
                { pattern: /^\s*\S{0,30}$/, message: '字体长度不能大于30', trigger: 'change, blur'}
              ]">
              <el-input v-model="form.psd" auto-complete="off"></el-input>
              <span class="name-length">{{ form.psd ? form.psd.length : 0 }}/30</span>
            </el-form-item>
          </el-col>
        </el-row>
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
  .auth-edit-dialog {
    .name-length {
      position: absolute;
      top: 50%;
      right: 10px;
      transform: translateY(-50%);
      color: $color5;
    }
  }
</style>
