# 📚 Book Legend

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)

Nowoczesna aplikacja mobilna na platformę Android służąca do przeglądania, wyszukiwania i kolekcjonowania ulubionych książek. Aplikacja korzysta z otwartego API **OpenLibrary** i została zbudowana w oparciu o najnowsze standardy **Modern Android Development**.

---

## ✨ Kluczowe Funkcje

* **📖 Przeglądanie Katalogu:** Pobieranie listy najpopularniejszych książek (kategoria Fiction) z API OpenLibrary.
* **♾️ Infinite Scroll (Paginacja):** Automatyczne doczytywanie kolejnych książek podczas przewijania listy.
* **🔄 Pull-to-Refresh:** Możliwość odświeżenia listy gestem pociągnięcia w dół.
* **🔍 Wyszukiwarka:** Możliwość wyszukiwania książek po tytule lub autorze (Live Search).
* **❤️ Ulubione:** Dodawanie i usuwanie książek z listy ulubionych. Dane są zapisywane lokalnie (persistent storage).
* **🌙 Tryb Ciemny (Dark Mode):** Pełna obsługa motywu ciemnego z przełącznikiem w pasku aplikacji. Stan jest zapamiętywany po restarcie.
* **📱 Szczegóły Książki:** Widok detali z dużą okładką, opisem, rokiem wydania i liczbą stron.

---

## 🛠️ Stack Technologiczny

Projekt wykorzystuje architekturę **MVVM (Model-View-ViewModel)** oraz zasadę **Clean Architecture**.

* **Język:** [Kotlin](https://kotlinlang.org/)
* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Asynchroniczność:** [Coroutines](https://developer.android.com/kotlin/coroutines) & [Flow](https://developer.android.com/kotlin/flow)
* **Sieć:** [Retrofit2](https://square.github.io/retrofit/) + [Gson](https://github.com/google/gson)
* **Ładowanie Obrazów:** [Coil](https://coil-kt.github.io/coil/)
* **Baza Danych (Lokalna):** [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) (zastępstwo dla SharedPreferences)
* **Nawigacja:** Jetpack Navigation Compose
* **System Budowania:** Gradle (Kotlin DSL)

---

## 🚀 Jak uruchomić projekt

1.  **Sklonuj repozytorium:**
    ```bash
    git clone [https://github.com/TwojLogin/booklegend.git](https://github.com/TwojLogin/booklegend.git)
    ```
2.  **Otwórz w Android Studio:**
    Uruchom Android Studio i wybierz opcję "Open an existing project", wskazując pobrany folder.
3.  **Synchronizacja Gradle:**
    Poczekaj, aż Android Studio pobierze niezbędne zależności.
4.  **Uruchom:**
    Podłącz telefon lub uruchom emulator (min. API 24) i naciśnij przycisk ▶️ Run.

---

## 📂 Struktura Projektu

```text
com.example.booklegend
├── data                # Warstwa Danych
│   ├── local           # DataStore (zapis lokalny)
│   ├── model           # Modele danych (Book, DTOs)
│   ├── network         # Retrofit API i Klient
│   └── repository      # Repozytorium (logika biznesowa)
├── ui                  # Warstwa Prezentacji
│   ├── screens         # Ekrany (Home, Detail, Favorites)
│   ├── theme           # Motywy i Kolory (Dark/Light)
│   └── viewmodel       # ViewModele (Zarządzanie stanem)
└── MainActivity.kt     # Punkt wejścia i nawigacja
