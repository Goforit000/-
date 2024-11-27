<template>
  <!-- <el-carousel :interval="5000" arrow="always">
    <el-carousel-item v-for="(item, index) in images" :key="index">
      <img :src="item" alt="Image" class="carousel-image"/>
    </el-carousel-item>
  </el-carousel> -->
  <div>
    <div class="carousel">
      <div class="carousel-item">
        <div class="introduction">
          <h2>欢迎来到软件造价评估系统</h2>
          <p>东北大学软件学院</p>
          <p>本系统是一款多租户软件造价评估工具，允许租户管理团队、创建项目、进行功能点分析和综合造价评估。它支持详细的评估报告生成与审核，并提供数据看板，以便租户监控项目进度和成本，提升项目管理效率与协作。</p>
        </div>
      </div>
    </div>
    <div class="content">
      <div class="introduction">
        <h2>资讯要闻</h2>
        <ul>
          <li v-for="(info, index) in infoNews" :key="index">{{ info }} ...... {{ date }}</li>
        </ul>
      </div>
      <div class="announcement">
        <h2>通知公告</h2>
        <ul>
          <li v-for="(announcement, index) in announcements" :key="index">{{ announcement }} ...... {{ date }}</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const images = ref([
  '@/assets/home1.png',
  console.log(111),
  '@/assets/home2.png',
  '@/assets/home3.png',
  '@/assets/home4.png',
  // '@/assets/home5.png'
])

const infoNews = ref([
  '资讯内容1: 新发布的软件造价评估指南，提高评估准确性。',
  '资讯内容2: 探讨大数据技术在软件造价评估中的应用。',
  '资讯内容3: 行业专家分享软件造价评估的最佳实践。',
  '资讯内容4: 软件造价评估工具更新，新增多项功能。',
  '资讯内容5: 软件造价评估行业报告出炉，揭示趋势和挑战。'
])
const announcements = ref([
  '公告内容1: 即日起，本平台将提供免费的软件造价评估咨询服务。',
  '公告内容2: 关于软件造价评估标准更新的通知，请查收。',
  '公告内容3: 本月将举办软件造价评估专题培训，报名从速。',
  '公告内容4: [重要]软件造价评估系统将于今晚进行升级维护。',
  '公告内容5: 欢迎新加入的合作伙伴，共同推动行业的发展。'
])


const date = '2024.5.1'

onMounted(() => {
  const carousel = document.querySelector('.carousel')
  if (carousel) {
    let index = 0
    setInterval(() => {
      index = (index + 1) % 1
      carousel.scrollTo({
        left: index * carousel.clientWidth,
        behavior: 'smooth'
      })
    }, 3000)
  }
})
</script>

<style scoped>
.el-carousel__item h3 {
  color: #475669;
  opacity: 0.75;
  line-height: 300px;
  margin: 0;
  text-align: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}

.carousel {
  display: flex;
  overflow: hidden;
  width: 100%;
  height: 300px;
  justify-content: center;
  align-items: center;
}

.carousel-item {
  min-width: 100%;
  transition: transform 3s ease-in-out;
  display: flex;
  justify-content: center;
  align-items: center;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.introduction {
  padding: 20px;
  font-size: 18px;
}

.announcement {
  padding: 20px;
  font-size: 18px;
}

.content {
  display: flex;
  justify-content: space-between;
  padding: 20px;
}

.introduction, .announcement {
  width: 45%;
}

h2 {
  margin-bottom: 10px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  margin-bottom: 10px;
}
</style>

