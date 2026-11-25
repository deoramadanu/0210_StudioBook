# StudioBook

StudioBook adalah aplikasi Android berbasis Kotlin dan Jetpack Compose untuk mempermudah penyewaan ruangan studio musik, penyewaan alat musik tambahan, serta pelacakan (*tracking*) slot jadwal sesi latihan antara **User** dan **Admin**.

Repository ini digunakan untuk **Project Akhir Mata Kuliah Pengembangan Aplikasi Mobile (PAM) 2026** oleh **Alfilla Deo Ramadanu** (NIM: `20240140210`, Kelas B — Teknologi Informasi, Universitas Muhammadiyah Yogyakarta).

## Dokumen SRS

- [SRS StudioBook (v0.4) — Google Docs](https://docs.google.com/document/d/1cH5uK-r65YppjXJ3IA9fmv4UPfXlLdFj/edit?usp=sharing&ouid=103894771429013311323&rtpof=true&sd=true)

Dokumen Software Requirement Specification (SRS) disusun mengikuti template mata kuliah dan telah diperbarui ke versi `v0.4` untuk menyelaraskan seluruh kebutuhan fungsional (`REQ-01` s.d. `REQ-18`), *Use Case Diagram* (Bab 2), *Flowchart* & *Activity Diagram* per fitur (Bab 3), *Entity Relationship Diagram* / ERD & *Requirements Traceability Matrix* / RAT (Bab 6), serta aturan bisnis aktual aplikasi.

## Ringkasan Proyek & Aturan Bisnis

- **Peran Pengguna (`USER` & `ADMIN`):** Pemisahan hak akses dan *role-based UI* untuk penyewa studio (`USER`) dan pengelola studio (`ADMIN`).
- **Katalog Studio & Alat Musik (CRUDS):** Manajemen dan pencarian ruangan studio (kapasitas, tarif per jam, fasilitas, status) serta alat musik/perlengkapan tambahan dengan operasi *Create, Read, Update, Delete, Search* (CRUDS).
- **Booking & Anti-Bentrok Spesifik:** Pengguna memilih studio, tanggal (`YYYY-MM-DD`, kompatibel `minSdk 24+`), slot jam, serta mengisi Nama Band, Nama Pemesan, dan konfirmasi transfer. Validasi anti-bentrok memeriksa kombinasi spesifik **`Studio + Tanggal + Jam/Slot`**, sehingga studio berbeda pada jam yang sama tetap dapat dipesan bila slotnya tersedia.
- **Pembayaran Transfer Non-Refundable:** Pembayaran transfer ke Admin bersifat *non-refundable* (tanpa tombol pembatalan/refund sepihak yang bertentangan dengan aturan bisnis).
- **ACC Transfer Admin & Auto-Purge:** Admin dapat meng-ACC transfer (`Pending -> Confirmed`) dan menghapus jadwal. Jadwal yang sudah melewati batas tanggal dan jam selesai dibersihkan secara otomatis (*auto-purge*).

## Teknologi dan Arsitektur

- **Bahasa & UI:** Kotlin, Jetpack Compose murni, dan Jetpack Navigation Compose (`Screen.kt`).
- **Arsitektur:** MVVM (`StudioViewModel` & `StudioRepository`) dengan pengelolaan *state* reaktif menggunakan `StateFlow` dan Kotlin Coroutines.
- **Backend & Jaringan:** REST API berbasis HTTP/HTTPS & JSON menggunakan Retrofit, OkHttp, dan Gson (`ApiService.kt`, `RetrofitClient.kt`) dengan autentikasi JWT (*Access Token* & *Refresh Token*).
- **Database & Penyimpanan Lokal:** Basis data relasional/cloud (MySQL / PostgreSQL / Supabase) melalui REST API, penyimpanan sesi/token melalui Preferences DataStore (`TokenManager.kt`), serta pengelolaan file lokal perangkat (**tanpa Room Database**).
- **Target Lingkungan:** Android `minSdk 24+` (teruji pada emulator `HP NENEKKU API 37.2`).

## Struktur Proyek (`com.example.studiobook`)

```text
com.example.studiobook/
├── data/
│   ├── local/        # TokenManager.kt (Preferences DataStore untuk JWT & sesi)
│   ├── model/        # StudioModels.kt (Users, Studios, Equipment, Bookings)
│   ├── remote/       # ApiService.kt, RetrofitClient.kt (Retrofit + OkHttp)
│   └── repository/   # StudioRepository.kt (Abstraksi akses data & REST API)
├── ui/
│   ├── component/    # StudioCard.kt (Komponen kartu UI reusable)
│   ├── navigation/   # Screen.kt (Rute & navigasi Compose)
│   ├── screen/       # StudioScreen.kt (Layar utama, katalog, booking, admin)
│   └── theme/        # Color.kt, Theme.kt, Type.kt
├── viewmodel/        # StudioViewModel.kt (StateFlow, validasi, anti-bentrok, auto-purge)
└── MainActivity.kt   # Entry point aplikasi Jetpack Compose
