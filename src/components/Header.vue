<template>
  <header
    class="header"
  >
    <h1 class="title">Check It Off</h1>
    <h1 class="subtitle">Social Anti-Fake News System</h1>
    <p class="description">
      Crowdsourced truth verification through community wisdom and collaboration fact-checking
    </p>

    <!-- Stats -->
    <div class="stats-container">
      <div
        v-for="(s, idx) in stats"
        :key="idx"
        class="stat-card"
      >
        <img :src="s.icon" :alt="s.label" width="48" height="48" />
        <div class="stat-value" :style="{ color: s.color }">{{ s.value }}</div>
        <div class="stat-label">{{ s.label }}</div>
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
    const res = await fetch("http://localhost:5175/api/news");
    const data = await res.json();

    const fetchedStats = data.stats ?? { total: 0, verified: 0, fake: 0, underReview: 0 };

    stats.value = [
  { ...stats.value[0], value: Number(fetchedStats.total) } as Stat,
  { ...stats.value[1], value: Number(fetchedStats.verified) } as Stat,
  { ...stats.value[2], value: Number(fetchedStats.fake) } as Stat,
  { ...stats.value[3], value: Number(fetchedStats.underReview) } as Stat,
];

  } catch (err) {
    console.error(err);
  }
});
</script>


<style scoped>
.header {
  width: 120%;
  height: 80%;
  background: linear-gradient(
    to right,
    rgba(59, 130, 246, 0.3),
    rgba(59, 130, 246, 0.3),
    rgba(134, 239, 172, 0.3),
    rgba(34, 197, 94, 0.3)
  );
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0;
  padding: 2rem;
  box-sizing: border-box;
  border-radius: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);

}

.title {
  color: #3380f2;
  font-family: "Outfit", sans-serif;
  font-size: 4.5rem;
  line-height: 1;
  margin: 0;
}

.subtitle {
  color: #22c55e;
  font-family: "Outfit", sans-serif;
  font-size: 3rem;
  line-height: 1;
  margin-top: 10px;
}

.description {
  color: #7e7e7eff;
  font-family: "Outfit", sans-serif;
  font-size: 1.25rem;
  line-height: 1;
  margin-top: -10px;
}

.stats-container {
  margin-top: 10px;
  width: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
  justify-items: center;
}

.stat-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 1rem;
  width: 200px;
  height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.stat-value {
  font-size: 2rem;
  font-weight: 600;
}

.stat-label {
  color: #4b5563;
  font-size: 1rem;
}
</style>
