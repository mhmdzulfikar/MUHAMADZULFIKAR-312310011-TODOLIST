MUHAMAD-ZULFIKAR-312310011-TO-DO-LIST

---

![image](https://github.com/user-attachments/assets/64197a5d-8ed1-43ef-92e9-170a465fbf48)



Storyboard
1. Halaman Utama
Elemen:
Header dengan nama pengguna yang terdaftar (atau opsi "Login" jika belum login).
Label "TODAY" untuk menampilkan tanggal saat ini.
Daftar kegiatan yang memiliki waktu mulai dan selesai.
Tombol tambah (+) di sudut kanan bawah untuk menambah kegiatan.
Interaksi:
Saat pengguna menekan tombol hamburger (☰), sebuah panel samping muncul dengan opsi "Login/Register" (jika belum login) atau "Logout" (jika sudah login) dan "Themes".
Saat tombol tambah (+) ditekan, pengguna diarahkan ke halaman untuk menambah kegiatan baru.
2. Panel Samping (Sidebar)
Elemen:
Menu dengan opsi "Login/Register" atau "Logout" (tergantung status pengguna) dan "Themes".
Interaksi:
Jika pengguna memilih "Login/Register" (belum login), sistem akan mengarahkan mereka ke halaman login atau registrasi.
Jika pengguna memilih "Logout" (sudah login), pengguna akan dikeluarkan dari akun dan data kegiatan tidak akan ditampilkan sampai login kembali.
Jika pengguna memilih "Themes," sistem memungkinkan mereka memilih tema aplikasi (misalnya, tema gelap atau terang).
3. Halaman Login/Register
Elemen:
Kolom untuk memasukkan email dan kata sandi (untuk login) atau kolom tambahan untuk nama pengguna dan konfirmasi kata sandi (untuk registrasi).
Tombol "Login" dan "Register" di bagian bawah.
Interaksi:
Jika pengguna memilih "Login" dan memasukkan informasi yang benar, mereka akan diarahkan ke halaman utama dengan data kegiatan yang sudah tersimpan sebelumnya.
Jika pengguna memilih "Register," data akun mereka akan disimpan ke database sehingga setiap kali mereka login, data kegiatan yang sebelumnya ditambahkan akan tetap ada, meskipun aplikasi dihapus atau diinstal ulang.
Jika data login salah, muncul pesan error yang menyarankan pengguna untuk memasukkan data yang benar.
4. Halaman Tambah Kegiatan
Elemen:
Kolom "Date" untuk memilih tanggal kegiatan.
Kolom "Time" untuk mengatur waktu mulai dan selesai.
Kolom "Title" untuk judul kegiatan.
Kolom "Notes" untuk catatan tambahan.
Tombol "Cancel" dan "Save" di bagian bawah.
Interaksi:
Pengguna dapat mengisi detail kegiatan, termasuk tanggal, waktu, judul, dan catatan.
Ketika pengguna menekan tombol "Save," kegiatan baru disimpan ke database dan muncul di daftar kegiatan di halaman utama.
Jika tombol "Cancel" ditekan, pengguna kembali ke halaman utama tanpa menyimpan perubahan.
5. Pemilihan Tanggal dan Waktu
Elemen:
Kalender untuk memilih tanggal kegiatan.
Selektor waktu untuk mengatur jam mulai dan selesai kegiatan.
Interaksi:
Ketika pengguna menekan kolom tanggal atau waktu, kalender atau selektor waktu muncul.
Pengguna memilih tanggal dan waktu sesuai keinginan, lalu kembali ke halaman sebelumnya dengan nilai yang telah dipilih.
