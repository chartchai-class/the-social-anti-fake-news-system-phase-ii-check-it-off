<template>
  <header
    class="ml-[50px] w-[calc(120%-50px)] text-center p-6 rounded-lg shadow-md bg-gradient-to-r from-blue-200 via-blue-100 to-green-100"
  >
    <h1 class="text-7xl font-bold mt-4 text-[#3380f2] font-[Outfit]">
      Check It Off
    </h1>

    <h1 class="text-6xl font-bold mt-4 text-green-400 font-[Outfit]">
      Social Anti-Fake News System
    </h1>

    <p class="text-gray-600 mt-4 text-lg font-[Outfit]">
      Crowdsourced truth verification through community wisdom and collaboration
      fact-checking
    </p>

    <div class="container mx-auto px-4 mt-6">
      <div
        class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6 justify-items-center"
      >
        <div
          v-for="(s, idx) in stats"
          :key="idx"
          class="bg-white shadow rounded-lg border border-gray-200 w-[220px] h-[130px] flex flex-col items-center justify-center p-4 text-center font-[Outfit]"
        >
          <div class="mb-2">
            <img :src="s.icon" :alt="s.label" width="48" height="48" />
          </div>
          <div
            class="text-4xl font-semibold font-[Outfit]"
            :style="{ color: s.color }"
          >
            {{ s.value }}
          </div>

          <div class="text-gray-600">{{ s.label }}</div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import TotalNewsIcon from "@/assets/Overview/Total-News.png";
import VerifiedIcon from "@/assets/Overview/Verified.png";
import FakeNewsIcon from "@/assets/Overview/Fake-News.png";
import UnderReviewIcon from "@/assets/Overview/Under-Review.png";

interface Stat {
  label: string;
  value: number;
  icon: string;
  color: string;
}

const stats = ref<Stat[]>([
  { label: "Total News", value: 0, icon: TotalNewsIcon, color: "#2563eb" },
  { label: "Verified", value: 0, icon: VerifiedIcon, color: "#22c55e" },
  { label: "Fake News", value: 0, icon: FakeNewsIcon, color: "#ef4444" },
  { label: "Under Review", value: 0, icon: UnderReviewIcon, color: "#facc15" },
]);

onMounted(async () => {
  try {
    const res = await fetch("http://localhost:8080/api/news");
    const data = await res.json();

    const visibleNews = Array.isArray(data)
      ? data.filter((n) => n.visible === true)
      : [];

    const total = visibleNews.length;
    const verified = visibleNews.filter((n) => n.category === "Verified").length;
    const fake = visibleNews.filter((n) => n.category === "Fake News").length;
    const unverified = visibleNews.filter(
      (n) =>
        n.category === "Under Review" || n.category === "Unverified"
    ).length;

    stats.value = [
      { ...stats.value[0], value: total } as Stat,
      { ...stats.value[1], value: verified } as Stat,
      { ...stats.value[2], value: fake } as Stat,
      { ...stats.value[3], value: unverified } as Stat,
    ];
  } catch (err) {
    console.error("❌ Error fetching news stats:", err);
  }
});

</script>
