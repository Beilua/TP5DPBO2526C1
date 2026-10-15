# TP 5 DPBO — PeopleData (Data Musisi + MySQL)

Tugas Praktikum 5 Mata Kuliah **Desain dan Pemrograman Berorientasi Objek (DPBO)**.

Program ini mengelola data musisi (`People`) dengan GUI Java Swing, mengikuti pola `ProductData`: form input di atas, tabel data di bawah, dengan operasi CRUD penuh plus validasi dan dialog konfirmasi hapus. Berbeda dari TP4, seluruh data tersimpan persisten di MySQL (`db_people.person`) via JDBC, bukan `ArrayList`.

## Janji

Saya Nabila Attaya Putri Cahyadi dengan NIM 2508355 mengerjakan Tugas Praktikum 5 pada Mata Kuliah Desain dan Pemrograman Berorientasi Objek (DPBO) untuk keberkahan-Nya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin

## Struktur Folder

```text
TP5DPBO2526C1/
├── README.md
├── db_people.sql
├── PeopleData/
│   ├── PeopleData.iml
│   ├── fonts/
│   │   └── CuteDino.ttf
│   └── src/
│       ├── Person.java
│       ├── Database.java
│       ├── PeopleMenu.java
│       └── PeopleMenu.form
└── Dokumentasi/
    ├── select.mp4 / select.gif
    ├── insert.mp4 / insert.gif
    ├── update.mp4 / update.gif
    ├── delete.mp4 / delete.gif
    ├── cancel.mp4 / cancel.gif
    ├── id_name_cannot_be_empty.mp4 / id_name_cannot_be_empty.gif
    ├── id_invalid_format.mp4 / id_invalid_format.gif
    ├── id_already_exists.mp4 / id_already_exists.gif
    ├── name_under_constraint.mp4 / name_under_constraint.gif
    ├── role_empty.mp4 / role_empty.gif
    ├── genre_empty.mp4 / genre_empty.gif
    ├── type_empty.mp4 / type_empty.gif
    └── birth_year_invalid.mp4 / birth_year_invalid.gif
```

## Desain / Fungsi Program

**Tema:** Data Musisi — setiap `Person` adalah satu musisi dengan 7 atribut. TP5 memakai tema visual cream/olive/mustard (`CREAM`, `OLIVE`, `MUSTARD`, `DARK`, `SOFT_WHITE`) + font custom `CuteDino.ttf` via `loadCustomFont()`.

**Atribut `Person.java` (`PeopleData/src/Person.java:4-10`):**

| # | Atribut | Tipe | Komponen GUI | Keterangan |
|---|---|---|---|---|
| 1 | ID | `String` | `JTextField` (`idField`) | Format `MUSXXX`, contoh `MUS001`, PRIMARY KEY di DB |
| 2 | Nama | `String` | `JTextField` (`namaField`) | Contoh `IU` |
| 3 | Tahun Lahir | `int` | `JTextField` (`tahunLahirField`) | Angka `1900`–tahun berjalan |
| 4 | Kategori (Peran) | `String` | `JComboBox` (`kategoriComboBox`) | `Select Role, Vocalist, Guitarist, Bassist, Drummer, Keyboardist, DJ/Producer` |
| 5 | Genre | `String` | `JComboBox` (`genreComboBox`) | `Select Genre, Pop, Rock, Jazz, Classical, Hip-Hop, Dangdut, K-Pop, EDM` |
| 6 | Popularitas | `int` 0–100 | `JSlider` + `JLabel` (`popularitySlider`, `popularityValueLabel`) | `setMinimum(0)`, `setMaximum(100)`, `setMajorTickSpacing(25)`, `setMinorTickSpacing(5)`, label update realtime via `ChangeListener` |
| 7 | Tipe | `String` | `JRadioButton` x3 + `ButtonGroup` (`soloRadioButton`, `duoRadioButton`, `bandRadioButton`, `tipeButtonGroup`) | `Solo / Duo / Band` |

**File program:**

- `PeopleData/src/Person.java` — model data + getter/setter semua atribut.
- `PeopleData/src/Database.java` — koneksi JDBC + `selectQuery()` untuk SELECT, `insertUpdateDeleteQuery()` untuk INSERT/UPDATE/DELETE, `getStatement()`.
- `PeopleData/src/PeopleMenu.java` — `JFrame` utama: `buildManualUI()`, CRUD via SQL (`insertData`, `updateData`, `deleteData`), `setTable()` dari `ResultSet`, `clearForm()`, styling + semua listener.
- `PeopleData/src/PeopleMenu.form` — binding GUI Designer IntelliJ, mirror dari `buildManualUI()` agar program tetap jalan dengan `javac` biasa.
- `db_people.sql` — skema `db_people.person(id, nama, tahun_lahir, kategori, genre, popularitas, tipe)` + 10 data awal.
- `PeopleData/fonts/CuteDino.ttf` — font custom yang diload di `loadCustomFont()` dengan fallback `SansSerif`.

**Fungsi tiap komponen:**

- Form (`JTextField`/`JComboBox`/`JSlider`/`JRadioButton`) untuk input dan edit data.
- `addUpdateButton` berlabel `Add` saat `selectedIndex == -1`, berubah jadi `Update` setelah row diklik.
- `deleteButton` disembunyikan (`setVisible(false)`) saat mode Add, dimunculkan setelah row diklik.
- `cancelButton` memanggil `clearForm()` untuk reset form.
- `personTable` (`JTable` + `DefaultTableModel` dengan `isCellEditable=false`) untuk Read: klik row mengisi seluruh form via `mousePressed`.

## Method Program

| Method | Fungsi |
|---|---|
| `main` | Titik masuk: `loadCustomFont()`, override `UIManager`, `SwingUtilities.invokeLater`, set `ContentPane`, judul `Musician Data`, ukuran `800x650`, `EXIT_ON_CLOSE`, tampilkan window. |
| `loadCustomFont()` | Load `PeopleData/fonts/CuteDino.ttf`, register ke `GraphicsEnvironment`, derive `TITLE_FONT/BODY_FONT/SMALL_FONT`; fallback `SansSerif` jika gagal. |
| `buildManualUI()` | Membangun seluruh UI manual (`BorderLayout` + `GridBagLayout`): title, form 7 baris, panel tombol vertikal, tabel + `JScrollPane`. Plus `styleTextField/styleComboBox/styleSlider/styleRadioButton`. |
| `Database()` | Konek `jdbc:mysql://localhost:3306/db_people` (`root`/`""`), buat `Statement`. |
| `selectQuery(sql)` | Eksekusi SELECT, return `ResultSet` (`Database.java:21-28`). |
| `insertUpdateDeleteQuery(sql)` | Eksekusi INSERT/UPDATE/DELETE, return row count (`Database.java:31-37`). |
| `setTable()` | Membuat `DefaultTableModel` read-only dengan kolom `No, ID, Name, Birth Year, Category, Genre, Popularity, Type`, mengisi dari `SELECT * FROM person` via `ResultSet`. |
| `insertData()` | **Create:** ambil nilai form, validasi penuh, cek duplikat via `SELECT id FROM person WHERE id='...'`, `INSERT INTO person VALUES(...)`, refresh tabel, `clearForm()`, dialog sukses. |
| `updateData()` | **Update:** cek `selectedIndex` valid, ambil `oldId` dari tabel, validasi (duplikat ID via DB, skip jika tidak berubah), `UPDATE person SET ... WHERE id='oldId'`, refresh tabel, `clearForm()`. |
| `deleteData()` | **Delete:** cek `selectedIndex` valid, ambil `id` dari tabel, `DELETE FROM person WHERE id='...'`, refresh tabel, `clearForm()`. Dipanggil hanya setelah konfirmasi `YES_OPTION`. |
| `clearForm()` | Reset form: kosongkan text field, combo ke index 0, slider ke 50, `ButtonGroup.clearSelection()`, tombol kembali ke `Add`, sembunyikan Delete, `selectedIndex = -1`. |
| `getSelectedTipe()` / `setSelectedTipe(tipe)` | Baca radio yang dipilih (`Solo`/`Duo`/`Band`/`null`) dan set radio dari string tipe tabel. |
| Listener tombol + tabel | `Add/Update` bercabang via `selectedIndex`; `Delete` menampilkan `showConfirmDialog(YES_NO_OPTION)`; `Cancel` memanggil `clearForm()`; `mousePressed` pada tabel mengisi form + ubah mode ke Update; `ChangeListener` slider update label realtime. |

## Penjelasan Alur

1. Program jalan: `main` load font + `constructor` memanggil `buildManualUI()`, `database = new Database()`, `personTable.setModel(setTable())` dari MySQL, combo/slider/radio diinisialisasi, `clearForm()` set mode Add.
2. **Read (tampilan awal):** tabel menampilkan isi `person` (`No, ID, Name, Birth Year, Category, Genre, Popularity, Type`) hasil `SELECT * FROM person`.
3. **Create:** isi form -> klik `Add` (`selectedIndex == -1`) -> `insertData()` validasi + cek duplikat di DB -> lolos: `INSERT` -> `setTable()` refresh dari DB -> `clearForm()` -> dialog `Data added successfully`.
4. **Select:** klik row tabel -> `mousePressed` set `selectedIndex = row` -> `idField`/`namaField`/`tahunLahirField`/`kategoriComboBox`/`genreComboBox` terisi, `popularitySlider.setValue(pop)`, `setSelectedTipe(...)` -> tombol jadi `Update`, tombol `Delete` muncul.
5. **Update:** edit form -> klik `Update` -> `updateData()` validasi (cek duplikat hanya jika ID berubah) -> lolos: `UPDATE ... WHERE id='oldId'` -> `setTable()` -> `clearForm()` kembali ke mode Add -> dialog `Data updated successfully`.
6. **Delete:** klik row -> klik `Delete` -> `showConfirmDialog(Are you sure...?, YES_NO_OPTION)` -> `YES`: `deleteData()` (`DELETE WHERE id`) -> `setTable()` -> `clearForm()` -> dialog sukses; `NO`: batal, data tetap.
7. **Cancel:** kapan saja klik `Cancel` -> `clearForm()`: field kosong, combo ke `Select Role`/`Select Genre`, slider 50, radio clear, tombol `Add`, Delete hidden, `selectedIndex = -1`.
8. **Slider:** setiap geser `popularitySlider` -> `ChangeListener` update `popularityValueLabel` realtime.
9. Data persisten di MySQL — tetap ada setelah restart, tidak seperti TP4 yang hanya di `ArrayList`.

## Validasi / Error Handling

| Bagian | Kondisi tidak valid | Penanganan |
|---|---|---|
| Field kosong | `ID` / `Name` / `Birth Year` kosong | `ERROR_MESSAGE`: `ID, Name, and Birth Year cannot be empty!` |
| Format ID | Tidak cocok `^MUS\d{3}$` | `ERROR_MESSAGE`: `ID must be in MUSXXX format! Example: MUS001.` |
| Duplikat ID | `SELECT id FROM person WHERE id='...'` ada hasil (Update mengabaikan jika ID tidak berubah) | `ERROR_MESSAGE`: `ID already exists! Use another ID.` |
| Nama | Panjang <2 (Insert) / <3 (Update) atau >50; mengandung selain huruf/spasi/`.'-` | `ERROR_MESSAGE`: `Name must be ...` / `Name can only contain letters, spaces, ., ' and -!` |
| Kategori | `kategoriComboBox.getSelectedIndex() <= 0` (`Select Role`) | `ERROR_MESSAGE`: `Please select a Category (Role)!` |
| Genre | `genreComboBox.getSelectedIndex() <= 0` (`Select Genre`) | `ERROR_MESSAGE`: `Please select a Genre!` |
| Tipe | Tidak ada radio dipilih (`getSelectedTipe() == null`) | `ERROR_MESSAGE`: `Please select a Type (Solo/Duo/Band)!` |
| Popularitas | `< 0` atau `> 100` | `ERROR_MESSAGE`: `Popularity must be between 0-100!` |
| Tahun Lahir | Bukan digit / bukan angka; `< 1900` atau `> tahun berjalan` | `ERROR_MESSAGE`: `Birth Year must be a number!` / `Birth Year must be between 1900-<currentYear>!` |
| Update/Delete tanpa seleksi | `selectedIndex` tidak valid | Dialog: `Select the data you want to update/delete.` |
| Delete | Klik `Delete` | `showConfirmDialog(..., YES_NO_OPTION, WARNING_MESSAGE)`; hapus hanya jika `YES_OPTION`. |

## Dokumentasi Saat Program Dijalankan

Semua operasi CRUD ditunjukkan di bawah (GIF agar langsung tampil di GitHub; file `.mp4` asli dengan nama sama tersedia di folder `Dokumentasi/`).

### Read — Klik row mengisi form

![Select row mengisi form](Dokumentasi/select.gif)

### Create — Insert data baru via tombol Add

![Insert data](Dokumentasi/insert.gif)

### Update — Edit data terpilih via tombol Update

![Update data](Dokumentasi/update.gif)

### Delete — Hapus data + confirmation prompt

![Delete data](Dokumentasi/delete.gif)

### Cancel — Reset form via tombol Cancel

![Cancel](Dokumentasi/cancel.gif)

### Validasi — ID, Name, Birth Year tidak boleh kosong

![ID Name empty](Dokumentasi/id_name_cannot_be_empty.gif)

### Validasi — Format ID harus MUSXXX

![ID invalid format](Dokumentasi/id_invalid_format.gif)

### Validasi — ID sudah ada

![ID already exists](Dokumentasi/id_already_exists.gif)

### Validasi — Nama di bawah constraint / karakter invalid

![Name constraint](Dokumentasi/name_under_constraint.gif)

### Validasi — Kategori (Role) belum dipilih

![Role empty](Dokumentasi/role_empty.gif)

### Validasi — Genre belum dipilih

![Genre empty](Dokumentasi/genre_empty.gif)

### Validasi — Tipe belum dipilih

![Type empty](Dokumentasi/type_empty.gif)

### Validasi — Tahun lahir invalid

![Birth year invalid](Dokumentasi/birth_year_invalid.gif)
