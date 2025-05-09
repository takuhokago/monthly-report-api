-- データベースの作成
CREATE DATABASE monthly_report_api CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

-- ユーザーの作成（パスワードは任意に変更可能）
CREATE USER 'monthly_api_user'@'localhost' IDENTIFIED BY 'monthly_api_pass';

-- 権限付与
GRANT ALL PRIVILEGES ON monthly_report_api.* TO 'monthly_api_user'@'localhost';
