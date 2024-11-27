
<template>
  <div class="header">
    <div class="left-section">
      <Collapse></Collapse>
      <BreadCrumb class="breadcrumb"></BreadCrumb>
    </div>
    <div class="right-section">
      <img src="https://oss.youlai.tech/youlai-boot/2023/05/16/811270ef31f548af9cffc026dfc3777b.gif?imageView2/1/w/80/h/80" alt="Avatar" class="avatar">
      <span class="username">admin</span>
      <el-button round @click="goToHome" >首页</el-button>
      <el-button round @click="goToInfo">个人</el-button>
      <el-button round @click="goToLogin">退出</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import Collapse from './Collapse.vue'
import BreadCrumb from './Breadcrumb.vue'
import { useRouter } from 'vue-router';

const router = useRouter();

const goToHome = () => {
      router.push('/home');
    };

    const goToInfo = () => {
      router.push('/info');
    };
    const goToLogin = () => {
      router.push('/login');
    };

</script>

<style lang="scss" scoped>
.header {
  display: flex;
  // align-items: center;
  justify-content: space-between;
  padding: 0px 5px; /* 根据需要调整内边距 */
  width: 2500px; /* 固定长度 */
}

.right-section {
  display: flex;
  align-items: center;
}

.left-section {
  display: flex;
  align-items: center;
}

.breadcrumb {
  margin-left: 20px; /* 调整间距 */
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.username {
  margin-right: 10px;
  color: #eff3f7;
  font-size: 16px;
}

  .demonstration {
    display: block;
    color: #8492a6;
    font-size: 14px;
    margin-bottom: 20px;
  }
</style>
