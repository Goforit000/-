
<template>

<el-icon color="#409efc" style="cursor: pointer;" size="30px" @click="setCollapse">
<component :is="status ? Fold : Expand"></component>
  </el-icon>
    </template>
    
    <script setup lang="ts">
    import{computed}from 'vue'

    import{Fold,Expand}from '@element-plus/icons-vue'
import { menuStore } from '../../store/menu';

    const store = menuStore();
    const  status = computed(()=>{
        return !store.getCollapse
    })

    const setCollapse =()=>{
      store.setCollapse(!store.getCollapse)
    }
    </script>

    
    
    <style lang="scss" scoped>
    
    </style>
    