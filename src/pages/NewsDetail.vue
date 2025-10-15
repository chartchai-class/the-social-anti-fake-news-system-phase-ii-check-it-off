<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

import AuthorIcon from "@/assets/Card/User.png";
import DateIcon from "@/assets/Card/Date.png";
import LikeIcon from "@/assets/Card/Like.png";
import DisLikeIcon from "@/assets/Card/Dislike.png";
import CommentIcon from "@/assets/Card/Comment.png";

import AsideMenu from "@/components/AsideMenu.vue"; // ✅ ใช้ component จริง

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

// ✅ ดึงข่าวจาก API
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

// ✅ โหลดรูปจากโฟลเดอร์
const images = import.meta.glob("@/assets/NewsImages/*.png", { eager: true });
function getImageById(id: number) {
  const path = `/src/assets/NewsImages/${id}.png`;
  return (images[path] as any)?.default || "";
}

// ✅ ปุ่มนำทาง
function goToVote() {
  router.push({ name: "Vote", params: { id: newsId } });
}
function goToComment() {
  router.push({ name: "Comment", params: { id: newsId } });
}

// ✅ จัดการผู้ใช้
interface User {
  name: string;
  email: string;
  surname?: string;
  access?: string;
}
const user = ref<User | null>(null);

onMounted(() => {
  const savedUser = localStorage.getItem("user");
  if (savedUser) {
    user.value = JSON.parse(savedUser);
  }
});

// ✅ Modal เพิ่มข่าว
const showAddNewsModal = ref(false);
const newNews = ref({
  title: "",
  author: "",
  date: "",
  image: "",
  description: "",
});

function openAddNewsModal() {
  showAddNewsModal.value = true;
  newNews.value.author = user.value
    ? `${user.value.name} ${user.value.surname || ""}`.trim()
    : "";
}

function closeAddNewsModal() {
  showAddNewsModal.value = false;
  Object.assign(newNews.value, {
    title: "",
    author: "",
    date: "",
    image: "",
    description: "",
  });
}

function saveNews() {
  if (!newNews.value.title || !newNews.value.author) {
    alert("Please fill in at least Title and Author.");
    return;
  }

  console.log("📰 New News Added:", newNews.value);
  alert("News added successfully!");
  closeAddNewsModal();
}
</script>

<template>
  <div class="flex min-h-screen font-[Outfit] bg-white">
    <!-- ✅ ใช้ AsideMenu.vue -->
    <AsideMenu :user="user" @openAddNews="openAddNewsModal" />

    <!-- ✅ Main Content -->
    <div class="flex-1 ml-[80px] px-8 py-6">
      <!-- ✅ Loading Spinner -->
      <div
        v-if="isLoading"
        class="fixed inset-0 bg-white/95 flex flex-col items-center justify-center z-[9999] text-center text-[18px] text-[#333]"
      >
        <div
          class="w-[60px] h-[60px] border-[6px] border-gray-200 border-t-[#003791] rounded-full animate-spin mb-[15px]"
        ></div>
        <p>Loading news...</p>
      </div>

      <!-- ✅ Main Content -->
      <div v-else>
        <router-link
          to="/"
          class="flex items-center gap-[6px] bg-gray-100 text-black text-[16px] rounded-[8px] px-[20px] py-[10px] max-w-fit cursor-pointer transition-colors duration-300 hover:bg-gray-300"
        >
          <img
            src="@/assets/Card/Back.png"
            alt="Back Icon"
            class="w-[20px] h-[20px] mr-[5px]"
          />
          Back to News List
        </router-link>

        <div
          class="text-black mt-[10px] bg-white border border-gray-300 rounded-[8px] shadow-[0_4px_10px_rgba(0,0,0,0.15)] min-h-[650px] text-left p-[20px]"
        >
          <h1 class="text-4xl font-bold mb-2 ml-[20px]">{{ news.title }}</h1>

          <!-- Info -->
          <div class="flex items-center gap-[12px] ml-[20px] mt-5">
            <span
              class="inline-flex items-center justify-center text-[17px] px-2 py-1 rounded-[6px] font-medium"
              :class="{
                'bg-green-100 text-green-700': news.stats === 'Verified',
                'bg-red-100 text-red-700': news.stats === 'Fake News',
                'bg-yellow-100 text-yellow-700':
                  news.stats === 'Under Review' || news.stats === 'Unverified',
              }"
            >
              {{ news.stats }}
            </span>

            <span class="inline-flex items-center text-gray-600">
              <img :src="AuthorIcon" alt="Author" class="w-[20px] h-[20px] mx-2" />
              By {{ news.author }}
            </span>

            <span class="inline-flex items-center text-gray-600">
              <img :src="DateIcon" alt="Date" class="w-[20px] h-[20px] mx-2" />
              {{ news.date }}
            </span>
          </div>

          <!-- Votes -->
          <div class="flex gap-[12px] mt-[15px] ml-[20px]">
            <span class="inline-flex items-center gap-[6px] font-medium bg-green-50 rounded-[6px] px-3 py-1">
              <img :src="LikeIcon" class="w-5 h-5" /> {{ news.upVotes }}
            </span>
            <span class="inline-flex items-center gap-[6px] font-medium bg-red-50 rounded-[6px] px-3 py-1">
              <img :src="DisLikeIcon" class="w-5 h-5" /> {{ news.downVotes }}
            </span>
            <span class="inline-flex items-center gap-[6px] font-medium bg-gray-100 rounded-[6px] px-3 py-1">
              <img :src="CommentIcon" class="w-5 h-5" /> {{ news.comments }} Comments
            </span>
          </div>

          <!-- Image -->
          <div class="w-full flex justify-center my-[15px]" v-if="getImageById(news.id)">
            <img
              :src="getImageById(news.id)"
              alt="News Image"
              class="w-[820px] max-h-[270px] object-cover rounded-[6px] shadow"
            />
          </div>

          <!-- Description -->
          <div class="text-[17px] w-[820px] mt-[20px] ml-[20px] mb-[20px] pb-[20px] border-b-2 border-gray-200">
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
              View More Comment
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ✅ Modal -->
    <div
      v-if="showAddNewsModal"
      class="fixed inset-0 bg-black/40 flex items-center justify-center z-[9999]"
    >
      <div
        class="bg-white w-[600px] h-[800px] rounded-2xl shadow-xl p-6 font-[Outfit] animate-fade-in overflow-y-auto"
      >
        <h2 class="text-3xl font-bold text-gray-800 mb-4 text-center">
          Add News Article
        </h2>

        <div class="space-y-3">
          <div>
            <label class="block text-xl text-gray-700 font-semibold mb-1">Title</label>
            <input
              v-model="newNews.title"
              type="text"
              class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              placeholder="Enter news title"
            />
          </div>

          <div>
            <label class="block text-xl text-gray-700 font-semibold mb-1">Author</label>
            <input
              v-model="newNews.author"
              type="text"
              readonly
              class="w-full border border-gray-200 bg-gray-100 text-gray-600 rounded-md px-3 py-2 focus:outline-none cursor-not-allowed select-none"
              placeholder="Author name"
            />
          </div>

          <div class="flex flex-col items-center">
            <label class="block text-gray-700 font-semibold mb-1 text-center text-xl">Date</label>
            <input
              v-model="newNews.date"
              type="date"
              class="w-[250px] border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-center"
            />
          </div>

          <div>
            <label class="block text-gray-700 font-semibold mb-1 text-xl">Image URL</label>
            <input
              v-model="newNews.image"
              type="text"
              class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              placeholder="Paste image link"
            />
          </div>

          <div>
            <label class="block text-gray-700 font-semibold mb-1 text-xl">Full Description</label>
            <textarea
              v-model="newNews.description"
              rows="4"
              class="w-full h-[250px] border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 resize-none"
              placeholder="Enter detailed description..."
            ></textarea>
          </div>
        </div>

        <div class="flex justify-end gap-4 mt-6">
          <button
            @click="closeAddNewsModal"
            class="px-5 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-md transition"
          >
            Cancel
          </button>
          <button
            @click="saveNews"
            class="px-5 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-md transition"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
