import { createRouter, createWebHistory } from 'vue-router';
import HouseView from '@/views/HouseView.vue';
import SubscriptionView from '@/views/SubscriptionView.vue';
import SubscriptionDetail from '@/views/SubscriptionDetail.vue';
import MainView from '@/views/MainView.vue';
import NoticeView from '@/views/NoticeView.vue';
import NoticeDetail from '@/components/notice/NoticeDetail.vue';
import NoticeList from '@/components/notice/NoticeList.vue';
import NoticeWrite from '@/components/notice/NoticeWrite.vue';
import NoticeModify from '@/components/notice/NoticeModify.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'MainView',
      component: MainView,
    },
    {
      path: '/house',
      name: 'HouseView',
      component: HouseView,
    },
    {
      path: '/notice',
      name: 'NoticeView',
      component: NoticeView,
      redirect: { name: 'NoticeList' },
      children: [
        {
          path: '/list',
          name: 'NoticeList',
          component: NoticeList,
        },
        {
          path: 'view/:id',
          name: 'NoticeDetail',
          component: NoticeDetail,
        },
        {
          path: '/notice/write',
          name: 'NoticeWrite',
          component: NoticeWrite,
        },
        {
          path: '/notice/modify/:id',
          name: 'NoticeModify',
          component: NoticeModify,
        },
      ],
    },

    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/UserLoginFormView.vue'),
    },
    {
      path: '/regist',
      name: 'regist',
      component: () => import('@/views/UserRegisterFormView.vue'),
    },
    {
      path : '/reset',
      name : 'reset',
      component : ()=> import('@/views/UserResetPassword.vue'),
    },
    {
      path: '/redirect',
      beforeEnter(to, from, next) {
        next(false);
      },
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: () => import('@/views/UserMyPageView.vue'),
      children: [
        {
          path: '',
          name: 'info',
          components: {
            sidebar: () => import('@/components/user/UserMyPageInfoSide.vue'),
            default: () => import('@/components/user/UserMyPageInfo.vue'),
          },
          props: {
            default: true,
            sidebar: true,
          },
        },
        {
          path: 'modify',
          name: 'modify',
          components: {
            sidebar: () => import('@/components/user/UserMyPageInfoSide.vue'),
            default: () => import('@/components/user/UserMyPageModify.vue'),
          },
        },
        {
          path: 'delete',
          name: 'delete',
          components: {
            sidebar: () => import('@/components/user/UserMyPageInfoSide.vue'),
            default: () => import('@/components/user/UserMyPageDelete.vue'),
          },
        },
        {
          path: 'management',
          name: 'management',
          components: {
            sidebar: () => import('@/components/user/EstateManagementInfoSide.vue'),
            default: () => import('@/components/user/EstateManagementInfo.vue'),
          },
        },
        {
          path: 'managementRegist',
          name: 'managementRegist',
          components: {
            sidebar: () => import('@/components/user/EstateManagementInfoSide.vue'),
            default: () => import('@/components/user/EstateManagementRegist.vue'),
          },
        },
        {
          path: 'managementDelete',
          name: 'managementDelete',
          components: {
            sidebar: () => import('@/components/user/EstateManagementInfoSide.vue'),
            default: () => import('@/components/user/EstateManagementDelete.vue'),
          },
        },
        {
          path: '/management-detail',
          name: 'managementDetail',
          components: {
            sidebar: () => import('@/components/user/EstateManagementInfoSide.vue'),
            default: () => import('@/components/user/EstateManagementDetail.vue'),
          },
          props: {
            default: true,
            sidebar: true,
          },
        },
        {
          path: "/management-modify",
          name: "managementModify",
          components: {
            sidebar: () => import("@/components/user/EstateManagementInfoSide.vue"),
            default: () => import("@/components/user/EstateManagementModify.vue"),
          },
          props: {
            default: true,
            sidebar: true,
          },
        },
        {
          path: '/management-detail',
          name: 'managementDetailFavorite',
          components: {
            sidebar: () => import('@/components/user/FavoriteSide.vue'),
            default: () => import('@/components/user/EstateManagementDetail.vue'),
          },
          props: {
            default: true,
            sidebar: true,
          },
        },
        {
          path: 'favorite',
          name: 'favorite',
          components: {
            sidebar: () => import('@/components/user/FavoriteSide.vue'),
            default: () => import('@/components/user/Favorite.vue'),
          },
        },
        {
          path: "chat",
          name: "chatMain",
          components: {
            sidebar: () => import("@/components/user/ChattingSide.vue"),
          },
          props: { sidebar: (route) => ({ key: route.query.key }) },
        },
        {
          path: "chat",
          name: "chat",
          components: {
            sidebar: () => import("@/components/user/ChattingSide.vue"),
            default: () => import("@/components/user/ChattingMainMain.vue"),
          },
          props: { sidebar: (route) => ({ key: route.query.key }) },
        },
        {
          path: "chatDetail",
          name: "chatDetail",
          components: {
            sidebar: () => import('@/components/user/ChattingSide.vue'),
            default: () => import('@/components/user/ChattingDetail.vue'),
          },
          props: { sidebar: (route) => ({ key: route.query.key }) },
        },
      ],
    },
    {
      path: '/Subscription',
      name: 'SubscriptionView',
      component: SubscriptionView,
    },
    {
      path: '/detail',
      name: 'EventDetail',
      component: SubscriptionDetail,
      props: (route) => ({ eventDetail: route.params.eventDetail }),
    },
  ],
});

export default router;
