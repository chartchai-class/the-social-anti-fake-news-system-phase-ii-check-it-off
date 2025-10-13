<template>
  <div class="w-[900px] min-h-[1000px] font-[Outfit] p-10 mx-auto">
    <router-link
      :to="`/news/${id}`"
      class="flex items-center gap-[6px] bg-gray-100 text-black text-[16px] rounded-[8px] px-[20px] py-[10px] max-w-fit cursor-pointer transition-colors duration-300 ease-in-out hover:bg-gray-300 hover:text-black no-underline"
    >
      <img
        src="@/assets/Card/Back.png"
        alt="Back Icon"
        class="w-[20px] h-[20px] mr-[5px] align-middle"
      />
      Back to News Detail
    </router-link>

    <div
      class="h-[170px] bg-white rounded-xl border border-[#d6d6d6] shadow-[0_3px_8px_rgba(0,0,0,0.08)] px-6 py-5 flex justify-between items-center w-full mt-2.5"
    >
      <div class="flex flex-col justify-center gap-[10px]">
        <h2 class="font-bold text-[25px] text-[#111827] m-0 p-0 mb-3">
          Government Launches New Education Reform Policy
        </h2>
        <div class="VoteInfo flex gap-6 text-[19px]">
          <!-- Up Vote -->
          <div class="flex items-center gap-2">
            <img :src="LikeIcon" alt="Like" class="w-7 h-7" />
            <span class="text-gray-800 font-medium"
              >{{ news.upVotes }} Up Votes</span
            >
          </div>

          <!-- Down Vote -->
          <div class="flex items-center gap-2">
            <img :src="DislikeIcon" alt="Dislike" class="w-7 h-7" />
            <span class="text-gray-800 font-medium"
              >{{ news.downVotes }} Down Votes</span
            >
          </div>

          <!-- Comment -->
          <div class="flex items-center gap-2">
            <img :src="CommentIcon" alt="Comment" class="w-7 h-7" />
            <span class="text-gray-800 font-medium"
              >{{ news.comments }} Total comments</span
            >
          </div>
        </div>
      </div>
      <router-link
        :to="{ name: 'Vote', params: { id } }"
        class="bg-blue-500 text-white rounded-md px-5 py-2.5 text-[15px] font-medium cursor-pointer transition-all duration-300 ease-in-out hover:bg-blue-600"
      >
        Add Your Vote
      </router-link>
    </div>
    <div class="w-[815px] my-10 mx-auto font-[Outfit]">
      <h1 class="text-[25px] font-bold mb-6 text-[#111827] text-left">
        Comments & Votes
      </h1>

      <div v-if="isLoadingComments">Loading comments...</div>

      <div
        v-else-if="comments.length === 0"
        class="text-center text-[#666] italic mt-[10px]"
      >
        No comments yet.
      </div>

      <div v-else>
        <div
          v-for="c in comments"
          :key="c.name"
          class="flex flex-col items-start justify-start text-left gap-4 bg-white rounded-xl py-5 w-full relative after:content-[''] after:block after:h-px after:bg-gray-300 after:w-full last:after:hidden"
        >
          <!-- ชื่อผู้เขียน + แท็กสถานะ -->
          <div class="flex items-center gap-3 w-full">
            <span
              class="font-[Outfit] font-semibold text-[18px] text-[#111827]"
            >
              {{ c.name }}
            </span>

            <span
              v-if="c.status === 'Real'"
              class="text-green-600 bg-green-100 px-2 py-[2px] rounded-md text-sm font-medium"
            >
              Real
            </span>
            <span
              v-else-if="c.status === 'Fake'"
              class="text-red-600 bg-red-100 px-2 py-[2px] rounded-md text-sm font-medium"
            >
              Fake
            </span>
            <span
              v-else
              class="text-yellow-600 bg-yellow-100 px-2 py-[2px] rounded-md text-sm font-medium"
            >
              Review
            </span>
          </div>

          <!-- ข้อความคอมเมนต์ -->
          <p
            class="font-[Outfit] text-gray-700 text-[16px] leading-relaxed w-full"
          >
            {{ c.comment }}
          </p>

          <!-- รูปภาพ (ถ้ามี) -->
          <img
            v-if="c.imageUrl"
            :src="c.imageUrl"
            alt="Comment Image"
            class="w-full max-w-[400px] rounded-lg object-cover"
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