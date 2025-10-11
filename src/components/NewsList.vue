<template>

  <div class="news-container">
    <!-- Loading -->
    <p v-if="loading" class="text-center text-gray-500 mt-4">Loading news...</p>

    <!-- Error -->
    <p v-else-if="error" class="text-center text-red-500 mt-4">{{ error }}</p>

    <!-- Empty -->
    <p
      v-else-if="filteredNews.length === 0"
      class="text-center text-gray-500 mt-4"
    >
      No news found.
    </p>

    <!-- News Grid -->
    <div v-else class="news-grid">
      <div v-for="item in paginatedNews" :key="item.id" class="news-card">
        <div class="news-header">
          <h2 class="news-title">{{ item.title }}</h2>
          <span
            class="news-status"
            :class="{
              verified: item.stats === 'Verified',
              fake: item.stats === 'Fake News',
              review:
                item.stats === 'Under Review' || item.stats === 'Unverified',
            }"
          >
            {{ item.stats }}
          </span>
        </div>

        <p class="news-description">{{ item.description }}</p>
        <p class="news-meta">By {{ item.author }} • {{ item.date }}</p>

        <div class="news-footer">
          <div class="news-stats">
            <span class="stat like">
              <img :src="LikeIcon" alt="Like" class="icon" /> {{ item.upVotes }}
            </span>
            <span class="stat dislike">
              <img :src="DislikeIcon" alt="Dislike" class="icon" />
              {{ item.downVotes }}
            </span>
            <span class="stat comment">
              <img :src="CommentIcon" alt="Comment" class="icon" />
              {{ item.comments }}
            </span>
          </div>

          <!-- ปุ่มนี้คือจุดสำคัญ -->
          <button class="view-btn" @click="goToDetail(item.id)">
            View Details
          </button>
        </div>
      </div>
    </div>

    <!-- More Button -->
    <button
      v-if="visibleItems < filteredNews.length"
      @click="loadMore"
      class="load-more-btn"
    >
      More
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import { useRouter } from "vue-router";

import LikeIcon from "@/assets/Card/Like.png";
import DislikeIcon from "@/assets/Card/Dislike.png";
import CommentIcon from "@/assets/Card/Comment.png";

const router = useRouter();

function goToDetail(id: number) {
  router.push({ name: "NewsDetail", params: { id } });
}


// Props
interface Props {
  filterIndex: number | null;
  itemsPerPage: number;
  newsList: Array<{
    id: number;
    title: string;
    category: string;
    stats?: string;
    description?: string;
    author?: string;
    date?: string;
    upVotes?: number;
    downVotes?: number;
    comments?: number;
  }>;
}
const props = defineProps<Props>();

// Types
interface NewsItem {
  id: number;
  title: string;
  stats: string;
  description: string;
  author: string;
  date: string;
  upVotes: number;
  downVotes: number;
  comments: number;
}

// State
const newsList = ref<NewsItem[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const visibleItems = ref(props.itemsPerPage);

// Fetch data
onMounted(async () => {
  try {
    const res = await fetch("http://localhost:5175/api/news");
    if (!res.ok) throw new Error("Failed to fetch data");

    const data = await res.json();
    if (data.news && Array.isArray(data.news)) {
      newsList.value = data.news;
    } else {
      error.value = "ไม่มีข้อมูลข่าวจาก Google Sheet";
    }
  } catch (err: any) {
    console.error(err);
    error.value = "ไม่สามารถโหลดข้อมูลได้: " + err.message;
  } finally {
    loading.value = false;
  }
});

// Filtered news
const filteredNews = computed(() => {
  if (props.filterIndex === 0 || props.filterIndex === null)
    return newsList.value;
  const statusMap = ["Verified", "Fake News", "Unverified"];
  const status = statusMap[props.filterIndex - 1];
  return newsList.value.filter((n) => n.stats === status);
});

// Watch for prop changes
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

// Load more
function loadMore() {
  visibleItems.value = Math.min(
    visibleItems.value + props.itemsPerPage,
    filteredNews.value.length
  );
}

// Paginated news
const paginatedNews = computed(() =>
  filteredNews.value.slice(0, visibleItems.value)
);
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Outfit:wght@400;600&display=swap");

.news-container {
  width: 125%;
  position: relative;
  left: -12.5%;
  font-family: "Outfit", sans-serif;
  padding: 1rem 2rem;
  box-sizing: border-box;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  justify-content: center; /* ให้ grid อยู่กลาง container */
}

@media (max-width: 1024px) {
  .news-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 640px) {
  .news-grid {
    grid-template-columns: 1fr;
  }
}

.news-card {
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 1rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: all 0.3s ease;
  line-height: 1.3;
}

.news-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.news-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  text-align: left;
}

.news-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0;
  color: #111827;
  transition: color 0.3s ease;
}

.news-card:hover .news-title {
  color: #3b82f6;
}

.news-status {
  font-size: 1rem;
  padding: 0.25rem 0.5rem;
  border-radius: 8px;
  font-weight: 500;
  white-space: nowrap;
  text-align: right;
}

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

.news-description {
  color: #374151;
  font-size: 1rem;
  margin-bottom: 0.1rem;
  text-align: left;
}
.news-meta {
  color: #6b7280;
  font-size: 0.875rem;
  margin-bottom: 0.1rem;
  text-align: left;
}

.news-footer {
  margin-top: 1rem;
}
.news-stats {
  display: flex;
  gap: 1rem;
  margin-bottom: 0.75rem;
}
.stat {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}
.stat.like {
  color: #16a34a;
}
.stat.dislike {
  color: #dc2626;
}
.stat.comment {
  color: #4b5563;
}
.icon {
  width: 20px;
  height: 20px;
}

.view-btn {
  width: 100%;
  height: 40px;
  border: 1px solid #e5e7eb;
  background-color: #f9fafb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.view-btn:hover {
  background-color: #3c83f6;
  color: white;
}

.load-more-btn {
  width: 200px;
  margin: 1rem auto;
  display: block;
  padding: 8px 16px;
  border-radius: 8px;
  background-color: #3b82f6;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}
.load-more-btn:hover {
  background-color: #2563eb;
}
</style>
