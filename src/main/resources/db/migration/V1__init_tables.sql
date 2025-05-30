CREATE TABLE departments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE employees (
  code VARCHAR(255) PRIMARY KEY,
  department_id BIGINT,
  last_name VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  delete_flg BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  email VARCHAR(255),
  CONSTRAINT fk_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE reports (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  report_month VARCHAR(10) NOT NULL,
  employee_code VARCHAR(255) NOT NULL,
  delete_flg BOOLEAN DEFAULT FALSE,
  submitted_at TIMESTAMP,
  updated_at TIMESTAMP,
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
  complete_flg BOOLEAN DEFAULT FALSE,
  comment TEXT,
  report_deadline DATE,
  approval_flg BOOLEAN,
  CONSTRAINT fk_employee FOREIGN KEY (employee_code) REFERENCES employees(code)
);
