<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
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

const firstLetter = computed(() =>
  user.value?.name ? user.value.name.charAt(0).toUpperCase() : "?"
);

const accessColor = computed(() => {
  const access = user.value?.access?.toLowerCase() || "";
  console.log("access =", access);
  if (access.includes("admin") || access.includes("full"))
    return "bg-red-500 border-none"; // Admin
  if (access.includes("vote")) return "bg-yellow-400 border-none"; // Vote only
  return "bg-gray-300";
});

function handleLogout() {
  localStorage.removeItem("user");
  alert("You have been logged out.");
  window.location.href = "/login";
}
</script>

<template>
  <div class="w-[900px] mx-auto justify-center pb-[40px]">
    <div
      v-if="isLoading"
      class="fixed inset-0 bg-white/95 flex flex-col items-center justify-center z-[9999] text-center text-[18px] text-[#333] font-[Outfit]"
    >
      <div
        class="w-[60px] h-[60px] border-[6px] border-gray-200 border-t-[#003791] rounded-full animate-spin mb-[15px]"
      ></div>
      <p>Loading news...</p>
    </div>

    <div v-else>
      <aside
        class="fixed top-0 left-0 w-[60px] h-full z-20 flex flex-col items-center justify-between py-6 border-r border-gray-200 bg-white"
      >
        <div class="flex flex-col items-center space-y-4">
          <!-- Avatar -->
          <div
            class="w-10 h-10 rounded-full bg-blue-500 text-white flex items-center justify-center text-xl font-bold"
            :title="user?.name"
          >
            {{ firstLetter }}
          </div>

          <!-- Access Circle -->
          <div
            :class="[
              'w-10 h-10 rounded-full border border-gray-300',
              accessColor,
            ]"
          ></div>

          <!-- Access Label -->
          <p
            class="text-[11px] font-semibold text-gray-600 text-center w-[60px] leading-tight break-words -mt-3"
          >
            {{ user?.access || "Unknown" }}
          </p>
        </div>

        <!-- 🔸 ส่วนล่าง: Logout -->
        <button
          @click="handleLogout"
          class="flex flex-col items-center space-y-1 text-gray-500 hover:text-red-500 transition-all duration-300"
        >
          <img
            src="@/assets/Aside/logout-icon.png"
            alt="Logout"
            class="w-7 h-7 opacity-80 hover:opacity-100"
          />
          <span class="text-[11px] font-semibold">Logout</span>
        </button>
      </aside>

      <router-link
        to="/"
        class="flex items-center gap-[6px] bg-gray-100 text-black text-[16px] rounded-[8px] px-[20px] py-[10px] max-w-fit cursor-pointer transition-colors duration-300 ease-in-out hover:bg-gray-300 hover:text-black no-underline"
      >
        <img
          src="@/assets/Card/Back.png"
          alt="Back Icon"
          class="w-[20px] h-[20px] mr-[5px] align-middle"
        />
        Back to News List
      </router-link>

      <div
        class="text-black mt-[10px] bg-white border border-gray-300 rounded-[8px] shadow-[0_4px_10px_rgba(0,0,0,0.15)] min-h-[650px] text-left p-[20px]"
      >
        <h1 class="text-4xl font-bold mb-2 ml-[20px]">{{ news.title }}</h1>
        <div
          class="flex items-center justify-start gap-[12px] w-full p-0 rounded-[6px] max-w-fit ml-[20px] mt-5"
        >
          <span
            class="inline-flex items-center justify-center text-[17px] px-2 py-1 rounded-[6px] font-medium m-0"
            :class="{
              'bg-green-100 text-green-700': news.stats === 'Verified',
              'bg-red-100 text-red-700': news.stats === 'Fake News',
              'bg-yellow-100 text-yellow-700':
                news.stats === 'Under Review' || news.stats === 'Unverified',
            }"
          >
            {{ news.stats }}
          </span>

          <span class="inline-flex items-center text-gray-600 m-0">
            <img
              :src="AuthorIcon"
              alt="Author Icon"
              class="w-[20px] h-[20px] ml-[8px] mr-[8px] align-middle"
            />
            By {{ news.author || "Unknown" }}
          </span>

          <span class="inline-flex items-center text-gray-600 m-0">
            <img
              :src="DateIcon"
              alt="Date Icon"
              class="w-[20px] h-[20px] mx-[8px] align-middle"
            />
            By {{ news.date || "Unknown" }}
          </span>
        </div>

        <div
          class="flex items-center justify-start gap-[12px] mt-[15px] max-w-fit ml-[20px]"
        >
          <span
            class="inline-flex items-center gap-[6px] font-medium text-black rounded-[6px] px-[12px] py-[6px] bg-green-50 transition-transform duration-200 ease-in-out"
          >
            <img :src="LikeIcon" alt="Like" class="w-5 h-5" />
            {{ news.upVotes }}
          </span>

          <span
            class="inline-flex items-center gap-[6px] font-medium text-black rounded-[6px] px-[12px] py-[6px] bg-red-50 transition-transform duration-200 ease-in-out"
          >
            <img :src="DisLikeIcon" alt="DisLike" class="w-5 h-5" />
            {{ news.downVotes }}
          </span>

          <span
            class="inline-flex items-center gap-[6px] font-medium text-black rounded-[6px] px-[12px] py-[6px] bg-gray-100 transition-transform duration-200 ease-in-out"
          >
            <img :src="CommentIcon" alt="Comment" class="w-5 h-5" />
            {{ news.comments }} Comments
          </span>
        </div>

        <div
          class="w-full flex justify-center my-[15px] rounded-[6px] mt-7"
          v-if="getImageById(news.id)"
        >
          <img
            :src="getImageById(news.id)"
            alt="News Image"
            class="w-[820px] max-h-[270px] object-cover rounded-[6px] shadow-[0_4px_12px_rgba(0,0,0,0.2)]"
          />
        </div>

        <div
          class="text-[17px] max-w-full w-[820px] mt-[20px] ml-[20px] mb-[20px] pb-[20px] border-b-2 border-gray-200"
        >
          {{ news.fulldescription }}
        </div>

        <div class="flex gap-[10px] ml-[20px] w-[820px]">
          <!-- ปุ่ม Vote -->
          <button
            @click="goToVote"
            class="flex-1 px-[15px] py-[10px] rounded-[6px] text-[16px] bg-blue-500 text-white font-medium cursor-pointer transition-colors duration-300 ease-in-out hover:bg-blue-600"
          >
            Vote on This News
          </button>

          <!-- ปุ่ม Comment -->
          <button
            @click="goToComment"
            class="flex-1 px-[15px] py-[10px] rounded-[6px] text-[16px] bg-gray-200 text-gray-900 font-medium cursor-pointer transition-colors duration-300 ease-in-out hover:bg-gray-300"
          >
            View More Comment
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
