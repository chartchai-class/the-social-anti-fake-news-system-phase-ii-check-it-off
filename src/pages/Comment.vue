<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import LikeIcon from "@/assets/Card/Like.png";
import DislikeIcon from "@/assets/Card/Dislike.png";
import CommentIcon from "@/assets/Card/Comment.png";
import AsideMenu from "@/components/AsideMenu.vue"; // ✅ ใช้ component เดิม

const router = useRouter();
const route = useRoute();
const id = Number(route.params.id);

interface NewsItem {
  id: number;
  upVotes: number;
  downVotes: number;
  comments: number;
}
interface CommentItem {
  id: string;
  name: string;
  status: string;
  comment: string;
  imageUrl?: string;
}

/* -------------------------------------------------------------------------- */
/*                                 STATE SETUP                                */
/* -------------------------------------------------------------------------- */
const isLoading = ref(true);
const isLoadingComments = ref(true);

const news = ref<NewsItem>({
  id,
  upVotes: 0,
  downVotes: 0,
  comments: 0,
});
const comments = ref<CommentItem[]>([]);

/* -------------------------------------------------------------------------- */
/*                              FETCH DATA                                   */
/* -------------------------------------------------------------------------- */
onMounted(async () => {
  try {
    const res = await fetch("http://localhost:5175/api/news");
    if (!res.ok) throw new Error("Failed to fetch news");
    const data = await res.json();
    const selected = data.news.find((n: any) => Number(n.id) === id);
    if (selected) {
      news.value = {
        id: selected.id,
        upVotes: selected.upVotes || 0,
        downVotes: selected.downVotes || 0,
        comments: selected.comments || 0,
      };
    }

    const res2 = await fetch("http://localhost:5175/api/comments");
    if (!res2.ok) throw new Error("Failed to fetch comments");
    const data2 = await res2.json();
    comments.value = data2.comments.filter(
      (c: CommentItem) => Number(c.id) === id
    );
  } catch (err) {
    console.error(err);
  } finally {
    isLoading.value = false;
    isLoadingComments.value = false;
  }
});

/* -------------------------------------------------------------------------- */
/*                             USER SESSION                                  */
/* -------------------------------------------------------------------------- */
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

/* -------------------------------------------------------------------------- */
/*                           ADD NEWS MODAL LOGIC                             */
/* -------------------------------------------------------------------------- */
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
    <!-- ✅ ใช้ AsideMenu -->
    <AsideMenu :user="user" @openAddNews="openAddNewsModal" />

    <!-- ✅ Main Content -->
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
            Government Launches New Education Reform Policy
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
              <span>{{ news.comments }} Total Comments</span>
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
        <h1 class="text-[25px] font-bold mb-6 text-[#111827] text-left">Comments & Votes</h1>

        <div v-if="isLoadingComments" class="text-gray-500">Loading comments...</div>
        <div v-else-if="comments.length === 0" class="text-center text-[#666] italic mt-3">
          No comments yet.
        </div>

        <div v-else>
          <div
            v-for="c in comments"
            :key="c.name"
            class="flex flex-col text-left gap-3 bg-white rounded-xl py-5 px-5 border-b border-gray-200 -ml-5"
          >
            <div class="flex items-center gap-3">
              <span class="font-semibold text-[18px]">{{ c.name }}</span>
              <span
                v-if="c.status === 'Real'"
                class="text-green-600 bg-green-100 px-2 py-[2px] rounded-md text-sm font-medium"
              >
                Real
              </span>
              <span
                v-else-if="c.status === 'Fake'"
                class="text-red-600 bg-red-100 px-2 py-[2px] rounded-md text-sm font-medium"
              >
                Fake
              </span>
              <span
                v-else
                class="text-yellow-600 bg-yellow-100 px-2 py-[2px] rounded-md text-sm font-medium"
              >
                Review
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

    <!-- ✅ Add News Modal -->
    <div
      v-if="showAddNewsModal"
      class="fixed inset-0 bg-black/40 flex items-center justify-center z-[9999]"
      @click.self="closeAddNewsModal"
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
            <label class="block text-gray-700 font-semibold mb-1 text-center text-xl"
              >Date</label
            >
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
