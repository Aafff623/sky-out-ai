import { RouteRecordRaw } from "vue-router";

export const Layout = () => import("@/layout/index.vue");

export const constantRoutes: RouteRecordRaw[] = [
  {
    path: "/redirect",
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index.vue"),
      },
    ],
  },

  {
    path: "/login",
    component: () => import("@/views/login/index.vue"),
    meta: { hidden: true },
  },

  {
    path: "/",
    name: "/",
    component: Layout,
    redirect: "/workbench",
    children: [
      {
        path: "workbench",
        name: "WorkbenchIndex",
        component: () => import("@/views/workbench/index.vue"),
        meta: {
          title: "工作台",
          icon: "s-workbench",
          affix: true,
          keepAlive: true,
        },
      },
      {
        path: "401",
        component: () => import("@/views/error-page/401.vue"),
        meta: { hidden: true },
      },
      {
        path: "404",
        component: () => import("@/views/error-page/404.vue"),
        meta: { hidden: true },
      },
    ],
  },

  {
    path: "/statistics",
    name: "Statistics",
    component: Layout,
    redirect: "/statistics/index",
    children: [
      {
        path: "index",
        name: "StatisticsIndex",
        component: () => import("@/views/statistics/index.vue"),
        meta: {
          title: "数据统计",
          icon: "s-data_statistics",
          keepAlive: true,
        },
      },
    ],
  },
  {
    path: "/order",
    name: "Order",
    component: Layout,
    redirect: "/order/index",
    children: [
      {
        path: "index",
        name: "OrderIndex",
        component: () => import("@/views/order/index.vue"),
        meta: {
          title: "订单管理",
          icon: "s-order",
          keepAlive: true,
        },
      },
    ],
  },

  {
    path: "/dish",
    name: "Dish",
    component: Layout,
    redirect: "/dish/index",
    children: [
      {
        path: "index",
        name: "DishIndex",
        component: () => import("@/views/dish/index.vue"),
        meta: {
          title: "菜品管理",
          icon: "s-dish",
          keepAlive: true,
        },
      },
    ],
  },
  {
    path: "/setmeal",
    name: "Setmeal",
    component: Layout,
    redirect: "/setmeal/index",
    children: [
      {
        path: "index",
        name: "SetmealIndex",
        component: () => import("@/views/setmeal/index.vue"),
        meta: {
          title: "套餐管理",
          icon: "s-setmeal",
          keepAlive: true,
        },
      },
    ],
  },
  {
    path: "/category",
    name: "Category",
    component: Layout,
    redirect: "/category/index",
    children: [
      {
        path: "index",
        name: "CategoryIndex",
        component: () => import("@/views/category/index.vue"),
        meta: {
          title: "分类管理",
          icon: "s-category",
          keepAlive: true,
        },
      },
    ],
  },
  {
    path: "/comment",
    name: "Comment",
    component: Layout,
    redirect: "/comment/index",
    children: [
      {
        path: "index",
        name: "CommentIndex",
        component: () => import("@/views/comment/index.vue"),
        meta: {
          title: "评论管理",
          icon: "el-icon-comment",
          keepAlive: true,
        },
      },
    ],
  },
  {
    path: "/sensitive",
    name: "Sensitive",
    component: Layout,
    redirect: "/sensitive/index",
    children: [
      {
        path: "index",
        name: "SensitiveIndex",
        component: () => import("@/views/sensitive/index.vue"),
        meta: {
          title: "敏感词管理",
          icon: "el-icon-warnTriangleFilled",
          keepAlive: true,
        },
      },
    ],
  },
  {
    path: "/knowledgeBase",
    name: "KnowledgeBase",
    component: Layout,
    redirect: "/knowledgeBase/index",
    children: [
      {
        path: "index",
        name: "knowledgeBase",
        component: () => import("@/views/knowledge-base/index.vue"),
        meta: {
          title: "AI知识库管理",
          icon: "el-icon-management",
          keepAlive: true,
        },
      },
    ],
  },

  {
    path: "/employee",
    name: "Employee",
    component: Layout,
    redirect: "/employee/index",
    children: [
      {
        path: "index",
        name: "EmployeeIndex",
        component: () => import("@/views/employee/index.vue"),
        meta: {
          title: "员工管理",
          icon: "s-user",
          keepAlive: true,
        },
      },
    ],
  },

  {
    path: "/customer-service",
    name: "CustomerService",
    component: Layout,
    redirect: "/customer-service/index",
    children: [
      {
        path: "index",
        name: "CustomerServiceIndex",
        component: () => import("@/views/customer-service/index.vue"),
        meta: {
          title: "客服管理",
          icon: "s-customer-service",
          keepAlive: true,
        },
      },
    ],
  },
];
