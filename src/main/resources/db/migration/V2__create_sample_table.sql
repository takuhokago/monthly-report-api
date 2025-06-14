-- V2__alter_sample_table.sql にリネームして中身を以下に変更
ALTER TABLE sample
  ADD created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
