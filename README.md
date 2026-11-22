# StudioBook

StudioBook adalah aplikasi Android untuk mempermudah rental studio musik, penyewaan alat tambahan, serta *tracking* jadwal sesi latihan antara *User* dan *Admin*.

Repository ini digunakan untuk Project Akhir mata kuliah Pengembangan Aplikasi Mobile 2026[cite: 2, 23]. Repository berisi informasi proyek dan progres pengerjaan[cite: 23]. Dokumen SRS utama disusun di Google Docs.

## Dokumen SRS

[SRS StudioBook — Google Docs](https://docs.google.com/document/d/1cH5uK-r65YppjXJ3IA9fmv4UPfXlLdFj/edit?usp=sharing&ouid=103894771429013311323&rtpof=true&sd=true)

SRS disusun mengikuti template yang diberikan untuk mata kuliah[cite: 11, 23], mencakup kebutuhan dan fitur sistem[cite: 17, 23], *use case*[cite: 17, 23], alur proses/diagram[cite: 17, 23], serta struktur data dan relasi entitas[cite: 19, 23]. Kesesuaian isi dokumen dengan template dan keselarasan proyek akan dijaga selama pengembangan[cite: 5, 23].

## Ringkasan Proyek

StudioBook dirancang untuk membantu pengguna (*User*) mencari studio dan alat tambahan, mengajukan *booking* dengan konfirmasi transfer (*non-refundable* / menunggu ACC admin) serta pencegahan bentrok slot per kombinasi studio, tanggal, dan jam, dan memantau jadwal aktif (dengan pembersihan otomatis/*auto-purge* jadwal yang sudah melewati batas waktu). Sementara itu, *Admin* memiliki akses untuk meng-ACC transfer, menghapus jadwal, serta mengelola data studio dan alat musik (CRUDS).

## Teknologi dan Batasan

- Android: Kotlin, Jetpack Compose, Jetpack Navigation Compose, dan MVVM[cite: 5, 7, 23].
- State UI dikelola melalui `ViewModel` menggunakan `StateFlow`[cite: 7, 23].
- Backend: REST API JSON dengan autentikasi token[cite: 7, 23].
- Database: MySQL / Postgres / Cloud Database (sesuai ketentuan pedoman)[cite: 7].
- Penyimpanan lokal untuk file/preferensi; Room Database tidak digunakan[cite: 7, 23].

## Status

- [x] Repository publik dibuat[cite: 5, 23].
- [x] README proyek disusun[cite: 23].
- [ ] SRS disusun dan ditinjau sesuai template mata kuliah[cite: 23].
- [ ] Kebutuhan TBD dan diagram SRS dilengkapi serta diperiksa[cite: 17, 23].
- [ ] UI/UX dan alur navigasi dirancang[cite: 7, 23].
- [ ] Aplikasi Android dan backend diimplementasikan[cite: 7, 23].
- [ ] Fitur, keamanan, integrasi API, serta penanganan kondisi gagal diverifikasi[cite: 7, 19, 23].

Checklist ini akan diperbarui secara bertahap sesuai pekerjaan yang benar-benar selesai[cite: 23].

## Progres

| Tanggal | Progres |
| :--- | :--- |
| 2026-11-22 | Repository `0210_StudioBook` dibuat dan README awal disusun dengan tautan SRS[cite: 6, 23]. |

Pedoman proyek meminta pembaruan progres sekurang-kurangnya sekali setiap minggu dan minimal 30 *commit* nyata[cite: 5, 6, 23]. *Commit* akan dibuat bertahap untuk mencatat perubahan yang benar-benar dikerjakan[cite: 10, 23].

## Catatan

Nama repository `0210_StudioBook` mengikuti format pedoman `4NIMBelakang_NamaProject` dengan `0210` sebagai empat digit terakhir NIM `20240140210`[cite: 6, 23]. Pastikan repository tetap publik dan selaras dengan SRS sebelum pengumpulan[cite: 5, 23].
