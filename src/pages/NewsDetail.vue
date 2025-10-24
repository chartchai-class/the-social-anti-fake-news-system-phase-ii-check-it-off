<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

import AuthorIcon from "@/assets/Card/User.png";
import DateIcon from "@/assets/Card/Date.png";
import LikeIcon from "@/assets/Card/Like.png";
import DisLikeIcon from "@/assets/Card/Dislike.png";
import CommentIcon from "@/assets/Card/Comment.png";

import AsideMenu from "@/components/AsideMenu.vue";
import AddNewsModal from "@/components/AddNewsModal.vue";

interface NewsItem {
  id: number;
  title: string;
  category: string;
  author: string;
  date?: string;
  upVotes?: number;
  downVotes?: number;
  commentsCount?: number;
  fulldescription?: string;
  contentLines: string[];
  image?: string;
}

interface User {
  name: string;
  email: string;
  surname?: string;
  access?: string;
}

const route = useRoute();
const router = useRouter();
const newsId = Number(route.params.id);

const isLoading = ref(true);
const news = ref<NewsItem>({
  id: 0,
  title: "Loading...",
  category: "Loading...",
  author: "Loading...",
  date: "Loading...",
  upVotes: 0,
  downVotes: 0,
  commentsCount: 0,
  fulldescription: "Loading content...",
  contentLines: ["Loading content..."],
  image: "",
});

onMounted(async () => {
  isLoading.value = true;
  try {
    const res = await axios.get(`http://localhost:8080/api/news/${newsId}`);

    if (res.status === 200 && res.data) {
      const data = res.data;

      news.value = {
        id: data.id,
        title: data.title || "Untitled News",
        category: data.category || "Unverified",
        author: data.author || "Unknown",
        date: data.date || "Unknown",
        upVotes: data.upVotes || 0,
        downVotes: data.downVotes || 0,
        commentsCount: data.commentsCount || 0,
        fulldescription:
          data.fullDescription || data.description || "No content available.",
        contentLines: data.description
          ? data.description.split("\n")
          : ["No content available."],
        image: data.image || "",
      };
    } else {
      news.value.title = "News not found";
    }
  } catch (err) {
    console.error("❌ Error fetching news:", err);
    news.value.title = "Error loading news";
  } finally {
    isLoading.value = false;
  }
});

const images = import.meta.glob("@/assets/NewsImages/*.{png,jpg,jpeg}", {
  eager: true,
});

function getImageSrc(image?: string, id?: number): string {
  if (!image)
    return new URL("@/assets/NewsImages/default.png", import.meta.url).href;

  if (image.startsWith("http")) return image;

  if (id) {
    const localById = `${id}.png`;
    for (const path in images) {
      if (path.endsWith(localById)) {
        return (images as any)[path].default;
      }
    }
  }

  return new URL("@/assets/NewsImages/default.png", import.meta.url).href;
}

function goToVote() {
  window.scrollTo({ top: 0, behavior: "smooth" });
  router.push({ name: "Vote", params: { id: newsId } });
}
function goToComment() {
  window.scrollTo({ top: 0, behavior: "smooth" });
  router.push({ name: "Comment", params: { id: newsId } });
}

const user = ref<User | null>(null);
onMounted(() => {
  const savedUser = localStorage.getItem("user");
  if (savedUser) user.value = JSON.parse(savedUser);
});

const showAddNewsModal = ref(false);
function openAddNewsModal() {
  showAddNewsModal.value = true;
}
function closeAddNewsModal() {
  showAddNewsModal.value = false;
}
function handleSaveNews(newItem: NewsItem) {
  console.log("📰 New News Added from NewsDetail:", newItem);
  alert("News added successfully!");
  closeAddNewsModal();
}
</script>

<template>
  <div class="flex min-h-screen font-[Outfit] bg-white">
    <!-- Sidebar -->
    <AsideMenu :user="user" @openAddNews="openAddNewsModal" />

    <!-- Main Content -->
    <div class="flex-1 ml-[80px] px-8 py-6">
      <!-- Loading Spinner -->
      <div
        v-if="isLoading"
        class="fixed inset-0 bg-white/95 flex flex-col items-center justify-center z-[9999] text-center text-[18px] text-[#333]"
      >
        <div
          class="w-[60px] h-[60px] border-[6px] border-gray-200 border-t-[#003791] rounded-full animate-spin mb-[15px]"
        ></div>
        <p>Loading news...</p>
      </div>

      <!-- News Detail -->
      <div v-else>
        <!-- Back Button -->
        <router-link
          to="/home"
          class="flex items-center gap-[6px] bg-gray-100 text-black text-[16px] rounded-[8px] px-[20px] py-[10px] max-w-fit cursor-pointer transition-colors duration-300 hover:bg-gray-300"
        >
          <img
            src="@/assets/Card/Back.png"
            alt="Back Icon"
            class="w-[20px] h-[20px] mr-[5px]"
          />
          Back to News List
        </router-link>

        <!-- News Card -->
        <div
          class="text-black mt-[10px] bg-white border border-gray-300 rounded-[8px] shadow-[0_4px_10px_rgba(0,0,0,0.15)] min-h-[650px] text-left p-[20px]"
        >
          <h1
            class="text-4xl font-bold mb-2 ml-[20px] w-[820px] whitespace-normal break-words"
          >
            {{ news.title }}
          </h1>

          <!-- Info -->
          <div class="flex items-center gap-[12px] ml-[20px] mt-5">
            <span
              class="inline-flex items-center justify-center text-[17px] px-2 py-1 rounded-[6px] font-medium"
              :class="{
                'bg-green-100 text-green-700': news.category === 'Verified',
                'bg-red-100 text-red-700': news.category === 'Fake News',
                'bg-yellow-100 text-yellow-700':
                  news.category === 'Under Review' ||
                  news.category === 'Unverified',
              }"
            >
              {{ news.category }}
            </span>

            <span class="inline-flex items-center text-gray-600">
              <img
                :src="AuthorIcon"
                alt="Author"
                class="w-[20px] h-[20px] mx-2"
              />
              By {{ news.author }}
            </span>

            <span class="inline-flex items-center text-gray-600">
              <img :src="DateIcon" alt="Date" class="w-[20px] h-[20px] mx-2" />
              {{ news.date }}
            </span>
          </div>

          <!-- Votes -->
          <div class="flex gap-[12px] mt-[15px] ml-[20px]">
            <span
              class="inline-flex items-center gap-[6px] font-medium bg-green-50 rounded-[6px] px-3 py-1"
            >
              <img :src="LikeIcon" class="w-5 h-5" /> {{ news.upVotes }}
            </span>
            <span
              class="inline-flex items-center gap-[6px] font-medium bg-red-50 rounded-[6px] px-3 py-1"
            >
              <img :src="DisLikeIcon" class="w-5 h-5" /> {{ news.downVotes }}
            </span>
            <span
              class="inline-flex items-center gap-[6px] font-medium bg-gray-100 rounded-[6px] px-3 py-1"
            >
              <img :src="CommentIcon" class="w-5 h-5" />
              {{ news.commentsCount }} Comments
            </span>
          </div>

          <div class="w-full flex justify-left my-[15px] ml-[15px]">
            <img
              :src="getImageSrc(news.image, news.id)"
              alt="News Image"
              class="w-[820px] max-h-[270px] object-cover rounded-[6px] shadow opacity-0 transition-opacity duration-700"
              loading="lazy"
              @load="(e) => ((e.target as HTMLImageElement).style.opacity = '1')"
              @error="(e) => ((e.target as HTMLImageElement).src = '/src/assets/NewsImages/default.png')"
            />
          </div>

          <!-- Full Description -->
          <div
            class="text-[17px] w-[820px] mt-[20px] ml-[20px] mb-[20px] pb-[20px] border-b-2 border-gray-200"
          >
            {{ news.fulldescription }}
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-[10px] ml-[20px] w-[820px]">
            <button
              @click="goToVote"
              class="flex-1 px-4 py-2 rounded-[6px] text-[16px] bg-[#3B82F6] text-white hover:bg-[#2563EB]"
            >
              Vote on This News
            </button>
            <button
              @click="goToComment"
              class="flex-1 px-4 py-2 rounded-[6px] text-[16px] bg-gray-200 text-gray-900 hover:bg-gray-300"
            >
              View More Comments
            </button>
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
