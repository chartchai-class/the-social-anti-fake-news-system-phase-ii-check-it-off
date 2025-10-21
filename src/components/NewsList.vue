<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import { useRouter } from "vue-router";

import LikeIcon from "@/assets/Card/Like.png";
import DislikeIcon from "@/assets/Card/Dislike.png";
import CommentIcon from "@/assets/Card/Comment.png";

const emit = defineEmits(["loaded"]);

const router = useRouter();

function goToDetail(id: number) {
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
}

const newsList = ref<NewsItem[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const visibleItems = ref(props.itemsPerPage);

onMounted(async () => {
  try {
    const res = await fetch("http://localhost/checkitoff/api/news.php");
    if (!res.ok) throw new Error("Failed to fetch data");

    const data = await res.json();
    if (data.news && Array.isArray(data.news)) {
      newsList.value = data.news
        .filter((n: any) => n.date)
        .sort(
          (a: any, b: any) =>
            new Date(b.date).getTime() - new Date(a.date).getTime()
        );
    } else {
      error.value = "No news data found from Google Sheet";
    }
  } catch (err: any) {
    console.error(err);
    error.value = "Failed to load data: " + err.message;
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
  }
);

watch(
  () => props.filterIndex,
  () => {
    visibleItems.value = props.itemsPerPage;
  }
);

const paginatedNews = computed(() =>
  filteredNews.value.slice(0, visibleItems.value)
);
</script>

<template>
  <div
    class="relative ml-[50px] w-[calc(125%-50px)] -left-[12.5%] font-[Outfit] py-4 px-8 box-border"
  >
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

    <div
      v-else
      class="grid justify-center gap-6 grid-cols-3 md:grid-cols-3 sm:grid-cols-1"
    >
      <div
        v-for="item in paginatedNews"
        :key="item.id"
        class="bg-white border border-gray-200 rounded-xl shadow-sm p-4 flex flex-col justify-between transition-all duration-300 ease-in-out leading-[1.3] hover:-translate-y-1 hover:shadow-lg group"
      >
        <div class="flex justify-between items-start text-left">
          <h2
            class="text-xl font-semibold m-0 text-gray-900 transition-colors duration-300 group-hover:text-blue-500 news-title"
          >
            {{ item.title }}
          </h2>
          <span
            class="text-sm px-2 py-1 rounded-lg font-medium whitespace-nowrap text-right"
            :class="{
              'bg-green-100 text-green-800': item.category === 'Verified',
              'bg-red-100 text-red-700': item.category === 'Fake News',
              'bg-yellow-100 text-yellow-600':
                item.category === 'Under Review' || item.category === 'Unverified',
            }"
          >
            {{ item.category }}
          </span>
        </div>

        <p
          class="mt-4 mb-[0.1rem] text-gray-700 text-base text-left news-description"
        >
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
    </div>
  </div>
</template>

