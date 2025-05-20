<h1 align="center">Tugas Kecil 3 IF2211 Strategi Algoritma</h1>
<h3 align="center">Penyelesaian Puzzle Rush Hour Menggunakan Algoritma Pathfinding</h3>

## Daftar Isi

- [Deskripsi](#deskripsi)
- [Kebutuhan Sistem](#kebutuhan-sistem)
- [Struktur](#struktur)
- [Cara Menjalankan](#cara-menjalankan)
- [Pengembang](#pengembang)

## Deskripsi

Program ini adalah program pemecah puzzle Rush Hour menggunakan algoritma pathfinding Uniform Cost Search (UCS), Greedy Best First Search (GBFS), dan A* Search. Puzzle dimainkan di papan grid berdimensi tertentu dengan kendaraan yang hanya bisa bergerak lurus sesuai orientasinya (horizontal atau vertikal), dan tujuan utamanya adalah memindahkan kendaraan utama (primary piece) ke pintu keluar. Program menerima input konfigurasi permainan dari file .txt, yang ditempatkan di folder test/input/ dengan isi yang menyatakan ukuran papan, jumlah kendaraan, posisi awal kendaraan, dan letak pintu keluar.

Pengguna dapat memilih algoritma yang digunakan, dan untuk algoritma berbasis heuristic, fungsi heuristic ditentukan melalui input. Program akan menampilkan jumlah node yang diperiksa, waktu eksekusi, serta langkah-langkah solusi lengkap dengan konfigurasi papan di setiap tahap. Hasil pencarian akan tersimpan dalam folder test/output dengan nama file dari masukan pengguna.

## Kebutuhan Sistem

* Java 8 atau versi yang lebih baru
* Sistem operasi Windows, macOS, atau Linux

## Struktur
```ssh
├── bin
│   ├── algorithms
│   │   ├─ AStarSearch$Node.class
│   │   ├─ AStarSearch.class
│   │   ├─ GreedyBestFirstSearch$Node.class
│   │   ├─ GreedyBestFirstSearch.class
│   │   ├─ SearchAlgorithm.class
│   │   ├─ UniformCostSearch$Node.class
│   │   └─ UniformCostSearch.class
│   ├── handler
│   │   ├─ InputHandler$PuzzleValidationException.class
│   │   └─ InputHandler.class
│   ├── heuristics
│   │   ├─ BlockingHeuristic.class
│   │   ├─ Heuristic.class
│   │   └─ MobilityHeuristic.class
│   ├── model
│   │   ├─ Board.class
│   │   ├─ Move.class
│   │   └─ Piece.class
│   └── Main.class
├── doc
│   └── Tucil3_13523081_Jethro Jens Norbert Simatupang.pdf
├── src
│   ├── algorithms
│   │   ├─ AStarSearch.java
│   │   ├─ GreedyBestFirstSearch.java
│   │   ├─ SearchAlgorithm.java
│   │   └─ UniformCostSearch.java
│   ├── handler
│   │   └─ InputHandler.java
│   ├── heuristics
│   │   ├─ BlockingHeuristic.java
│   │   ├─ Heuristic.java
│   │   └─ MobilityHeuristic.java
│   ├── model
│   │   ├─ Board.java
│   │   ├─ Move.java
│   │   └─ Piece.java
│   └── Main.java
├── test
│   ├── input
│   │   ├─ input1.png
│   │   ├─ input2.jpg
│   │   ├─ input3.jpg
│   │   ├─ input4.jpg
│   │   ├─ input5.jpeg
│   │   └─ input6.jpg
│   └── output
│       ├─ output1.png
│       ├─ output2.jpg
│       ├─ output3.jpg
│       ├─ output4.jpg
│       ├─ output5.jpeg
│       └─ output6.jpg
└── README.md
```

## Cara Menjalankan

1. Clone repository ini:

```bash
git clone https://github.com/JethroJNS/Tucil3_13523081.git
```

2. Navigasi ke direktori repositori dan jalankan command berikut:

```bash
javac -d bin src/handler/*.java src/model/*.java src/algorithms/*.java src/heuristics/*.java src/Main.java
```

```bash
java -cp bin Main
```

## Pengembang

| **NIM**  | **Nama Anggota**               | **Github** |
| -------- | ------------------------------ | ---------- |
| 13523081 | Jethro Jens Norbert Simatupang | [JethroJNS](https://github.com/JethroJNS) |
