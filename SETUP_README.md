## CA Sahayak — Setup Guide

### Step 1: Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click **Add Project** → name it `CASahayak`
3. Enable **Google Analytics** (optional)
4. In the left panel → **Authentication** → **Get Started**
   - Enable **Email/Password** provider
   - Enable **Google** provider (add your SHA-1 fingerprint)
5. In the left panel → **Firestore Database** → **Create Database**
   - Start in **test mode** (you can add rules later)
6. Click **Project Settings** → **Add App** → select Android
   - Package name: `com.casahayak.app`
   - Download `google-services.json`
   - Place it at: `app/google-services.json`

---

### Step 2: Groq API Key

1. Sign up free at [console.groq.com](https://console.groq.com)
2. Go to **API Keys** → **Create API Key**
3. Copy the key

Open `local.properties` (at project root) and add:
```
GROQ_API_KEY=gsk_your_key_here
```

> ⚠️ **Never commit local.properties to git.** It's already in `.gitignore` by default.

---

### Step 3: AdMob Setup

1. Sign up at [admob.google.com](https://admob.google.com)
2. Create an app → get your **App ID** (format: `ca-app-pub-XXXXX~XXXXX`)
3. Create a **Banner Ad Unit** → get your **Ad Unit ID**
4. Add to `local.properties`:
```
ADMOB_APP_ID=ca-app-pub-XXXXX~XXXXX
```
5. In `util/Constants.kt`, replace `ADMOB_BANNER_AD_UNIT_ID` with your real Banner Ad Unit ID.

> During development, the app uses Google's test IDs so you won't receive real ads.

---

### Step 4: Google Sign-In SHA-1

Run in terminal (from your project root):
```bash
./gradlew signingReport
```
Copy the **SHA-1** from the `debug` variant and add it to Firebase → Project Settings → Your App → Add Fingerprint.

Also add to **Google Cloud Console** → APIs & Services → Credentials → OAuth 2.0 Client (Android).

---

### Step 5: Google Play Billing

1. Create an app in [Google Play Console](https://play.google.com/console)
2. Go to **Monetization** → **Subscriptions** → Create a subscription
   - Product ID: `premium_monthly` (matches `Constants.PREMIUM_SUBSCRIPTION_ID`)
   - Price: ₹999/month
3. The app must be uploaded to at least **Internal Testing** track for billing to work.

---

### Step 6: Build & Run

```bash
# Debug build
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug
```

Open in **Android Studio Hedgehog (2023.1.1)** or newer for best compatibility.

---

### Firestore Security Rules (Recommended)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /responses/{docId} {
      allow read, write: if request.auth != null
        && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null
        && request.auth.uid == request.resource.data.userId;
    }
    match /usage/{docId} {
      allow read, write: if request.auth != null
        && request.auth.uid == resource.data.userId;
    }
  }
}
```

---

### Project Structure

```
app/src/main/java/com/casahayak/app/
├── data/
│   ├── model/          → Data classes
│   ├── remote/         → Groq API (Retrofit)
│   ├── repository/     → All repositories
│   └── prompt/         → AI prompt templates
├── di/                 → Hilt DI modules
├── ui/
│   ├── theme/          → Material 3 theme
│   ├── navigation/     → NavGraph
│   ├── components/     → Reusable UI
│   ├── splash/
│   ├── onboarding/
│   ├── auth/
│   ├── dashboard/
│   ├── generator/
│   ├── history/
│   ├── account/
│   └── upgrade/
└── util/               → Constants, helpers
```
