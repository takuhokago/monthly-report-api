-- 部署テーブル
CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- 従業員テーブル
CREATE TABLE employees (
    code VARCHAR(10) PRIMARY KEY,
    department_id INT,
    last_name VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    delete_flg BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    email VARCHAR(100),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- 報告書テーブル
CREATE TABLE reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_month VARCHAR(7) NOT NULL,
    employee_code VARCHAR(10) NOT NULL,
    delete_flg BOOLEAN NOT NULL DEFAULT FALSE,
    submitted_at DATETIME,
    updated_at DATETIME,
    content_business TEXT,
    time_worked INT,
    time_over INT,
    rate_business INT,
    rate_study INT,
    trend_business INT,
    content_member TEXT,
    content_customer TEXT,
    content_problem TEXT,
    evaluation_business TEXT,
    evaluation_study TEXT,
    goal_business TEXT,
    goal_study TEXT,
    content_company TEXT,
    content_others TEXT,
    complete_flg BOOLEAN NOT NULL DEFAULT FALSE,
    comment TEXT,
    report_deadline DATE,
    approval_flg BOOLEAN,
    FOREIGN KEY (employee_code) REFERENCES employees(code)
);
