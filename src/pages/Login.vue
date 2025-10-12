<template>
  <div class="flex h-screen font-['Outfit']">
    <!-- Left Panel -->
    <div
      class="flex-1 flex items-center justify-center rounded-r-[40px] bg-gradient-to-br from-pink-500 via-purple-500 to-orange-400"
    >

    </div>

    <!-- Right Panel -->
    <div class="flex-1 flex flex-col justify-center items-start px-20 space-y-6">
      <!-- Header -->
      <h1 class="text-4xl font-extrabold text-gray-900">USER LOGIN</h1>

      <!-- Form -->
      <form class="w-full space-y-5">
        <!-- Email -->
        <div>
          <label class="block text-gray-700 text-base font-medium mb-1"
            >Email</label
          >
          <div
            class="flex items-center border border-gray-300 rounded-full px-4 py-2 focus-within:border-blue-500 transition"
          >
            <i class="fa-regular fa-user text-gray-400 mr-3"></i>
            <input
              type="email"
              placeholder="user@example.com"
              class="w-full focus:outline-none placeholder-gray-400 text-gray-700"
            />
          </div>
        </div>

        <!-- Password -->
        <div>
          <label class="block text-gray-700 text-base font-medium mb-1"
            >Password</label
          >
          <div
            class="flex items-center border border-gray-300 rounded-full px-4 py-2 focus-within:border-blue-500 transition"
          >
            <i class="fa-solid fa-lock text-gray-400 mr-3"></i>
            <input
              type="password"
              placeholder="Password"
              class="w-full focus:outline-none placeholder-gray-400 text-gray-700"
            />
          </div>
        </div>

        <!-- Buttons -->
        <div class="flex items-center space-x-4 pt-3">
          <button
            type="submit"
            class="bg-gradient-to-r from-pink-500 to-orange-400 text-white font-semibold px-8 py-2 rounded-full hover:opacity-90 transition"
          >
            Continue
          </button>
          <button
            type="button"
            class="bg-blue-500 text-white font-semibold px-8 py-2 rounded-full hover:bg-blue-600 transition"
          >
            Sign up
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
// ไม่มี logic ในหน้านี้ (static login UI)
// แต่สามารถเพิ่ม emit หรือ router push ได้ภายหลัง
</script>

<style scoped>
/* ปรับขอบภาพให้มน */
img {
  border-radius: 20px;
}
</style>
