import { createRouter, createWebHistory } from 'vue-router';
import Layout from '../components/layout.vue';
import Login from '../components/login.vue';
import Register from '@/components/register.vue'; // 根据实际路径调整
import Info from '../components/info.vue';
import WorkloadChoose from '../views/workloadchoose.vue'; // 导入新的组件
import WorkloadResult from '../views/workloadresult.vue'; // 导入新的组件
import WorkloadWord from '../views/workloadword.vue';  // 
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
      // zhq
      {
        path:"/project/:projectId/workload",
        name:"evaluation",
        component: () => import("../views/evaluation/workload.vue"),
        meta:{
          title:"工作量与造价评估",
          icon:"Menu",
        },
      },
      //查看PDF
      {
        path: "/pdf-viewer",
        name: "pdfViewer",
        component: () => import("@/views/PDFViewer.vue"), // PDF 页面组件
      },
      // 新的工作量选择页面的路由配置
      {
        path: '/workloadchoose/:projectId/:stateId/:projectIid',
        name: 'workloadchoose',
        component: WorkloadChoose,
        props: true, // 将路由参数作为 props 传递给组件
        meta: {
          title: "选择参数",
          icon: "Menu",
        },
      },
      {
        path: '/workloadresult/:projectId',
        name: 'workloadresult',
        component: WorkloadResult,
        props: true, // 将路由参数作为 props 传递给组件
        meta: {
          title: "子系统评估结果",
          icon: "Menu",
        },
      },
      {
        path: '/workloadword/:bigProjectId',
        name: 'workloadword',
        component: WorkloadWord,
        props: true, // 将路由参数作为 props 传递给组件
        meta: {
          title: "可视化与导出",
          icon: "Menu",
        },
      },
      
      {
        path: '/meetmenu',
        name: 'meet',
        component: () => import('../views/meet/MeetingManagement.vue'),
        meta: {
          title: '会议管理',
          icon: 'Menu',
        },
      },
      {
        path: '/costCreation',
        name: 'cost',
        component: () => import('../views/cost/costCreation.vue'),
        meta: {
          title: '造价标准',
          icon: 'Menu',
        }
      },

      {
        path: '/projectManage',
        name: 'projectManage',
        component: () => import('../views/project/projectManage.vue'),
        meta: {
          title: '项目管理',
          icon: 'Menu',
        },
      },
         //zhq
      {
           path: '/project/:projectId/systempoint',
           name: 'systempoint',
           component: () => import('../views/systempoint/systempoint.vue'),
           meta: {
             title: '子系统管理',
             icon: 'Menu',
           },
         },
         //zhq
  
         {
           path: '/project/:projectId/functionmenu',
           name: 'functionmenu',
           component: () => import('../views/function/FunctionPoint.vue'),
           meta: {
             title: '功能点管理',
             icon: 'Menu',
           },
         },
         //zhq
  {
    path:"/project/:projectId/detailmenu",
    name:"detailmenu",
    component: () => import("../views/function/DetailMenu.vue"),
    meta:{
            title:"功能详情",
            icon:"Menu",
            
        },
  },
  //zhq
         {
           path: '/project/:projectId/uploadMenu',
           name: 'uploadMenu',
           component: () => import('../views/upload/Uploadmenu.vue'),
           meta: {
             title: '上传管理',
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
