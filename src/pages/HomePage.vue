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
</script>

<template>
  <div id="app" class="flex flex-col min-h-screen bg-white font-[Outfit]">
    <router-link to="/add-news" class="bg-gray-500 font-[Outfit]">
    </router-link>

    <Header />

    <div
      class="w-[125%] flex justify-between items-center gap-6 px-8 py-4 rounded-xl flex-wrap mb-[-15px]"
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
