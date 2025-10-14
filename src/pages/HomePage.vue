<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import Header from "../components/Header.vue";
import NewsList from "../components/NewsList.vue";
import Footer from "../components/Footer.vue";

const buttons = ["All News", "Verified", "Fake News", "Under Review"];
const activeIndex = ref(0);
const itemsPerPage = ref(6);
const isLoading = ref(true);

const allNews = ref(
  Array.from({ length: 24 }, (_, i) => ({
    id: i + 1,
    title: `News Item ${i + 1}`,
    category:
      i % 3 === 0 ? "Verified" : i % 3 === 1 ? "Fake News" : "Under Review",
  }))
);

function setActive(index: number) {
  activeIndex.value = index;
}

onMounted(() => {
  setTimeout(() => {
    isLoading.value = false;
  }, 1000);
});

const filteredNews = computed(() => {
  const filter = buttons[activeIndex.value];
  if (filter === "All News") return allNews.value;
  return allNews.value.filter((n) => n.category === filter);
});

const visibleItems = ref(itemsPerPage.value);

const displayedNews = computed(() => {
  return filteredNews.value.slice(0, visibleItems.value);
});

watch(itemsPerPage, (newVal) => {
  visibleItems.value = newVal;
});

function handleItemsChange() {
  console.log(`User selected: ${itemsPerPage.value} items per page`);
}

function loadMore() {
  const nextVisible = visibleItems.value + itemsPerPage.value;
  visibleItems.value = Math.min(nextVisible, filteredNews.value.length);
  itemsPerPage.value = visibleItems.value;
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
  <div
    id="app"
    class="flex flex-col min-h-screen bg-white font-[Outfit] relative"
  >
    <aside
      class="fixed top-0 left-0 w-[60px] h-full z-20 flex flex-col items-center justify-between py-6 border-r border-gray-200 bg-white"
    >
      <!-- 🔹 ส่วนบน: Avatar + สถานะ -->
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

    <Header />

    <div
      class="ml-[50px] w-[calc(125%-50px)] flex justify-between items-center gap-6 px-8 py-4 rounded-xl flex-wrap mb-[-15px]"
    >
      <div class="flex flex-wrap gap-3">
        <button
          v-for="(btn, index) in buttons"
          :key="index"
          class="w-[150px] h-[40px] flex items-center justify-center text-base rounded-[10px] cursor-pointer border-none transition-colors duration-300 font-[Outfit]"
          :style="{
            backgroundColor: activeIndex === index ? '#3b82f6' : '#e5e7eb',
            color: activeIndex === index ? 'white' : 'black',
          }"
          @click="setActive(index)"
        >
          {{ btn }}
        </button>
      </div>

      <div class="flex items-center gap-2">
        <span>Items per page:</span>
        <select
          v-model.number="itemsPerPage"
          class="px-2 py-1 rounded-md border border-gray-300 text-base"
          @change="handleItemsChange"
        >
          <option v-for="n in [6, 12, 24]" :key="n" :value="n">{{ n }}</option>
        </select>
      </div>
    </div>

    <div
      class="flex flex-1 min-w-[1200px] min-h-[600px] items-center justify-center"
    >
      <div
        v-if="isLoading"
        class="flex flex-col items-center justify-center text-center text-[#555]"
      >
        <div
          class="w-[60px] h-[60px] border-[6px] border-gray-200 border-t-[#2563eb] rounded-full animate-spin mb-4"
        ></div>
        <p>Loading news...</p>
      </div>

      <div v-else>
        <NewsList
          :filterIndex="activeIndex"
          :itemsPerPage="visibleItems"
          :newsList="displayedNews"
        />
      </div>
    </div>

    <div class="flex justify-center my-6">
      <button
        v-if="visibleItems < filteredNews.length"
        @click="loadMore"
        class="px-6 py-2 !bg-blue-500 !text-white rounded-md hover:!bg-blue-600 transition font-[Outfit]"
      >
        More News
      </button>
    </div>

    <Footer />
  </div>
</template>
