<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-left">
        <h2 class="footer-title">Check It Off</h2>
        <p class="footer-tagline">Social Anti-Fake News System</p>
      </div>

      <div class="footer-center">
        <a
          href="https://github.com/chartchai-class/project-01-the-anti-fake-news-system-check-it-off-Vue.git"
          target="_blank"
          rel="noopener"
          class="social-icon github"
          aria-label="GitHub Repository"
        >
          <img src="@/assets/github.svg" alt="GitHub" class="icon-img" />
        </a>

        <a
          href="https://docs.google.com/spreadsheets/d/1xXd_djAF1jp7c5jrY-mzAwVYxSN9wbl_0RTpKKvH0AA/edit?usp=sharing"
          target="_blank"
          rel="noopener"
          class="social-icon sheet"
          aria-label="Google Sheets"
        >
          <img src="@/assets/google-sheet.svg" alt="Google Sheets" class="icon-img" />
        </a>
      </div>

      <div class="footer-right">
        <p class="footer-credit">
          © 2025 <span class="highlight">Check It Off</span> — Developed by
          <b>Nawapon Somruang 662115027</b>
        </p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  width: 115%;
  background: linear-gradient(
    120deg,
    rgba(59, 130, 246, 0.7),
     rgba(59, 130, 246, 0.5),
    rgba(34, 197, 94, 0.7),
    rgba(134, 239, 172, 0.7)
  );
  padding: 1.8rem 2rem;
 border-radius: 30px;
  color: #ffffff;
  font-family: "Outfit", sans-serif;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.15);
  margin-top: 3rem;
}

.footer-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  text-align: center;
  gap: 1rem;
}

.footer-left {
  flex: 1;
}

.footer-title {
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: 1px;
}

.footer-tagline {
  font-size: 0.95rem;
  opacity: 0.85;
}

.footer-center {
  display: flex;
  gap: 1.2rem;
  justify-content: center;
  align-items: center;
}

.social-icon {
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  width: 50px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
}

.social-icon:hover {
  background-color: #ffffff;
  transform: translateY(-4px);
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.2);
}

.social-icon.github:hover img {
  filter: brightness(0) invert(0);
}

.social-icon.sheet:hover img {
  filter: brightness(0) saturate(100%) invert(45%) sepia(96%) saturate(511%)
    hue-rotate(90deg) brightness(95%) contrast(89%);
}

.icon-img {
  width: 28px;
  height: 28px;
  filter: brightness(0) invert(1);
  transition: filter 0.3s ease;
}

.footer-right {
  flex: 1;
}

.footer-credit {
  font-size: 0.95rem;
}

.footer-credit .highlight {
  font-weight: 600;
  color: #e0f2fe;
}

@media (max-width: 768px) {
  .footer-container {
    flex-direction: column;
    text-align: center;
  }

  .footer-left,
  .footer-right {
    flex: unset;
  }
}
</style>
