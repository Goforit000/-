
<template>
    
    <div id="app">
     
      <router-view />
    </div>
  </template>
  
  <script setup lang="ts">

</script>



  <style>
  .top-nav {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    background-color: #007bff; /* 顶部导航栏的蓝色背景 */
    padding: 10px;
  }
  
  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 10px;
  }
  
  .btn-settings, .btn-logout {
    background-color: #fff; /* 按钮的白色背景 */
    border: none;
    color: #007bff; /* 按钮的文字颜色 */
    padding: 10px 20px;
    margin-left: 10px;
    cursor: pointer;
    border-radius: 5px;
  }
  
  .btn-settings:hover, .btn-logout:hover {
    background-color: #f7f7f7; /* 按钮悬停时的背景颜色 */
  }
  </style>
  

