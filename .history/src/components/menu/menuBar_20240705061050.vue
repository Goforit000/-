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
    path: "/system",
    name: "system",
    component: "layout",
    meta: {
      title: "管理系统",
      icon: "menu",
      roles: ["sys:system"]
    },
    children: [
      {
        path: "/usermenu",
        name: "user",
        component: "/system/user/usermenu",
        meta: {
          title: "用户管理",
          icon: "User",
          roles: ["sys:user"]
        },
        children: [
          {
            path: "/user_manage",
            name: "user_manage",
            component: "/system/user/user_manage",
            meta: {
              title: "用户管理",
              icon: "Menu",
              roles: ["sys:user_manage"]
            },
          },
          {
            path: "/tenant_manage",
            name: "tenant_manage",
            component: "/system/user/tenant_manage",
            meta: {
              title: "租户管理",
              icon: "Menu",
              roles: ["sys:tenant_manage"]
            },
          }
        ]
      },
      {
        path: "/planmenu",
        name: "plan",
        component: "/system/plan/planmenu",
        meta: {
          title: "功能点分析",
          icon: "Avatar",
          roles: ["sys:plan"]
        },
        children:[
          {
            path: "/department_manage",
            name: "department_manage",
            component: "/system/plan/department_manage",
            meta: {
              title: "部门管理",
              icon: "coffee-cup",
              roles: ["sys:department_manage"]
            },
          },
          {
            path: "/role_manage",
            name: "role_manage",
            component: "/system/plan/role_manage",
            meta: {
              title: "角色管理",
              icon: "cold-drink",
              roles: ["sys:role_manage"]
            },
          },
        ]
      },
      {
        path: "/fieldmenu",
        name: "field",
        component: "/system/field/fieldmenu",
        meta: {
          title: "造价综合评估",
          icon: "Loading",
          roles: ["sys:field"]
        },
        children: [
          {
            path: "/fielddetail",
            name: "fielddetail",
            component: "/system/field/fields/fielddetail",
            meta: {
              title: "动态列表",
              icon: "Menu",
              roles: ["sys:fielddetail"]
            }
          },
          {
            path: "/fieldcreat",
            name: "fieldcreat",
            component: "/system/field/fields/fieldcreat",
            meta: {
              title: "新建动态",
              icon: "Menu",
              roles: ["sys:fieldcreat"]
            }
          }

        ]
      },
      {
        path: "/coursemenu",
        name: "course",
        component: "/system/course/coursemenu",
        meta: {
          title: "课程管理",
          icon: "Menu",
          roles: ["sys:course"]
        },
      },
      // zhq
{
    path:"/superCourse",
    name:"superCourse",
    component:"/system/course/superCourse",
    meta:{
            title:"课程管理与审核",
            icon:"Menu",
            roles:["sys:superCourse"]
        },
},

//zhq
      {
        path: "/meetmenu",
        name: "meet",
        component: "/system/meet/meetmenu",
        meta: {
          title: "会议管理",
          icon: "Menu",
          roles: ["sys:meet"]
        }
      }
    ]
  }
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