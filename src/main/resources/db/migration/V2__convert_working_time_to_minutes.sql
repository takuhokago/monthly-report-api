-- 勤務時間・残業時間を時間単位 → 分単位 に変換する
-- カラム名は変更せず、データだけを変換

-- 1. バックアップ用の一時カラムを作成
ALTER TABLE reports ADD COLUMN timeWorked_tmp INT;
ALTER TABLE reports ADD COLUMN timeOver_tmp INT;

-- 2. データを分単位に変換して一時カラムに移す
UPDATE reports SET timeWorked_tmp = time_worked * 60;
UPDATE reports SET timeOver_tmp = time_over * 60;

-- 3. 元のカラムに上書き（分単位の値）
UPDATE reports SET time_worked = timeWorked_tmp;
UPDATE reports SET time_over = timeOver_tmp;

-- 4. 一時カラムを削除
ALTER TABLE reports DROP COLUMN timeWorked_tmp;
ALTER TABLE reports DROP COLUMN timeOver_tmp;
