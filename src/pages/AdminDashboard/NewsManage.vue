<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import axios from "axios";

interface NewsItem {
  id: number;
  title: string;
  category?: string;
  author?: string;
  date?: string;
  image?: string;
  upVotes?: number;
  downVotes?: number;
  commentsCount?: number;
  description?: string;
  fullDescription?: string;
  visible?: boolean;
}

const isLoading = ref(true);
const newsList = ref<NewsItem[]>([]);
const selectedNews = ref<NewsItem | null>(null);
const showDetailModal = ref(false);

const sortOrderDate = ref<"asc" | "desc">("desc");
const sortOrdercategory = ref<"asc" | "desc">("asc");
const sortOrderId = ref<"asc" | "desc">("asc");

function sortBycategory() {
  sortOrdercategory.value = sortOrdercategory.value === "asc" ? "desc" : "asc";
  newsList.value.sort((a, b) => {
    const catA = a.category ?? "";
    const catB = b.category ?? "";
    if (catA < catB) return sortOrdercategory.value === "asc" ? -1 : 1;
    if (catA > catB) return sortOrdercategory.value === "asc" ? 1 : -1;
    return 0;
  });
}

function sortByDate() {
  sortOrderDate.value = sortOrderDate.value === "asc" ? "desc" : "asc";
  newsList.value.sort((a, b) => {
    const dateA = new Date(a.date ?? "1970-01-01").getTime();
    const dateB = new Date(b.date ?? "1970-01-01").getTime();
    return sortOrderDate.value === "asc" ? dateA - dateB : dateB - dateA;
  });
}

function sortById() {
  sortOrderId.value = sortOrderId.value === "asc" ? "desc" : "asc";
  newsList.value.sort((a, b) => {
    const idA = a.id ?? 0;
    const idB = b.id ?? 0;
    return sortOrderId.value === "asc" ? idA - idB : idB - idA;
  });
}

function resetSort() {
  newsList.value.sort(
    (a, b) =>
      new Date(b.date ?? "1970-01-01").getTime() -
      new Date(a.date ?? "1970-01-01").getTime()
  );
  sortOrderDate.value = "desc";
  sortOrdercategory.value = "asc";
  sortOrderId.value = "asc";
}

async function fetchNews() {
  try {
    const res = await fetch("http://localhost:8080/api/news");
    if (!res.ok) throw new Error("Failed to fetch news");

    const data = await res.json();
    newsList.value = data
      .map((n: any) => ({
        ...n,
        visible: n.visible ?? true,
      }))
      .sort(
        (a: any, b: any) =>
          new Date(b.date).getTime() - new Date(a.date).getTime()
      );
  } catch (err) {
    console.error("Error fetching news:", err);
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

const visibleNews = computed(() => newsList.value.filter((n) => n.visible));
const hiddenNews = computed(() => newsList.value.filter((n) => !n.visible));

async function hideNews(news: NewsItem) {
  try {
    const res = await axios.put(
      `http://localhost:8080/api/news/hide/${news.id}`
    );
    alert(res.data.message || " News hidden successfully");
    await fetchNews();
  } catch (err) {
    console.error(err);
    alert("Failed to hide news");
  }
}

async function showAgain(id: number) {
  try {
    const res = await axios.put(`http://localhost:8080/api/news/show/${id}`);
    alert(res.data.message || "🌟 News shown successfully");
    await fetchNews();
  } catch (err) {
    console.error(err);
    alert("Failed to show news");
  }
}
</script>

<template>
  <div class="flex min-h-screen font-[Outfit] bg-white">
    <!-- Main Content -->
    <div class="flex-1 ml-[80px] px-10 py-8 w-[1500px]">
      <h1 class="text-3xl font-bold text-[#111827] mb-6">Manage News</h1>

      <!-- Sorting Buttons -->
      <div class="mb-4 flex items-center justify-between gap-3 flex-nowrap">
        <router-link
          to="/"
          class="inline-flex items-center justify-center gap-2 h-[40px] px-4 bg-gray-100 text-black rounded-md hover:bg-gray-300 transition whitespace-nowrap"
        >
          <img
            src="@/assets/Card/Back.png"
            alt="Back"
            class="w-[20px] h-[20px]"
          />
          Back to News List
        </router-link>

        <div class="flex items-center gap-3 flex-nowrap">
          <button
            @click="sortBycategory"
            class="h-[40px] px-4 bg-green-500 text-white rounded-md hover:bg-green-600"
          >
            Sort by Status
          </button>
          <button
            @click="sortById"
            class="h-[40px] px-4 bg-blue-500 text-white rounded-md hover:bg-blue-600"
          >
            Sort by ID
          </button>
          <button
            @click="sortByDate"
            class="h-[40px] px-4 bg-yellow-500 text-white rounded-md hover:bg-yellow-600"
          >
            Sort by Date
          </button>
          <button
            @click="resetSort"
            class="h-[40px] px-4 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
          >
            Reset Sort
          </button>
        </div>
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

      <!-- Active News -->
      <div v-else>
        <div
          class="overflow-x-auto shadow rounded-lg border border-gray-200 mb-10"
        >
          <h2 class="text-xl font-semibold text-gray-800 px-6 pt-4 pb-2">
            Active News
          </h2>

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
                v-for="news in visibleNews"
                :key="news.id"
                class="hover:bg-gray-50 border-b border-gray-100 cursor-pointer transition"
                @click="openNewsDetail(news)"
              >
                <td class="px-6 py-3">{{ news.id }}</td>
                <td class="px-6 py-3 font-medium text-blue-600">
                  {{ news.title }}
                </td>
                <td class="px-6 py-3">{{ news.author }}</td>
                <td class="px-6 py-3">
                  <div class="flex items-center gap-2">
                    <span
                      class="inline-block w-3 h-3 rounded-full"
                      :class="{
                        'bg-green-500': news.category === 'Verified',
                        'bg-red-500': news.category === 'Fake News',
                        'bg-yellow-400':
                          news.category === 'Unverified' ||
                          news.category === 'Under Review',
                        'bg-gray-400': !news.category,
                      }"
                    ></span>
                    <span
                      :class="{
                        'text-green-600 font-semibold':
                          news.category === 'Verified',
                        'text-red-600 font-semibold':
                          news.category === 'Fake News',
                        'text-yellow-500 font-semibold':
                          news.category === 'Unverified' ||
                          news.category === 'Under Review',
                        'text-gray-500': !news.category,
                      }"
                    >
                      {{ news.category || "Unverified" }}
                    </span>
                  </div>
                </td>

                <td class="px-6 py-3">{{ news.date }}</td>
                <td class="px-6 py-3">
                  <button
                    @click.stop="hideNews(news)"
                    class="px-3 py-1 bg-red-700 text-white rounded-md hover:bg-red-800"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!--  Hidden News -->
        <div
          v-if="hiddenNews.length > 0"
          class="overflow-x-auto shadow rounded-lg border border-gray-200 mt-10"
        >
          <h2 class="text-xl font-semibold text-gray-800 px-6 pt-4 pb-2">
            Hidden News ({{ hiddenNews.length }})
          </h2>

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
                v-for="news in hiddenNews"
                :key="news.id"
                class="hover:bg-gray-50 border-b border-gray-100 transition"
                @click="openNewsDetail(news)"
              >
                <td class="px-6 py-3">{{ news.id }}</td>
                <td class="px-6 py-3 text-gray-500 italic">{{ news.title }}</td>
                <td class="px-6 py-3">{{ news.author }}</td>
                <td class="px-6 py-3">
                  <div class="flex items-center gap-2" title="Hidden News">
                    <span
                      class="inline-block w-3 h-3 rounded-full bg-purple-500"
                    ></span>
                    <span class="text-purple-600 font-semibold italic"
                      >Hidden</span
                    >
                  </div>
                </td>

                <td class="px-6 py-3">{{ news.date }}</td>
                <td class="px-6 py-3">
                  <button
                    @click.stop="showAgain(news.id)"
                    class="px-3 py-1 bg-green-600 text-white rounded-md hover:bg-green-700"
                  >
                    Show Again
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="text-gray-500 text-sm italic text-right mt-2">
          No hidden news.
        </div>
      </div>
    </div>

    <!-- Detail Modal -->
    <div
      v-if="showDetailModal && selectedNews"
      class="fixed inset-0 bg-black/40 flex items-center justify-center z-[9999]"
      @click.self="closeNewsDetail"
    >
      <div
        class="bg-white w-[750px] max-h-[90vh] overflow-y-auto rounded-2xl shadow-xl p-8 font-[Outfit]"
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
        <p class="text-gray-600 mb-2">
          <strong class="mr-2">Category:</strong>

          <span
            class="inline-block w-3 h-3 rounded-full mr-2"
            :class="{
              'bg-purple-500': selectedNews.visible === false,
              'bg-green-500':
                selectedNews.category === 'Verified' &&
                selectedNews.visible !== false,
              'bg-red-500':
                selectedNews.category === 'Fake News' &&
                selectedNews.visible !== false,
              'bg-yellow-400':
                (selectedNews.category === 'Unverified' ||
                  selectedNews.category === 'Under Review') &&
                selectedNews.visible !== false,
              'bg-gray-400':
                !selectedNews.category && selectedNews.visible !== false,
            }"
          ></span>

          <span
            :class="{
              'text-purple-600 font-semibold italic':
                selectedNews.visible === false,
              'text-green-600 font-semibold':
                selectedNews.category === 'Verified' &&
                selectedNews.visible !== false,
              'text-red-600 font-semibold':
                selectedNews.category === 'Fake News' &&
                selectedNews.visible !== false,
              'text-yellow-500 font-semibold':
                (selectedNews.category === 'Unverified' ||
                  selectedNews.category === 'Under Review') &&
                selectedNews.visible !== false,
              'text-gray-500':
                !selectedNews.category && selectedNews.visible !== false,
            }"
          >
            {{
              selectedNews.visible === false
                ? "Hidden"
                : selectedNews.category || "Unverified"
            }}
          </span>
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
            class="px-5 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-md"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
