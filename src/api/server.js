import express from "express";
import { google } from "googleapis";
import path from "path";
import cors from "cors";

const app = express();
const port = 5175;

app.use(cors()); // allow cross-origin requests
app.use(express.json()); // parse JSON body

// ✅ Google Sheet Info
const SPREADSHEET_ID = "1xXd_djAF1jp7c5jrY-mzAwVYxSN9wbl_0RTpKKvH0AA";
const SHEET_NAME = "Sheet1";

// ✅ Auth (must have write permission)
const auth = new google.auth.GoogleAuth({
  keyFile: path.join(process.cwd(), "secret/newsdatabaseproject-f6c77e6cf130.json"),
  scopes: ["https://www.googleapis.com/auth/spreadsheets"], // ✅ full read/write scope
});

const sheets = google.sheets({ version: "v4", auth });

// =============================
// 📍 GET /api/news
// =============================
app.get("/api/news", async (_req, res) => {
  try {
    const response = await sheets.spreadsheets.values.get({
      spreadsheetId: SPREADSHEET_ID,
      range: `${SHEET_NAME}!A:K`, // id | title | stats | description | author | image | date | upVotes | downVotes | comments | fullDescription
    });

    const rows = response.data.values || [];
    if (rows.length < 2) return res.json({ news: [], stats: {} });

    const safeText = (v) => (v && v.trim() !== "" ? v.trim() : "N/A");
    const safeNumber = (v) => (v && !isNaN(v) ? Number(v) : 0);

    const newsData = rows
      .slice(1)
      .map((row) => ({
        id: safeText(row[0]),
        title: safeText(row[1]),
        stats: safeText(row[2]),
        description: safeText(row[3]),
        author: safeText(row[4]),
        image: safeText(row[5]),
        date: safeText(row[6]),
        upVotes: safeNumber(row[7]),
        downVotes: safeNumber(row[8]),
        comments: safeNumber(row[9]),
        fullDescription: safeText(row[10]),
      }))
      .filter((n) => n.title !== "N/A");

    const stats = {
      total: newsData.length,
      verified: newsData.filter((n) => n.stats === "Verified").length,
      fake: newsData.filter((n) => n.stats === "Fake News").length,
      underReview: newsData.filter((n) => n.stats === "Unverified").length,
    };

    res.json({ news: newsData, stats });
  } catch (err) {
    console.error("Google Sheets error:", err);
    res.status(500).json({
      error: "Failed to fetch data from Google Sheets",
      details: err.message,
    });
  }
});

// =============================
// 📍 POST /api/vote
// =============================
app.post("/api/vote", async (req, res) => {
  const { id, name, status, comment, imageUrl } = req.body;

  if (!id || !name || !status || !comment) {
    return res.status(400).json({ message: "Missing required fields" });
  }

  const readableStatus =
    status === "up"
      ? "up"
      : status === "down"
      ? "down"
      : status || "Unknown";

  try {
    await sheets.spreadsheets.values.append({
      spreadsheetId: SPREADSHEET_ID,
      range: "Sheet2!A:E",
      valueInputOption: "USER_ENTERED",
      requestBody: {
        values: [[id, name, readableStatus, comment, imageUrl || ""]],
      },
    });

    res.status(200).json({ message: "✅ Vote saved successfully" });
  } catch (error) {
    console.error("Error writing to Google Sheets:", error);
    res.status(500).json({ message: "❌ Failed to save vote" });
  }
});

app.get("/api/comments", async (_req, res) => {
  try {
    const response = await sheets.spreadsheets.values.get({
      spreadsheetId: SPREADSHEET_ID,
      range: "Sheet2!A:E",
    });

    const rows = response.data.values || [];
    const comments = rows.slice(1).map((row) => ({
      id: row[0],
      name: row[1],
      status: row[2] === "up" ? "Real" : row[2] === "down" ? "Fake" : "Review",
      comment: row[3],
      imageUrl: row[4] || "",
    }));

    res.json({ comments });
  } catch (error) {
    console.error("Error fetching comments:", error);
    res.status(500).json({ error: "Failed to fetch comments" });
  }
});


// =============================
// 🚀 Start Server
// =============================
app.listen(port, () => {
  console.log(`✅ Server running at http://localhost:${port}`);
});
