<template>
  <div id="main">
    <div id="main-Content">
      <!-- 直接显示基本设置内容，移除左侧导航栏 -->
      <div class="settings-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {};
  },
  created() {},
  methods: {}
};
</script>

<style scoped>
#main {
  width: 100%;
  height: 100%;
}

#main-Content {
  border: #d9d9d9 solid 1px;
  margin: 2%;
  margin-top: 3%;
  margin-left: 3%;
  width: 92%;
  height: 80%;
  background-color: rgb(255, 255, 255);
}

/* 调整内容区域样式，填满整个容器 */
.settings-content {
  width: 98%;
  height: 100%;
  margin: 0 auto;
  padding: 1%;
}
</style>
