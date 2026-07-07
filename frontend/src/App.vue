<template>
  <router-view />
</template>

<script setup>
// 根组件，只放一个路由出口
// 登录页 / 首页会通过 router-view 显示
</script>

<style>
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
}
</style>