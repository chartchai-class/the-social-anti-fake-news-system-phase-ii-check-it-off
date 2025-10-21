import express from "express";
import mysql from "mysql2/promise";
import cors from "cors";

const app = express();
app.use(cors());

const db = await mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "password",
  database: "checkitoff"
});

app.get("/api/news", async (req, res) => {
  try {
    const [rows] = await db.execute("SELECT * FROM news ORDER BY date DESC");
    res.json({ news: rows });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch news" });
  }
});

const PORT = 5175;
app.listen(PORT, () => console.log(`API running at http://localhost:${PORT}`));
