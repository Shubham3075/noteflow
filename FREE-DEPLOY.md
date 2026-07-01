# 🆓 NoteFlow — Free Lifetime Deployment Guide

> Deploy NoteFlow **100% free, forever** using:
>
> - 🗄️ **Neon.tech** — Free PostgreSQL database (500MB)
> - 🚀 **Render.com** — Free Spring Boot backend hosting
> - 🌐 **Vercel** — Free AngularJS frontend hosting

**Total cost: $0/month, forever.**

---

## 📋 What You Need

- A GitHub account (free)
- A Neon.tech account (free)
- A Render.com account (free)
- A Vercel account (free)
- ~20 minutes

---

## 🗄️ Step 1 — Free Database on Neon.tech

Neon gives you a free PostgreSQL database that never expires.

1. Go to **https://neon.tech** → Sign up free (use GitHub login)
2. Click **"New Project"**

   - Name: `noteflow`
   - Region: pick closest to you (e.g. `US East`)
   - Click **Create Project**
3. On the dashboard, click **"Connection string"**

   - Select **JDBC** format
   - Copy the string — it looks like:

   ```
   jdbc:postgresql://ep-cool-name-123456.us-east-2.aws.neon.tech/neondb?user=neondb_owner&password=XXXXXX&sslmode=require
   ```

   💾 **Save this — you'll need it in Step 2**
4. That's it! Neon auto-creates tables when Spring Boot starts.

---

## 🐙 Step 2 — Push Code to GitHub

1. Create a new repo at **https://github.com/new**

   - Name: `noteflow`
   - Visibility: Private (recommended)
   - Do NOT initialize with README
2. Push the code:

   ```bash
   cd noteflow
   git init
   git add .
   git commit -m "🚀 Initial NoteFlow commit"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/noteflow.git
   git push -u origin main
   ```

---

## 🚀 Step 3 — Deploy Backend on Render.com (Free)

Render's free tier runs your Spring Boot app 24/7 (spins down after 15min idle, starts in ~30sec on next request).

1. Go to **https://render.com** → Sign up with GitHub
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repo → Select **`noteflow`**
4. Configure the service:

   | Field          | Value            |
   | -------------- | ---------------- |
   | Name           | `noteflow-api` |
   | Region         | Oregon (US West) |
   | Branch         | `main`         |
   | Root Directory | `backend`      |
   | Runtime        | **Docker** |
   | Instance Type  | **Free**   |
5. Click **"Advanced"** → **"Add Environment Variable"** — add these one by one:

   | Key              | Value                                                     |
   | ---------------- | --------------------------------------------------------- |
   | `DATABASE_URL` | _(paste your Neon JDBC string from Step 1)_             |
   | `JWT_SECRET`   | _(any long random string, 64+ chars)_                   |
   | `FRONTEND_URL` | `https://noteflow.vercel.app` _(update after Step 4)_ |
   | `PORT`         | `8080`                                                  |
6. Click **"Create Web Service"**

   ⏳ First deploy takes **5–10 minutes** (Maven downloads dependencies, builds Docker image).
7. Once deployed, your backend URL will be:

   ```
   https://noteflow-api.onrender.com
   ```

   💾 **Save this URL — you need it in Step 4**
8. Test it:

   ```
   https://noteflow-api.onrender.com/api/actuator/health
   ```

   Should return: `{"status":"UP"}`

---

## 🌐 Step 4 — Deploy Frontend on Vercel (Free)

Before deploying, update the API URL in your frontend:

1. Open `frontend/src/app/shared/api.service.js`

   Find this line:

   ```javascript
   var BASE = 'http://localhost:8080/api';
   ```

   Change it to your Render backend URL:

   ```javascript
   var BASE = 'https://noteflow-api.onrender.com/api';
   ```
2. Also update `frontend/src/app/auth/auth.controller.js`:

   ```javascript
   // Find:
   var BASE = 'http://localhost:8080/api';
   // Change to:
   var BASE = 'https://noteflow-api.onrender.com/api';
   ```
3. Commit and push:

   ```bash
   git add .
   git commit -m "Set production API URL"
   git push
   ```
4. Go to **https://vercel.com** → Sign up with GitHub
5. Click **"Add New"** → **"Project"**
6. Import your `noteflow` GitHub repo
7. Configure:

   | Field            | Value             |
   | ---------------- | ----------------- |
   | Framework Preset | **Other**   |
   | Root Directory   | `frontend`      |
   | Build Command    | _(leave empty)_ |
   | Output Directory | `.`             |
8. Click **"Deploy"**

   ⏳ Takes ~1 minute.
9. Your frontend URL will be:

   ```
   https://noteflow.vercel.app
   ```

   (or similar — Vercel shows you the exact URL)

---

## 🔁 Step 5 — Update CORS on Render

Now update the backend to allow your Vercel frontend:

1. Go to Render dashboard → `noteflow-api` → **Environment**
2. Update `FRONTEND_URL`:

   ```
   https://noteflow.vercel.app
   ```

   (use your actual Vercel URL)
3. Click **"Save Changes"** — Render auto-redeploys.

---

## 👑 Step 6 — Make Yourself Admin

1. Go to **https://neon.tech** → Your project → **SQL Editor**
2. Run:

   ```sql
   UPDATE users SET role = 'ADMIN' 
   WHERE email = 'you@example.com';
   ```

   (replace with your mobile number, including country code)
3. Log out and log back in — Admin panel now appears in sidebar!

---

## ✅ Done! Your Live URLs

|                          | URL                                                       |
| ------------------------ | --------------------------------------------------------- |
| 🌐**Frontend**     | `https://noteflow.vercel.app`                           |
| 🔗**Backend API**  | `https://noteflow-api.onrender.com/api`                 |
| 💚**Health Check** | `https://noteflow-api.onrender.com/api/actuator/health` |
| 🗄️**Database**   | Neon.tech dashboard                                       |

---

## 🔄 Auto-Deploy on Code Changes

After the initial setup, every `git push` to `main` automatically:

- **Render** rebuilds and redeploys the backend
- **Vercel** rebuilds and redeploys the frontend

Zero manual work needed.

---

## ⚠️ Free Tier Limits & Notes

| Service | Free Limit                                   | Notes                                         |
| ------- | -------------------------------------------- | --------------------------------------------- |
| Neon    | 500MB storage, 1 compute unit                | More than enough for thousands of notes       |
| Render  | 750 hours/month, spins down after 15min idle | First request after idle takes ~30sec to wake |
| Vercel  | 100GB bandwidth/month                        | Essentially unlimited for personal use        |

### About Render's "spin down":

- After 15 minutes of no traffic, the backend sleeps
- The next request wakes it up in ~25–30 seconds
- After that, it's fast again
- To avoid this: ping the health endpoint every 14 minutes using **UptimeRobot** (also free!)

### UptimeRobot (keep Render awake for free):

1. Go to **https://uptimerobot.com** → Sign up free
2. Add Monitor → HTTP(s)
3. URL: `https://noteflow-api.onrender.com/api/actuator/health`
4. Interval: **5 minutes**
5. This pings your backend every 5 minutes → it never sleeps! ✅

---

## 🐛 Troubleshooting

| Problem                    | Fix                                                                       |
| -------------------------- | ------------------------------------------------------------------------- |
| Render build fails         | Check logs → usually a Maven dependency issue                            |
| "Connection refused" on DB | Verify the Neon JDBC URL is correct in Render env vars                    |
| CORS error                 | Make sure`FRONTEND_URL` in Render matches your exact Vercel URL         |
| OTP shown in logs          | Check Render logs (Dashboard → Logs) — OTP is printed there in dev mode |
| Frontend blank page        | Open browser console — likely API URL mismatch                           |
| Render app sleeping        | Set up UptimeRobot as described above                                     |

### View Render logs:

Dashboard → noteflow-api → **Logs** tab (live streaming)

### View Neon DB contents:

Neon dashboard → **SQL Editor** → `SELECT * FROM users;`

---

## 📧 Real Email OTP in Production

NoteFlow sends OTP codes via **Gmail SMTP** — 100% free, no third-party SMS service needed.

See **`SETUP-EMAIL.md`** in this project for full step-by-step instructions. Quick summary:

1. Enable 2-Step Verification on your Google account
2. Create an App Password at https://myaccount.google.com/apppasswords
3. Add these env vars on Render:
   ```
   GMAIL_ADDRESS      = youremail@gmail.com
   GMAIL_APP_PASSWORD = your16charapppassword
   EMAIL_ENABLED      = true
   ```
4. Redeploy — OTPs now arrive in real inboxes ✅

Free Gmail SMTP allows ~500 emails/day, more than enough for personal projects.

---

*NoteFlow — Free forever. Built with AngularJS + Spring Boot + PostgreSQL*
