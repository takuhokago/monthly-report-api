CREATE TABLE `departments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employees` (
  `delete_flg` tinyint NOT NULL,
  `department_id` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `code` varchar(10) NOT NULL,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(10) NOT NULL,
  PRIMARY KEY (`code`),
  KEY `FKgy4qe3dnqrm3ktd76sxp7n4c2` (`department_id`),
  CONSTRAINT `FKgy4qe3dnqrm3ktd76sxp7n4c2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `report_due_date` (
  `id` int NOT NULL AUTO_INCREMENT,
  `yearmonth` varchar(7) COLLATE utf8mb4_bin NOT NULL,
  `due_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `yearmonth` (`yearmonth`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `reports` (
  `approval_flg` tinyint DEFAULT NULL,
  `complete_flg` tinyint NOT NULL,
  `delete_flg` tinyint NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `rate_business` int NOT NULL,
  `rate_study` int NOT NULL,
  `report_deadline` date NOT NULL,
  `time_over` int NOT NULL,
  `time_worked` int NOT NULL,
  `trend_business` int NOT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `employee_code` varchar(10) NOT NULL,
  `content_company` longtext,
  `content_others` longtext,
  `evaluation_business` longtext,
  `evaluation_study` longtext,
  `goal_business` longtext,
  `goal_study` longtext,
  `comment` longtext,
  `content_customer` longtext,
  `content_member` longtext,
  `content_problem` longtext,
  `content_business` longtext,
  `report_month` varchar(255) NOT NULL,
  `due_date_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKml3syulw48umvbjfdaeqawq3b` (`employee_code`),
  KEY `fk_reports_due_date` (`due_date_id`),
  CONSTRAINT `fk_reports_due_date` FOREIGN KEY (`due_date_id`) REFERENCES `report_due_date` (`id`),
  CONSTRAINT `FKml3syulw48umvbjfdaeqawq3b` FOREIGN KEY (`employee_code`) REFERENCES `employees` (`code`),
  CONSTRAINT `reports_chk_1` CHECK (((`rate_business` >= 0) and (`rate_business` <= 999))),
  CONSTRAINT `reports_chk_2` CHECK (((`rate_study` >= 0) and (`rate_study` <= 999))),
  CONSTRAINT `reports_chk_5` CHECK (((`trend_business` >= 0) and (`trend_business` <= 999)))
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO departments (id, name) VALUES
  (1, 'A'),
  (2, 'B');

INSERT INTO employees (
  code,
  department_id,
  last_name,
  first_name,
  role,
  password,
  delete_flg,
  created_at,
  updated_at,
  email
) VALUES (
  '9999',
  1,
  'last_name',
  'first_name',
  'ADMIN',
  '$2a$10$PpSqsG6kVXXSBXDzx5h5d..YrRuUEEjfifp3SWVU5pJRA1qOfbUdS',
  0,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  'admin@example.com'
);

