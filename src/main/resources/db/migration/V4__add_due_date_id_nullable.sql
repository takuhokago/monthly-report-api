-- reports テーブルに due_date_id を追加（NULL許可）
ALTER TABLE reports
ADD COLUMN due_date_id INT;

-- 外部キー制約を追加（report_due_date.id への参照）
ALTER TABLE reports
ADD CONSTRAINT fk_reports_due_date
FOREIGN KEY (due_date_id) REFERENCES report_due_date(id);
