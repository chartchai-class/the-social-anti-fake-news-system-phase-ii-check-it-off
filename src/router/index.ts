import { createRouter, createWebHistory } from "vue-router";
import Home from "@/pages/HomePage.vue";
import NewsDetail from "@/pages/NewsDetail.vue";
import Vote from "@/pages/Vote.vue";
import Comment from "@/pages/Comment.vue";
import Login from "@/pages/Login.vue";
import NewsManage from "@/pages/AdminDashboard/NewsManage.vue";
import CommentsManage from "@/pages/AdminDashboard/CommentsManage.vue";

const routes = [
  { path: "/", name: "login", component: Login },
  { path: "/home", name: "Home", component: Home },
  { path: "/news/:id", name: "NewsDetail", component: NewsDetail },
  { path: "/vote/:id", name: "Vote", component: Vote },
  { path: "/comment/:id", name: "Comment", component: Comment },
  { path: "/login", name: "Login", component: Login },
  {
    path: "/admin/manage-news",
    name: "NewsManage",
    component: NewsManage,
  },
  {
    path: "/admin/delete-comment",
    name: "CommentsManage",
    component: CommentsManage,
  },
];

export default createRouter({
  history: createWebHistory(),
  routes,
});
