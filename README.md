## Term Project
The Term Project SE331 Component-Based Software Development <br>
**project-01-the-anti-fake-news-system-check-it-off** <br>
**Deployment New Version Vue :** [View on Verce New Version Vue.js](https://project-01-the-anti-fake-news-system-check-it-off-8akrmm5yg.vercel.app) <br>
**Old Version (Reference) :** [View on Vercel Old Version Next.js](https://project-01-the-anti-fake-news-syste-eight.vercel.app/)

---

<br>

## Group Member : Check It Off
- **Name:** Nawapon Somruang  
- **Student ID:** 662115027  

Welcome to the **Check it off Anti-Fake News System** project.  
This project is designed to help identify and manage fake news using modern web technologies.  

---

<br>

## About the Project  
This web application allows users to:  

- **Dynamic News Fetching**  
  All news data is fetched in real-time using the **Google Sheets API**, ensuring up-to-date and synchronized data management.  

- **Home Page**  
  Displays all news articles from the Google Sheet API dynamically.  
  Users can browse, filter, and select individual news items to explore further.  

- **Detail Page**  
  Displays the full article information including title, author, date, and status.  
  Includes two main actions:
  - **Go to Vote Page** – Submit feedback and vote authenticity  
  - **View Comments** – Read community opinions  

- **Vote Page**  
  Collects user feedback with name, vote type (upvote/downvote), comments, and an optional image link.  

- **Community Comments**  
  Displays public opinions and reasoning to encourage transparent discussion.  

- **Real-time Status Update**  
  News credibility is automatically updated on the site:
  - **Upvotes > Downvotes → “Verified”**
  - **Downvotes > Upvotes → “Fake News”**
  - **Upvotes = Downvotes → "Unverified"**

- **Instant Refresh**  
  All vote and comment updates appear live — no page reload required.  

---

<br>

## Technologies Used  

This version is fully rebuilt with **Vue 3 + TypeScript + Vite**, improving both performance and modularity.

- **Frontend Framework** – **Vue 3 + TypeScript + Vite** - Build fast, modular, and type-safe UI components
- **UI Styling** - **TailwindCSS** - Design responsive and modern interfaces quickly
- **Data Source** – **Google Sheets API** - Fetch and update news data in real time
- **Package Management** – **Node.js & npm** - Manage dependencies and run build scripts
- **Deployment** – **Vercel** - Auto-deploy website with continuous integration
- **Version Control** - **Git & GitHub** - Track code changes and team collaboration

---

<br>

## Links

- **Vercel Deployment:** [View on Verce New Version Vue.js](https://project-01-the-anti-fake-news-system-check-it-off-8akrmm5yg.vercel.app) 
- **Local Project (GitHub):** [Repository](https://github.com/chartchai-class/project-01-the-anti-fake-news-system-check-it-off-Vue.git)
- **YouTube Demo:** [Video Link](https://www.youtube.com/watch?v=9BmjJhth1Tk)
- **Google Sheets Data:** [Spreadsheet](https://docs.google.com/spreadsheets/d/1xXd_djAF1jp7c5jrY-mzAwVYxSN9wbl_0RTpKKvH0AA/edit?usp=sharing)

---

<br>

## Note  
All commits in this repository represent the complete development process of rebuilding the **Check It Off** web application from the original **Next.js** framework to **Vue 3 + TypeScript + Vite**.  

Each commit documents one of the following:
- Adding new pages or components  
- Refactoring and fixing code issues during the migration process  
- Enhancing UI/UX and improving system integration with Google Sheets API  

> For detailed commit history of the original (Next.js) version,  
> please visit the following repository:  
> [project-01-the-anti-fake-news-syste-eight (Next.js Version)](https://github.com/chartchai-class/project-01-the-anti-fake-news-system-check-it-off.git).

This repository contains **the full implementation** of the new Vue version,  
including all page structures, components, API connections, and deployment configuration.

---

<br>

## Getting Started (Local)  

1. Clone this project  

```bash
git clone https://github.com/chartchai-class/project-01-the-anti-fake-news-system-check-it-off-Vue.git
cd check-it-off
```

2. Install dependencies

```bash
npm install
# or
yarn install
# or
pnpm install
```

3. Start the development server

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
```

<br>

## System Features

### 1. News Management System
- **Complete News Display**: View all news articles added to the system.  
- **Detailed Information**: Each article includes:
  - **Title**
  - **Description**
  - **Author**
  - **Publication Date**
  - **Status**: Verified, Fake News, Under Review
  - **Associated Images**

### 2. News Filtering System
- **Category-based Filtering**:  
  - All News – Display all articles  
  - Verified – Confirmed authentic news  
  - Fake News – Confirmed false information  
  - Under Review – Pending verification
- **Display Options**: Choose the number of articles per page (6, 12, 24)

### 3. Dashboard & Statistics
- **Real-time Statistics**: Summary of news by category  
- **Quick Overview**: Instant visibility of authentic vs. fake news ratios  
- **Administrative Tools**: Easy monitoring for system administrators

### 4. Voting & Comment System
- **Community Voting**: Users can agree/disagree with news status  
- **Comment System**: Provide reasoning and source citations  
- **Community Engagement**: Crowdsourced news verification

### 5. Analytics & Reporting (Planned)
- **Historical Statistics**: Track verified vs. fake news over time  
- **User Engagement Metrics**: Monitor community participation  
- **Data Quality Assessment**: Evaluate system reliability

<br>

## Technical Architecture

- Frontend
  - Framework: React with Next.js
  - Styling: Modern CSS/Component styling
  - State Management: React hooks and context

- Backend
  - API Routes: Next.js API endpoints
  - Data Storage: JSON files (db.json) with Google Sheets integration
  - Real-time Updates: Live data synchronization
 
- Deployment
  - Platform: Vercel
  - CI/CD: Automated deployment pipeline

 <br>

## Usage Example

1. **Access Website** → View dashboard with news category summary  
2. **Select Category** → Choose specific news type (e.g., Fake News)  
3. **Read Articles** → Click on news items for detailed information  
4. **Read Comments** → View comments provided by other users for each article  
5. **Participate** → Vote and comment on news authenticity  
6. **Contribute** → Add new articles for community verification

<br>

## System Highlights
- **User-Friendly Interface**: Intuitive design for all user levels
- **Crowdsourced Verification**: Community-driven fact-checking
- **Scalable Architecture**: Ready for future enhancements
- **Future-Ready**: Extensible for AI integration and external APIs
- **Real-time Performance**: Live updates without page refresh

<br>

## Future Enhancements
- **AI Integration**: Automated preliminary fact-checking to assist community verification  
- **External APIs**: Integration with third-party news verification services  
- **Database Migration**: Implement SQL or NoSQL database for better data management  
- **Mobile App**: Develop native mobile applications for iOS and Android  
- **Notification System**: Real-time alerts for news updates and status changes

<br>

## Contributing
This project is currently maintained as a solo effort.  
If you notice any mistakes or have suggestions, I take full responsibility and welcome all feedback to improve the system further.

<br>

## License
This project is developed for educational purposes as part of **Term Project 1** in the course **SE331 - Component-Based Software Development**.

<br>

## Acknowledgments
Thank you for taking the time to review this project. Your feedback and guidance are greatly appreciated as I continue to learn and improve my software development skills.
