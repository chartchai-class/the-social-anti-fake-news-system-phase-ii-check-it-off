<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";

const user = ref<{ name: string; surname?: string; access?: string } | null>(
  null
);
const router = useRouter();

onMounted(async () => {
  const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
  if (storedUser?.email) {
    const res = await fetch(
      `http://localhost/checkitoff/api/get_user.php?email=${storedUser.email}`
    );
    const data = await res.json();
    if (data.success) {
      user.value = data.user;
      console.log("Loaded user:", user.value);
    } else {
      console.error("Error fetching user:", data.message);
    }
  }
});

const firstLetter = computed(() =>
  user.value?.name ? user.value.name.charAt(0).toUpperCase() : "?"
);

const accessColor = computed(() => {
  const role = user.value?.access?.toUpperCase() || "";
  if (role.includes("ADMIN")) return "bg-[#D70000] border-none";
  if (role.includes("MEMBER")) return "bg-[#27809A] border-none";
  if (role.includes("READER")) return "bg-[#FFC800] border-none";
  return "bg-gray-300";
});

const isAdmin = computed(() =>
  user.value?.access?.toUpperCase().includes("ADMIN")
);
const isMember = computed(() =>
  user.value?.access?.toUpperCase().includes("MEMBER")
);

function confirmLogout() {
  localStorage.removeItem("user");
  window.location.href = "/login";
}

function formatRole(role?: string) {
  if (!role) return "Unknown";
  return role.replace(/^ROLE_/, "");
}

const adminButtons = [
  {
    label: "Del News",
    title: "Delete existing news",
    icon: new URL("@/assets/Aside/delete-news.png", import.meta.url).href,
    colorClass: "bg-[#3A75A2] text-[#6B2E2E] hover:scale-110 transition-transform duration-200",
    action: () => router.push("/admin/delete-news"),
  },
  {
    label: "Del User",
    title: "Delete a user account",
    icon: new URL("@/assets/Aside/delete-user.png", import.meta.url).href,
    colorClass: "bg-[#5EA1C5] text-[#6B2E2E] hover:scale-110 transition-transform duration-200",
    action: () => router.push("/admin/delete-user"),
  },
  {
    label: "Del Comment",
    title: "Delete comment",
    icon: new URL("@/assets/Aside/delete-comment.png", import.meta.url).href,
    colorClass: "bg-[#94CDE6] text-[#6B2E2E] hover:scale-110 transition-transform duration-200",
    action: () => router.push("/admin/delete-comment"),
  },
  {
    label: "Change Role",
    title: "Change user role",
    icon: new URL("@/assets/Aside/change-user-role.png", import.meta.url).href,
    colorClass: "bg-[#C6E6F1] text-[#6B2E2E] hover:scale-110 transition-transform duration-200",
    action: () => router.push("/admin/change-role"),
  },
];

const showLogoutModal = ref(false);

function openLogoutModal() {
  showLogoutModal.value = true;
}
function cancelLogout() {
  showLogoutModal.value = false;
}
</script>

<template>
  <aside
    v-if="user"
    class="fixed top-0 left-0 w-[60px] h-full flex flex-col items-center justify-between py-6 border-r border-gray-200 bg-white"
  >
    <div class="flex flex-col items-center space-y-4">
      <div
        class="w-10 h-10 rounded-full bg-[#5AC5F0] text-white flex items-center justify-center text-xl font-bold"
      >
        {{ firstLetter }}
      </div>

      <div
        :class="[
          'w-10 h-10 rounded-full border border-gray-300 -mt-1',
          accessColor,
        ]"
      ></div>
      <p
        class="text-[11px] font-semibold text-gray-600 text-center w-[60px] -mt-3"
      >
        {{ formatRole(user.access) }}
      </p>

      <!-- Add News -->
      <div
        v-if="isAdmin || isMember"
        class="flex flex-col items-center space-y-1 mt-20"
      >
        <button
          class="w-10 h-10 rounded-full overflow-hidden shadow-md transition-transform duration-200 flex items-center justify-center bg-[#134573] hover:scale-110 transition-transform duration-200"
          title="Add News"
          @click="$emit('openAddNews')"
        >
          <img
            src="@/assets/Aside/add-news.png"
            alt="Add News Icon"
            class="w-7 h-7 object-contain"
          />
        </button>
        <p class="text-[12px] font-semibold text-center w-[60px] leading-tight">
          Add News
        </p>
      </div>

      <!-- Admin Buttons -->
      <div v-if="isAdmin" class="flex flex-col items-center space-y-2">
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

    <!-- Logout -->
    <button
      @click="openLogoutModal"
      class="flex flex-col items-center space-y-1 text-gray-500 hover:text-red-500 transition-all duration-300"
    >
      <img
        src="@/assets/Aside/logout-icon.png"
        alt="Logout"
        class="w-7 h-7 opacity-80 hover:opacity-100"
      />
      <span class="text-[11px] font-semibold">Logout</span>
    </button>

    <!-- Logout Modal -->
    <!-- Logout Modal -->
    <teleport to="body">
      <div
        v-if="showLogoutModal"
        class="fixed inset-0 bg-gray-400/70 backdrop-blur-[2px] flex items-center justify-center z-[99999]"
      >
        <div
          class="bg-white rounded-2xl shadow-2xl w-[350px] p-6 text-center font-[Outfit]"
        >
          <h2 class="text-xl font-semibold text-gray-800 mb-3">
            Confirm Logout
          </h2>
          <p class="text-gray-600 mb-6">
            Are you sure you want to log out of your account?
          </p>

          <div class="flex justify-center gap-4">
            <button
              @click="cancelLogout"
              class="px-5 py-2 rounded-md bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold transition-all duration-200"
            >
              Cancel
            </button>

            <button
              @click="confirmLogout"
              class="px-5 py-2 rounded-md bg-red-500 hover:bg-red-600 text-white font-semibold transition-all duration-200"
            >
              Log Out
            </button>
          </div>
        </div>
      </div>
    </teleport>
  </aside>
</template>
