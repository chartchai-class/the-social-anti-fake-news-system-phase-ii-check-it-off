<template>
  <div class="PageContainer">
    <div v-if="isLoading" class="LoadingScreen">
      <div class="spinner"></div>
      <p>Loading news...</p>
    </div>

    <div v-else>
      <router-link to="/" class="BackButtonContainer">
        <img src="@/assets/Card/Back.png" alt="Back Icon" class="BackIcon" />
        Back to News List
      </router-link>

      <div class="NewsDetailContent">
        <!-- Main Class -->
        <h1 class="text-2xl font-bold mb-2">{{ news.title }}</h1>
        <div class="NewsMeta">
          <span
            class="news-status"
            :class="{
              verified: news.stats === 'Verified',
              fake: news.stats === 'Fake News',
              review:
                news.stats === 'Under Review' || news.stats === 'Unverified',
            }"
          >
            {{ news.stats }}
          </span>

          <span class="AuthorContainer">
            <img :src="AuthorIcon" alt="Author Icon" class="AuthorIcon" />
            By {{ news.author || "Unknown" }}
          </span>

          <span class="DateContainer">
            <img :src="DateIcon" alt="Date Icon" class="DateIcon" />
            By {{ news.date || "Unknown" }}
          </span>
        </div>

        <div class="FeedbackSection">
          <span class="VotingSystem Like">
            <img :src="LikeIcon" alt="Like" class="icon" /> {{ news.upVotes }}
          </span>
          <span class="VotingSystem Dislike">
            <img :src="DisLikeIcon" alt="DisLike" class="icon" />
            {{ news.downVotes }}
          </span>
          <span class="VotingSystem Comment">
            <img :src="CommentIcon" alt="Comment" class="icon" />
            {{ news.comments }} Comments
          </span>
        </div>

        <div class="ImageOfNews" v-if="getImageById(news.id)">
          <img
            :src="getImageById(news.id)"
            alt="News Image"
            class="NewsMainImage"
          />
        </div>

        <div class="NewsDescription">
          {{ news.fulldescription }}
        </div>

        <div class="NewsActions">
          <button class="vote-btn" @click="goToVote">Vote on This News</button>
          <button class="comment-btn" @click="goToComment">
            View More Comment
          </button>
        </div>
      </div>
    </div>
    <!-- Main Class -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

import AuthorIcon from "@/assets/Card/User.png";
import DateIcon from "@/assets/Card/Date.png";
import LikeIcon from "@/assets/Card/Like.png";
import DisLikeIcon from "@/assets/Card/Dislike.png";
import CommentIcon from "@/assets/Card/Comment.png";

interface NewsItem {
  id: number;
  title: string;
  stats: string;
  author: string;
  date?: string;
  upVotes?: number;
  downVotes?: number;
  comments?: number;
  fulldescription?: string;
  contentLines: string[];
}

const route = useRoute();
const router = useRouter();
const newsId = Number(route.params.id);

const isLoading = ref(true);
const news = ref<NewsItem>({
  id: 0,
  title: "Loading...",
  stats: "Loading...",
  author: "Loading...",
  date: "Loading...",
  upVotes: 0,
  downVotes: 0,
  comments: 0,
  fulldescription: "Loading content...",
  contentLines: ["Loading content..."],
});

onMounted(async () => {
  try {
    const res = await fetch("http://localhost:5175/api/news");
    if (!res.ok) throw new Error("Failed to fetch news");

    const data = await res.json();
    const selected = data.news.find((n: any) => Number(n.id) === newsId);

    if (selected) {
      news.value = {
        id: selected.id,
        title: selected.title,
        stats: selected.stats || "Unverified",
        author: selected.author || "Unknown",
        date: selected.date || "Unknown",
        upVotes: selected.upVotes || 0,
        downVotes: selected.downVotes || 0,
        comments: selected.comments || 0,
        fulldescription: selected.fullDescription || "No content available.",
        contentLines: selected.description
          ? selected.description.split("\n")
          : ["No content available."],
      };
    } else {
      news.value.title = "News not found";
    }
  } catch (err) {
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});

const images = import.meta.glob("@/assets/NewsImages/*.png", { eager: true });

function getImageById(id: number) {
  const path = `/src/assets/NewsImages/${id}.png`;
  return (images[path] as any)?.default || "";
}

function goToVote() {
  router.push({ name: "Vote", params: { id: newsId } });
}

function goToComment() {
  router.push({ name: "Comment", params: { id: newsId } });
}
</script>

<style scoped>
.LoadingScreen {
  position: fixed;
  inset: 0;
  background-color: rgba(255, 255, 255, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  text-align: center;
  font-size: 18px;
  color: #333;
  font-family: "Outfit", sans-serif;
}

.spinner {
  border: 6px solid #e5e7eb;
  border-top: 6px solid #003791;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.PageContainer {
  width: 900px;
  margin: 0 auto;
  justify-content: center;
  padding-bottom: 40px;
}

.BackButtonContainer {
  max-width: fit-content;
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: #f3f4f6;
  color: #000;
  font-size: 16px;
  text-decoration: none;
  border-radius: 8px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-left: 0;
}


.back-button {
  display: flex;
  align-items: center;
  font-size: 16px;
  line-height: 1;
  transition: all 0.3s ease;
  color: #000;
}

.BackButtonContainer:hover {
  background-color: #d1d5db;
}

.BackIcon {
  width: 20px;
  height: 20px;
  margin-right: 5px;
  vertical-align: middle;
}

.NewsDetailContent {
  color: #000000;
  margin-top: 10px;
  background-color: #ffffff; /* เพิ่มพื้นหลังอ่อนๆ จะเห็นเงาชัดขึ้น */
  border: 1px solid #d1d5db; /* เส้นขอบเทาอ่อน (เหมือน Tailwind gray-300) */
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15); /* เงาเบลอหน่อย ดูนุ่ม */
  min-height: 650px;
  text-align: left;
  padding: 20px;
}

.NewsDetailContent h1 {
  margin-top: -5px;
  font-size: 30px;
  margin-left: 20px;
}

.NewsMeta {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  width: 100%;
  padding: 0px;
  border-radius: 6px;
  max-width: fit-content;
  margin-left: 20px;
}

/* Status badge */
.news-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  font-weight: 500;
  margin: 0;
}

/* สีของแต่ละสถานะ (คงไว้ตามเดิม) */
.news-status.verified {
  background-color: #d1fae5;
  color: #047857;
}
.news-status.fake {
  background-color: #fee2e2;
  color: #b91c1c;
}
.news-status.review {
  background-color: #fef9c3;
  color: #ca8a04;
}

/* Author (icon + text) */
.AuthorContainer {
  display: inline-flex;
  align-items: center;
  color: #4b5563;
  margin: 0;
}

.AuthorIcon {
  width: 20px;
  height: 20px;
  margin-left: 8px;
  margin-right: 8px;
  vertical-align: middle;
}

.DateContainer {
  display: inline-flex;
  align-items: center;
  color: #4b5563;
  margin: 0;
}

.DateIcon {
  width: 20px;
  height: 20px;
  margin-left: 8px;
  margin-right: 8px;
  vertical-align: middle;
}

/* กล่องรวม feedback ทั้งหมด */
.FeedbackSection {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  margin-top: 15px;
  max-width: fit-content;
  margin-left: 20px;
}

/* กล่องพื้นฐานของแต่ละอัน */
.VotingSystem {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  color: #ffffff; /* ตัวอักษรขาว */
  border-radius: 6px;
  padding: 6px 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  color: #000;
}

/* Like = สีเขียว */
.VotingSystem.Like {
  background-color: #f0fdf4; /* Tailwind green-600 */
}

/* Dislike = สีแดง */
.VotingSystem.Dislike {
  background-color: #fef2f2; /* Tailwind red-600 */
}

/* Comment = สีฟ้า (หรือจะเปลี่ยนเป็นสีแดงก็ได้) */
.VotingSystem.Comment {
  background-color: #f3f4f6; /* Tailwind blue-600 */
}

/* Icon ข้างใน */
.icon {
  width: 20px;
  height: 20px;
  vertical-align: middle;
}

.ImageOfNews {
  width: 100%;
  display: flex;
  justify-content: center;
  margin: 15px 0;
  border-radius: 6px;
}

.NewsMainImage {
  width: 820px;
  max-height: 270px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  border-radius: 6px;
}

.NewsDescription {
  font-size: 17px;
  max-width: 100%;
  width: 820px;
  margin-top: 20px;
  margin-left: 20px;
  margin-bottom: 20px;
  padding-bottom: 20px; /* เพิ่มช่องว่างก่อนเส้นเล็กน้อย */
  border-bottom: 2px solid #e5e7eb; /* เส้นสีเทาอ่อน (#e5e7eb = Tailwind gray-200) */
}

.NewsActions {
  display: flex;
  gap: 10px; /* ระยะห่างระหว่างปุ่ม */
  margin-left: 20px;
  width: 820px;
}

.NewsActions button {
  flex: 1; /* ให้ปุ่มเท่ากัน */
  padding: 10px 15px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: 0.3s ease;
}

/* ปุ่ม Vote */
.vote-btn {
  background-color: #3b82f6; /* น้ำเงิน */
  color: white;
}

.vote-btn:hover {
  background-color: #2563eb; /* น้ำเงินเข้ม */
}

/* ปุ่ม Comment */
.comment-btn {
  background-color: #e5e7eb; /* เทาอ่อน */
  color: #111827;
}

.comment-btn:hover {
  background-color: #d1d5db; /* เทาเข้มขึ้น */
}
</style>
