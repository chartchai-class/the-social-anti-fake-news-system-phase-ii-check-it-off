<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import LikeIcon from "@/assets/Card/Like.png";
import DislikeIcon from "@/assets/Card/Dislike.png";
import CommentIcon from "@/assets/Card/Comment.png";

const router = useRouter();

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

//  Sidebar logic
interface User {
  name: string;
  email: string;
  surname?: string;
  access?: string;
}

const user = ref<User | null>(null);

onMounted(() => {
  const savedUser = localStorage.getItem("user");
  if (savedUser) {
    user.value = JSON.parse(savedUser);
  }
});

const firstLetter = computed(() =>
  user.value?.name ? user.value.name.charAt(0).toUpperCase() : "?"
);

const accessColor = computed(() => {
  const access = user.value?.access?.toLowerCase() || "";
  if (access.includes("admin") || access.includes("full"))
    return "bg-[#D70000] border-none";
  if (access.includes("member")) return "bg-[#27809A] border-none";
  if (access.includes("reader")) return "bg-[#FFC800] border-none";
  return "bg-gray-300";
});

const adminButtons = [
  {
    label: "Del News",
    title: "Delete existing news",
    icon: new URL("@/assets/Aside/delete-news.png", import.meta.url).href,
    colorClass: "bg-[#5AC5F0]",
    action: () => router.push("/admin/delete-news"),
  },
  {
    label: "Del User",
    title: "Delete user",
    icon: new URL("@/assets/Aside/delete-user.png", import.meta.url).href,
    colorClass: "bg-[#D70000]",
    action: () => router.push("/admin/delete-user"),
  },
  {
    label: "Del Comment",
    title: "Delete comment",
    icon: new URL("@/assets/Aside/delete-comment.png", import.meta.url).href,
    colorClass: "bg-[#FF7801]",
    action: () => router.push("/admin/delete-comment"),
  },
  {
    label: "Change Role",
    title: "Change role",
    icon: new URL("@/assets/Aside/change-user-role.png", import.meta.url).href,
    colorClass: "bg-[#FFC800]",
    action: () => router.push("/admin/change-role"),
  },
];

//  Add News Modal Logic
const showAddNewsModal = ref(false);
const newNews = ref({
  title: "",
  author: "",
  date: "",
  image: "",
  description: "",
});

function openAddNewsModal() {
  showAddNewsModal.value = true;
  newNews.value.author = user.value
    ? `${user.value.name} ${user.value.surname || ""}`.trim()
    : "";
}

function closeAddNewsModal() {
  showAddNewsModal.value = false;
  Object.assign(newNews.value, {
    title: "",
    author: "",
    date: "",
    image: "",
    description: "",
  });
}

function saveNews() {
  if (!newNews.value.title || !newNews.value.author) {
    alert("Please fill in at least Title and Author.");
    return;
  }

  console.log("📰 New News Added:", newNews.value);
  alert(" News added successfully!");
  closeAddNewsModal();
}
</script>

<template>
  <div class="w-[900px] min-h-[1000px] font-[Outfit] p-10 mx-auto">
    <!--  Sidebar -->
    <aside
      class="fixed top-0 left-0 w-[60px] h-full z-20 flex flex-col items-center justify-between py-6 border-r border-gray-200 bg-white"
    >
      <div class="flex flex-col items-center space-y-4">
        <!-- Avatar -->
        <div
          class="w-10 h-10 rounded-full bg-[#5AC5F0] text-white flex items-center justify-center text-xl font-bold"
          :title="user?.name"
        >
          {{ firstLetter }}
        </div>

        <!-- Access Circle -->
        <div
          :class="[ 'w-10 h-10 rounded-full border border-gray-300 -mt-1', accessColor ]"
        ></div>

        <!-- Access Label -->
        <p
          class="text-[11px] font-semibold text-gray-600 text-center w-[60px] leading-tight break-words -mt-3"
        >
          {{ user?.access || 'Unknown' }}
        </p>

        <!--  Add News Button -->
        <div
          v-if="
            user?.access?.toLowerCase().includes('member') ||
            user?.access?.toLowerCase().includes('admin')
          "
          class="flex flex-col items-center space-y-1 mt-20"
        >
          <button
            @click="openAddNewsModal"
            class="w-10 h-10 rounded-full overflow-hidden shadow-md hover:scale-110 transition-transform duration-200 flex items-center justify-center bg-[#00005F]"
            title="Add News"
          >
            <img
              src="@/assets/Aside/add-news.png"
              alt="Add News"
              class="w-7 h-7 object-contain"
            />
          </button>
          <p class="text-[12px] font-semibold text-center w-[60px] leading-tight">
            Add News
          </p>
        </div>

        <!--  Admin -->
        <div
          v-if="user?.access?.toLowerCase().includes('admin')"
          class="flex flex-col items-center space-y-2 mt-20"
        >
          <div
            v-for="btn in adminButtons"
            :key="btn.label"
            class="flex flex-col items-center"
          >
            <button
              :title="btn.title"
              @click="btn.action"
              :class="[
                'w-10 h-10 rounded-full flex items-center justify-center text-white text-lg font-bold shadow-md hover:scale-110 transition-transform duration-200',
                btn.colorClass,
              ]"
            >
              <img
                :src="btn.icon"
                :alt="btn.label"
                class="w-6 h-6 object-contain"
              />
            </button>
            <p
              class="text-[11px] font-semibold text-gray-600 text-center w-[60px] leading-tight mt-2"
            >
              {{ btn.label }}
            </p>
          </div>
        </div>
      </div>
    </aside>

    <!--  Back button -->
    <router-link
      :to="`/news/${id}`"
      class="flex items-center gap-[6px] bg-gray-100 text-black text-[16px] rounded-[8px] px-[20px] py-[10px] max-w-fit cursor-pointer transition-colors duration-300 ease-in-out hover:bg-gray-300 hover:text-black no-underline ml-[80px]"
    >
      <img
        src="@/assets/Card/Back.png"
        alt="Back Icon"
        class="w-[20px] h-[20px] mr-[5px] align-middle"
      />
      Back to News Detail
    </router-link>

    <!--  News Stats -->
    <div
      class="h-[170px] bg-white rounded-xl border border-[#d6d6d6] shadow-[0_3px_8px_rgba(0,0,0,0.08)] px-6 py-5 flex justify-between items-center w-full mt-2.5 ml-[80px]"
    >
      <div class="flex flex-col justify-center gap-[10px]">
        <h2 class="font-bold text-[25px] text-[#111827] m-0 p-0 mb-3">
          Government Launches New Education Reform Policy
        </h2>
        <div class="VoteInfo flex gap-6 text-[19px]">
          <div class="flex items-center gap-2">
            <img :src="LikeIcon" alt="Like" class="w-7 h-7" />
            <span class="text-gray-800 font-medium"
              >{{ news.upVotes }} Up Votes</span
            >
          </div>

          <div class="flex items-center gap-2">
            <img :src="DislikeIcon" alt="Dislike" class="w-7 h-7" />
            <span class="text-gray-800 font-medium"
              >{{ news.downVotes }} Down Votes</span
            >
          </div>

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

    <!--  Comment Section -->
    <div class="w-[815px] my-10 mx-auto font-[Outfit] ml-[80px]">
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
          <div class="flex items-center gap-3 w-full">
            <span class="font-semibold text-[18px] text-[#111827]">
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

          <p class="text-gray-700 text-[16px] leading-relaxed w-full">
            {{ c.comment }}
          </p>

          <img
            v-if="c.imageUrl"
            :src="c.imageUrl"
            alt="Comment Image"
            class="w-full max-w-[400px] rounded-lg object-cover"
          />
        </div>
      </div>
    </div>

    <!--  Add News Modal -->
    <div
      v-if="showAddNewsModal"
      class="fixed inset-0 bg-black/40 flex items-center justify-center z-[9999]"
      @click.self="closeAddNewsModal"
    >
      <div
        class="bg-white w-[600px] h-[800px] rounded-2xl shadow-xl p-6 font-[Outfit] animate-fade-in overflow-y-auto"
      >
        <h2 class="text-3xl font-bold text-gray-800 mb-4 text-center">
          Add News Article
        </h2>

        <div class="space-y-3">
          <div>
            <label class="block text-xl text-gray-700 font-semibold mb-1"
              >Title</label
            >
            <input
              v-model="newNews.title"
              type="text"
              class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              placeholder="Enter news title"
            />
          </div>

          <div>
            <label class="block text-xl text-gray-700 font-semibold mb-1"
              >Author</label
            >
            <input
              v-model="newNews.author"
              type="text"
              readonly
              class="w-full border border-gray-200 bg-gray-100 text-gray-600 rounded-md px-3 py-2 focus:outline-none cursor-not-allowed select-none"
            />
          </div>

          <div class="flex flex-col items-center">
            <label
              class="block text-gray-700 font-semibold mb-1 text-center text-xl"
              >Date</label
            >
            <input
              v-model="newNews.date"
              type="date"
              class="w-[250px] border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-center"
            />
          </div>

          <div>
            <label class="block text-gray-700 font-semibold mb-1 text-xl"
              >Image URL</label
            >
            <input
              v-model="newNews.image"
              type="text"
              class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              placeholder="Paste image link"
            />
          </div>

          <div>
            <label class="block text-gray-700 font-semibold mb-1 text-xl"
              >Full Description</label
            >
            <textarea
              v-model="newNews.description"
              rows="4"
              class="w-full h-[250px] border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 resize-none"
              placeholder="Enter detailed description..."
            ></textarea>
          </div>
        </div>

        <div class="flex justify-end gap-4 mt-6">
          <button
            @click="closeAddNewsModal"
            class="px-5 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-md transition"
          >
            Cancel
          </button>
          <button
            @click="saveNews"
            class="px-5 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-md transition"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
