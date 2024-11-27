
<template>
<div class="logo">
<img :src="menulogo" class="llo"></img>
<span v-if="show" class="title">{{ title }}</span>
</div>
    </template>
    
    <script setup lang="ts">
    import menulogo from '@/assets/CMH.png'
import { watch, ref } from 'vue';
import { menuStore } from '../../store/menu';

    
    const title= ref('软件造价评估')
    const store = menuStore();
// const show=computed(()=>{
// return !store.getCollapse
// })
const show= ref(true)
watch(
    ()=>store.getCollapse,
    (collapse:boolean)=>{
        if(!collapse){
            setTimeout(() => {
                show.value=!collapse
            }, 3000
        );
        }else{
            show.value=!collapse
        }
    }
)

    </script>
    
    
    
    <style lang="scss" scoped>
    .logo{
        display: flex;
        width: 100%;
        height: 60px;
        background-color:#0d61b6;
        cursor:pointer;
        text-align: center;
        align-items: center;

    }
    .llo{
        width: 70px;
        height: 55px;
        margin-right: 0px;
        margin-left: 0px;
    }
    .title{
        color: #ffff;
        font-size:21px;
        font-family: 宋体;
        margin-left: 0px;

      


    }

    
    </style>
    