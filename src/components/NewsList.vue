<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import { useRouter } from "vue-router";

import axios from "axios";

import LikeIcon from "@/assets/Card/Like.png";
import DislikeIcon from "@/assets/Card/Dislike.png";
import CommentIcon from "@/assets/Card/Comment.png";

const emit = defineEmits(["loaded"]);

const router = useRouter();

const showBackToTop = ref(false);

function goToDetail(id: number) {
  window.scrollTo({ top: 0, behavior: "smooth" });
  router.push({ name: "NewsDetail", params: { id } });
}

interface Props {
  filterIndex: number | null;
  itemsPerPage: number;
  newsList: Array<{
    id: number;
    title: string;
    category: string;
    description?: string;
    author?: string;
    date?: string;
    image?: string;
    upVotes?: number;
    downVotes?: number;
    commentsCount?: number;
    fullDescription?: string;
  }>;
}
const props = defineProps<Props>();

interface NewsItem {
  id: number;
  title: string;
  category: string;
  description: string;
  author: string;
  date: string;
  image: string;
  upVotes: number;
  downVotes: number;
  commentsCount: number;
  fullDescription?: string;
  visible?: boolean;
}

const newsList = ref<NewsItem[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const visibleItems = ref(props.itemsPerPage);

onMounted(async () => {
  loading.value = true;
  try {
    const res = await axios.get("http://localhost:8080/api/news");

    if (res.status !== 200) throw new Error("Failed to fetch data");
    const data = res.data;

    if (Array.isArray(data)) {
      newsList.value = data
        // ✅ กรองเฉพาะข่าวที่ visible = true ก่อนจัดเรียง
        .filter((n: any) => n.visible === true && n.date)
        .sort(
          (a: any, b: any) =>
            new Date(b.date).getTime() - new Date(a.date).getTime()
        );
    } else {
      error.value = "No news data found from backend";
    }
  } catch (err: any) {
    console.error("Error fetching news:", err);
    error.value = "Failed to load data: " + (err.message || "Unknown error");
  } finally {
    loading.value = false;
    emit("loaded");
  }
});

const filteredNews = computed(() => {
  if (props.filterIndex === 0 || props.filterIndex === null)
    return newsList.value;
  const statusMap = ["Verified", "Fake News", "Unverified"];
  const status = statusMap[props.filterIndex - 1];
  return newsList.value.filter((n) => n.category === status);
});

watch(
  () => props.itemsPerPage,
  (newVal) => {
    visibleItems.value = newVal;
    currentPage.value = 1;
  }
);

watch(
  () => props.filterIndex,
  () => {
    visibleItems.value = props.itemsPerPage;
  }
);

const currentPage = ref(1);
const direction = ref<"left" | "right">("left");
const previousPage = ref(1);

const totalPages = computed(() =>
  Math.ceil(searchedNews.value.length / props.itemsPerPage)
);

const paginatedNews = computed(() => {
  const start = (currentPage.value - 1) * props.itemsPerPage;
  const end = start + props.itemsPerPage;
  return searchedNews.value.slice(start, end);
});

watch(
  () => props.filterIndex,
  () => {
    currentPage.value = 1;
  }
);

function goToPage(page: number) {
  if (page >= 1 && page <= totalPages.value) {
    direction.value = page > currentPage.value ? "left" : "right";
    previousPage.value = currentPage.value;
    currentPage.value = page;
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    direction.value = "left";
    previousPage.value = currentPage.value;
    currentPage.value++;
  }
}

function prevPage() {
  if (currentPage.value > 1) {
    direction.value = "right";
    previousPage.value = currentPage.value;
    currentPage.value--;
  }
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: "smooth" });
}

window.addEventListener("scroll", () => {
  showBackToTop.value = window.scrollY > 500;
});

const searchQuery = ref("");

const searchedNews = computed(() => {
  const query = searchQuery.value.toLowerCase().trim();
  let list = filteredNews.value;

  if (query) {
  list = list.filter(
    (n) =>
      n.title.toLowerCase().includes(query) ||
      (n.author && n.author.toLowerCase().includes(query)) ||
      (n.category && n.category.toLowerCase().includes(query)) ||
      (n.description && n.description.toLowerCase().includes(query))
  );
}
  return list;
});
</script>

<template>
  <div
    class="relative ml-[50px] w-[calc(125%-50px)] -left-[12.5%] font-[Outfit] py-4 px-8 box-border"
  >
    <!-- Search Bar -->
    <div class="flex justify-start mb-6">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Search news by title, author, or category..."
        class="w-[330px] h-[40px] px-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-gray-700"
      />
    </div>

    <div
      v-if="loading"
      class="grid justify-center gap-6 grid-cols-3 md:grid-cols-3 sm:grid-cols-1 animate-pulse"
    >
      <div
        v-for="n in 6"
        :key="n"
        class="bg-white border border-gray-200 rounded-xl shadow-sm p-4 flex flex-col justify-between leading-[1.3]"
      >
        <div class="flex justify-between items-start">
          <div class="h-5 w-[70%] bg-gray-200 rounded"></div>
          <div class="h-5 w-[20%] bg-gray-200 rounded"></div>
        </div>

        <div class="mt-4 space-y-2">
          <div class="h-4 w-full bg-gray-200 rounded"></div>
          <div class="h-4 w-[90%] bg-gray-200 rounded"></div>
          <div class="h-4 w-[80%] bg-gray-200 rounded"></div>
        </div>

        <div class="mt-4 flex gap-4">
          <div class="h-4 w-12 bg-gray-200 rounded"></div>
          <div class="h-4 w-12 bg-gray-200 rounded"></div>
          <div class="h-4 w-20 bg-gray-200 rounded"></div>
        </div>

        <div class="mt-4 h-10 bg-gray-200 rounded"></div>
      </div>
    </div>

    <p v-else-if="error" class="text-center text-red-500 mt-4">{{ error }}</p>

    <p
      v-else-if="filteredNews.length === 0"
      class="text-center text-gray-500 mt-4"
    >
      No news found.
    </p>

    <Transition
      :name="direction === 'left' ? 'slide-left' : 'slide-right'"
      mode="out-in"
    >
      <div
        :key="currentPage"
        class="grid gap-6 grid-cols-3 md:grid-cols-3 sm:grid-cols-1 items-stretch"
      >
        <div
          v-for="item in paginatedNews"
          :key="item.id"
          class="h-[300px] bg-white border border-gray-200 rounded-xl shadow-sm p-4 flex flex-col justify-between transition-all duration-300 ease-in-out leading-[1.3] hover:-translate-y-1 hover:shadow-lg group"
        >
          <div class="flex justify-between items-start text-left">
            <h2
              class="text-xl font-semibold m-0 text-gray-900 transition-colors duration-300 group-hover:text-blue-500 line-clamp-2 h-[56px] leading-snug"
            >
              {{ item.title }}
            </h2>
            <span
              class="text-sm px-2 py-1 rounded-lg font-medium whitespace-nowrap text-right"
              :class="{
                'bg-green-100 text-green-800': item.category === 'Verified',
                'bg-red-100 text-red-700': item.category === 'Fake News',
                'bg-yellow-100 text-yellow-600':
                  item.category === 'Under Review' ||
                  item.category === 'Unverified',
              }"
            >
              {{ item.category }}
            </span>
          </div>

          <p
            class="mt-3 text-gray-700 text-base text-left line-clamp-3 h-[72px]"
          >
            <span class="font-bold text-[#003791]">#{{ item.id }}</span> •
            {{ item.description }}
          </p>

          <p class="mt-4 mb-[0.1rem] text-gray-500 text-sm text-left news-meta">
            By {{ item.author }} • {{ item.date }}
          </p>

          <div class="mt-4 news-footer">
            <div class="flex gap-4 mb-3 news-category">
              <span class="flex items-center gap-1 text-green-500">
                <img :src="LikeIcon" alt="Like" class="w-5 h-5" />
                {{ item.upVotes }}
              </span>

              <span class="flex items-center gap-1 text-red-600">
                <img :src="DislikeIcon" alt="Dislike" class="w-5 h-5" />
                {{ item.downVotes }}
              </span>

              <span class="flex items-center gap-1 text-gray-600">
                <img :src="CommentIcon" alt="Comment" class="w-5 h-5" />
                {{ item.commentsCount }}
              </span>
            </div>

            <button
              class="w-full h-10 border border-gray-200 bg-gray-100 rounded-lg cursor-pointer transition-colors duration-300 ease-in-out hover:!bg-blue-600 hover:!text-white"
              @click="goToDetail(item.id)"
            >
              View Details
            </button>
          </div>
        </div>
        <div
          v-for="n in props.itemsPerPage - paginatedNews.length"
          :key="'placeholder-' + n"
          class="h-[300px] bg-transparent border border-transparent rounded-xl p-4"
        ></div>
      </div>
    </Transition>

    <!-- Pagination -->
    <div
      v-if="totalPages > 1"
      class="flex justify-center items-center mt-10 space-x-2 select-none font-[Outfit]"
    >
      <!-- Prev -->
      <button
        class="flex items-center justify-center px-4 py-2 rounded-xl border border-gray-200 bg-white shadow-sm text-sm text-gray-700 font-medium transition-all duration-300 ease-in-out hover:bg-gray-100 hover:-translate-y-[1px] active:scale-95 disabled:opacity-40 disabled:cursor-not-allowed"
        :disabled="currentPage === 1"
        @click="prevPage"
      >
        ← Prev
      </button>

      <!-- Page Numbers -->
      <button
        v-for="page in totalPages"
        :key="page"
        @click="goToPage(page)"
        class="w-9 h-9 rounded-lg font-semibold text-sm transition-all duration-300 ease-in-out flex items-center justify-center border border-gray-200"
        :class="[
          page === currentPage
            ? 'bg-[#003791] text-white shadow-md scale-110'
            : 'bg-gray-100 text-gray-700 hover:bg-[#E5E8F0] hover:scale-105',
        ]"
      >
        {{ page }}
      </button>

      <!-- Next -->
      <button
        class="flex items-center justify-center px-4 py-2 rounded-xl border border-gray-200 bg-white shadow-sm text-sm text-gray-700 font-medium transition-all duration-300 ease-in-out hover:bg-gray-100 hover:-translate-y-[1px] active:scale-95 disabled:opacity-40 disabled:cursor-not-allowed"
        :disabled="currentPage === totalPages"
        @click="nextPage"
      >
        Next →
      </button>
    </div>

    <!-- Back to Top Button -->
    <button
      v-if="showBackToTop"
      @click="scrollToTop"
      class="fixed bottom-6 right-6 w-12 h-12 bg-blue-700 rounded-full flex items-center justify-center shadow-lg cursor-pointer z-50 hover:bg-blue-800 transition-colors duration-300"
    >
      <img
        src="@/assets/Detail/back-to-top.png"
        alt="Back to Top"
        class="w-6 h-6"
      />
    </button>
  </div>
</template>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.6s ease;
  position: relative;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(40px);
}
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-40px);
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.5s ease;
  position: relative;
}
.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-40px);
}
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(40px);
}
</style>
