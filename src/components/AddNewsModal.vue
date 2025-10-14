<script setup lang="ts">
import { ref, watch, reactive } from "vue";

interface NewsItem {
  title: string;
  author: string;
  date: string;
  image: string;
  description: string;
}

// Props รับค่าจาก parent
const props = defineProps<{
  show: boolean;
  user: { name: string; surname?: string } | null;
}>();

// Emits event กลับไปให้ parent
const emit = defineEmits(["close", "save"]);

const newNews = reactive<NewsItem>({
  title: "",
  author: "",
  date: "",
  image: "",
  description: "",
});

// เมื่อ modal เปิด ให้ตั้ง author อัตโนมัติ
watch(
  () => props.show,
  (newVal) => {
    if (newVal && props.user) {
      newNews.author = `${props.user.name} ${props.user.surname || ""}`.trim();
    }
  }
);

function closeModal() {
  emit("close");
  Object.assign(newNews, {
    title: "",
    author: "",
    date: "",
    image: "",
    description: "",
  });
}

function saveNews() {
  if (!newNews.title || !newNews.author) {
    alert("Please fill in at least Title and Author.");
    return;
  }
  emit("save", { ...newNews });
  closeModal();
}


</script>

<template>
  <div
    v-if="props.show"
    class="fixed inset-0 bg-black/40 flex items-center justify-center z-[9999]"
    @click.self="closeModal"
  >
    <div
      class="bg-white w-[600px] h-[800px] rounded-2xl shadow-xl p-6 font-[Outfit] animate-fade-in"
    >
      <h2 class="text-3xl font-bold text-gray-800 mb-4 text-center">
        Add News Article
      </h2>

      <div class="space-y-3">
        <div>
          <label
            class="block text-xl text-gray-700 font-semibold mb-1 font-weight-500"
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
            placeholder="Author name"
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
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-center"
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
          @click="closeModal"
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
</template>
