<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { useNewsStore } from "@/stores/newsStore";
import { useRouter } from "vue-router";
import NewsList from "@/components/NewsList.vue";
import AsideMenu from "@/components/AsideMenu.vue";

const router = useRouter();
const newsStore = useNewsStore();

const query = ref("");
const isLoading = ref(false);
const results = ref<any[]>([]);
const user = ref(JSON.parse(localStorage.getItem("user") || "null"));

async function searchNews() {
  if (!query.value.trim()) {
    results.value = [];
    return;
  }
  try {
    isLoading.value = true;
    const res = await newsStore.searchNews(query.value);
    results.value = res || [];
  } finally {
    isLoading.value = false;
  }
}

watch(query, searchNews); // ค้นอัตโนมัติเมื่อพิมพ์

onMounted(() => {
  query.value = "";
});
</script>

<template>
  <div class="flex min-h-screen font-[Outfit] bg-white">
    <AsideMenu :user="user" />

    <div class="flex-1 ml-[80px] px-8 py-6">
      <div class="flex items-center gap-3 mb-6">
        <button
          @click="router.push('/home')"
          class="flex items-center gap-2 bg-gray-100 hover:bg-gray-300 text-black px-4 py-2 rounded-md"
        >
          <img src="@/assets/Card/Back.png" class="w-5 h-5" />
          Back to Home
        </button>

        <input
          v-model="query"
          type="text"
          placeholder="Search news title, author, or category..."
          class="flex-1 border border-gray-300 rounded-md px-4 py-2 text-[16px]"
        />
      </div>

      <div v-if="isLoading" class="text-gray-500 text-center">Searching...</div>
      <div v-else-if="results.length === 0" class="text-center text-gray-500">
        No results found.
      </div>

      <NewsList
        v-else
        :filterIndex="0"
        :itemsPerPage="results.length"
        :newsList="results"
      />
    </div>
  </div>
</template>
