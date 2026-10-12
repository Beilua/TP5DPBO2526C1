// class to represent a person (musician) for PeopleData
public class Person {
    // private attributes
    private String id;
    private String nama;
    private int tahunLahir;
    private String kategori;
    private String genre;
    private int popularitas;
    private String tipe;

    // constructor with all attributes
    public Person(String id, String nama, int tahunLahir, String kategori,
                  String genre, int popularitas, String tipe) {
        this.id = id;
        this.nama = nama;
        this.tahunLahir = tahunLahir;
        this.kategori = kategori;
        this.genre = genre;
        this.popularitas = popularitas;
        this.tipe = tipe;
    }

    // id getter and setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // nama getter and setter
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    // tahunLahir getter and setter
    public int getTahunLahir() {
        return tahunLahir;
    }

    public void setTahunLahir(int tahunLahir) {
        this.tahunLahir = tahunLahir;
    }

    // kategori getter and setter
    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    // genre getter and setter
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    // popularitas getter and setter
    public int getPopularitas() {
        return popularitas;
    }

    public void setPopularitas(int popularitas) {
        this.popularitas = popularitas;
    }

    // tipe getter and setter
    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
}
