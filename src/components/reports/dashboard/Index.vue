<script>
  /*
   * Created by zhengji
   * Date: 2017/7/26
   */

  import {
    getExcelApi,
    uploadMonth,
    uploadYear
  } from '../api'

  const PANEL_DICT = {
    bulletin: '快报',
    annal: '年报',
    budget: '预算'
  }

  import {
    INDEX,
    BUDGET,
    AUDIT
  } from '../router'

  import {
    getServerDate
  } from '@/utils/index'

  export default {
    name: 'Reports_Dashboard',
    created () {
      this.init()
      getServerDate((date) => {
        date = new Date(date)
        this.currYear = date.getFullYear()
        this.currMonth = date.getMonth() + 1
      })
      this.$eventBus.$on('Reports:budget.uploaded', () => {
        this.uploaded.budget = true
      })
      if (!this.$route.params.year) {
        this.$router.replace({
          name: this.$route.name,
          params: {
            year: '2017'
          }
        })
      }
    },
    data () {
      return {
        pageLoading: false,
        years: ['2017', '2016', '2015', '2014', '2013'],
        activeYear: '2017',
        // 当前日期
        currYear: new Date().getFullYear(),
        currMonth: new Date().getMonth() + 1,
        uploadDialogVisible: false,
        uploaded: {
          bulletin: [],
          annal: false,
          budget: false
        },
        activeUpload: {
          type: '',
          panel: '',
          target: ''
        },
        uploading: false,
        fileName: ''
      }
    },
    computed: {
      BUDGET () {
        return BUDGET
      },
      INDEX () {
        return INDEX
      },
      AUDIT () {
        return AUDIT
      }
    },
    watch: {
      activeYear (newVal) {
        this.$router.replace({
          name: this.$route.name,
          params: {
            year: newVal
          }
        })
        this.init()
      }
    },
    methods: {
      // 初始
      init () {
        this.pageLoading = true
        getExcelApi({
          companyId: this.$route.params.companyId,
          year: this.$route.params.year || this.activeYear
        }).then((data) => {
          this.pageLoading = false
          let content = data.content
          this.uploaded.bulletin = content.monthSet.map((item, index) => {
            return (item) + ''
          })
          this.uploaded.annal = content.yearFlag
        })
      },
      // 监听文件控件
      handleFileChange (e) {
        let file = e.target.files[0]
        if (file) {
          this.fileName = file.name
          let formData = new FormData()
          formData.append('file', file)
          if (this.activeUpload.panel === 'bulletin') {
            formData.append('companyId', this.$route.params.companyId)
            formData.append('year', this.$route.params.year)
            formData.append('month', '6')
            this.uploading = true
            uploadMonth(formData).then(() => {
              this.$message({
                type: 'success',
                message: '上传成功'
              })
              if (Array.isArray(this.uploaded[this.activeUpload.panel])) {
                this.uploaded[this.activeUpload.panel].push(this.activeUpload.target + '')
              } else {
                this.uploaded[this.activeUpload.panel] = true
              }
              this.uploadDialogVisible = false
              this.fileName = ''
            }).catch(() => {
              this.$refs.uploadInput.value = null
            }).finally(() => {
              this.uploading = false
            })
          } else {
            formData.append('companyId', this.$route.params.companyId)
            formData.append('year', this.$route.params.year)
            uploadYear(formData).then(() => {
              this.$message({
                type: 'success',
                message: '上传成功'
              })
              if (Array.isArray(this.uploaded[this.activeUpload.panel])) {
                this.uploaded[this.activeUpload.panel].push(this.activeUpload.target + '')
              } else {
                this.uploaded[this.activeUpload.panel] = true
              }
              this.uploadDialogVisible = false
              this.fileName = ''
            }).catch(() => {
              this.$refs.uploadInput.value = null
            }).finally(() => {
              this.uploading = false
            })
          }
        }
      },
      openUploadDialog (type, panel, target) {
        this.activeUpload = {
          type,
          panel,
          target
        }
        this.uploadDialogVisible = true
      },
      // panel字典映射
      dictMap (key) {
        return PANEL_DICT[key]
      },
      specifyItemStatus (index) {
        if (index > this.currMonth) {
          if (this.$route.params.year === (this.currYear + '')) {
            return 'not-yet'
          }
        } else if (this.uploaded.bulletin.indexOf(index + '') !== -1) {
          return 'upload-success'
        }
      },
      // 重新上传
      handReUp () {
        this.activeUpload.type = ''
      },
      handleItemClick (type, panel, target) {
        // if (type === 'not-yet' || type === 'upload-success') {
        //   return
        // }
        if (type === 'not-yet') {
          return
        }
        this.openUploadDialog(type, panel, target)
      },
      dialogBeforeClose (done) {
        this.fileName = ''
        done()
      }
    }
  }
</script>

<template>
  <div
    id="report-dashboard"
    :class="{'dialog-full': $route.name === BUDGET.name}"
    v-loading="pageLoading">
    <div class="flex" style="justify-content: space-between">
      <h3 class="page-title">查看上传财务报表</h3>
      <el-button
        type="primary"
        @click="$router.push(INDEX.path)">
        返回
      </el-button>
    </div>
    <el-tabs class="custom-tabs" v-model="activeYear">
      <el-tab-pane
        v-for="year in years"
        :label="year"
        :name="year"
        :key="year">
      </el-tab-pane>
    </el-tabs>
    <div class="panel">
      <h4 class="panel-title">快报</h4>
      <div class="panel-body">
        <template v-for="n in (12 / 6)">
          <el-row class="bulletin-list list-wrap" :gutter="40">
            <el-col :span="4" v-for="m in 6" :key="m">
              <div
                class="list-item flex--center"
                :class="specifyItemStatus((n - 1) * 6 + m)"
                @click="handleItemClick(specifyItemStatus((n - 1) * 6 + m), 'bulletin', (n - 1) * 6 + m)">
                <span class="item-label">{{ (n - 1) * 6 + m }}月</span>
                <span class="item-tip flex--center wait-upload"><i class="el-icon-upload icon"></i>等待上传</span>
                <span class="item-tip flex--center upload-success"><i class="el-icon-circle-check icon"></i>上传成功</span>
                <span class="item-tip not-yet">未到上传时间</span>
              </div>
            </el-col>
          </el-row>
        </template>
      </div>
    </div>
    <el-row :gutter="40">
      <el-col :span="4">
        <div class="panel">
          <h4 class="panel-title">年报</h4>
          <div class="panel-body">
            <div
              class="list-item flex--center"
              :class="{'upload-success': uploaded.annal}"
              @click="handleItemClick(uploaded.annal ? 'upload-success' : '', 'annal')">
              <span class="item-label">年报</span>
              <span class="item-tip flex--center wait-upload"><i class="el-icon-upload icon"></i>等待上传</span>
              <span class="item-tip flex--center upload-success"><i class="el-icon-circle-check icon"></i>上传成功</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="panel">
          <h4 class="panel-title">预算</h4>
          <div class="panel-body">
            <div
              class="list-item flex--center"
              :class="{'upload-success': uploaded.budget}"
              @click="$router.push({ name: BUDGET.name, params: {companyId: $route.params.companyId, year: activeYear} })">
              <span class="item-label">预算</span>
              <span class="item-tip flex--center wait-upload"><i class="el-icon-upload icon"></i>等待上传</span>
              <span class="item-tip flex--center upload-success"><i class="el-icon-circle-check icon"></i>上传成功</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="panel">
          <h4 class="panel-title">审计/修正</h4>
          <div class="panel-body">
            <div
              class="list-item flex--center"
              @click="$router.push({ name: AUDIT.name, params: {companyId: $route.params.companyId, year: activeYear} })">
              <span class="item-label">审计/修正</span>
              <span class="item-tip flex--center wait-upload"><i class="el-icon-upload icon"></i>等待上传</span>
              <span class="item-tip flex--center upload-success"><i class="el-icon-circle-check icon"></i>上传成功</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-dialog
      :title="`${activeUpload.type === 'upload-success' ? '' : '上传'}${activeYear}年${activeUpload.target ? activeUpload.target + '月' : ''}${dictMap(activeUpload.panel)}`"
      custom-class="reports-upload-dialog"
      class="dialog--center"
      :visible.sync="uploadDialogVisible"
      :before-close="dialogBeforeClose"
      size="tiny">
      <div v-if="this.activeUpload.type === 'upload-success'">
        <div style="display: flex; align-items: center; margin-bottom: 32px;">
          <i class="el-icon-information" style="font-size: 24px; color: #f7ba2a; margin-right: 14px;"></i>
          <p>点击可下载Excle或者重新上传</p>
        </div>
        <div style="display: flex; justify-content: flex-end;">
          <el-button @click="handReUp">重新上传</el-button>
          <el-button type="primary">点击下载</el-button>
        </div>
      </div>
      <div v-else>
        <p style="margin-top: 0">命名规范：公司全称-2016{{ activeUpload.target ? '06' : '' }}.xls</p>
        <el-input disabled :value="fileName">
          <template slot="append">
            <el-button
              type="primary"
              v-loading="uploading"
              :disabled="uploading">
              上传
            </el-button>
            <input
              type="file"
              ref="uploadInput"
              class="file-input"
              v-if="uploadDialogVisible"
              @change="handleFileChange"
              :disabled="uploading"/>
          </template>
        </el-input>
      </div>
    </el-dialog>
    <router-view class='dialog--full'></router-view>
  </div>
</template>

<style lang="scss">
  #report-dashboard {
    position: relative;

    &.dialog-full {
      height: 100%;
      overflow: hidden;
    }

    .dialog--full {
      overflow-y: auto;
    }

    .custom-tabs {

      .el-tabs__header {
        border: 0;
      }

      .el-tabs__active-bar {
        display: none;
      }

      .el-tabs__item {
        margin-right: 10px;
        padding: 8px 16px;
        height: auto;
        line-height: inherit;
        color: inherit;
        border-radius: 4px;

        &.is-active {
          color: #fff;
          background: #20a0ff;
        }
        &:hover:not(.is-active) {
          color: #20a0ff;
        }
      }
    }

    .panel {
      .panel-title {
        font-size: 16px;
        margin-left: 10px;
      }

      .list-wrap {
        & + .list-wrap {
          margin-top: 20px;
        }
      }

      .list-item {
        position: relative;
        /*max-width: 180px;*/
        height: 100px;
        border: 1px solid #e0e6ed;
        border-radius: 4px;
        font-size: 12px;
        cursor: pointer;

        &.upload-success {
          border-color: #13ce66;
          .item-tip {
            &.wait-upload {
              display: none;
            }
            &.upload-success {
              display: flex;
            }
          }
        }

        &.not-yet {
          background: #e5e9f2;
          cursor: not-allowed;

          .item-tip {
            &.wait-upload {
              display: none;
            }
            &.not-yet {
              display: flex;
            }
          }
        }

        .item-tip {
          color: #8292a7;
          display: none;

          &.upload-success {
            .icon {
              color: #13ce66;
            }
          }
          &.wait-upload {
            display: flex;
          }
        }
        .icon {
          font-size: 24px;
          color: #778293;
          margin-right: 6px;
        }

        .item-label {
          position: absolute;
          top: 10px;
          right: 10px;
        }
      }
    }

    .dialog--full {
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
    }
    
    .el-input-group__append button.el-button {
      border-color: #20a0ff;
      background-color: #20a0ff;
      color: #fff;
      border-radius: 0 4px 4px 0;
    }
  }

  .reports-upload-dialog {
    .file-input {
      position: absolute;
      left: 0;
      width: 100%;
      top: 0;
      height: 100%;
      opacity: 0;
      cursor: pointer;
    }
    .el-input.is-disabled .el-input__inner {
      background: transparent;
      color: inherit;
    }
  }
</style>
