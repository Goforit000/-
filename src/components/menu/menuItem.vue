<template>
    <!-- 遍历menuList渲染菜单项 -->
    <template v-for="menu in menuList" :key="menu.path">
        <!-- 如果存在子菜单，则使用el-sub-menu -->
        <el-sub-menu v-if="menu.children" :index="menu.path">
            <template #title>
                <el-icon>
                    <component :is="menu.meta.icon"></component>
                </el-icon>
                <span>{{ menu.meta.title }}</span>
            </template>
            <!-- 递归渲染子菜单 -->
            <menu-item :menuList="menu.children"></menu-item>
        </el-sub-menu>
        
        <!-- 如果没有子菜单，则直接使用el-menu-item -->
        <el-menu-item v-else :index="menu.path">
            <template #title>
                <el-icon>
                    <component :is="menu.meta.icon"></component>
                </el-icon>
                <span>{{ menu.meta.title }}</span>
            </template>
        </el-menu-item>
    </template>
</template>

<script setup lang="ts">
import menuItem from './menuItem.vue'; // 确保导入menuItem组件
import { defineProps } from 'vue';

defineProps(["menuList"]);
</script>

<!-- 样式部分保持不变 -->