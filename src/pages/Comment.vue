<template>
  <div class="PageContainer">
    <router-link
      :to="{ name: 'NewsDetail', params: { id } }"
      class="BackButtonContainer"
    >
      <img src="@/assets/Card/Back.png" alt="Back Icon" class="BackIcon" />
      Back to News Details
    </router-link>

    <div class="NewsCard">
      <div class="LeftSection">
        <h2 class="NewsTitle">
          Government Launches New Education Reform Policy
        </h2>
        <div class="VoteInfo">
          <div class="vote-item">
            <span class="Like">
              <img :src="LikeIcon" alt="Like" class="icon" />
              {{ news.upVotes }} Up Votes
            </span>
          </div>
          <div class="vote-item">
            <span class="DisLike">
              <img :src="DislikeIcon" alt="DisLike" class="icon" />
              {{ news.downVotes }} Down Votes
            </span>
          </div>
          <div class="vote-item">
            <span class="Comment">
              <img :src="CommentIcon" alt="Comment" class="icon" />
              {{ news.comments }} Total comments
            </span>
          </div>
        </div>
      </div>
      <router-link :to="{ name: 'Vote', params: { id } }" class="AddVoteBtn">
        Add Your Vote
      </router-link>
    </div>
    <div class="CommentsSection">
      <h1 class="CommentHeader">Comments & Votes</h1>

      <div v-if="isLoadingComments">Loading comments...</div>

      <div v-else-if="comments.length === 0" class="NoComments">
        No comments yet.
      </div>

      <div v-else>
        <div v-for="c in comments" :key="c.name" class="CommentCard">
          <div class="CommentAuthor">
            <span class="AuthorName">{{ c.name }}</span>
            <span v-if="c.status === 'Real'" class="TagReal"> Real </span>
            <span v-else-if="c.status === 'Fake'" class="TagFake"> Fake </span>
            <span v-else class="TagReview"> Review </span>
          </div>

          <p class="CommentText">{{ c.comment }}</p>

          <img
            v-if="c.imageUrl"
            :src="c.imageUrl"
            alt="Comment Image"
            class="CommentImage"
          />
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
import CommentIcon from "@/assets/Card/Comment.png";

interface NewsItem {
  id: number;
  upVotes: number;
  downVotes: number;
  comments: number;
}

interface CommentItem {
  id: string;
  name: string;
  status: string;
  comment: string;
  imageUrl?: string;
}

const route = useRoute();
const id = Number(route.params.id);

const isLoading = ref(true);
const news = ref<NewsItem>({
  id,
  upVotes: 0,
  downVotes: 0,
  comments: 0,
});

const comments = ref<CommentItem[]>([]);
const isLoadingComments = ref(true);

onMounted(async () => {
  try {
    const res = await fetch("http://localhost:5175/api/news");
    if (!res.ok) throw new Error("Failed to fetch news");
    const data = await res.json();
    const selected = data.news.find((n: any) => Number(n.id) === id);

    if (selected) {
      news.value = {
        id: selected.id,
        upVotes: selected.upVotes || 0,
        downVotes: selected.downVotes || 0,
        comments: selected.comments || 0,
      };
    } else {
      console.warn("News not found for ID:", id);
    }

    const res2 = await fetch("http://localhost:5175/api/comments");
    if (!res2.ok) throw new Error("Failed to fetch comments");
    const data2 = await res2.json();
    comments.value = data2.comments.filter(
      (c: CommentItem) => Number(c.id) === id
    );
  } catch (err) {
    console.error(err);
  } finally {
    isLoading.value = false;
    isLoadingComments.value = false;
  }
});
</script>

<style scoped>
.PageContainer {
  width: 800px;
  min-height: 1000px;
  font-family: "Outfit", sans-serif;
  padding: 40px;
  margin: 0 auto;
}
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
  text-decoration: none;
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
.NewsCard {
  height: 110px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #d6d6d6;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.08);
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
   margin-top: 10px;
}
.LeftSection {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}
.NewsTitle {
  font-weight: 700;
  font-size: 25px;
  color: #111827;
  margin: 0;
  padding: 0;
}
.VoteInfo {
  display: flex;
  align-items: center;
  gap: 20px;
  margin: 0;
  padding: 0;
}
.vote-item {
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #333;
}
.vote-icon {
  width: 22px;
  height: 22px;
}
.AddVoteBtn {
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px 20px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}
.AddVoteBtn:hover {
  background-color: #1d4ed8;
}
.CommentsSection {
  width: 850px;
  margin: 40px auto;
  font-family: "Outfit", sans-serif;
}
.CommentHeader {
  font-size: 25px;
  font-weight: 700;
  margin-bottom: 24px;
  color: #111827;
  text-align: left;
}
.CommentCard {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  text-align: left;
  gap: 16px;
  background: #fff;
  border-radius: 12px;
  padding: 20px 0 20px 0;
  width: 100%;
}
.CommentAuthor {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 0;
}
.AuthorName {
  font-weight: 700;
  font-size: 18px;
  color: #111827;
  margin-left: 20px;
}
.TagReal {
  background-color: #d1fae5;
  color: #047857;
  font-weight: 600;
  font-size: 14px;
  padding: 4px 10px;
  border-radius: 6px;
}
.CommentImage {
  width: 350px;
  height: auto;
  border-radius: 12px;
  object-fit: cover;
  margin-left: 20px;
}
.CommentText {
  font-size: 16px;
  line-height: 1.7;
  color: #111;
  margin: 0;
  text-align: left;
  margin-left: 20px;
  margin-right: 20px;
}

.icon {
  width: 20px;
  height: 20px;
  vertical-align: middle;
}
.TagFake {
  background-color: #fee2e2;
  color: #b91c1c;
  font-weight: 600;
  font-size: 14px;
  padding: 4px 10px;
  border-radius: 6px;
}

.TagReview {
  background-color: #fef3c7;
  color: #92400e;
  font-weight: 600;
  font-size: 14px;
  padding: 4px 10px;
  border-radius: 6px;
}

.NoComments {
  text-align: center;
  color: #666;
  font-style: italic;
  margin-top: 10px;
}

.CommentCard:not(:last-child)::after {
  content: "";
  display: block;
  height: 1px;
  background-color: #d1d5db;
  width: 100%;
  margin-top: 16px;
}

</style>
