<template>
  <div
    class="PageContainer fixed inset-0 flex overflow-hidden transition-all duration-700"
  >
    <transition :name="isSignUp ? 'slide-left' : 'slide-right'" mode="out-in">
      <!-- Login -->
      <div
        v-if="!isSignUp"
        key="login"
        class="flex-1 flex items-center justify-center bg-white transition-all duration-700"
      >
        <div class="flex flex-col items-center justify-center w-[600px] px-16">
          <h1
            class="text-7xl font-extrabold text-gray-900 mb-10 font-[Outfit] whitespace-nowrap"
          >
            User Login
          </h1>

          <form
            class="w-[500px] space-y-6 mx-auto"
            @submit.prevent="handleLogin"
            @keydown.enter.prevent
          >
            <div>
              <label
                class="block text-[30px] text-gray-700 font-semibold mb-2 text-left"
                >Email</label
              >
              <div
                class="flex items-center w-full border border-gray-400 rounded-full px-6 py-3"
              >
                <img src="@/assets/Login/User-Icon.png" class="w-7 h-7 mr-3" />
                <input
                  v-model="loginEmail"
                  @input="clearError('loginEmail')"
                  type="email"
                  placeholder="user@example.com"
                  class="flex-1 outline-none text-gray-700 placeholder-gray-400 bg-transparent text-[22px]"
                />
              </div>
              <p
                v-if="errors.loginEmail"
                class="text-red-500 text-[18px] mt-1 text-left"
              >
                {{ errors.loginEmail }}
              </p>
            </div>

            <div>
              <label
                class="block text-[30px] text-gray-700 font-semibold mb-2 text-left -mt-3"
                >Password</label
              >
              <div
                class="flex items-center w-full border border-gray-400 rounded-full px-6 py-3"
              >
                <img
                  src="@/assets/Login/Password-Icon.png"
                  class="w-7 h-7 mr-3"
                />
                <input
                  v-model="loginPassword"
                  @input="clearError('loginPassword')"
                  type="password"
                  placeholder="Enter your password"
                  class="flex-1 outline-none text-gray-700 placeholder-gray-400 bg-transparent text-[22px]"
                />
              </div>
              <p
                v-if="errors.loginPassword"
                class="text-red-500 text-[18px] mt-1 text-left"
              >
                {{ errors.loginPassword }}
              </p>
            </div>

            <div class="flex justify-center space-x-6 mt-8">
              <button
                type="submit"
                class="basis-[60%] py-3 text-white font-semibold rounded-full bg-gradient-to-r from-[#f6b33b] to-[#ef86cb] hover:opacity-90 transition"
              >
                Continue
              </button>
              <button
                type="button"
                @click="toggleSignUp"
                class="basis-[35%] py-3 text-white font-semibold rounded-full bg-blue-500 hover:bg-blue-600 transition"
              >
                Sign Up
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Sign up  -->
      <div
        v-else
        key="signup"
        class="flex-1 flex items-center justify-center bg-white transition-all duration-700"
      >
        <div class="flex flex-col items-center justify-center w-[600px] px-16">
          <h1
            class="text-7xl font-extrabold text-gray-900 mb-10 font-[Outfit] whitespace-nowrap"
          >
            Create Account
          </h1>

          <form
            class="w-[500px] space-y-6 mx-auto"
            @submit.prevent="handleCreateAccount"
          >
            <div>
              <label
                class="block text-[30px] text-gray-700 font-semibold mb-2 text-left"
                >Name</label
              >
              <div
                class="flex items-center w-full border border-gray-400 rounded-full px-6 py-3"
              >
                <img src="@/assets/Login/User-Icon.png" class="w-7 h-7 mr-3" />
                <input
                  v-model="name"
                  @input="clearError('name')"
                  type="text"
                  placeholder="Your Name"
                  class="flex-1 outline-none text-gray-700 bg-transparent text-[22px]"
                />
              </div>
              <p
                v-if="errors.name"
                class="text-red-500 text-[18px] mt-1 text-left"
              >
                {{ errors.name }}
              </p>
            </div>

            <div>
              <label
                class="block text-[30px] text-gray-700 font-semibold mb-2 text-left"
                >Surname</label
              >
              <div
                class="flex items-center w-full border border-gray-400 rounded-full px-6 py-3"
              >
                <img src="@/assets/Login/User-Icon.png" class="w-7 h-7 mr-3" />
                <input
                  v-model="surname"
                  @input="clearError('surname')"
                  type="text"
                  placeholder="Your Surname"
                  class="flex-1 outline-none text-gray-700 bg-transparent text-[22px]"
                />
              </div>
              <p
                v-if="errors.surname"
                class="text-red-500 text-[18px] mt-1 text-left"
              >
                {{ errors.surname }}
              </p>
            </div>

            <div>
              <label
                class="block text-[30px] text-gray-700 font-semibold mb-2 text-left"
                >Email</label
              >
              <div
                class="flex items-center w-full border border-gray-400 rounded-full px-6 py-3"
              >
                <img src="@/assets/Login/User-Icon.png" class="w-7 h-7 mr-3" />
                <input
                  v-model="email"
                  @input="clearError('email')"
                  type="email"
                  placeholder="user@example.com"
                  class="flex-1 outline-none text-gray-700 bg-transparent text-[22px]"
                />
              </div>
              <p
                v-if="errors.email"
                class="text-red-500 text-[18px] mt-1 text-left"
              >
                {{ errors.email }}
              </p>
            </div>

            <div>
              <label
                class="block text-[30px] text-gray-700 font-semibold mb-2 text-left"
                >Password</label
              >
              <div
                class="flex items-center w-full border border-gray-400 rounded-full px-6 py-3"
              >
                <img
                  src="@/assets/Login/Password-Icon.png"
                  class="w-7 h-7 mr-3"
                />
                <input
                  v-model="password"
                  @input="clearError('password')"
                  type="password"
                  placeholder="Create a password"
                  class="flex-1 outline-none text-gray-700 bg-transparent text-[22px]"
                />
              </div>
              <p
                v-if="errors.password"
                class="text-red-500 text-[18px] mt-1 text-left"
              >
                {{ errors.password }}
              </p>
            </div>

            <div class="flex justify-center space-x-6 mt-8">
              <button
                type="submit"
                class="basis-[60%] py-3 text-white font-semibold rounded-full bg-gradient-to-r from-[#f6b33b] to-[#ef86cb] hover:opacity-90 transition"
              >
                Create Account
              </button>
              <button
                type="button"
                @click="toggleSignUp"
                class="basis-[35%] py-3 text-white font-semibold rounded-full bg-blue-500 hover:bg-blue-600 transition"
              >
                Back
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>

    <!-- Page Transition -->
    <transition :name="isSignUp ? 'slide-left' : 'slide-right'" mode="out-in">
      <div
        v-if="!isSignUp"
        key="login-img"
        class="flex-1 flex items-center justify-center bg-gradient-to-r from-[#f6b33b] via-[#ef86cb] to-[#c52286] rounded-[50px] transition-all duration-700"
      >
        <img
          src="@/assets/Login/LoginImage.png"
          class="w-[700px] h-[700px] object-cover rounded-[30px]"
        />
      </div>

      <div
        v-else
        key="signup-img"
        class="flex-1 flex items-center justify-center bg-gradient-to-r from-[#93c5fd] via-[#1e40af] to-[#000000] rounded-[50px] transition-all duration-700"
      >
        <img
          src="@/assets/Login/LoginImage.png"
          class="w-[700px] h-[700px] object-cover rounded-[30px]"
        />
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from "vue";
import * as yup from "yup";
import { useRouter } from "vue-router";
const router = useRouter();

const isSignUp = ref(false);
const name = ref("");
const surname = ref("");
const email = ref("");
const password = ref("");
const loginEmail = ref("");
const loginPassword = ref("");
const errors = ref({ name: "", surname: "", email: "", password: "" });
const role = ref("READER");

function toggleSignUp() {
  isSignUp.value = !isSignUp.value;
}

function clearError(field) {
  if (errors.value[field]) {
    delete errors.value[field];
  }
}

const signUpSchema = yup.object({
  name: yup.string().required("Please enter your name"),
  surname: yup.string().required("Please enter your surname"),
  email: yup
    .string()
    .email("Invalid email format")
    .required("Please enter your email"),
  password: yup
    .string()
    .min(6, "Password must be at least 6 characters")
    .required("Please enter your password"),
});

const loginSchema = yup.object({
  loginEmail: yup
    .string()
    .email("Invalid email format")
    .required(" Please enter your email"),
  loginPassword: yup.string().required(" Please enter your password"),
});

async function handleCreateAccount() {
  errors.value = {};

  try {
    await signUpSchema.validate(
      {
        name: name.value,
        surname: surname.value,
        email: email.value,
        password: password.value,
      },
      { abortEarly: false }
    );

    const res = await fetch("http://localhost:8080/api/votes/create-user", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name: name.value,
        surname: surname.value,
        email: email.value,
        password: password.value,
        role: "READER",
        visible: true,
      }),
    });

    const text = await res.text();
    console.log("Raw response (SignUp):", text);

    let data;
    try {
      data = JSON.parse(text);
    } catch {
      throw new Error("Server response is not valid JSON");
    }

    if (data.success) {
      alert("🌟 Your account has been created successfully!");
      name.value = "";
      surname.value = "";
      email.value = "";
      password.value = "";
      isSignUp.value = false;
    } else {
      alert(`${data.message}`);
    }
  } catch (err) {
    if (err.inner) {
      err.inner.forEach((e) => (errors.value[e.path] = e.message));
    } else {
      alert(" Cannot connect to server or invalid response.");
    }
  }
}

async function handleLogin() {
  try {
    errors.value = {};

    await loginSchema.validate(
      { loginEmail: loginEmail.value, loginPassword: loginPassword.value },
      { abortEarly: false }
    );

    const response = await fetch("http://localhost:8080/api/users/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: loginEmail.value,
        password: loginPassword.value,
      }),
    });

    const text = await response.text();
    console.log("Raw response (Login):", text);

    let result;
    try {
      result = JSON.parse(text);
    } catch {
      throw new Error("Server response is not JSON");
    }

    if (result.success) {
      if (result.visible === false || result.visible === 0) {
        alert("Your account is currently inactive. You cannot log in.");
        return;
      }
      const userData = {
        id: result.id,
        name: result.name,
        surname: result.surname,
        email: result.email,
        role: result.role,
      };
      localStorage.setItem("user", JSON.stringify(userData));

      if (result.role === "ADMIN") {
        alert(`🌟Welcome Admin, ${result.name}!`);
        window.location.href = "/home";
      } else {
        alert(`🌟Welcome, ${result.name}! You’ve logged in successfully.`);
        window.location.href = "/home";
      }
    } else {
      alert(result.message || "Invalid email or password");
    }
  } catch (err) {
    if (err.inner) {
      err.inner.forEach((e) => (errors.value[e.path] = e.message));
    } else {
      console.error("Unexpected Error:", err);
      alert("Unable to log in right now. Please check your connection.");
    }
  }
}
</script>

<style>
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.7s ease;
}
.slide-left-enter-from {
  transform: translateX(100%);
  opacity: 0;
}
.slide-left-leave-to {
  transform: translateX(-100%);
  opacity: 0;
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.7s ease;
  transform: translateX(-100%);
  opacity: 0;
}
.slide-right-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>
