# Android Authentication Architecture

[![Kotlin](https://img.shields.io/badge/kotlin-2.4.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose](https://img.shields.io/badge/compose-2026.06.00-green.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/hilt-2.60-orange.svg)](https://dagger.dev/hilt/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A production-ready authentication foundation for Android applications. This project implements a secure, scalable, and reactive authentication system following **Clean Architecture** principles, specifically designed for **JWT with Refresh Token authentication**.

The goal is to provide a reusable, production-ready template that can be directly integrated into future Android applications, handling the complexities of session management, secure persistence, and reactive UI updates with minimal setup.

---

## 🚦 Authentication Strategy

This architecture implements a robust dual-token strategy:

- **JWT Access Tokens**: Proactive authentication for standard requests.
- **Refresh Token Flow**: Reactive token recovery handled automatically at the network layer.
- **OkHttp Authenticator**: Thread-safe, centralized handling of `401 Unauthorized` responses.
- **Secure Persistence**: Encrypted session storage using Android Keystore.
- **Session Restoration**: Seamless session recovery after process death or app restarts.
- **Automatic Header Injection**: Authorization Bearer headers are handled centrally at the network layer.

---

## 🚀 Features

- **Clean Architecture**: Decoupled layers (Presentation, Domain, Data, Core) for high maintainability and testability.
- **Jetpack Compose**: Modern, declarative UI with reactive state management.
- **Hilt**: Industry-standard dependency injection for modularity.
- **Secure Persistence**: 
    - Encrypted **DataStore Preferences**.
    - **Android Keystore** integration for hardware-backed security.
    - **AES-GCM** encryption with random IVs for every write.
- **Reactive Session Management**: Real-time UI updates across the app when the authentication state changes.
- **Robust Networking**:
    - **Retrofit** integration with **Kotlin Serialization**.
    - **AuthInterceptor**: Proactive **Bearer Token injection**.
    - **TokenAuthenticator**: Reactive, thread-safe **Refresh Token handling** for 401 errors.
    - Centralized error mapping for clean architecture.
- **Developer Friendly**: 
    - `FakeAuthRepository` for rapid local testing.
    - `Debug401Interceptor` to verify automatic refresh flows offline.
    - **Timber** integration for clean, lifecycle-aware logging.

---

## 🏗️ Architecture

The project follows a strict layered architecture:

```text
app
├── core           # Generic utilities (Security, Network, Result)
├── app/session    # Session orchestration and state management
├── domain         # Business logic (Use Cases, Repository interfaces, Models)
├── data           # Infrastructure (Repo implementations, Local/Remote storage)
└── presentation   # UI logic (ViewModels, Compose Screens)
```

### Dependency Rules
*   **Domain** depends on nothing.
*   **Data** depends on **Domain** and **Core**.
*   **Presentation** depends on **Domain**.
*   **App** (Session) coordinates between **Data** and **Domain**.

---

## 🛠️ Session Architecture

The authentication system is built around three distinct components:

### SessionProvider
The **in-memory source of truth** for the current authentication state. It exposes the session as a reactive `StateFlow` so the UI and network layers can respond immediately to token updates or logout events.

### SessionStorage
Abstracts how sessions are persisted. It currently uses **DataStore Preferences** combined with **Android Keystore** encryption. The storage is abstracted by an interface, allowing the persistence mechanism to be replaced (e.g., with Room) without affecting the rest of the architecture.

### SessionManager
The orchestrator responsible for the authentication lifecycle (Restore, Create, Clear). It synchronizes the `SessionProvider`'s memory state with the `SessionStorage`'s persistent state.

---

## 🔐 Security & Network Implementation

### Persistent Storage Flow
When a session is saved:
1.  `UserSession` is serialized to JSON.
2.  The JSON string is encrypted using **AES-GCM** via the `AndroidKeystoreCryptoService`.
3.  The combined data is stored in **DataStore**.

### Network Resilience
- **AuthInterceptor**: Attaches the `Authorization` header. It includes a safety check to prevent overwriting new tokens during a retry flow.
- **TokenAuthenticator**: Handles `401 Unauthorized` responses. It performs a **synchronized refresh** to handle multiple concurrent failures gracefully. If the refresh succeeds, the request is automatically retried; if it fails, the session is cleared.
- **Circular Dependency Management**: Uses `dagger.Lazy` to break the common `OkHttpClient -> Authenticator -> Repository -> Api -> Retrofit -> OkHttpClient` loop.

---

## 🛡️ Production Security Recommendations

When moving from this template to a live production application, consider the following hardening steps:

### 1. Enable R8/ProGuard Obfuscation
Ensure `minifyEnabled true` is set in your `build.gradle.kts`. This obfuscates your class names and logic, making it much harder for an attacker to perform memory-dump analysis or reverse-engineer your session models.

### 2. Domain-Limited Interceptor
Update the `AuthInterceptor` to only attach the `Authorization` header to your specific API domain. This prevents accidentally leaking your JWT to third-party services (like analytics or maps) if you add more network calls later.

### 3. Certificate Pinning
For high-security applications, implement **SSL Certificate Pinning** within the `OkHttpClient`. This prevents Man-in-the-Middle (MitM) attacks even if a user's device is compromised with a malicious root certificate.

### 4. Root & Emulator Detection
Consider adding a check during app startup to detect if the device is rooted or running on an emulator, and restrict authentication features accordingly to prevent advanced memory tampering.

---

## 🧪 Testing the Auth Flow

The project includes built-in tools to test the entire lifecycle locally:

- **FakeAuthRepository**: Simulates network responses and token updates.
- **Debug401Interceptor**: A network interceptor that forces a 401 error locally when a specific test endpoint is called, allowing you to watch the `TokenAuthenticator` fetch a new token and retry the request in real-time.
- **Timber Logs**: Tagged logs (`AuthTest`) provide clear visibility into the background refresh process.

---

## ⚙️ Getting Started

### Switching to a Real Backend
1.  Implement the `AuthRepository` interface in the `data` layer.
2.  Update `RepositoryBindings.kt` to bind your new implementation.
3.  Update the `BASE_URL` in `app/build.gradle.kts`.

### Requirements
- Android Studio Ladybug or newer.
- JDK 17.
- Android 8.0 (API 26) or higher.

---

## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
