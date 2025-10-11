<template>
  <div class="PageContainer">
    <div v-if="isLoading" class="LoadingScreen">
      <div class="spinner"></div>
      <p>Loading news...</p>
    </div>

    <div v-else>
      <router-link :to="`/news/${id}`" class="BackButtonContainer">
        <img src="@/assets/Card/Back.png" alt="Back Icon" class="BackIcon" />
        Back to News Detail
      </router-link>

      <div class="VoteWrapper">
        <div class="NewsCard">
          <h2 class="NewsTitle">{{ news.title }}</h2>

          <span
            class="news-status"
            :class="{
              verified: news.stats === 'Verified',
              fake: news.stats === 'Fake News',
              review:
                news.stats === 'Under Review' || news.stats === 'Unverified',
            }"
          >
            {{ news.stats }}
          </span>

          <p class="NewsDesc mt-4">{{ news.description }}</p>
          <p class="NewsMeta mt-3">By {{ news.author }} • {{ news.date }}</p>

          <div class="VotingSystem">
            <span class="Like">
              <img :src="LikeIcon" alt="Like" class="icon" />
              {{ news.upVotes }}
            </span>
            <span class="DisLike">
              <img :src="DislikeIcon" alt="DisLike" class="icon" />
              {{ news.downVotes }}
            </span>
          </div>
        </div>

        <div class="VoteForm">
          <h3 class="FormTitle">Cast Your Vote</h3>

          <label class="Label name">Your Name</label>
          <input
            v-model="form.name"
            type="text"
            placeholder="Enter your name"
            class="Input"
          />

          <label class="Label vote">Your Vote</label>
          <div class="VoteButtons">
            <button
              :class="['VoteBtn', form.vote === 'up' ? 'active real' : '']"
              @click="form.vote = 'up'"
            >
              This is Real
            </button>

            <button
              :class="['VoteBtn', form.vote === 'down' ? 'active fake' : '']"
              @click="form.vote = 'down'"
            >
              This is Fake
            </button>
          </div>

          <label class="Label comment">Your Comment *</label>
          <textarea
            v-model="form.comment"
            placeholder="Explain why you think this news is real or fake..."
            class="Textarea"
          ></textarea>

          <label class="Label mt-4">Supporting Image URL (optional)</label>
          <input
            v-model="form.imageUrl"
            type="text"
            placeholder="https://example.com/image.jpg"
            class="Input"
          />

          <button class="SubmitBtn mt-5" @click="submitVote">
            Submit Vote & Comment
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
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
  if (!form.value.comment || !form.value.vote || !form.value.name) {
    alert("⚠️ Please fill out all required fields!");
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
        name: form.value.name,
        status: form.value.vote,
        comment: form.value.comment,
        imageUrl: form.value.imageUrl || "",
      }),
    });

    const data = await res.json();

    if (res.ok) {
      alert("✅ you for your vote! Your feedback has been successfully recorded.");

      form.value = { name: "", vote: "", comment: "", imageUrl: "" };

      await fetchNewsById();
    } else {
      alert("❌ Failed to save your vote: " + data.message);
    }
  } catch (err) {
    console.error("Error submitting vote:", err);
    alert("❌ An error occurred while connecting to the server.");
  } finally {
    isSubmitting.value = false;
  }
}


</script>

<style scoped>
.PageContainer {
  width: 800px;
  height: 1000px;
  font-family: "Outfit", sans-serif;
  padding: 40px;
}

/* Back Button */
.BackButtonContainer {
  max-width: fit-content;
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: #f3f4f6;
  color: #000;
  font-size: 16px;
  text-decoration: none;
  border-radius: 8px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-left: 0;
}

.back-button {
  display: flex;
  align-items: center;
  font-size: 16px;
  line-height: 1;
  transition: all 0.3s ease;
  color: #000;
}
.BackButtonContainer:hover {
  background-color: #d1d5db;
}
.BackIcon {
  width: 20px;
  height: 20px;
  margin-right: 5px;
  vertical-align: middle;
}

/* Layout */
.VoteWrapper {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 10px;
}

/* Left Side */
.NewsCard {
  flex: 1;
  background: rgb(255, 255, 255);
  border: 1px solid #d1d5db;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  text-align: left;
}
.NewsTitle {
  font-size: 1.6rem;
  font-weight: 700;
  margin-top: -10px;
}

/* News Status Badge */
.news-status {
  display: block;
  margin: 10px auto 0;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  width: fit-content;
}
.news-status.verified {
  background-color: #d1fae5;
  color: #047857;
}
.news-status.fake {
  background-color: #fee2e2;
  color: #b91c1c;
}
.news-status.review {
  background-color: #fef9c3;
  color: #ca8a04;
}

.NewsDesc {
  color: #444;
  line-height: 1.6;
}
.NewsMeta {
  color: #666;
  font-size: 0.9rem;
}

.icon {
  width: 20px;
  height: 20px;
  vertical-align: middle;
}

/* Voting System */
.VotingSystem {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 15px;
}
.VotingSystem .Like {
  color: #15803d;
  border-radius: 6px;
  padding: 6px 12px;
  font-weight: 500;
}
.VotingSystem .DisLike {
  color: #b91c1c;
  border-radius: 6px;
  padding: 6px 12px;
  font-weight: 500;
}

/* Right Side */
.VoteForm {
  flex: 1;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  text-align: left;
}

.FormTitle {
  margin-top: -10px;
  font-size: 1.4rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.Label {
  display: block;
  font-weight: 500;
  margin-bottom: 0.3rem;
}

.Input,
.Textarea {
  font-family: "Outfit", sans-serif;
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 10px;
  font-size: 1rem;
  outline: none;
  margin-top: -5px;
}
.Input:focus,
.Textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.15);
}

.Textarea {
  min-height: 100px;
  resize: none;
}

/* Vote Buttons */
.VoteButtons {
  display: flex;
  width: 100%;
  gap: 5px;
  margin-top: -5px;
}
.VoteBtn {
  flex: 1;
  border-radius: 6px;
  padding: 10px;
  font-size: 1rem;
  background: #f3f4f6;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
}
.VoteBtn:hover {
  background: #1d4ed8;
  color: white;
}
.VoteBtn.active.real {
  background: #008235;
  color: white;
}
.VoteBtn.active.fake {
  background: #c10007;
  color: white;
}

/* Submit Button */
.SubmitBtn {
  width: 100%;
  background: #2563eb;
  color: white;
  padding: 10px;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.SubmitBtn:hover {
  background: #1d4ed8;
}

/* Loader */
.LoadingScreen {
  position: fixed;
  inset: 0;
  background-color: rgba(255, 255, 255, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  font-size: 18px;
  color: #333;
}
.spinner {
  border: 6px solid #e5e7eb;
  border-top: 6px solid #003791;
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
</style>
