<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import Header from "../components/Header.vue";
import NewsList from "../components/NewsList.vue";
import Footer from "../components/Footer.vue";
import AsideMenu from "../components/AsideMenu.vue";
import AddNewsModal from "../components/AddNewsModal.vue";

interface User {
  name: string;
  email: string;
  surname?: string;
  access?: string;
}

interface NewsItem {
  id: number;
  title: string;
  category: string;
  author?: string;
  date?: string;
  image?: string;
  description?: string;
}

const buttons = ["All News", "Verified", "Fake News", "Under Review"];
const activeIndex = ref(0);
const itemsPerPage = ref(6);
const isLoading = ref(true);

const allNews = ref<NewsItem[]>(
  Array.from({ length: 24 }, (_, i) => ({
    id: i + 1,
    title: `News Item ${i + 1}`,
    category:
      i % 3 === 0 ? "Verified" : i % 3 === 1 ? "Fake News" : "Under Review",
  }))
);

const user = ref<User | null>(null);
const visibleItems = ref(itemsPerPage.value);

const showAddNewsModal = ref(false);

const filteredNews = computed(() => {
  const filter = buttons[activeIndex.value];
  if (filter === "All News") return allNews.value;
  return allNews.value.filter((n) => n.category === filter);
});

const displayedNews = computed(() =>
  filteredNews.value.slice(0, visibleItems.value)
);

watch(itemsPerPage, (newVal) => {
  visibleItems.value = newVal;
});

function setActive(index: number) {
  activeIndex.value = index;
}

function handleItemsChange() {
  console.log(`User selected: ${itemsPerPage.value} items per page`);
}

function openAddNewsModal() {
  showAddNewsModal.value = true;
}

function closeAddNewsModal() {
  showAddNewsModal.value = false;
}

function handleSaveNews(newItem: NewsItem) {
  allNews.value.unshift({
    id: allNews.value.length + 1,
    title: newItem.title,
    author: newItem.author,
    date: newItem.date,
    image: newItem.image,
    description: newItem.description,
    category: "Under Review",
  });
  console.log("✅ New News Added:", newItem);
}

onMounted(() => {
  const savedUser = localStorage.getItem("user");
  if (savedUser) {
    user.value = JSON.parse(savedUser);
  }

  isLoading.value = true;
  setTimeout(() => {
    isLoading.value = false;
  }, 2000);
});
</script>

<template>
  <div
    id="app"
    class="flex flex-col min-h-screen bg-white font-[Outfit] relative"
  >
    <!-- Sidebar -->
    <AsideMenu :user="user" @openAddNews="openAddNewsModal" />

    <!-- Header -->
    <Header />

    <!-- Filter Buttons + Items per Page -->
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
          <option
            v-for="n in [6, 12, 24, 48]"
            :key="n"
            :value="n"
          >
            {{ n }}
          </option>
        </select>
      </div>
    </div>

    <!-- News List -->
    <div
      class="flex flex-1 min-w-[1280px] min-h-[600px] justify-start items-start px-8"
    >
      <div class="flex-1">
        <NewsList
          :filterIndex="activeIndex"
          :itemsPerPage="visibleItems"
          :newsList="displayedNews"
        />
      </div>
    </div>

    <!-- Footer -->
    <div
      v-if="isLoading"
      class="w-full h-[100px] bg-gray-100 animate-pulse"
    ></div>
    <Footer v-else />

    <!-- Add News Modal -->
    <AddNewsModal
      :show="showAddNewsModal"
      :user="user"
      @close="closeAddNewsModal"
      @save="handleSaveNews"
    />
  </div>
</template>
