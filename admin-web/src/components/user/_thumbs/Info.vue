<script>
/*
 * Created by zhengji
 * Date: 2017/10/20
 */
export default {
  name: 'Info',
  data () {
    this.dataList = [{
      key: 'name',
      label: '姓名'
    }, {
      key: 'tel',
      label: '手机号'
    }, {
      key: 'visitNo',
      label: '就诊号'
    }, {
      key: 'ID_no',
      label: '身份证号'
    }, {
      key: 'age',
      label: '年龄'
    }, {
      key: 'id',
      label: '用户ID'
    }]
    return {
      data: []
    }
  }
}
</script>

<template>
  <div>
    <el-row
      class="data-item flex"
      v-for="item in dataList"
      :key="item.label">
      <span class="flex-item--none data-item__label">{{ item.label }}</span>
      <span class="colon">:</span>
      <span class="flex-item data-item__value"></span>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
  .data-item {
    & + .data-item {
      margin-top: 20px;
    }
    &__label {
      color: #b2b2b2;
      width: 70px;
    }
    .colon {
      color: #b2b2b2;
    }
    &__value {
    }
  }
</style>
