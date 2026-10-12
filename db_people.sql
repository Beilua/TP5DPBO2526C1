-- Database: db_people

CREATE DATABASE db_people;
USE db_people;

CREATE TABLE person (
  id varchar(10) PRIMARY KEY,
  nama varchar(50) NOT NULL,
  tahun_lahir int NOT NULL,
  kategori varchar(50) NOT NULL,
  genre varchar(50) NOT NULL,
  popularitas int NOT NULL,
  tipe varchar(20) NOT NULL
);

INSERT INTO person (id, nama, tahun_lahir, kategori, genre, popularitas, tipe) VALUES
('MUS001', 'Han Jisung', 2000, 'Vocalist', 'K-Pop', 93, 'Band'),
('MUS002', 'Gaon', 2002, 'Guitarist', 'Rock', 82, 'Band'),
('MUS003', 'Sabrina Carpenter', 1999, 'Vocalist', 'Pop', 95, 'Solo'),
('MUS004', 'Gerard Way', 1977, 'Vocalist', 'Rock', 90, 'Band'),
('MUS005', 'Taka Moriuchi', 1988, 'Vocalist', 'Rock', 88, 'Band'),
('MUS006', 'Lee Suhyun', 1999, 'Vocalist', 'K-Pop', 86, 'Duo'),
('MUS007', 'Wendy', 1994, 'Vocalist', 'K-Pop', 89, 'Band'),
('MUS008', 'IU', 1993, 'Vocalist', 'K-Pop', 97, 'Solo'),
('MUS009', 'Gunil', 1998, 'Drummer', 'Rock', 78, 'Band'),
('MUS010', 'Dongmyeong', 2000, 'Keyboardist', 'Rock', 75, 'Band');