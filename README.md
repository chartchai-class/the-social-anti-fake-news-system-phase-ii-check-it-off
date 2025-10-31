## Term Project
The Term Project SE331 Component-Based Software Development <br>
- **project-01-the-anti-fake-news-system-check-it-off Phase II**. <br>
- **Frontend Deployment:** https://the-social-anti-fake-news-system-ph.vercel.app/ (but it can't use various functions because it can't connect to the backend yet). <br>
- **Backend Deployment:** I have to admit that the backend deployment was not completed because the frontend was deployed but could not connect to the backend. <br> <br>
Ajan may run the backend and frontend separately on machines to test the system. <br>


## Group Member : Check It Off Phase II
**Name:** Nawapon Somruang  **Student ID:** 662115027 
<br>

## Check it off Anti-Fake News System Phase II.
**Local Testing:** Ajan may download the project files from **[Google Drive](https://drive.google.com/drive/folders/1kmDnCV_U07wUAyrRj72mjnnJTvCtjANi?usp=sharing),** and then run the frontend and backend separately on machines:
<br>

**1. Run Frontend:**  
```bash
# Navigate to the frontend folder of the project
cd frontend

# Install all necessary project dependencies
npm install

# Start the frontend development server (usually runs at http://localhost:5173)
npm run dev
````

The frontend will run at `http://localhost:5173` (or the port shown when running `npm run dev`)

**2. Run Backend**
```bash
# Navigate to the Spring Boot project folder
cd backend/checkitoff-backend

# Run the backend on Windows
.\mvnw.cmd spring-boot:run

# Run the backend on Mac/Linux
./mvnw spring-boot:run
```
**Or**
open the checkitoff-backend project in **IntelliJ IDEA** and run the backend by clicking the green Run button on the CheckitoffBackendApplication class.

The backend will run at `http://localhost:8080` (or the port configured in your settings)

---
