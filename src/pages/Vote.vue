<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

import LikeIcon from "@/assets/Card/Like.png";
import DislikeIcon from "@/assets/Card/Dislike.png";
import AsideMenu from "@/components/AsideMenu.vue";
import AddNewsModal from "@/components/AddNewsModal.vue";

interface User {
  id: number;
  name: string;
  email: string;
  surname?: string;
  access?: string;
}

interface NewsItem {
  id: number;
  title: string;
  category?: string;
  author?: string;
  date?: string;
  image?: string;
  description?: string;
  upVotes?: number;
  downVotes?: number;
}

const route = useRoute();
const id = Number(route.params.id);

const isLoading = ref(true);
const isSubmitting = ref(false);
const news = ref<NewsItem | null>(null);

const user = ref<User | null>(null);
const showAddNewsModal = ref(false);

const form = ref({
  vote: "",
  comment: "",
  imageUrl: "",
});

async function fetchNewsById() {
  try {
    const res = await axios.get(`http://localhost:8080/api/news/${id}`);
    if (res.status !== 200) throw new Error("Failed to fetch news");

    const data = res.data;
    news.value = {
      id: data.id,
      title: data.title,
      category: data.category,
      author: data.author,
      date: data.date,
      image: data.image,
      description: data.description,
      upVotes: data.upVotes || 0,
      downVotes: data.downVotes || 0,
    };
  } catch (err) {
    console.error("❌ Error fetching news:", err);
    news.value = null;
  } finally {
    isLoading.value = false;
  }
}

onMounted(fetchNewsById);

async function submitVote() {
  if (!form.value.comment || !form.value.vote) {
    alert("Please select your vote and add a comment!");
    return;
  }

  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    const votePayload = {
      news_id: id,
      user_id: user.value ? (user.value as any).id || 0 : 0,
      name: user.value
        ? `${user.value.name} ${user.value.surname || ""}`
        : "Unknown User",
      vote: form.value.vote === "up" ? "upvote" : "downvote",
      comment: form.value.comment,
      image_url: form.value.imageUrl || null,
    };

    const res = await axios.post(
      "http://localhost:8080/api/votes",
      votePayload
    );

    if (res.status === 200 || res.status === 201) {
      alert("🌟 Thank you! Your vote has been recorded.");

      await fetchNewsById();

      form.value = { vote: "", comment: "", imageUrl: "" };
    } else {
      alert("❌ Failed to save your vote.");
    }
  } catch (err) {
    console.error("Error submitting vote:", err);
    alert("An error occurred while submitting your vote.");
  } finally {
    isSubmitting.value = false;
  }
}

onMounted(() => {
  const savedUser = localStorage.getItem("user");
  if (savedUser) user.value = JSON.parse(savedUser);
});

function openAddNewsModal() {
  showAddNewsModal.value = true;
}
function closeAddNewsModal() {
  showAddNewsModal.value = false;
}
function handleSaveNews(newItem: NewsItem) {
  console.log("📰 New News Added from Vote Page:", newItem);
  alert(" News added successfully!");
  closeAddNewsModal();
}
</script>

<template>
  <div class="flex min-h-screen font-[Outfit] bg-white">
    <!-- Sidebar -->
    <AsideMenu :user="user" @openAddNews="openAddNewsModal" />

    <!-- Main Content -->
    <div class="flex-1 ml-[80px] px-8 py-6 overflow-y-auto">
      <!-- Loading Spinner -->
      <div
        v-if="isLoading"
        class="fixed inset-0 bg-white/95 flex flex-col items-center justify-center z-[9999] text-[18px] text-[#333]"
      >
        <div
          class="w-[60px] h-[60px] mb-[15px] rounded-full border-[6px] border-gray-200 border-t-[#003791] animate-spin"
        ></div>
        <p>Loading news...</p>
      </div>

      <!-- Content -->
      <div v-else class="pb-12">
        <!-- Back Button -->
        <router-link
          :to="{ name: 'NewsDetail', params: { id } }"
          class="flex items-center gap-[6px] bg-gray-100 text-black text-[16px] rounded-[8px] px-[20px] py-[10px] max-w-fit cursor-pointer transition-colors duration-300 hover:bg-gray-300"
        >
          <img
            src="@/assets/Card/Back.png"
            alt="Back Icon"
            class="w-[20px] h-[20px] mr-[5px]"
          />
          Back to News Detail
        </router-link>

        <!-- Layout -->
        <div
          class="flex flex-col lg:flex-row justify-center gap-6 mt-6 w-full max-w-[800px] mx-auto text-left"
        >
          <!-- News Card -->
          <div
            class="flex-1 bg-white border border-gray-300 rounded-[12px] p-[30px] shadow-md"
          >
            <h2 class="text-[1.6rem] font-bold">{{ news?.title }}</h2>

            <span
              class="block mx-auto mt-[10px] px-[12px] py-[6px] rounded-[6px] text-[1rem] font-medium w-fit"
              :class="{
                'bg-emerald-100 text-emerald-700':
                  news?.category === 'Verified',
                'bg-red-100 text-red-700': news?.category === 'Fake News',
                'bg-yellow-100 text-yellow-600':
                  news?.category === 'Under Review' ||
                  news?.category === 'Unverified',
              }"
            >
              {{ news?.category }}
            </span>

            <p class="text-[#444] leading-[1.6] mt-4">
              {{ news?.description }}
            </p>
            <p class="text-[#666] text-[0.9rem] mt-4">
              By {{ news?.author }} • {{ news?.date }}
            </p>

            <div class="flex justify-center items-center gap-[10px] mt-[15px]">
              <span class="text-green-700 flex items-center gap-1">
                <img :src="LikeIcon" class="w-[25px] h-[25px]" />
                {{ news?.upVotes }}
              </span>
              <span class="text-red-700 flex items-center gap-1">
                <img :src="DislikeIcon" class="w-[25px] h-[25px]" />
                {{ news?.downVotes }}
              </span>
            </div>
          </div>

          <!-- Vote Form -->
          <div
            class="flex-1 bg-white border border-gray-300 rounded-[12px] p-[30px] shadow-md"
          >
            <h3 class="text-[1.4rem] font-semibold mb-4">Cast Your Vote</h3>

            <!-- Name -->
            <label class="block font-medium mb-[0.3rem]">Your Name</label>
            <input
              :value="user ? `${user.name} ${user.surname || ''}` : ''"
              readonly
              type="text"
              class="w-full border border-gray-300 rounded-[6px] p-[10px] text-[1rem] bg-gray-50 cursor-not-allowed"
            />

            <!-- Vote Buttons -->
            <label class="block font-medium mb-[0.3rem] mt-4">Your Vote</label>
            <div class="flex gap-3 mt-[-5px]">
              <button
                :class="[
                  form.vote === 'up'
                    ? 'flex-1 rounded-[6px] py-[10px] font-medium bg-green-600 text-white hover:bg-green-700 transition'
                    : 'flex-1 rounded-[6px] py-[10px] font-medium bg-gray-200 text-black hover:bg-green-600 hover:text-white transition',
                ]"
                @click="form.vote = 'up'"
              >
                This is Real
              </button>

              <button
                :class="[
                  form.vote === 'down'
                    ? 'flex-1 rounded-[6px] py-[10px] font-medium bg-red-600 text-white hover:bg-red-700 transition'
                    : 'flex-1 rounded-[6px] py-[10px] font-medium bg-gray-200 text-black hover:bg-red-600 hover:text-white transition',
                ]"
                @click="form.vote = 'down'"
              >
                This is Fake
              </button>
            </div>

            <!-- Comment -->
            <label class="block font-medium mb-[0.3rem] mt-4"
              >Your Comment</label
            >
            <textarea
              v-model="form.comment"
              placeholder="Explain why you think this news is real or fake..."
              class="w-full border border-gray-300 rounded-[6px] p-[10px] text-[1rem] min-h-[100px] resize-none"
            ></textarea>

            <!-- Image -->
            <label class="block font-medium mb-[0.3rem] mt-4">
              Supporting Image URL (optional)
            </label>
            <input
              v-model="form.imageUrl"
              type="text"
              placeholder="https://example.com/image.jpg"
              class="w-full border border-gray-300 rounded-[6px] p-[10px] text-[1rem]"
            />

            <!-- Submit -->
            <button
              class="mt-5 w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-[12px] rounded-[6px] transition"
              @click="submitVote"
            >
              Submit Vote & Comment
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
