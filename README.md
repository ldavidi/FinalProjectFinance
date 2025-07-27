# FinalProjectFinance

An Android personal finance manager built with modern Kotlin, Jetpack Compose, and real-time cloud data. Track your savings, investments, loans, and see interactive charts—all powered by best-in-class libraries and APIs.

---

## 🚀 Features

- **Real-time sync** with Firebase Firestore
- **MVVM + Kotlin Flow** architecture
- **Jetpack Compose & Material 3**
- **Retrofit 2 + Moshi** for type-safe JSON parsing
- **Alpha Vantage API** for live stock quotes
- **MPAndroidChart** for interactive line & pie charts
- **Atomic Firestore transactions** to keep balances correct
- **Projection utilities** for balance & amortization forecasts

---

## 🏗 Architecture

com.example.finalprojectfinance
├─ data
│ ├─ model ← Account, Holding, Loan, Transaction
│ ├─ source ← FirestoreDataSource: realtime listeners & coroutine Flow
│ └─ repository ← FinanceRepository: single entry point for data ops
├─ ui
│ ├─ screen ← Compose screens (Savings, Portfolio, Loans, Analytics)
│ └─ viewmodel ← ViewModels exposing StateFlows
├─ util
│ └─ ProjectionUtils.kt ← balance-vs-time calculators
└─ MainActivity.kt & MyFinanceApp.kt ← app entry & Firebase init

---

## 🔧 Libraries & Tools

- **Kotlin & Coroutines**
- **Jetpack Compose & Material 3**
- **AndroidX Lifecycle** (ViewModel & Flow)
- **Retrofit 2** + **moshi-kotlin-codegen**
- **OkHttp Logging Interceptor**
- **Firebase Firestore-KTX & Auth-KTX**
- **MPAndroidChart**
- **KAPT** for annotation processing

---

## 🌐 APIs & Real-time

1. **Alpha Vantage** – on-demand stock pricing
2. **Firebase Firestore** – real-time collections:
    - `accounts`, `transactions`, `holdings`, `loans`
    - uses `FieldValue.serverTimestamp()` & coroutine `await()`
3. **Firestore transactions** for atomic balance updates

---

## 📈 Charts & Projections

- **Savings Projection**: 12-month forecast of balances
- **Loan Amortization**: monthly schedule for any term
- **Pie Chart Analytics**: color-coded Assets (green), Loans (red), Deposits/Accounts (blue)
- **Compose wrappers** around MPAndroidChart

---

## 🏁 Getting Started

1. **Clone** & open in Android Studio
2. **Place** `google-services.json` under `app/`
3. **Insert** your Alpha Vantage key in `PortfolioViewModel` (or via gradle properties)
4. **Sync & run** on emulator (with Google Play) or device
5. **Enable** Firestore rules (dev only):
   ```js
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} { allow read, write: if true; }
     }
   }
6. Anonymous auth auto-runs in MyFinanceApp

7. Explore: add accounts, transactions, holdings, loans, and watch charts update live!

---

## 🛡️ Security & Next Steps

1. Lock down Firestore rules before release
2. Add user auth (Email/Google)
3. Support multiple currencies & exchange-rate API
4. Expand analytics: budgets, expense categories, trends

