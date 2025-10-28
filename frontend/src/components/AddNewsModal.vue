<script setup lang="ts">
import { watch, reactive } from "vue";

interface NewsItem {
  title: string;
  author: string;
  date: string;
  image: string;
  description: string;
  fullDescription: string;
}

const props = defineProps<{
  show: boolean;
  user: { name: string; surname?: string } | null;
}>();

const emit = defineEmits(["close", "save"]);

const newNews = reactive<NewsItem>({
  title: "",
  author: "",
  date: "",
  image: "",
  description: "",
  fullDescription: "",
});

watch(
  () => props.show,
  (newVal) => {
    if (newVal && props.user) {
      newNews.author = `${props.user.name} ${props.user.surname || ""}`.trim();

      const today = new Date();
      const yyyy = today.getFullYear();
      const mm = String(today.getMonth() + 1).padStart(2, "0");
      const dd = String(today.getDate()).padStart(2, "0");
      newNews.date = `${yyyy}-${mm}-${dd}`;
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
    fullDescription: "",
  });
}

const errors = reactive({
  title: "",
  image: "",
  description: "",
  fullDescription: "",
});

async function saveNews() {
  errors.title = "";
  errors.image = "";
  errors.description = "";
  errors.fullDescription = "";

  let hasError = false;
  if (!newNews.title) {
    errors.title = "Please enter the news title.";
    hasError = true;
  }
  if (!newNews.image) {
    errors.image = "Please provide an image URL.";
    hasError = true;
  }
  if (!newNews.description) {
    errors.description = "Short description is required.";
    hasError = true;
  }
  if (!newNews.fullDescription) {
    errors.fullDescription = "Full description cannot be empty.";
    hasError = true;
  }

  if (hasError) return;

  try {
    const res = await fetch("http://localhost:8080/api/news", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        title: newNews.title,
        author: newNews.author,
        date: newNews.date,
        image: newNews.image,
        description: newNews.description,
        fullDescription: newNews.fullDescription,
        category: "Unverified",
        upVotes: 0,
        downVotes: 0,
        commentsCount: 0,
      }),
    });

    if (!res.ok) throw new Error("Failed to add news");

    alert("News added successfully!");
    emit("save", { ...newNews });
    closeModal();
  } catch (error) {
    console.error("Error:", error);
    alert("Failed to add news.");
  }
}

function clearForm() {
  Object.assign(newNews, {
    title: "",
    image: "",
    description: "",
    fullDescription: "",
  });
}
</script>

<template>
  <div
    v-if="props.show"
    class="fixed inset-0 bg-black/40 flex items-center justify-center z-[9999]"
    @click.self="closeModal"
  >
    <div
      class="bg-white w-[650px] rounded-2xl shadow-xl p-8 font-[Outfit] animate-fade-in"
    >
      <h2 class="text-3xl font-bold text-gray-800 text-center">
        Add News Article
      </h2>

      <div class="space-y-1 text-left">
        <!-- Title -->
        <div>
          <label class="block text-xl text-gray-700 font-semibold">Title</label>
          <input
            v-model="newNews.title"
            @input="errors.title = ''"
            type="text"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
            placeholder="Enter news title"
          />
          <p v-if="errors.title" class="text-red-600 text-sm mt-1">
            {{ errors.title }}
          </p>
        </div>

        <!-- Author -->
        <div>
          <label class="block text-xl text-gray-700 font-semibold mt-2"
            >Author</label
          >
          <input
            v-model="newNews.author"
            type="text"
            readonly
            class="w-full border border-gray-200 bg-gray-100 text-gray-600 rounded-md px-3 py-2 cursor-not-allowed select-none"
            placeholder="Author name"
          />
        </div>

        <!-- Date -->
        <div>
          <label class="block text-xl text-gray-700 font-semibold mt-2"
            >Date</label
          >
          <input
            v-model="newNews.date"
            type="date"
            readonly
            class="w-full border border-gray-200 bg-gray-100 text-gray-600 rounded-md px-3 py-2 cursor-not-allowed text-center select-none"
          />
        </div>

        <!-- Image -->
        <div>
          <label class="block text-xl text-gray-700 font-semibold mt-2"
            >Image URL</label
          >
          <input
            v-model="newNews.image"
            @input="errors.image = ''"
            type="text"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
            placeholder="Paste image link"
          />
          <p v-if="errors.image" class="text-red-600 text-sm mt-1">
            {{ errors.image }}
          </p>
        </div>

        <!-- Short Description -->
        <div>
          <label class="block text-xl text-gray-700 font-semibold mt-2"
            >Short Description</label
          >
          <textarea
            v-model="newNews.description"
            @input="errors.description = ''"
            rows="2"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 resize-none"
            placeholder="Enter short summary..."
          ></textarea>
          <p v-if="errors.description" class="text-red-600 text-sm mt-1">
            {{ errors.description }}
          </p>
        </div>

        <!-- Full Description -->
        <div>
          <label class="block text-xl text-gray-700 font-semibold"
            >Full Description</label
          >
          <textarea
            v-model="newNews.fullDescription"
            @input="errors.fullDescription = ''"
            rows="7"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 resize-none"
            placeholder="Enter detailed description..."
          ></textarea>
          <p v-if="errors.fullDescription" class="text-red-600 text-sm mt-1">
            {{ errors.fullDescription }}
          </p>
        </div>
      </div>

      <!-- Buttons -->
      <div class="flex justify-between items-center mt-8">
        <!-- Left: Clear -->
        <button
          @click="clearForm"
          class="px-6 py-2 bg-red-700 hover:bg-red-800 text-white rounded-md transition"
        >
          Clear
        </button>
        <!-- Right: Cancel + Save -->
        <div class="flex gap-4">
          <button
            @click="closeModal"
            class="px-6 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-md transition"
          >
            Cancel
          </button>
          <button
            @click="saveNews"
            class="px-6 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-md transition"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
