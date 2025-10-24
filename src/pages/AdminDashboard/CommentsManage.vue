<template>
  <div class="flex-1 p-8 w-[1400px] min-h-screen">
    <!-- Header -->
    <div class="mb-8 text-center">
      <!-- Title -->
      <h1 class="text-5xl font-bold text-gray-900 mb-4">Manage Comments</h1>
    </div>

    <div class="flex items-center mb-4 relative">
      <!-- Back -->
      <div class="flex-shrink-0">
        <button
          @click="goBack"
          class="inline-flex items-center gap-2 h-[40px] px-4 bg-gray-100 text-black rounded-md hover:bg-gray-300 transition whitespace-nowrap"
        >
          <img
            src="@/assets/Card/Back.png"
            alt="Back"
            class="w-[20px] h-[20px]"
          />
          <span class="font-medium">Back to News List</span>
        </button>
      </div>

      <!-- Dropdown Centered -->
      <div class="absolute left-1/2 transform -translate-x-1/2">
        <select
          v-model="selectedCategory"
          class="h-[40px] px-4 rounded-md border border-gray-300 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
        >
          <option value="">All Categories</option>
          <option value="Verified">Verified</option>
          <option value="Unverified">Unverified</option>
          <option value="Fake News">Fake News</option>
        </select>
      </div>

      <!-- Refresh -->
      <div class="ml-auto flex-shrink-0">
        <button
          @click="refresh"
          class="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-lg transition font-medium"
        >
          Refresh
        </button>
      </div>
    </div>

    <!-- Table Section -->
    <div class="bg-white rounded-2xl shadow-lg border border-gray-200">
      <div
        class="flex items-center justify-between border-b border-gray-100 px-6 py-4"
      >
        <h2 class="text-xl font-semibold text-gray-800">All News</h2>
        <p class="text-gray-500 text-sm">
          Click on a row to view or delete comments
        </p>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full text-left text-gray-800">
          <thead
            class="bg-gray-100 text-gray-700 text-[15px] uppercase tracking-wide"
          >
            <tr>
              <th class="px-6 py-3 w-[60px] font-semibold">#</th>
              <th class="px-6 py-3 font-semibold">News Title</th>
              <th class="px-6 py-3 font-semibold">Category</th>
              <th class="px-6 py-3 font-semibold text-center">Action</th>
            </tr>
          </thead>

          <tbody>
            <template v-for="news in filteredNews" :key="news.id">
              <!-- Row -->
              <tr
                class="hover:bg-blue-50 transition cursor-pointer border-b border-gray-100"
                @click="toggleComments(news.id)"
              >
                <td class="px-6 py-3">{{ news.id }}</td>
                <td class="px-6 py-3 font-semibold text-blue-600">
                  {{ news.title }}
                </td>

                <!-- Category -->
                <td class="px-6 py-3">
                  <div
                    class="inline-flex items-center justify-start gap-2 min-w-[120px] h-[32px] rounded-full text-sm font-semibold"
                    :class="{
                      'text-green-700': news.category === 'Verified',
                      'text-yellow-700':
                        news.category === 'Under Review' ||
                        news.category === 'Unverified',
                      'text-red-700': news.category === 'Fake News',
                    }"
                  >
                    <span
                      class="w-3 h-3 rounded-full"
                      :class="{
                        'bg-green-500': news.category === 'Verified',
                        'bg-yellow-400':
                          news.category === 'Under Review' ||
                          news.category === 'Unverified',
                        'bg-red-500': news.category === 'Fake News',
                      }"
                    ></span>
                    {{ news.category }}
                  </div>
                </td>

                <!-- Action -->
                <td class="px-6 py-3 text-center">
                  <button
                    class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-1.5 rounded-lg font-medium transition"
                  >
                    {{
                      expandedNewsId === news.id
                        ? "Hide Comments"
                        : "View Comments"
                    }}
                  </button>
                </td>
              </tr>

              <!-- Comments -->
              <transition name="slide">
                <tr v-if="expandedNewsId === news.id">
                  <td colspan="4" class="bg-gray-50 px-8 py-5">
                    <h3 class="font-semibold text-gray-700 mb-3 text-base">
                      Comments for "{{ news.title }}"
                    </h3>

                    <div v-if="comments.length > 0">
                      <div
                        v-for="comment in comments"
                        :key="comment.id"
                        class="text-base flex justify-between items-center bg-white border border-gray-200 rounded-lg px-5 py-3 mb-2 shadow-sm hover:shadow-md transition"
                      >
                        <div>
                          <p class="text-gray-700">{{ comment.comment }}</p>
                          <p
                            class="text-gray-500 text-base flex items-center gap-2"
                          >
                            ↪ {{ comment.name }}
                            <span
                              :class="{
                                'text-green-600 font-semibold text-sm':
                                  comment.vote === 'upvote',
                                'text-red-600 font-semibold':
                                  comment.vote === 'downvote',
                              }"
                            >
                              {{
                                comment.vote === "upvote"
                                  ? "▲ Upvote"
                                  : comment.vote === "downvote"
                                  ? "▼ Downvote"
                                  : ""
                              }}
                            </span>
                          </p>
                        </div>

                        <!-- Delete Button -->
                        <button
                          @click.stop="confirmDelete(comment)"
                          class="bg-red-600 hover:bg-red-700 text-white font-medium px-3 py-1.5 rounded-md transition ml-4"
                        >
                          Delete
                        </button>
                      </div>
                    </div>

                    <p v-else class="text-gray-500 italic">
                      No comments found.
                    </p>
                  </td>
                </tr>
              </transition>
            </template>
          </tbody>
        </table>
      </div>
    </div>

    <div
      v-if="showDeleteModal"
      class="fixed inset-0 flex items-center justify-center bg-opacity-50 backdrop-blur-sm"
    >
      <div class="bg-white p-6 rounded-lg shadow-lg w-96 text-center">
        <h2 class="text-lg font-bold mb-4">Confirm Delete</h2>

        <p class="mb-2">
          News: <strong>{{ commentToDelete?.newsTitle }}</strong>
        </p>
        <p class="mb-2">
          Comment by: <strong>{{ commentToDelete?.name }}</strong>
        </p>
        <p class="mb-4 italic text-gray-700">
          "{{ commentToDelete?.commentText }}"
        </p>

        <div class="flex justify-center gap-4">
          <button
            @click="deleteComment"
            class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md"
          >
            Delete
          </button>
          <button
            @click="cancelDelete"
            class="bg-gray-300 hover:bg-gray-400 px-4 py-2 rounded-md"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>

    <!-- Deleted Comments Table -->
    <div class="mt-8 bg-white rounded-2xl shadow-lg border border-gray-200">
      <div
        class="flex items-center justify-between border-b border-gray-100 px-6 py-4"
      >
        <h2 class="text-xl font-semibold text-gray-800">Deleted Comments</h2>
        <p class="text-gray-500 text-sm">
          Comments that were deleted from the news
        </p>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full text-left text-gray-800">
          <thead
            class="bg-gray-100 text-gray-700 text-[15px] uppercase tracking-wide"
          >
            <tr>
              <th class="px-6 py-3 w-[60px] font-semibold">#</th>
              <th class="px-6 py-3 font-semibold">Comment</th>
              <th class="px-6 py-3 font-semibold text-center">Author</th>
              <th class="px-6 py-3 font-semibold text-center">Original News</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="comment in deletedComments"
              :key="comment.id"
              class="border-b border-gray-100"
            >
              <td class="px-6 py-3 text-center">{{ comment.newsId }}</td>
              <td class="px-6 py-3">{{ comment.comment }}</td>
              <td class="px-6 py-3 text-center align-middle">
                {{ comment.name }}
              </td>
              <td class="px-6 py-3 text-center">{{ comment.newsTitle }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { eventBus } from "@/eventBus";

const router = useRouter();

interface NewsItem {
  id: number;
  title: string;
  category: string;
}

interface CommentItem {
  id: number;
  userId: number;
  name: string;
  vote: string;
  upVotes: number;
  downVotes: number;
  comment: string;
  createdAt: string;
  isVisible?: boolean;
}

const newsList = ref<NewsItem[]>([]);
const expandedNewsId = ref<number | null>(null);
const comments = ref<CommentItem[]>([]);
const deletedComments = ref<
  Array<CommentItem & { newsId: number | string; newsTitle: string }>
>([]);

// --- Modal delete state ---
const showDeleteModal = ref(false);

const commentToDelete = ref<{
  id: number;
  newsTitle: string;
  name: string;
  commentText: string;
  vote?: string;
} | null>(null);

const fetchNews = async () => {
  try {
    const res = await axios.get("http://localhost:8080/api/news");
    newsList.value = res.data;
  } catch (err) {
    console.error("Failed to fetch news:", err);
  }
};

onMounted(() => {
  fetchNews();
  fetchDeletedComments();
});

function goBack() {
  router.push("/home");
}

function refresh() {
  if (expandedNewsId.value) {
    fetchComments(expandedNewsId.value);
  }
  fetchDeletedComments();
}

function toggleComments(newsId: number) {
  if (expandedNewsId.value === newsId) {
    expandedNewsId.value = null;
    comments.value = [];
  } else {
    expandedNewsId.value = newsId;
    fetchComments(newsId);
  }
}

async function fetchComments(newsId: number) {
  try {
    const res = await axios.get(
      `http://localhost:8080/api/votes/news/${newsId}`
    );
    const allComments = res.data;

    comments.value = allComments.filter((c: any) => c.isVisible === true);
  } catch (err) {
    console.error("Error fetching comments:", err);
    comments.value = [];
  }
}

const selectedCategory = ref("");

const filteredNews = computed(() => {
  if (!selectedCategory.value) return newsList.value;
  return newsList.value.filter(
    (news) => news.category === selectedCategory.value
  );
});

// --- Functions for delete modal ---
function confirmDelete(comment: CommentItem) {
  const newsTitle =
    newsList.value.find((n) => n.id === expandedNewsId.value)?.title || "";
  commentToDelete.value = {
    id: comment.id,
    newsTitle,
    name: comment.name,
    commentText: comment.comment,
    vote: comment.vote,
  };
  showDeleteModal.value = true;
}

function cancelDelete() {
  commentToDelete.value = null;
  showDeleteModal.value = false;
}

async function deleteComment() {
  if (!commentToDelete.value) return;

  const commentId = commentToDelete.value.id;
  const newsId = expandedNewsId.value!;

  try {
    await axios.put(`http://localhost:8080/api/votes/hide/${commentId}`, {
      is_visible: 0,
      vote: commentToDelete.value.vote,
    });

    await fetchComments(newsId);
    await fetchDeletedComments();

    eventBus.emit("comments-updated");

    commentToDelete.value = null;
    showDeleteModal.value = false;
  } catch (err) {
    console.error("Failed to hide comment:", err);
    alert("Failed to hide comment. Please try again.");
  }
}

async function fetchDeletedComments() {
  try {
    const res = await axios.get("http://localhost:8080/api/votes/hidden");
    deletedComments.value = (res.data || []).map((c: any) => ({
      id: c.id,
      userId: c.userId,
      name: c.name,
      comment: c.comment,
      createdAt: c.createdAt,
      newsId: c.news?.id ?? "-",
      newsTitle: c.news?.title ?? "Unknown",
    }));
  } catch (err) {
    console.error("Failed to load deleted comments:", err);
    deletedComments.value = [];
  }
}
</script>

<style scoped>
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}
.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
