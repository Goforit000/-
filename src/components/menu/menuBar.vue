<template>
    <menulogo></menulogo>
    <el-menu :default-active="defaultActive" class="el-menu-vertical-demo" 
    :collapse="isCollapse"
    @open="handleOpen"
    @close="handleClose"
    unique-opened   
    active-text-color="#ffd04b" background-color="#0d61b6"  text-color="#fff" router>
       <menuItem :menuList="filteredMenuList"></menuItem>

    </el-menu>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue';
import menuItem from './menuItem.vue';
import menulogo from './menulogo.vue';
import { useRoute } from 'vue-router';
import { menuStore } from '../../store/menu';
import WorkloadWord from '../views/workloadword.vue';  // 
const route = useRoute();
const store = menuStore();

const defaultActive = computed(() => {
  const { path } = route;
  console.log("path===>" + path);
  return path;
});

const isCollapse = computed(() => {
  return store.getCollapse;
});

const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};

let menuList = reactive([
{
        path: "/home",
        name: "home",
        component: "../views/home/home.vue",
        meta: {
          title: "首页",
          icon: "Menu",
          roles: ["sys:home"]
        },
      },
{
        path: "/costCreation",
        name: "cost",
        component: "/system/cost/costCreation",
        meta: {
          title: "造价标准",
          icon: "document",
          roles: ["sys:cost"]
        },
      },
      
      {
        path: "/projectManage",
        name: "projectManage",
        component: "/system/project/projectManage",
        meta: {
          title: "项目管理",
          icon: "files",
  
        },
      },
      
{
    path:"/workloadword/请选择项目",
    name:"workloadword",
    component:"WorkloadWord",
    meta:{
            title:"评估结果",
            icon:"share",
            roles:["sys:WorkloadWord"]
        },
},
//zhq
     
]);


const filteredMenuList = computed(() => {
  const roles = localStorage.getItem('role');
  console.log(roles);

  const filterMenu = (menus: any[]) => {
    return menus.filter((menu: { children: any; meta: { roles: string | string[]; }; }) => {
      if (menu.children) {
        menu.children = filterMenu(menu.children);
      }
      if (menu.meta.roles) {
        if(roles!=null){
          if (roles.includes('超级管理员')) {
            // 过滤掉租户管理员相关的菜单
            if (menu.meta.roles.includes('sys:course')) {
              return false;
            }
          } 
          if (roles.includes('租户管理员')) {
            // 过滤掉超级管理员相关的菜单
            if (menu.meta.roles.includes('sys:superCourse')||menu.meta.roles.includes('sys:tenant_manage')) {
              return false;
            }
          }
          // if (menu.meta.roles.includes('sys:tenant_manage') && !roles.includes('superadmin')) {
          //   return false;
          // }
        }
      }
      return true;
    });
  };
return filterMenu(menuList);
});

</script>



<style lang="scss" scoped>
:deep(.el-sub-menu .el-menu-item){
background-color:#0d61b6
}
:deep(.el-sub-menu .el-menu-item:hover){
    background-color:#0d61b6
}
:deep(.el-menu-item.is-active){
    color: #ffed49
}
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}

        </style>