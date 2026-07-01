# 📧 Real Email OTP Setup (Gmail SMTP — 100% Free)

NoteFlow sends OTP codes via email using Gmail's free SMTP relay. No paid service needed — just a Gmail account.

---

## Step 1 — Enable 2-Step Verification on your Google account

1. Go to https://myaccount.google.com/security
2. Turn on **2-Step Verification** (required to create App Passwords)

## Step 2 — Create a Gmail App Password

1. Go to https://myaccount.google.com/apppasswords
2. App name: type `NoteFlow`
3. Click **Create**
4. Copy the **16-character password** shown (e.g. `abcd efgh ijkl mnop`)
5. Remove the spaces → use it as one continuous string: `abcdefghijklmnop`

> ⚠️ This is NOT your normal Gmail password. It's a special app-only password.

## Step 3 — Configure the app

### Local development:
Edit `backend/src/main/resources/application.properties`:
```properties
spring.mail.username=youremail@gmail.com
spring.mail.password=abcdefghijklmnop
app.email.from=youremail@gmail.com
app.email.enabled=true
```

### On Render (live deployment):
Add these environment variables in Render Dashboard → Environment:
```
GMAIL_ADDRESS       = youremail@gmail.com
GMAIL_APP_PASSWORD  = abcdefghijklmnop
EMAIL_ENABLED       = true
```

## That's it! Restart the backend — OTPs now arrive in real inboxes. ✅

---

## Testing without email (Dev Mode)

When `app.email.enabled=false` (the default), the OTP is:
- Printed in the **Spring Boot console** (terminal logs)
- Also shown on screen on the login page, for convenience

Look for: `=== DEV OTP for user@example.com: 123456 ===`

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| "Authentication failed" | Make sure you're using the App Password, not your real Gmail password |
| Emails go to spam | Normal for new Gmail senders — check spam/promotions folder |
| "Username/password not accepted" | 2-Step Verification must be ON before App Passwords work |
| Gmail blocks sign-in | Go to https://myaccount.google.com/lesssecureapps — but App Passwords (above) is the recommended modern way |
| Daily limit reached | Free Gmail SMTP allows ~500 emails/day — more than enough for personal projects |

---

## Want a different free email provider?

### SendGrid (100 emails/day free, no Gmail account needed):
```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=YOUR_SENDGRID_API_KEY
```

### Brevo (formerly Sendinblue — 300 emails/day free):
```properties
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=your_brevo_login
spring.mail.password=YOUR_BREVO_SMTP_KEY
```

Just swap the `spring.mail.host`, `username`, and `password` — everything else in the app works unchanged.
