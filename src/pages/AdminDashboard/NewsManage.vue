<script setup lang="ts">
import { ref, onMounted } from "vue";

interface NewsItem {
  id: number;
  title: string;
  author: string;
  date: string;
  stats: string;
  description?: string;
  fullDescription?: string;
  upVotes?: number;
  downVotes?: number;
  comments?: number;
}

const isLoading = ref(true);
const newsList = ref<NewsItem[]>([]);
const selectedNews = ref<NewsItem | null>(null);
const showDetailModal = ref(false);

// state สำหรับเรียง
const sortOrderDate = ref<"asc" | "desc">("desc");
const sortOrderStats = ref<"asc" | "desc">("asc");

// toggle เรียงตาม Status
function sortByStats() {
  sortOrderStats.value = sortOrderStats.value === "asc" ? "desc" : "asc";

  newsList.value.sort((a, b) => {
    if (a.stats < b.stats) return sortOrderStats.value === "asc" ? -1 : 1;
    if (a.stats > b.stats) return sortOrderStats.value === "asc" ? 1 : -1;
    return 0;
  });
}

// toggle เรียงตาม Date
function sortByDate() {
  sortOrderDate.value = sortOrderDate.value === "asc" ? "desc" : "asc";

  newsList.value.sort((a, b) => {
    const dateA = new Date(a.date).getTime();
    const dateB = new Date(b.date).getTime();
    return sortOrderDate.value === "asc" ? dateA - dateB : dateB - dateA;
  });
}

// Reset Sort
function resetSort() {
  newsList.value.sort(
    (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
  );
  sortOrderDate.value = "desc";
  sortOrderStats.value = "asc";
}

async function fetchNews() {
  try {
    const res = await fetch("http://localhost:5175/api/news");
    if (!res.ok) throw new Error("Failed to fetch news");

    const data = await res.json();
    newsList.value = data.news
      .filter((n: any) => n.date)
      .sort(
        (a: any, b: any) =>
          new Date(b.date).getTime() - new Date(a.date).getTime()
      );
  } catch (err) {
    console.error(" Error fetching news:", err);
  } finally {
    isLoading.value = false;
  }
}

function openNewsDetail(news: NewsItem) {
  selectedNews.value = news;
  showDetailModal.value = true;
}

function closeNewsDetail() {
  showDetailModal.value = false;
  selectedNews.value = null;
}

onMounted(fetchNews);
</script>

<template>
  <div class="flex min-h-screen font-[Outfit] bg-white">
    <!-- Main Content -->
    <div class="flex-1 ml-[80px] px-10 py-8 w-[1500px]">
      <h1 class="text-3xl font-bold text-[#111827] mb-6">Manage News</h1>

      <!-- ปุ่ม Sorting -->
      <div class="mb-4 flex items-center gap-3 justify-end">
        <router-link
          to="/"
          class="flex items-center gap-2 px-4 py-2 bg-gray-100 text-black rounded-md hover:bg-gray-300 transition mr-174"
        >
          <img
            src="@/assets/Card/Back.png"
            alt="Back Icon"
            class="w-[20px] h-[20px] mr-[5px]"
          />
          Back to News List
        </router-link>

        <!-- Sort by Status -->
        <button
          @click="sortByStats"
          class="flex items-center gap-2 px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition"
        >
          Sort by Status
          <svg
            v-if="sortOrderStats === 'asc'"
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M5 15l7-7 7 7"
            />
          </svg>
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M19 9l-7 7-7-7"
            />
          </svg>
        </button>

        <!-- Sort by Date -->
        <button
          @click="sortByDate"
          class="flex items-center gap-2 px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition"
        >
          Sort by Date
          <svg
            v-if="sortOrderDate === 'asc'"
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M5 15l7-7 7 7"
            />
          </svg>
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M19 9l-7 7-7-7"
            />
          </svg>
        </button>

        <!-- Reset Sort -->
        <button
          @click="resetSort"
          class="flex items-center gap-2 px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition ml-17"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4 text-gray-700"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M4 4v6h6M20 20v-6h-6M4 10a8 8 0 0116 0c0 2.21-.9 4.21-2.35 5.65M20 14a8 8 0 01-16 0"
            />
          </svg>
          Reset Sort
        </button>
      </div>

      <!-- Loading -->
      <div v-if="isLoading" class="text-gray-500 text-lg">Loading news...</div>

      <!-- No Data -->
      <div
        v-else-if="newsList.length === 0"
        class="text-gray-500 text-lg italic mt-4"
      >
        No news found.
      </div>

      <!-- News Table -->
      <div
        v-else
        class="overflow-x-auto shadow rounded-lg border border-gray-200"
      >
        <table class="min-w-full text-left text-gray-700">
          <thead class="bg-gray-100 text-[16px] font-semibold">
            <tr>
              <th class="px-6 py-3">#</th>
              <th class="px-6 py-3">Title</th>
              <th class="px-6 py-3">Author</th>
              <th class="px-6 py-3">Status</th>
              <th class="px-6 py-3">Date</th>
              <th class="px-6 py-3">Action</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="news in newsList"
              :key="news.id"
              class="hover:bg-gray-50 border-b border-gray-100 cursor-pointer transition"
              @click="openNewsDetail(news)"
            >
              <td class="px-6 py-3">{{ news.id }}</td>
              <td class="px-6 py-3 font-medium text-blue-600">
                {{ news.title }}
              </td>
              <td class="px-6 py-3">{{ news.author }}</td>

              <!-- Status + จุดสี -->
              <td class="px-6 py-3">
                <div class="flex items-center gap-2">
                  <span
                    class="inline-block w-3 h-3 rounded-full flex-shrink-0"
                    :class="{
                      'bg-green-500': news.stats === 'Verified',
                      'bg-red-500': news.stats === 'Fake News',
                      'bg-yellow-400':
                        news.stats === 'Unverified' ||
                        news.stats === 'Under Review',
                    }"
                  ></span>
                  <span
                    class="text-[16px] font-medium leading-none"
                    :class="{
                      'text-green-700': news.stats === 'Verified',
                      'text-red-600': news.stats === 'Fake News',
                      'text-yellow-600':
                        news.stats === 'Unverified' ||
                        news.stats === 'Under Review',
                    }"
                  >
                    {{ news.stats || "Unverified" }}
                  </span>
                </div>
              </td>

              <td class="px-6 py-3">{{ news.date }}</td>

              <td class="px-6 py-3">
                <button
                  class="px-3 py-1 bg-red-500 text-white rounded-md hover:bg-red-600"
                >
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Detail Modal -->
    <div
      v-if="showDetailModal && selectedNews"
      class="fixed inset-0 bg-black/40 flex items-center justify-center z-[9999]"
      @click.self="closeNewsDetail"
    >
      <div
        class="bg-white w-[750px] max-h-[90vh] overflow-y-auto scrollbar-hide rounded-2xl shadow-xl p-8 font-[Outfit]"
      >
        <h2 class="text-2xl font-bold text-gray-800 mb-4">
          {{ selectedNews.title }}
        </h2>
        <p class="text-gray-600 mb-2">
          <strong>Author:</strong> {{ selectedNews.author }}
        </p>
        <p class="text-gray-600 mb-2">
          <strong>Date:</strong> {{ selectedNews.date }}
        </p>
        <p class="text-gray-600 mb-2 flex items-center justify-center gap-2">
          <strong>Status:</strong>
          <span
            class="inline-block w-3 h-3 rounded-full"
            :class="{
              'bg-green-500': selectedNews.stats === 'Verified',
              'bg-red-500': selectedNews.stats === 'Fake News',
              'bg-yellow-400':
                selectedNews.stats === 'Unverified' ||
                selectedNews.stats === 'Under Review',
            }"
          ></span>
          <span class="font-medium">{{ selectedNews.stats }}</span>
        </p>

        <div class="mt-4 border-t border-gray-300 pt-3">
          <p class="text-gray-600 whitespace-pre-line text-left ml-5">
            {{ selectedNews.description }}
          </p>
        </div>

        <div class="mt-4 border-t border-gray-300 pt-3">
          <p class="text-gray-600 whitespace-pre-line text-left ml-5">
            {{ selectedNews.fullDescription }}
          </p>
        </div>

        <div class="flex justify-end mt-6">
          <button
            @click="closeNewsDetail"
            class="px-5 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-md transition"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
