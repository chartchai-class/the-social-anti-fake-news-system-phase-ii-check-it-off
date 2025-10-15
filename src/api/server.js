import express from "express";
import { google } from "googleapis";
import path from "path";
import cors from "cors";

const app = express();
const port = 5175;

app.use(cors());
app.use(express.json());
app.use(express.text());
app.use(express.urlencoded({ extended: true }));

// Google Sheet Info
const SPREADSHEET_ID = "1xXd_djAF1jp7c5jrY-mzAwVYxSN9wbl_0RTpKKvH0AA";
const SHEET_NAME = "Sheet1";

// Auth (must have write permission)
const auth = new google.auth.GoogleAuth({
  keyFile: path.join(
    process.cwd(),
    "secret/newsdatabaseproject-f6c77e6cf130.json"
  ),
  scopes: ["https://www.googleapis.com/auth/spreadsheets"], // full read/write scope
});

const sheets = google.sheets({ version: "v4", auth });

// GET /api/news
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

// POST /api/vote
app.post("/api/vote", async (req, res) => {
  try {
    const { id, name, status, comment, imageUrl } = req.body;

    if (!id || !name || !status || !comment) {
      return res.status(400).json({ message: "Missing required fields" });
    }

    console.log("🗳️ New Vote Received:", { id, name, status, comment });

    await sheets.spreadsheets.values.append({
      spreadsheetId: SPREADSHEET_ID,
      range: "Sheet2!A:E",
      valueInputOption: "USER_ENTERED",
      requestBody: {
        values: [[id, name, status, comment, imageUrl || ""]],
      },
    });

    const newsResponse = await sheets.spreadsheets.values.get({
      spreadsheetId: SPREADSHEET_ID,
      range: `${SHEET_NAME}!A:K`,
    });

    const rows = newsResponse.data.values || [];
    const rowIndex = rows.findIndex((r) => Number(r[0]) === Number(id));

    if (rowIndex > 0) {
      const current = rows[rowIndex];
      let upVotes = Number(current[7]) || 0;
      let downVotes = Number(current[8]) || 0;

      if (status === "up") upVotes++;
      else if (status === "down") downVotes++;

      const updateRange = `${SHEET_NAME}!H${rowIndex + 1}:I${rowIndex + 1}`;
      await sheets.spreadsheets.values.update({
        spreadsheetId: SPREADSHEET_ID,
        range: updateRange,
        valueInputOption: "USER_ENTERED",
        requestBody: {
          values: [[upVotes, downVotes]],
        },
      });
    }

    res.status(200).json({ message: "Vote saved successfully" });
  } catch (error) {
    console.error("Error saving vote:", error);
    res
      .status(500)
      .json({ message: "Failed to save vote", error: error.message });
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

// POST /api/register
app.post("/api/register", async (req, res) => {
  try {
    const { name, surname, email, password } = req.body;

    if (!name || !surname || !email || !password) {
      return res.status(400).json({ message: "Missing required fields" });
    }

    const response = await sheets.spreadsheets.values.get({
      spreadsheetId: SPREADSHEET_ID,
      range: "Sheet3!A:A",
    });

    const rows = response.data.values || [];
    const newId = rows.length;

    await sheets.spreadsheets.values.append({
      spreadsheetId: SPREADSHEET_ID,
      range: "Sheet3!A:H",
      valueInputOption: "USER_ENTERED",
      requestBody: {
        values: [
          [
            newId,
            name,
            surname,
            email,
            password,
            `=IF(INDIRECT("G"&${newId + 1})="", "", IF(INDIRECT("G"&${
              newId + 1
            })>3, "Member", "Reader"))`,
            `=COUNTIF(Sheet2!B:B, INDIRECT("J" & ${newId + 1}))`,
            0,
          ],
        ],
      },
    });

    res
      .status(200)
      .json({ message: "Account created successfully", id: newId });
  } catch (error) {
    console.error("Error saving user:", error);
    res
      .status(500)
      .json({ message: "Failed to register user", error: error.message });
  }
});

// POST /api/login
app.post("/api/login", async (req, res) => {
  try {
    const { email, password } = req.body;
    console.log(" [Login Request]", email, password);

    if (!email || !password) {
      return res.status(400).json({ message: " Missing email or password" });
    }

    const response = await sheets.spreadsheets.values.get({
      spreadsheetId: SPREADSHEET_ID,
      range: "Sheet3!A:H",
    });

    const rows = response.data.values || [];
    console.log(" [Sheet3 Rows]", rows.length);
    console.log(" [First Row Example]", rows[1]);

    // ✅ ตรวจทั้ง email และ password
    const user = rows
      .slice(1)
      .find(
        (row) =>
          row[3]?.trim().toLowerCase() === email.trim().toLowerCase() &&
          row[4]?.trim() === password.trim()
      );

    console.log("🔍 [Found User]", user || " Not found");

    if (!user) {
      return res.status(401).json({ message: " Invalid email or password" });
    }

    res.status(200).json({
      message: "✅ You’ve successfully logged in",
      id: user[0],
      name: user[1],
      surname: user[2],
      email: user[3],
      access: user[5],
      voteCount: user[6],
      newsCount: user[7],
    });
  } catch (error) {
    console.error(" Error reading Google Sheets:", error);
    res.status(500).json({ message: "Server error", error: error.message });
  }
});

// ✅ POST /api/news
app.post("/api/news", async (req, res) => {
  try {
    const {
      id,
      title,
      stats,
      description,
      author,
      image,
      date,
      upVotes,
      downVotes,
      comments,
      fullDescription,
      mockUpvote,
      mockDownvote,
    } = req.body;

    // ตรวจสอบค่าบังคับ
    if (!title || !author || !date) {
      return res.status(400).json({ message: "Missing required fields." });
    }

    // ดึงข้อมูลทั้งหมดเพื่อหาล่าสุด (หา id ใหม่)
    const response = await sheets.spreadsheets.values.get({
      spreadsheetId: SPREADSHEET_ID,
      range: `${SHEET_NAME}!A:A`,
    });

    const rows = response.data.values || [];
    const newId = rows.length + 1;

    const statsFormula = `=IF(H${newId}>I${newId},"Verified",IF(I${newId}>H${newId},"Fake News","Unverified"))`;
    const voteupFormula = `=M${newId} + Sheet2!I${newId}`;
    const votedownFormula = `=N${newId} + Sheet2!J${newId}`;
    const countcommentFormula = `=COUNTIF(Sheet2!A2:A, ${newId})`;

    await sheets.spreadsheets.values.append({
      spreadsheetId: SPREADSHEET_ID,
      range: "Sheet1!A1", // ✅ เริ่มใส่จากคอลัมน์ A เสมอ
      valueInputOption: "USER_ENTERED",
      insertDataOption: "INSERT_ROWS", // ✅ บังคับให้เพิ่มแถวใหม่
      requestBody: {
        values: [
          [
            newId,
            title,
            statsFormula,
            description || "",
            author,
            image || "",
            date,
            voteupFormula,
            votedownFormula,
            countcommentFormula,
            fullDescription || "",
            "",
            mockUpvote || 0,
            mockDownvote || 0,
          ],
        ],
      },
    });

    res.status(200).json({ message: "News added successfully!" });
  } catch (error) {
    console.error("❌ Error adding news:", error);
    res.status(500).json({ message: "Failed to add news." });
  }
});

// Start Server
app.listen(port, () => {
  console.log(`✅ Server running at http://localhost:${port}`);
});
