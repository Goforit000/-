<template>
    <div style="position: relative; height: 100vh;">
      <!-- 关闭按钮 -->
      <el-button
        type="danger"
        size="small"
        style="position: absolute; top: 10px; right: 10px; z-index: 10;"
        @click="closePDF"
      >
        关闭
      </el-button>
  
      <!-- PDF 显示区域 -->
      <iframe
        :src="pdfPath"
        style="width: 100%; height: 100%; border: none;"
      ></iframe>
    </div>
  </template>
  
  <script>
  import { useRouter } from 'vue-router'; // 引入 Vue Router 相关的钩子
  
  export default {
    data() {
      return {
        pdfPath: "/2024年中国软件行业基准数据.pdf", // PDF 文件路径，存储在 public 文件夹下
      };
    },
    setup() {
      const router = useRouter(); // 获取 router 实例
  
      const closePDF = () => {
        // 跳转到名为 "cost" 的路由页面
        router.push({ name: "cost" });
      };
  
      return {
        closePDF,
      };
    },
  };
  </script>
  