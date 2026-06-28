# Android Authentication Architecture

[![Kotlin](https://img.shields.io/badge/kotlin-2.4.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose](https://img.shields.io/badge/compose-2026.06.00-green.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/hilt-2.60-orange.svg)](https://dagger.dev/hilt/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A production-ready authentication foundation for Android applications. This project implements a secure, scalable, and reactive authentication system following **Clean Architecture** principles, specifically designed for **JWT access-token based authentication**.

The goal is to provide a reusable, production-ready template that can be directly integrated into future Android applications, handling the complexities of session management, secure persistence, and reactive UI updates with minimal setup.

---

## 🚦 Authentication Strategy

This architecture currently supports:

- **JWT Access Tokens**: Standard token-based authentication.
- **Access Token Flow**: Focused on direct access-token usage without a refresh token flow (simplicity for MVP/V1).
- **Secure Persistence**: Encrypted session storage using Android Keystore.
- **Session Restoration**: Seamless session recovery after process death or app restarts.
- **Automatic Header Injection**: Authorization Bearer headers are handled centrally at the network layer.

The architecture is designed to be extensible; a refresh token flow can be added later without redesigning the core session management layers.

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
    - Automatic **Bearer Token injection** via OkHttp interceptor.
    - Centralized error mapping for clean architecture.
- **Developer Friendly**: Includes a `FakeAuthRepository` for rapid local testing and development.

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
The **in-memory source of truth** for the current authentication state. It exposes the session as a reactive `StateFlow` so the UI can respond immediately to login or logout events.

### SessionStorage
Abstracts how sessions are persisted. It currently uses **DataStore Preferences** combined with **Android Keystore** encryption. The storage is abstracted by an interface, allowing the persistence mechanism to be replaced (e.g., with Room or another secure implementation) without affecting the rest of the architecture.

### SessionManager
The orchestrator responsible for the authentication lifecycle (Restore, Create, Clear). It synchronizes the `SessionProvider`'s memory state with the `SessionStorage`'s persistent state. **DataStore** remains the persistent source used to restore the session after process death.

---

## 🔐 Security Implementation

### Persistent Storage Flow
When a session is saved:
1.  `UserSession` is serialized to JSON.
2.  The JSON string is encrypted using **AES-GCM** via the `AndroidKeystoreCryptoService`.
3.  A random 12-byte **Initialization Vector (IV)** is generated and prefixed to the ciphertext.
4.  The combined data is Base64 encoded and stored in **DataStore**.

The encryption key is generated and stored within the **Android Keystore**, ensuring it never leaves the secure hardware.

### Network Security
- **AuthInterceptor**: An OkHttp interceptor that automatically attaches the `Authorization: Bearer <token>` header to all requests if a valid session exists.
- **Tamper Detection**: If the encrypted session data is corrupted or cannot be decrypted, the system fails safely by clearing the local session and redirecting the user to the login screen.

---

## 🎯 Project Goals

This repository focuses on building a reusable authentication foundation rather than a complete application. Key goals include:

- **Clean Architecture**: Strict separation of concerns to prevent business logic from leaking into infrastructure.
- **Reusability**: Designed to be "copy-pasted" into new projects with minimal changes.
- **Secure by Default**: Leveraging Android Keystore for hardware-backed session encryption.
- **Reactive State**: Using Kotlin Flows to make the entire app react to authentication changes.
- **Testability**: Interfaces at every boundary to facilitate unit and integration testing.

---

## 🧪 Testing Without a Backend

The project includes a `FakeAuthRepository` which allows the complete authentication lifecycle to be tested locally. This includes:

- **Login/Logout** flows.
- **Session restoration** after app restarts.
- **Process death recovery** scenarios.
- **Encrypted DataStore persistence** verification.

Integrating a real backend only requires implementing the `AuthRepository` interface and replacing the dependency injection binding in `RepositoryBindings.kt`.

---

## ⚙️ Getting Started

### Switching to a Real Backend
1.  Implement the `AuthRepository` interface in the `data` layer.
2.  Update `RepositoryBindings.kt` to bind your new implementation:

```kotlin
@Binds
@Singleton
abstract fun bindAuthRepository(
    impl: AuthRepositoryImpl // Your real implementation
): AuthRepository
```

3.  Update the `BASE_URL` in `app/build.gradle.kts`.

### Requirements
- Android Studio Ladybug or newer.
- JDK 17.
- Android 8.0 (API 26) or higher.

---

## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
