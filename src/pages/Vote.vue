<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import LikeIcon from "@/assets/Card/Like.png";
import DislikeIcon from "@/assets/Card/Dislike.png";

const route = useRoute();
const id = Number(route.params.id);

const isLoading = ref(true);
const news = ref<any | null>(null);

const isSubmitting = ref(false);

const form = ref({
  name: "",
  vote: "",
  comment: "",
  imageUrl: "",
});

async function fetchNewsById() {
  try {
    const res = await fetch("http://localhost:5175/api/news");
    if (!res.ok) throw new Error("Failed to fetch news");

    const data = await res.json();
    const selected = data.news.find((n: any) => Number(n.id) === id);
    news.value = selected || null;
  } catch (err) {
    console.error("Error fetching news:", err);
    news.value = null;
  } finally {
    isLoading.value = false;
  }
}

onMounted(fetchNewsById);

async function submitVote() {
  if (!form.value.comment || !form.value.vote) {
    alert("⚠️ Please select your vote and add a comment!");
    return;
  }

  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    const res = await fetch("http://localhost:5175/api/vote", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        id,
        name: user.value
          ? `${user.value.name} ${user.value.surname || ""}`
          : "Unknown User",
        status: form.value.vote,
        comment: form.value.comment,
        imageUrl: form.value.imageUrl || "",
      }),
    });

    console.log("📧 Submitting vote:", {
      id,
      name: user.value ? `${user.value.name} ${user.value.surname || ""}` : "",
      email: user.value?.email,
    });

    const data = await res.json();

    if (res.ok) {
      alert("Thank you for your vote! Your feedback has been recorded.");
      form.value = { name: "", vote: "", comment: "", imageUrl: "" };
      await fetchNewsById();
    } else {
      alert("Failed to save your vote: " + data.message);
    }
  } catch (err) {
    console.error("Error submitting vote:", err);
    alert("An error occurred while connecting to the server.");
  } finally {
    isSubmitting.value = false;
  }
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
    return "bg-red-500 border-none";
  if (access.includes("reader")) return "bg-yellow-400 border-none";
  return "bg-gray-300";
});

function handleLogout() {
  localStorage.removeItem("user");
  alert("You have been logged out.");
  window.location.href = "/login";
}
</script>

<template>
  <div class="w-[800px] mx-auto justify-center pb-[40px]">
    <div
      v-if="isLoading"
      class="fixed inset-0 bg-white/95 flex flex-col items-center justify-center z-[9999] text-[18px] text-[#333]"
    >
      <div
        class="w-[60px] h-[60px] mb-[15px] rounded-full border-[6px] border-gray-200 border-t-[#003791] animate-spin"
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
        :to="`/news/${id}`"
        class="flex items-center gap-[6px] bg-gray-100 text-black text-[16px] rounded-[8px] px-[20px] py-[10px] max-w-fit cursor-pointer transition-colors duration-300 ease-in-out hover:bg-gray-300 hover:text-black no-underline"
      >
        <img
          src="@/assets/Card/Back.png"
          alt="Back Icon"
          class="w-[20px] h-[20px] mr-[5px] align-middle"
        />
        Back to News Detail
      </router-link>

      <div class="flex justify-center gap-[15px] mt-[10px]">
        <div
          class="flex-1 bg-white border border-gray-300 rounded-[12px] p-[30px] shadow-[0_4px_10px_rgba(0,0,0,0.1)] text-left"
        >
          <h2 class="text-[1.6rem] font-bold -mt-[10px]">{{ news.title }}</h2>

          <span
            class="block mx-auto mt-[10px] px-[12px] py-[6px] rounded-[6px] text-[1rem] font-medium w-fit"
            :class="{
              'bg-emerald-100 text-emerald-700': news.stats === 'Verified',
              'bg-red-100 text-red-700': news.stats === 'Fake News',
              'bg-yellow-100 text-yellow-600':
                news.stats === 'Under Review' || news.stats === 'Unverified',
            }"
          >
            {{ news.stats }}
          </span>

          <p class="text-[#444] leading-[1.6] mt-4">{{ news.description }}</p>
          <p class="text-[#666] text-[0.9rem] mt-4">
            By {{ news.author }} • {{ news.date }}
          </p>

          <div class="flex justify-center items-center gap-[10px] mt-[15px]">
            <span
              class="text-green-700 rounded-[6px] px-[12px] py-[6px] font-medium flex items-center gap-1"
            >
              <img
                :src="LikeIcon"
                alt="Like"
                class="w-[25px] h-[25px] align-middle"
              />
              {{ news.upVotes }}
            </span>

            <span
              class="text-red-700 rounded-[6px] px-[12px] py-[6px] font-medium flex items-center gap-1"
            >
              <img
                :src="DislikeIcon"
                alt="Dislike"
                class="w-[25px] h-[25px] align-middle"
              />
              {{ news.downVotes }}
            </span>
          </div>
        </div>

        <div
          class="flex-1 bg-white border border-gray-300 rounded-[12px] p-[30px] shadow-[0_4px_10px_rgba(0,0,0,0.1)] text-left"
        >
          <h3 class="text-[1.4rem] font-semibold -mt-[10px] mb-4">
            Cast Your Vote
          </h3>

          <label class="block font-medium mb-[0.3rem]">Your Name</label>
          <input
            :value="user ? `${user.name} ${user.surname || ''}` : ''"
            readonly
            type="text"
            placeholder="Enter your name"
            class="w-full border border-gray-300 rounded-[6px] p-[10px] text-[1rem] font-[Outfit] mt-[-5px] outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/15 bg-gray-50 cursor-not-allowed"
          />

          <label class="block font-medium mb-[0.3rem] mt-4">Your Vote</label>
          <div class="flex gap-3 mt-[-5px]">
            <button
              :class="[
                'flex-1 rounded-[6px] py-[10px] font-medium transition-colors duration-200',
                form.vote === 'up'
                  ? 'bg-green-600 text-white hover:bg-green-700'
                  : 'bg-gray-200 text-black hover:bg-green-700',
              ]"
              @click="form.vote = 'up'"
            >
              This is Real
            </button>

            <button
              :class="[
                'flex-1 rounded-[6px] py-[10px] font-medium transition-colors duration-200',
                form.vote === 'down'
                  ? 'bg-red-600 text-white hover:bg-red-700'
                  : 'bg-gray-200 text-black hover:bg-red-700',
              ]"
              @click="form.vote = 'down'"
            >
              This is Fake
            </button>
          </div>

          <label class="block font-medium mb-[0.3rem] mt-4">Your Comment</label>
          <textarea
            v-model="form.comment"
            placeholder="Explain why you think this news is real or fake..."
            class="w-full border border-gray-300 rounded-[6px] p-[10px] text-[1rem] font-[Outfit] mt-[-5px] outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/15 min-h-[100px] resize-none"
          ></textarea>

          <label class="block font-medium mb-[0.3rem] mt-4"
            >Supporting Image URL (optional)</label
          >
          <input
            v-model="form.imageUrl"
            type="text"
            placeholder="https://example.com/image.jpg"
            class="w-full border border-gray-300 rounded-[6px] p-[10px] text-[1rem] font-[Outfit] mt-[-5px] outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/15"
          />

          <button
            class="mt-5 w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-[12px] rounded-[6px] transition-colors duration-200"
            @click="submitVote"
          >
            Submit Vote & Comment
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
