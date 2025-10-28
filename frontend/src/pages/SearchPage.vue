<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useNewsStore } from "@/stores/newsStore";
import { useRouter } from "vue-router";
import AsideMenu from "@/components/AsideMenu.vue";
import NewsList from "@/components/NewsList.vue";

const router = useRouter();
const newsStore = useNewsStore();
const user = ref(JSON.parse(localStorage.getItem("user") || "null"));

onMounted(async () => {
  if (newsStore.newsList.length === 0) {
    await newsStore.fetchAllNews();
  }
});
</script>

<template>
  <div class="flex min-h-screen font-[Outfit] bg-white">
    <AsideMenu :user="user" />

    <div class="flex-1 px-8 py-6">
      <div class="flex items-center gap-3">
        <button
          @click="router.push('/home')"
          class="flex items-center bg-gray-100 hover:bg-gray-300 text-black px-4 py-2 rounded-md -ml-[60px]"
        >
          <img src="@/assets/Card/Back.png" class="w-5 h-5" />
          Back to Home Page
        </button>
      </div>

      <div v-if="newsStore.isLoading" class="text-gray-500 text-center mt-10">
        Loading news...
      </div>

      <div
        v-else-if="!newsStore.isLoading && newsStore.newsList.length === 0"
        class="text-center text-gray-500 mt-10"
      >
        No news found.
      </div>

      <NewsList
        v-else
        :filterIndex="0"
        :itemsPerPage="newsStore.newsList.length"
        :newsList="newsStore.newsList"
        :hideSearchButton="true"
      />
    </div>
  </div>
</template>
