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

const displayedNews = computed(() => {
  return filteredNews.value.slice(0, itemsPerPage.value);
});

watch(itemsPerPage, (newVal) => {
  if (newVal % 6 !== 0) {
    itemsPerPage.value = 6;
  }
});

function handleItemsChange() {
  const current = itemsPerPage.value;
  console.log(`User selected: ${current} items per page`);
}
</script>

<template>
  <div id="app" class="page-container">
    <!-- Header -->
    <Header />

    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="button-group">
        <button
          v-for="(btn, index) in buttons"
          :key="index"
          :class="[
            'btn',
            activeIndex === index ? 'btn-active' : 'btn-inactive',
          ]"
          @click="setActive(index)"
          :disabled="activeIndex === index"
        >
          {{ btn }}
        </button>
      </div>

      <div class="items-per-page">
        <span>Items per page:</span>
        <select
          v-model.number="itemsPerPage"
          class="select"
          @change="handleItemsChange"
        >
          <option v-for="n in [6, 12, 24]" :key="n" :value="n">{{ n }}</option>
        </select>
      </div>
    </div>

    <!-- ✅ Section ข่าว -->
    <div class="news-section">
      <!-- Loading -->
      <div v-if="isLoading" class="loader-container">
        <div class="spinner"></div>
        <p>Loading news...</p>
      </div>

      <!-- แสดงข่าว -->
      <div v-else>
        <NewsList
          :filterIndex="activeIndex"
          :itemsPerPage="itemsPerPage"
          :newsList="displayedNews"
        />
      </div>
    </div>

    <Footer />
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: white;
  font-family: "Outfit", sans-serif;
}

/* ให้ filter bar มีระยะห่างแน่นอน */
.filter-bar {
  width: 120%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  padding: 1rem 2rem;
  border-radius: 12px;
  flex-wrap: wrap;
  margin-bottom: -15px;
}

/* ส่วนข่าว */
.news-section {
  flex: 1;
  min-height: 600px; /* ✅ กันพื้นที่คงที่ไว้ ไม่ให้ Header ขยับ */
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Loader */
.loader-container {
  text-align: center;
  color: #555;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.spinner {
  border: 6px solid #e5e7eb;
  border-top: 6px solid #2563eb;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* ปุ่มกรอง */
.button-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.btn {
  width: 150px;
  height: 40px;
  font-size: 1rem;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  transition: background-color 0.3s;
}
.btn-active {
  background-color: #2b7fff;
  color: white;
}
.btn-inactive {
  background-color: #f3f4f6;
  color: black;
}
.btn:disabled {
  cursor: default;
  opacity: 0.8;
}
.items-per-page {
  display: flex;
  align-items: center;
  gap: 8px;
}
.select {
  padding: 4px 8px;
  border-radius: 6px;
  border: 1px solid #d1d5db;
  font-size: 1rem;
}
</style>
