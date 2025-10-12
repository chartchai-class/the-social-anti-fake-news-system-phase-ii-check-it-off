import { createRouter, createWebHistory } from "vue-router";
import Home from "@/pages/HomePage.vue";
import NewsDetail from "@/pages/NewsDetail.vue";
import Vote from "@/pages/Vote.vue";
import Comment from "@/pages/Comment.vue";
import Login from "@/pages/Login.vue";

const routes = [
  { path: "/", name: "Home", component: Home },
  { path: "/news/:id", name: "NewsDetail", component: NewsDetail },
  { path: "/vote/:id", name: "Vote", component: Vote },
  { path: "/comment/:id", name: "Comment", component: Comment },
  { path: "/login", name: "Login", component: Login },
];

export default createRouter({
  history: createWebHistory(),
  routes,
});
