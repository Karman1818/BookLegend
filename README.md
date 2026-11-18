# 📚 Book Legend

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)

A modern Android mobile application for browsing, searching, and collecting your favorite books. The app consumes the public **OpenLibrary** API and is built according to the latest **Modern Android Development** standards.

---

## ✨ Key Features

* **📖 Catalog Browsing:** Fetches a list of the most popular books (Fiction category) from the OpenLibrary API.
* **♾️ Infinite Scroll (Pagination):** Automatically loads subsequent books as the user scrolls down the list.
* **🔄 Pull-to-Refresh:** Allows users to refresh the list with a swipe-down gesture.
* **🔍 Search:** Search for books by title or author (Live Search).
* **❤️ Favorites:** Add and remove books from your favorites list. Data is saved locally (persistent storage).
* **🌙 Dark Mode:** Full dark theme support with a toggle in the app bar. The preference state is preserved across app restarts.
* **🌐 Multi-Language Support (i18n):** Easily switch between Polish and English directly from the app bar. User preference is saved across app restarts.
* **📱 Book Details:** A detailed view featuring a large cover image, description, publication year, and page count.

---

## 🛠️ Tech Stack

The project utilizes the **MVVM (Model-View-ViewModel)** architecture and **Clean Architecture** principles.

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Asynchrony:** [Coroutines](https://developer.android.com/kotlin/coroutines) & [Flow](https://developer.android.com/kotlin/flow)
* **Networking:** [Retrofit2](https://square.github.io/retrofit/) + [Gson](https://github.com/google/gson)
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
* **Local Storage:** [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) (SharedPreferences replacement)
* **Navigation:** Jetpack Navigation Compose
* **Build System:** Gradle (Kotlin DSL)

---

## 🚀 How to Run the Project

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YourUsername/booklegend.git](https://github.com/YourUsername/booklegend.git)
    ```
2.  **Open in Android Studio:**
    Launch Android Studio, select "Open an existing project", and choose the downloaded folder.
3.  **Gradle Synchronization:**
    Wait for Android Studio to download the necessary dependencies.
4.  **Run:**
    Connect a physical device or start an emulator (min. API 24) and press the ▶️ Run button.

---

## 📂 Project Structure

```text
com.example.booklegend
├── data                # Data Layer
│   ├── local           # DataStore (local storage)
│   ├── model           # Data models (Book, DTOs)
│   ├── network         # Retrofit API and Client
│   └── repository      # Repository (business logic)
├── ui                  # Presentation Layer
│   ├── screens         # Screens (Home, Detail, Favorites)
│   ├── theme           # Themes and Colors (Dark/Light)
│   └── viewmodel       # ViewModels (State management)
└── MainActivity.kt     # Entry point and navigation
