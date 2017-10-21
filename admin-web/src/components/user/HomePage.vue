<script>
  /*
   * Created by zhengji
   * Date: 2017/10/20
   */
  import Info from './_thumbs/Info.vue'
  import Log from './_thumbs/Log.vue'
  import Relation from './_thumbs/Relation.vue'

  export default {
    name: 'HomePage',
    components: {
      info: Info,
      log: Log,
      relation: Relation
    },
    data () {
      return {
        activeTab: 'info'
      }
    }
  }
</script>

<template>
  <div class="personal-homepage">
    <div class="flex--vcenter page-top">
      <div class="page-title">
        用户管理 &gt; 个人主页
      </div>
    </div>
    <div class="content-wrap flex">
      <div class="content-lt flex-item--none">
        <img
          src="http://www.baidu.com"
          class="avatar"
          width="80"
          height="80"
          alt=""/>
        <p class="text-center">很长的昵称</p>
        <el-button type="primary" size="small" style="width: 100%;">发私信</el-button>
      </div>
      <div class="content-rt flex-item">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="个人信息" name="info"></el-tab-pane>
          <el-tab-pane label="日记" name="log"></el-tab-pane>
          <el-tab-pane label="关联就诊人" name="relation"></el-tab-pane>
        </el-tabs>
        <div class="content-body">
          <keep-alive>
            <component :is="activeTab"></component>
          </keep-alive>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
  .personal-homepage {
    .content-wrap {
      margin-top: 40px;
    }
    .content-lt {
      .avatar {
        display: inline-block;
        background: #d8d8d8;
      }
    }
    .content-rt {
      margin-left: 30px;
      border: 1px solid #d3dce6;
      min-height: 580px;

      .el-tabs__item {
        width: 120px;
        text-align: center;
      }

      .content-body {
        padding: 30px;
      }
    }
  }
</style>
