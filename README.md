<h1 align="center">
  <img src="app/src/main/ic_launcher-playstore.png" alt="CA Sahayak Logo" width="120" />
  <br>
  CA Sahayak
</h1>

<p align="center">
  <b>Your Intelligent App for Chartered Accountant Workflow Automation</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Backend-Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black" />
  <img src="https://img.shields.io/badge/AI-Groq_API-000000?style=flat-square" />
</p>

---

## 📖 Overview

**CA Sahayak** is a production-ready, feature-rich Android application designed specifically to assist Chartered Accountants, tax professionals, and financial advisors. By leveraging cutting-edge AI (powered by Groq APIs), the app streamlines daily tasks such as generating professional tax notices, drafting client replies, summarizing complex financial documents, and organizing client data.

Built with modern Android development standards, CA Sahayak offers a beautiful, premium user interface, robust state management, and seamless integration with third-party services like Firebase, Google built-in authentication, AdMob, and Google Play Billing.

---

## ✨ Key Features

### 🤖 AI-Powered Generators
*   **Draft Legal Notices:** Instantly generate professional, legally sound notices and responses.
*   **Tax Summaries & Explanations:** Simplify complex tax regulations and provide easy-to-understand summaries.
*   **Contextual Email Drafts:** Create tailored, professional emails for client communication in seconds.

### 🔐 Secure Authentication (Firebase)
*   **Email & Password:** Standard, secure credential-based login and registration.
*   **Google Sign-In:** One-tap, frictionless authentication via Google accounts.
*   **Password Recovery:** Built-in forgot password flow.

### 💎 Freemium Model & Monetization
*   **Free Tier:** Users get full access to the AI generator with a daily usage limit (e.g., 5 requests/day).
*   **AdMob Integration:** Strategic placement of banner and interstitial ads for free-tier users.
*   **Premium Subscription (Google Play Billing):** Users can upgrade to a "Pro" plan to unlock unlimited AI generations and an entirely ad-free experience.

### 📚 History & Content Management
*   **Cloud Sync:** All generated content is securely saved to Firebase Cloud Firestore.
*   **History Ledger:** View, copy, or manage past generated documents organized chronologically.
*   **Real-time Updates:** Data syncs seamlessly across devices.

### 🎨 Premium UI/UX
*   **Jetpack Compose:** Constructed entirely using modern declarative UI, ensuring smooth animations and a responsive feel.
*   **Material Design 3:** Adheres to the latest design guidelines with dynamic theming, elegant typography, and carefully crafted components.
*   **Dark Mode Support:** Beautiful aesthetics adapted for both light and dark system preferences.

---

## 🏗️ Architecture & Tech Stack

This project strictly follows the **Modern Android Development (MAD)** guidelines, utilizing a robust **MVVM (Model-View-ViewModel)** architecture ensuring separation of concerns, testability, and scalability.

*   **UI Toolkit:** Jetpack Compose (Material 3)
*   **Language:** Kotlin (with Coroutines and Flows for asynchronous programming)
*   **Dependency Injection:** Hilt (Dagger)
*   **Navigation:** Jetpack Navigation Compose
*   **Remote Data/Backend:** Firebase Authentication, Cloud Firestore
*   **AI Integration:** Groq Free API (Llama 3 / Mixtral models) for ultra-fast text generation.
*   **Network Requests:** Retrofit2 & OkHttp3
*   **Monetization:** Google Mobile Ads (AdMob), Google Play Billing Library
*   **Local Storage/Preferences:** DataStore (Preferences)

---

## 🚀 Getting Started

To build and run this application locally, you'll need to set up several API keys and configuration files.

### 1. Prerequisites
*   Android Studio Ladybug (or newer)
*   JDK 17

### 2. Firebase Setup
1.  Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2.  Add an Android app to the project using the package name: `com.casahayak.app`.
3.  Download the `google-services.json` file and place it in the `app/` directory of this repository.
4.  Enable **Authentication** (Email/Password and Google providers).
5.  Enable **Firestore Database** and set appropriate security rules.

### 3. Groq API Setup
1.  Sign up for an API key at the [Groq Console](https://console.groq.com/).
2.  Open `app/src/main/java/com/casahayak/app/util/Constants.kt` and replace the placeholder with your key:
    ```kotlin
    const val GROQ_API_KEY = "gsk_your_actual_api_key_here"
    ```

### 4. AdMob Setup
1.  Register your app on [Google AdMob](https://apps.admob.com/).
2.  Update the `<meta-data android:name="com.google.android.gms.ads.APPLICATION_ID" ... />` in `AndroidManifest.xml` with your actual AdMob App ID.
3.  *(Optional for Debug)* The current ad units in `Constants.kt` use Google's test IDs. Replace them with your real ad unit IDs before publishing.

---

## 📱 Screenshots

*(Add screenshots of your application here once running!)*

<p align="center">
  <img src="https://via.placeholder.com/250x500.png?text=Login+Screen" width="200"/>
  <img src="https://via.placeholder.com/250x500.png?text=Dashboard" width="200"/>
  <img src="https://via.placeholder.com/250x500.png?text=AI+Generator" width="200"/>
  <img src="https://via.placeholder.com/250x500.png?text=History" width="200"/>
</p>

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome!
Feel free to check the [issues page](https://github.com/Naveenmehta121/CA-sahayak-app-/issues) if you want to contribute.

## 📝 License
This project is licensed under the [MIT License](LICENSE).
