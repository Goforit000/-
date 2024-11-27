import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
 import Layout from '../components/layout.vue'
 import Login from '../components/login.vue';
 import Register from '@/components/register.vue'; // 根据实际路径调整
 import Info from '../components/info.vue';

 
 const routes = [
   {
     // 初始指向登录界面
     path: '/',
     redirect: '/login',
   },
   {
     path: '/login',
     name: 'login',
     component: Login,
     meta: {
       title: '登录',
     },
   },
  //  {
  //   path: '/info',
  //   name: 'info',
  //   component: Info,
  //   meta: {
  //     title: '个人资料',
  //   },
  // },
  {
    path: '/',
    component: Layout,
    redirect: '/info',
    children: [
      {
        path: '/info',
        name: 'info',
        component: () => import('../components/info.vue'),
        meta: {
          title: '个人资料',
          icon: 'menu',
        },
      },
    ],
  },
   {
    path: '/register',
    name: 'register',
    component: Register,
  },
   {
     path: '/',
     component: Layout,
     redirect: '/home',
     children: [
       {
         path: '/home',
         name: 'home',
         component: () => import('../views/home/home.vue'),
         meta: {
           title: '首页',
           icon: 'menu',
         },
       },
     ],
   },
   {
     path: '/system',
     name: 'system',
     component: Layout,
     meta: {
       title: '管理系统',
       icon: 'menu',
     },
     children: [
       {
         path: '/usermenu',
         name: 'user',
         component: () => import('../views/user/usermenu.vue'),
         meta: {
           title: '用户管理',
           icon: 'User',
         },
       },
       {
         path: '/user_manage',
         name: 'user_manage',
         component: () => import('../views/user/users/user_manage.vue'),
         meta: {
           title: '用户管理',
           icon: 'Menu',
         },
       },
       {
         path: '/tenant_manage',
         name: 'tenant_manage',
         component: () => import('../views/user/users/tenant_manage.vue'),
         meta: {
           title: '租户管理',
           icon: 'Menu',
         },
       },
       {
        path: "/planmenu",
        name: "plan",
        component: () => import("../views/plan/planmenu.vue"),
        meta: {
            title: "功能点分析",
            icon: "Avatar",
        },
    },

    {
        path: "/department_manage",
        name: "department_manage",
        component: () => import("../views/plan/plans/department_manage.vue"),
        meta: {
            title: "部门管理",
            icon: "User",
        },
    },
    {
        path: "/role_manage",
        name: "role_manage",
        component: () => import("../views/plan/plans/role_manage.vue"),
        meta: {
            title: "角色管理",
            icon: "User",
        },
    },
    {
      path:"/fieldmenu",
      name:"field",
      component: () => import("../views/field/fieldmenu.vue"),
      meta:{
              title:"造价综合评估",
              icon:"Menu",
          },
          
    },
    {
      path:"/fielddetail",
      name:"fielddetail",
      component: () => import("../views/field/fields/fielddetail.vue"),
      meta:{
              title:"动态列表",
              icon:"Menu",
    
    }
    },
    {
      path:"/fieldcreat",
      name:"fieldcreat",
      component: () => import("../views/field/fields/fieldcreat.vue"),
      meta:{
              title:"新建动态",
              icon:"Menu",
    
    }
    },
       {
         path: '/coursemenu',
         name: 'course',
         component: () => import('../views/course/coursemenu.vue'),
         meta: {
           title: '课程管理',
           icon: 'Menu',
         },
       },
       //zhq
{
  path:"/superCourse",
  name:"superCourse",
  component: () => import("../views/course/superCourse.vue"),
  meta:{
          title:"课程管理与审核",
          icon:"Menu",
          roles: ['requiresAdmin']
      },
},
//zhq
       {
         path: '/meetmenu',
         name: 'meet',
         component: () => import('../views/meet/MeetingManagement.vue'),
         meta: {
           title: '会议管理',
           icon: 'Menu',
         },
       },
       
     ],
   },
 ];
 
 const router = createRouter({
   history: createWebHistory(),
   routes,
 });
 
 export default router;
 