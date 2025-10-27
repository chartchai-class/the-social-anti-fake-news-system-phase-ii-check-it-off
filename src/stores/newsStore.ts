//src/stores/newsStore.ts
import { defineStore } from "pinia";
import axios from "axios";

export const useNewsStore = defineStore("newsStore", {
  state: () => ({
    newsList: [] as any[],
    currentNews: null as any,
    comments: [] as any[],
    isLoading: false,
  }),

  actions: {
    async fetchAllNews() {
      this.isLoading = true;
      try {
        const res = await axios.get("http://localhost:8080/api/news");
        this.newsList = res.data;
      } catch (err) {
        console.error("Failed to fetch all news:", err);
      } finally {
        this.isLoading = false;
      }
    },

    async fetchNewsById(id: number) {
      if (this.currentNews && this.currentNews.id === id) return;

      this.isLoading = true;
      try {
        const res = await axios.get(`http://localhost:8080/api/news/${id}`);
        this.currentNews = res.data;
      } catch (err) {
        console.error("Failed to fetch news detail:", err);
      } finally {
        this.isLoading = false;
      }
    },

    async submitVote(votePayload: any) {
      try {
        const res = await axios.post(
          "http://localhost:8080/api/votes",
          votePayload
        );

        if (res.status === 200 || res.status === 201) {
          console.log("🌟 Vote submitted successfully.");
          if (votePayload.news_id) await this.fetchNewsById(votePayload.news_id);
        } else {
          throw new Error("Vote submission failed");
        }
      } catch (err) {
        console.error("Error submitting vote:", err);
        throw err;
      }
    },

    async fetchCommentsByNewsId(newsId: number) {
      this.isLoading = true;
      try {
        const res = await axios.get(`http://localhost:8080/api/votes/news/${newsId}`);
        this.comments = Array.isArray(res.data) ? res.data : [];
      } catch (err) {
        console.error("Failed to fetch comments:", err);
      } finally {
        this.isLoading = false;
      }
    },
  },
});
