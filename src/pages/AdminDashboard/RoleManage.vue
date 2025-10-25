<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import axios from "axios";

interface UserItem {
  id: number;
  name: string;
  surname?: string;
  email: string;
  role: string;
  visible?: boolean;
}

const isLoading = ref(true);
const users = ref<UserItem[]>([]);
const selectedUser = ref<UserItem | null>(null);
const showDetailModal = ref(false);

const sortOrderRole = ref<"asc" | "desc">("asc");
const sortOrderId = ref<"asc" | "desc">("asc");

function sortByRole() {
  sortOrderRole.value = sortOrderRole.value === "asc" ? "desc" : "asc";
  users.value.sort((a, b) => {
    const roleA = a.role ?? "";
    const roleB = b.role ?? "";
    if (roleA < roleB) return sortOrderRole.value === "asc" ? -1 : 1;
    if (roleA > roleB) return sortOrderRole.value === "asc" ? 1 : -1;
    return 0;
  });
}

function sortById() {
  sortOrderId.value = sortOrderId.value === "asc" ? "desc" : "asc";
  users.value.sort((a, b) => {
    const idA = a.id ?? 0;
    const idB = b.id ?? 0;
    return sortOrderId.value === "asc" ? idA - idB : idB - idA;
  });
}

async function fetchUsers() {
  try {
    const res = await axios.get("http://localhost:8080/api/users");
    console.log("👀 API Response:", res.data);

    users.value = res.data.map((u: any) => ({
      ...u,
      visible: u.visible ?? true,
      role:
        u.role && typeof u.role === "string"
          ? u.role.charAt(0).toUpperCase() + u.role.slice(1).toLowerCase()
          : "Reader",
    }));
  } catch (err) {
    console.error("Error fetching users:", err);
  } finally {
    isLoading.value = false;
  }
}


function openUserDetail(user: UserItem) {
  selectedUser.value = user;
  showDetailModal.value = true;
}
function closeUserDetail() {
  showDetailModal.value = false;
  selectedUser.value = null;
}

onMounted(fetchUsers);

// filter active / inactive users
const activeUsers = computed(() => users.value.filter((u) => u.visible));
const inactiveUsers = computed(() => users.value.filter((u) => !u.visible));

async function changeRole(user: UserItem, newRole: string) {
  try {
    const res = await axios.put(
      `http://localhost:8080/api/users/${user.id}/role`,
      { role: newRole }
    );

    alert(res.data.message || `Changed role to ${newRole}`);
    await fetchUsers();
  } catch (err) {
    console.error(err);
    alert("Failed to change role");
  }
}

// reactivate user
async function reactivateUser(id: number) {
  try {
    const res = await axios.put(`http://localhost:8080/api/users/show/${id}`);
    alert(res.data.message || "User reactivated");
    await fetchUsers();
  } catch (err) {
    console.error(err);
    alert("Failed to reactivate user");
  }
}

const adminUsers = computed(() =>
  users.value.filter((u) => u.visible && u.role === "Admin")
);

const memberUsers = computed(() =>
  users.value.filter((u) => u.visible && u.role === "Member")
);

const readerUsers = computed(() =>
  users.value.filter((u) => u.visible && u.role === "Reader")
);
</script>

<template>
  <div class="flex min-h-screen font-[Outfit] bg-white">
    <div class="flex-1  px-10 py-8 w-[1500px]">
      <h1 class="text-5xl font-bold text-[#111827] mb-6">Manage Role of Users</h1>

      <!-- Sorting Buttons -->
      <div class="mb-4 flex items-center justify-between gap-3 flex-nowrap">
        <router-link
          to="/home"
          class="inline-flex items-center justify-center gap-2 h-[40px] px-4 bg-gray-100 text-black rounded-md hover:bg-gray-300 transition whitespace-nowrap"
        >
          <img src="@/assets/Card/Back.png" class="w-[20px] h-[20px]" />
          Back to Homepage
        </router-link>

        <div class="flex items-center gap-3 flex-nowrap">
           <router-link
            to="/admin/delete-user"
            class="inline-flex items-center justify-center gap-2 h-[40px] px-4 text-white bg-red-600 text-black rounded-md hover:bg-red-700 transition whitespace-nowrap"
          >
            Open User Management Page
          </router-link>
          <button
            @click="sortByRole"
            class="h-[40px] px-4 bg-green-500 text-white rounded-md hover:bg-green-600"
          >
            Sort by Role
          </button>
          <button
            @click="sortById"
            class="h-[40px] px-4 bg-blue-500 text-white rounded-md hover:bg-blue-600"
          >
            Sort by ID
          </button>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="isLoading" class="text-gray-500 text-lg">Loading users...</div>

      <div v-else>
        <!-- Active Users -->
        <div
          class="overflow-x-auto shadow rounded-lg border border-gray-200 mb-10"
        >
          <h2
            class="text-xl font-semibold text-gray-800 pt-4 pb-2 pl-0 ml-[22px] mb-[9px]"
          >
            Active Users
          </h2>

          <table class="min-w-full text-left text-gray-700">
            <thead class="bg-gray-100 text-[16px] font-semibold">
              <tr>
                <th class="px-6 py-3">#</th>
                <th class="px-6 py-3">Name</th>
                <th class="px-6 py-3">Email</th>
                <th class="px-6 py-3">Role</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="user in activeUsers"
                :key="user.id"
                class="hover:bg-gray-50 border-b border-gray-100 cursor-pointer transition"
                @click="openUserDetail(user)"
              >
                <td class="px-6 py-3">{{ user.id }}</td>
                <td class="px-6 py-3 font-medium text-blue-600">
                  {{ user.name }} {{ user.surname }}
                </td>
                <td class="px-6 py-3">{{ user.email }}</td>
                <td class="px-6 py-3">
                  <span
                    :class="{
                      'text-red-600 font-semibold': user.role === 'Admin',
                      'text-green-600 font-semibold': user.role === 'Member',
                      'text-yellow-500 font-semibold': user.role === 'Reader',
                    }"
                    >{{ user.role }}</span
                  >
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- ✅ Admin Users -->
        <div
          v-if="adminUsers.length > 0"
          class="overflow-x-auto shadow rounded-lg border border-gray-200 mb-10"
        >
          <h2
            class="text-xl font-semibold text-gray-800 pt-4 pb-2 pl-0 ml-[22px] mb-[9px]"
          >
            Member Users ({{ adminUsers.length }})
          </h2>

          <table class="min-w-full text-left text-gray-700">
            <thead class="bg-gray-100 text-[16px] font-semibold">
              <tr>
                <th class="px-6 py-3">#</th>
                <th class="px-6 py-3">Name</th>
                <th class="px-6 py-3">Email</th>
                <th class="px-6 py-3">Role</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="user in adminUsers"
                :key="user.id"
                class="hover:bg-gray-50 border-b border-gray-100 cursor-pointer transition"
                @click="openUserDetail(user)"
              >
                <td class="px-6 py-3">{{ user.id }}</td>
                <td class="px-6 py-3 font-medium text-blue-600">
                  {{ user.name }} {{ user.surname }}
                </td>
                <td class="px-6 py-3">{{ user.email }}</td>
                <td class="px-6 py-3 text-red-600 font-semibold">Admin</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- ✅ Member Users -->
        <div
          v-if="memberUsers.length > 0"
          class="overflow-x-auto shadow rounded-lg border border-gray-200 mb-10"
        >
          <h2
            class="text-xl font-semibold text-gray-800 pt-4 pb-2 pl-0 ml-[22px] mb-[9px]"
          >
            Member Users ({{ memberUsers.length }})
          </h2>

          <table class="min-w-full text-left text-gray-700">
            <thead class="bg-gray-100 text-[16px] font-semibold">
              <tr>
                <th class="px-6 py-3">#</th>
                <th class="px-6 py-3">Name</th>
                <th class="px-6 py-3">Email</th>
                <th class="px-6 py-3">Role</th>
                <th class="px-6 py-3">Action</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="user in memberUsers"
                :key="user.id"
                class="hover:bg-gray-50 border-b border-gray-100 cursor-pointer transition"
                @click="openUserDetail(user)"
              >
                <td class="px-6 py-3">{{ user.id }}</td>
                <td class="px-6 py-3 font-medium text-blue-600">
                  {{ user.name }} {{ user.surname }}
                </td>
                <td class="px-6 py-3">{{ user.email }}</td>
                <td class="px-6 py-3 text-green-600 font-semibold">Member</td>
                <td class="px-6 py-3 flex gap-2">
                  <button
                    @click.stop="changeRole(user, 'Admin')"
                    class="px-3 py-1 bg-red-600 text-white rounded-md hover:bg-red-700"
                  >
                    Set Admin
                  </button>
                  <button
                    @click.stop="changeRole(user, 'Reader')"
                    class="px-3 py-1 bg-yellow-500 text-white rounded-md hover:bg-yellow-600"
                  >
                    Set Reader
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- ✅ Reader Users -->
        <div
          v-if="readerUsers.length > 0"
          class="overflow-x-auto shadow rounded-lg border border-gray-200 mb-10"
        >
          <h2
            class="text-xl font-semibold text-gray-800 pt-4 pb-2 pl-0 ml-[22px] mb-[9px]"
          >
            Reader Users ({{ readerUsers.length }})
          </h2>

          <table class="min-w-full text-left text-gray-700">
            <thead class="bg-gray-100 text-[16px] font-semibold">
              <tr>
                <th class="px-6 py-3">#</th>
                <th class="px-6 py-3">Name</th>
                <th class="px-6 py-3">Email</th>
                <th class="px-6 py-3">Role</th>
                <th class="px-6 py-3">Action</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="user in readerUsers"
                :key="user.id"
                class="hover:bg-gray-50 border-b border-gray-100 cursor-pointer transition"
                @click="openUserDetail(user)"
              >
                <td class="px-6 py-3">{{ user.id }}</td>
                <td class="px-6 py-3 font-medium text-blue-600">
                  {{ user.name }} {{ user.surname }}
                </td>
                <td class="px-6 py-3">{{ user.email }}</td>
                <td class="px-6 py-3 text-yellow-600 font-semibold">Reader</td>
                <td class="px-6 py-3 flex gap-2">
                  <button
                    @click.stop="changeRole(user, 'Member')"
                    class="px-3 py-1 bg-green-600 text-white rounded-md hover:bg-green-700"
                  >
                    Set Member
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Inactive Users -->
        <div
          v-if="inactiveUsers.length > 0"
          class="overflow-x-auto shadow rounded-lg border border-gray-200 mt-10"
        >
          <h2
            class="text-xl font-semibold text-gray-800 pt-4 pb-2 ml-[22px] mb-[9px]"
          >
            Inactive Users ({{ inactiveUsers.length }})
          </h2>

          <table class="min-w-full text-left text-gray-700">
            <thead class="bg-gray-100 text-[16px] font-semibold">
              <tr>
                <th class="px-6 py-3">#</th>
                <th class="px-6 py-3">Name</th>
                <th class="px-6 py-3">Email</th>
                <th class="px-6 py-3">Role</th>
                <th class="px-6 py-3">Action</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="user in inactiveUsers"
                :key="user.id"
                class="hover:bg-gray-50 border-b border-gray-100 transition"
                @click="openUserDetail(user)"
              >
                <td class="px-6 py-3">{{ user.id }}</td>
                <td class="px-6 py-3 text-gray-500 italic">
                  {{ user.name }} {{ user.surname }}
                </td>
                <td class="px-6 py-3">{{ user.email }}</td>
                <td class="px-6 py-3 text-purple-600 font-semibold italic">
                  {{ user.role }}
                </td>
                <td class="px-6 py-3">
                  <button
                    @click.stop="reactivateUser(user.id)"
                    class="px-3 py-1 bg-green-600 text-white rounded-md hover:bg-green-700"
                  >
                    Reactivate
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="text-gray-500 text-sm italic text-right mt-2">
          No inactive users.
        </div>
      </div>
    </div>

    <!-- Detail Modal -->
    <div
      v-if="showDetailModal && selectedUser"
      class="fixed inset-0 bg-black/40 flex items-center justify-center z-[9999]"
      @click.self="closeUserDetail"
    >
      <div class="bg-white w-[600px] rounded-2xl shadow-xl p-8 font-[Outfit]">
        <h2 class="text-2xl font-bold text-gray-800 mb-4">
          {{ selectedUser.name }} {{ selectedUser.surname }}
        </h2>
        <p class="text-gray-600 mb-2">
          <strong>Email:</strong> {{ selectedUser.email }}
        </p>
        <p class="text-gray-600 mb-2">
          <strong>Role:</strong> {{ selectedUser.role }}
        </p>

        <div class="flex justify-end mt-6">
          <button
            @click="closeUserDetail"
            class="px-5 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-md"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
