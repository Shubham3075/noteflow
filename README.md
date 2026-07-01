# 🚀 NoteFlow - Full Stack Notes & Todo Application

> Google Keep-inspired notes & task manager with mobile OTP login, built with AngularJS + Spring Boot + MSSQL

---

## 📦 Project Structure

```
noteflow/
├── backend/          ← Spring Boot (Java 17)
│   ├── src/main/java/com/noteflow/
│   │   ├── controller/   ← REST APIs
│   │   ├── service/      ← Business logic
│   │   ├── entity/       ← JPA entities
│   │   ├── repository/   ← Spring Data repos
│   │   ├── security/     ← JWT auth
│   │   ├── dto/          ← Data transfer objects
│   │   └── config/       ← Security & CORS config
│   └── src/main/resources/
│       └── application.properties
│
└── frontend/         ← AngularJS 1.8
    ├── index.html
    └── src/app/
        ├── auth/       ← OTP login
        ├── dashboard/  ← Stats overview
        ├── notes/      ← Google Keep-style notes
        ├── todos/      ← Task manager
        ├── settings/   ← Profile & preferences
        ├── admin/      ← Admin panel
        └── shared/     ← API service
```

---

## ⚙️ Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ |
| SQL Server | 2019+ (or Azure SQL) |
| Node.js | 18+ (for frontend dev server) |
| Browser | Chrome / Firefox / Edge |

---

## 🗄️ Database Setup (MSSQL)

```sql
-- Run in SQL Server Management Studio (SSMS)
CREATE DATABASE noteflow_db;
GO

-- Create a login (or use SA for dev)
CREATE LOGIN noteflow_user WITH PASSWORD = 'YourPassword123!';
USE noteflow_db;
CREATE USER noteflow_user FOR LOGIN noteflow_user;
ALTER ROLE db_owner ADD MEMBER noteflow_user;
GO
```

> **Tip:** Hibernate will auto-create tables on first run (`spring.jpa.hibernate.ddl-auto=update`)

### To make your first user an ADMIN:
```sql
UPDATE users SET role = 'ADMIN' WHERE mobile_number = '+91XXXXXXXXXX';
```

---

## 🔧 Backend Setup

### 1. Configure Database
Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=noteflow_db;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourPassword123!

app.jwt.secret=NoteFlowSuperSecretKey2024VeryLongAndSecureJWTSecretKeyForSigning
```

### 2. Build & Run

```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

Backend starts at: **http://localhost:8080/api**

### 3. Test API
```bash
# Send OTP
curl -X POST http://localhost:8080/api/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"mobileNumber": "+919876543210"}'

# Response includes OTP in dev mode (remove in production!)
```

---

## 🌐 Frontend Setup

### Option A – Direct Browser (no server needed)
Open `frontend/index.html` directly in Chrome.

> ⚠️ Note: Due to CORS, use a local server instead.

### Option B – Live Server (recommended)
```bash
cd frontend
npx live-server --port=4200
```

Frontend runs at: **http://localhost:4200**

---

## ✨ Features

### 👤 Authentication
- Mobile number + OTP login (6-digit)
- JWT tokens (24-hour expiry)
- Auto-register new users on first login
- 30-second OTP resend timer

### 📝 Notes (Google Keep Style)
- Create, edit, delete notes
- 8 color themes per note
- Pin important notes (shown at top)
- Archive notes (hidden from main view)
- Masonry grid layout
- Full-text search

### ✅ To-Do
- Add tasks with title, description, category
- 4 priority levels: Low / Medium / High / Urgent
- Due date support
- Filter by status & priority
- One-click completion toggle
- Edit & delete tasks

### ⚙️ Settings
- Upload profile photo
- Change display name
- Dark mode / Light mode toggle
- Collapsible sidebar

### 🛡️ Admin Panel
- View all users with stats
- Toggle user active/inactive status
- Delete users
- View & delete all notes
- View & delete all tasks

---

## 🔌 API Reference

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/send-otp` | Send OTP to mobile |
| POST | `/auth/verify-otp` | Verify OTP & get JWT |

### Notes
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/notes` | Get user's notes |
| POST | `/notes` | Create note |
| PUT | `/notes/{id}` | Update note |
| PATCH | `/notes/{id}/pin` | Toggle pin |
| PATCH | `/notes/{id}/archive` | Toggle archive |
| DELETE | `/notes/{id}` | Delete note |
| GET | `/notes/archived` | Get archived notes |

### Todos
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/todos` | Get user's todos |
| POST | `/todos` | Create todo |
| PUT | `/todos/{id}` | Update todo |
| PATCH | `/todos/{id}/toggle` | Toggle complete |
| DELETE | `/todos/{id}` | Delete todo |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users/me` | Get current user |
| PUT | `/users/me` | Update profile |
| POST | `/users/me/photo` | Upload profile photo |
| GET | `/users/admin/all` | (Admin) Get all users |
| PATCH | `/users/admin/{id}/toggle-status` | (Admin) Toggle status |
| DELETE | `/users/admin/{id}` | (Admin) Delete user |

---

## 🏭 Production Deployment

### Backend
```bash
mvn clean package -DskipTests
java -jar target/noteflow-backend-1.0.0.jar \
  --spring.datasource.url=YOUR_PROD_DB_URL \
  --spring.datasource.password=YOUR_PROD_PASSWORD \
  --app.jwt.secret=YOUR_SECURE_64_CHAR_SECRET
```

### Frontend
- Upload `frontend/` folder to any static host (Nginx, S3, Vercel, Netlify)
- Update `BASE` URL in `src/app/shared/api.service.js`

### SMS Integration (Production)
Replace mock OTP in `AuthService.java` with real SMS provider:
- **Twilio** (international)
- **MSG91** (India)
- **Fast2SMS** (India, free tier)

---

## 🎨 UI Features
- **Dark/Light mode** with smooth transition
- **Responsive** – works on mobile, tablet, desktop
- **Masonry grid** for notes (like Google Keep)
- **Animated** modals and toast notifications
- **Collapsible sidebar** with icon-only mode
- **Color-coded** priority badges
- **Glassmorphism** auth page with animated blobs

---

## 🐛 Troubleshooting

| Issue | Fix |
|-------|-----|
| CORS error | Check `app.cors.allowed-origins` in properties |
| DB connection failed | Verify MSSQL is running, check port 1433 |
| OTP not working | Check console logs; OTP is printed to Spring logs in dev |
| Photo upload fails | Create `uploads/profiles/` directory manually |
| JWT expired | Re-login; token lasts 24 hours |

---

## 📄 Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | AngularJS 1.8.3 |
| Backend | Spring Boot 3.2, Java 17 |
| Database | Microsoft SQL Server |
| Auth | JWT (jjwt 0.11.5) |
| ORM | Spring Data JPA + Hibernate |
| Security | Spring Security |
| Styling | Pure CSS (no framework) |
| Icons | Font Awesome 6.5 |
| Fonts | Inter + Plus Jakarta Sans |

---

*Built with ❤️ — NoteFlow v1.0.0*
