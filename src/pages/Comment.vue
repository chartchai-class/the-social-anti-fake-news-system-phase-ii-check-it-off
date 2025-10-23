<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

import LikeIcon from "@/assets/Card/Like.png";
import DislikeIcon from "@/assets/Card/Dislike.png";
import CommentIcon from "@/assets/Card/Comment.png";

import AsideMenu from "@/components/AsideMenu.vue";
import AddNewsModal from "@/components/AddNewsModal.vue";

interface NewsItem {
  id: number;
  title: string;
  upVotes: number;
  downVotes: number;
  commentsCount: number;
}

interface CommentItem {
  id: number;
  name: string;
  vote: string;
  comment: string;
  imageUrl?: string;
}

interface User {
  name: string;
  email: string;
  surname?: string;
  access?: string;
}

const route = useRoute();
const id = Number(route.params.id);

const isLoading = ref(true);
const isLoadingComments = ref(true);

const news = ref<NewsItem>({
  id,
  title: "Loading...",
  upVotes: 0,
  downVotes: 0,
  commentsCount: 0,
});

const comments = ref<CommentItem[]>([]);
const user = ref<User | null>(null);

async function fetchNews() {
  try {
    const res = await axios.get(`http://localhost:8080/api/news/${id}`);
    if (res.status === 200 && res.data) {
      const data = res.data;
      news.value = {
        id: data.id,
        title: data.title,
        upVotes: data.upVotes || 0,
        downVotes: data.downVotes || 0,
        commentsCount: data.commentsCount || 0,
      };
    }
  } catch (err) {
    console.error("Error fetching news:", err);
  } finally {
    isLoading.value = false;
  }
}

async function fetchComments() {
  try {
    const res = await axios.get(
      `http://localhost:8080/api/votes/news/${id}`
    );
    if (res.status === 200 && Array.isArray(res.data)) {
      comments.value = res.data.map((c: any) => ({
        id: c.id,
        name: c.name,
        vote: c.vote,
        comment: c.comment,
        imageUrl: c.imageUrl || null,
      }));
    }
  } catch (err) {
    console.error("Error fetching comments:", err);
  } finally {
    isLoadingComments.value = false;
  }
}

onMounted(async () => {
  await Promise.all([fetchNews(), fetchComments()]);
});

onMounted(() => {
  const savedUser = localStorage.getItem("user");
  if (savedUser) {
    user.value = JSON.parse(savedUser);
  }
});

const showAddNewsModal = ref(false);

function openAddNewsModal() {
  showAddNewsModal.value = true;
}

function closeAddNewsModal() {
  showAddNewsModal.value = false;
}

function handleSaveNews(newItem: any) {
  console.log("New News Added (from Comment Page):", newItem);
  alert(" News added successfully!");
  closeAddNewsModal();
}
</script>

<template>
  <div class="flex min-h-screen font-[Outfit] bg-white">
    <!-- ASIDE MENU -->
    <AsideMenu :user="user" @openAddNews="openAddNewsModal" />

    <!-- MAIN CONTENT -->
    <div class="flex-1 ml-[80px] px-8 py-6">
      <!-- Back -->
      <router-link
        :to="`/news/${id}`"
        class="flex items-center gap-[6px] bg-gray-100 text-black text-[16px] rounded-[8px] px-[20px] py-[10px] max-w-fit cursor-pointer transition-colors duration-300 hover:bg-gray-300"
      >
        <img
          src="@/assets/Card/Back.png"
          alt="Back Icon"
          class="w-[20px] h-[20px] mr-[5px]"
        />
        Back to News Detail
      </router-link>

      <!-- News Stats -->
      <div
        class="mt-4 bg-white rounded-xl border border-gray-200 shadow-md px-6 py-5 flex justify-between items-center w-full h-[130px]"
      >
        <div class="flex flex-col gap-2">
          <h2 class="font-bold text-[24px] text-[#111827]">
            {{ news.title }}
          </h2>
          <div class="flex gap-6 text-[18px]">
            <div class="flex items-center gap-2">
              <img :src="LikeIcon" alt="Like" class="w-7 h-7" />
              <span>{{ news.upVotes }} Up Votes</span>
            </div>
            <div class="flex items-center gap-2">
              <img :src="DislikeIcon" alt="Dislike" class="w-7 h-7" />
              <span>{{ news.downVotes }} Down Votes</span>
            </div>
            <div class="flex items-center gap-2">
              <img :src="CommentIcon" alt="Comment" class="w-7 h-7" />
              <span>{{ news.commentsCount }} Total Comments</span>
            </div>
          </div>
        </div>
        <router-link
          :to="{ name: 'Vote', params: { id } }"
          class="bg-blue-500 text-white rounded-md px-5 py-2.5 text-[15px] font-medium cursor-pointer transition-all duration-300 hover:bg-blue-600"
        >
          Add Your Vote
        </router-link>
      </div>

      <!-- Comments Section -->
      <div class="w-[820px] my-10 font-[Outfit]">
        <h1 class="text-[25px] font-bold mb-6 text-[#111827] text-left">
          Comments & Votes
        </h1>

        <div v-if="isLoadingComments" class="text-gray-500">
          Loading comments...
        </div>

        <div
          v-else-if="comments.length === 0"
          class="text-center text-[#666] italic mt-3"
        >
          No comments yet.
        </div>

        <div v-else>
          <div
            v-for="c in comments"
            :key="c.id"
            class="flex flex-col text-left gap-3 bg-white rounded-xl py-5 px-5 border-b border-gray-200 -ml-5"
          >
            <div class="flex items-center gap-3">
              <span class="font-semibold text-[18px]">{{ c.name }}</span>

              <span
                v-if="c.vote === 'upvote'"
                class="text-green-600 bg-green-100 px-2 py-[2px] rounded-md text-sm font-medium"
              >
                Real
              </span>
              <span
                v-else-if="c.vote === 'downvote'"
                class="text-red-600 bg-red-100 px-2 py-[2px] rounded-md text-sm font-medium"
              >
                Fake
              </span>
            </div>

            <p class="text-gray-700 text-[16px] leading-relaxed">
              {{ c.comment }}
            </p>

            <img
              v-if="c.imageUrl"
              :src="c.imageUrl"
              alt="Comment Image"
              class="w-full max-w-[400px] rounded-lg object-cover mt-2"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Add News Modal -->
    <AddNewsModal
      :show="showAddNewsModal"
      :user="user"
      @close="closeAddNewsModal"
      @save="handleSaveNews"
    />
  </div>
</template>
