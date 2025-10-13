<template>
  <footer
    class="w-[120%] bg-gradient-to-tr from-blue-500/70 via-blue-500/50 via-green-500/70 to-green-300/70 px-8 py-7 rounded-[30px] text-white font-[Outfit] shadow-[0_-2px_10px_rgba(0,0,0,0.15)] mt-12 text-center"
  >
    <div class="flex flex-wrap justify-between items-center text-center gap-4">
      <div class="flex-1">
        <h2 class="text-[1.6rem] font-bold tracking-[1px]">Check It Off</h2>
        <p class="text-[0.95rem] opacity-85">Social Anti-Fake News System</p>
      </div>

      <div class="flex justify-center items-center gap-[1.2rem]">
        <a
          href="https://github.com/chartchai-class/project-01-the-anti-fake-news-system-check-it-off-Vue.git"
          target="_blank"
          rel="noopener"
          aria-label="GitHub Repository"
          class="flex justify-center items-center w-[50px] h-[50px] rounded-full bg-white/15 transition-all duration-300 ease-in-out hover:bg-white hover:-translate-y-1 hover:shadow-[0_3px_6px_rgba(0,0,0,0.2)]"
        >
          <img
            src="@/assets/github.svg"
            alt="GitHub"
            class="w-[28px] h-[28px] brightness-0 invert transition-all duration-300 group-hover:brightness-0 group-hover:invert-0"
          />
        </a>

        <a
          href="https://docs.google.com/spreadsheets/d/1xXd_djAF1jp7c5jrY-mzAwVYxSN9wbl_0RTpKKvH0AA/edit?usp=sharing"
          target="_blank"
          rel="noopener"
          aria-label="Google Sheets"
          class="flex justify-center items-center w-[50px] h-[50px] rounded-full bg-white/15 transition-all duration-300 ease-in-out hover:bg-white hover:-translate-y-1 hover:shadow-[0_3px_6px_rgba(0,0,0,0.2)]"
        >
          <img
            src="@/assets/google-sheet.svg"
            alt="Google Sheets"
            class="w-[28px] h-[28px] brightness-0 invert transition-all duration-300 hover:brightness-0 hover:saturate-[100%] hover:invert-[45%] hover:sepia-[96%] hover:saturate-[511%] hover:hue-rotate-[90deg] hover:brightness-[95%] hover:contrast-[89%]"
          />
        </a>
      </div>

      <div class="flex-1">
        <p class="text-[0.95rem]">
          © 2025 <span class="font-semibold text-[#e0f2fe]">Check It Off</span> — Developed by
          <b>Nawapon Somruang 662115027</b>
        </p>
      </div>
    </div>
  </footer>
</template>
